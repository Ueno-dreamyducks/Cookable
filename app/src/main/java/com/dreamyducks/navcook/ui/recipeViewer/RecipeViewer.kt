package com.dreamyducks.navcook.ui.recipeViewer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flatware
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.dreamyducks.navcook.R
import com.dreamyducks.navcook.format.nonScaledSp
import com.dreamyducks.navcook.network.Recipe
import com.dreamyducks.navcook.ui.theme.NavCookTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RecipeViewer(
    modifier: Modifier = Modifier,
    viewModel: ViewerViewModel = viewModel(),
    innerPadding: PaddingValues,
    onNavigateBack: () -> Unit
) {
    val recipeUiState = viewModel.recipe //.recipeUiState.collectAsState()

    val context = LocalContext.current
    val window = (context as? Activity)?.window ?: return
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)

    val viewerUiState = viewModel.viewerUiState.collectAsState()

    var showExitDialog by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(recipeUiState.value!!.steps[viewerUiState.value.currentIndex]) }
    var audioScript by remember { mutableStateOf("") }
    var isChangingStep by remember { mutableStateOf(false) }

    var overlayHeight by remember { mutableStateOf(0.dp) }

    BackHandler {
        showExitDialog = true
    }

    LaunchedEffect(Unit) {
        viewModel.updateViewerUiState(
            viewerUiState.value.copy(
                size = recipeUiState.value!!.steps.size - 1
            )
        )
    }

    LaunchedEffect(viewerUiState.value.currentIndex) {
        isChangingStep = true
        delay(100)
        currentStep = recipeUiState.value!!.steps[viewerUiState.value.currentIndex]
        isChangingStep = false

        audioScript = currentStep.title //Build audio script
        viewModel.textToSpeech(context = context, text = audioScript)
    }

    LaunchedEffect(viewerUiState.value.isShowStatusBar) {
        if (!viewerUiState.value.isShowStatusBar) {
            insetsController.hide(WindowInsetsCompat.Type.statusBars())
        } else {
            insetsController.show(WindowInsetsCompat.Type.statusBars())
        }
    }

    DisposableEffect(Unit) {
        if (!viewerUiState.value.isShowStatusBar) {
            insetsController.hide(WindowInsetsCompat.Type.statusBars())
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //Keep screen on

        onDispose { //Disable hide status bars and keep screen on when the compose disposed
            insetsController.show(WindowInsetsCompat.Type.statusBars())
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            viewModel.pause() //stop vosk voice recognition
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        //Pop up for end recipe viewer
        if (showExitDialog) {
            ExitDialog(
                onExit = {
                    showExitDialog = false
                    onNavigateBack()
                },
                onCancel = { showExitDialog = !showExitDialog }
            )
        }
        OverlayControl(
            //Control on bottom of screen
            innerPadding = innerPadding,
            isChangingStep = isChangingStep,
            overlayHeight = { dp ->
                overlayHeight = dp
            },
            viewModel = viewModel,
            recipeUiState = recipeUiState,
            onShowExitDialog = { showExitDialog = true },
        )

        Column( //Viewer contents
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = overlayHeight)
                .animateContentSize()
        ) {
            if (currentStep.image != null) { //step image
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data("https://drive.google.com/uc?export=download&id=" + currentStep.image)
                        .build(),
                    contentDescription = null,
                    loading = {
                        Card(
                            shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
                            modifier = modifier
                                .fillMaxSize()
                                .padding(dimensionResource(R.dimen.padding_medium))
                        ) {
                            Box(
                                modifier = modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularWavyProgressIndicator()
                            }
                        }
                    },
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                )
            } else { //Show placeholder
                Card(
                    modifier = modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                    ) {
                        Icon(
                            Icons.Filled.Flatware,
                            null,
                            modifier = modifier
                                .align(Alignment.Center)
                                .size(64.dp)
                        )
                    }
                }
            }
            Column(
                modifier = modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
            ) {
                Row( //step title and play audio icon
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.fillMaxWidth()
                ) {
                    Text(
                        //step title
                        text = currentStep.title,
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 32.sp.nonScaledSp,
                        modifier = modifier
                            .weight(1f, fill = true)
                    )
                    IconButton(
                        onClick = {
                            viewModel.textToSpeech(context = context, text = audioScript)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                            contentDescription = stringResource(R.string.play_audio_description),
                            modifier = modifier.size(
                                dimensionResource(R.dimen.padding_large)
                            )
                        )
                    }
                }

                if (currentStep.ingredients != null) { //show ingredients used in this step if available
                    Text(
                        text = stringResource(R.string.you_need),
                        fontWeight = FontWeight(500),
                        fontSize = 24.sp.nonScaledSp,
                        style = MaterialTheme.typography.titleLarge
                    )
                    currentStep.ingredients!!.forEach { ingredient ->
                        Text(text = ingredient)
                    }
                }
                Text(
                    text = stringResource(R.string.description),
                    fontWeight = FontWeight(500),
                    fontSize = 24.sp.nonScaledSp,
                    style = MaterialTheme.typography.titleLarge
                )
                Text( //description
                    text = currentStep.description.orEmpty()
                )
            }
        }
    }
}

//State to show which menu screen is selected
sealed interface ToolMenuState {
    object None : ToolMenuState
    object CameraView : ToolMenuState
    object MicView : ToolMenuState
    object Menu : ToolMenuState
    object RecordPermission : ToolMenuState
    object CameraPermission : ToolMenuState
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun OverlayControl(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    viewModel: ViewerViewModel,
    recipeUiState: StateFlow<Recipe?>,
    isChangingStep: Boolean,
    overlayHeight: (Dp) -> Unit,
    onShowExitDialog: () -> Unit,
) {
    val context = LocalContext.current

    val viewerUiState = viewModel.viewerUiState.collectAsState()
    val recipe = viewModel.recipe.collectAsState()

    val micPermissionStatus = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val cameraPermissionStatus = rememberPermissionState(Manifest.permission.CAMERA)

    val micColor by animateColorAsState(
        targetValue = if (viewerUiState.value.isMicOn) MaterialTheme.colorScheme.onPrimary else LocalContentColor.current,
        animationSpec = tween(1000),
        label = "Mic background color animate"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isChangingStep) MaterialTheme.colorScheme.surface else Color.Transparent,
        animationSpec = tween(300)
    )

    var menuComposableSize by remember { mutableStateOf(Rect.Zero) }
    var toolMenuState: ToolMenuState by remember { mutableStateOf(ToolMenuState.None) }
    var toolMenuContent by remember { mutableStateOf<@Composable () -> Unit>({ Text("Initial Menu") }) } //set composes to show when menu container show up
    var titleResId by remember { mutableStateOf<Int?>(null) }

    val density = LocalDensity.current
    var controlHeight by remember { mutableStateOf(0.dp) }

    val animatedOverlayRadius by animateDpAsState(
        targetValue = if (viewerUiState.value.isShowMenu) dimensionResource(R.dimen.padding_medium) else dimensionResource(
            R.dimen.padding_extra_large
        ), animationSpec = tween(2000)
    )

    Box(
        modifier
            .fillMaxSize()
            .zIndex(2f)
            .background(backgroundColor)
            .padding(innerPadding)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset: Offset ->
                        if (viewerUiState.value.isShowMenu && !menuComposableSize.contains(offset)) {
                            viewModel.updateViewerUiState(viewerUiState.value.copy(isShowMenu = false))
                            toolMenuState = ToolMenuState.None
                        }
                    }
                )
            }
    ) {
        AnimatedVisibility( //expanded menu
            viewerUiState.value.isShowMenu,
            enter = slideInVertically {
                with(density) { 40.dp.roundToPx() }
            } + fadeIn(),
            exit = slideOutVertically {
                with(density) { 40.dp.roundToPx() }
            } + fadeOut()
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = (-controlHeight - 40.dp).roundToPx()
                        )
                    }
                    .onGloballyPositioned { coordinates ->
                        val position = coordinates.positionInWindow()
                        val size = coordinates.size
                        menuComposableSize = Rect(
                            left = position.x,
                            top = position.y,
                            right = position.x + size.width,
                            bottom = position.y + size.height
                        )
                    }
            ) {
                ToolMenu(
                    modifier = modifier
                        .align(Alignment.BottomCenter),
                    closeMenu = {
                        changeMenuState(
                            currentState = toolMenuState,
                            onShowMenuChange = { it ->
                                viewModel.updateViewerUiState(viewerUiState.value.copy(isShowMenu = it))
                            },
                            newState = { it ->
                                toolMenuState = it
                            },
                            newTitle = { it ->
                                titleResId = it
                            }
                        )
                    },
                    titleResId = titleResId
                ) {
                    toolMenuContent()
                }
            }
        }
        Card(
            shape = RoundedCornerShape(
                animatedOverlayRadius,
                animatedOverlayRadius,
                dimensionResource(R.dimen.padding_extra_large),
                dimensionResource(R.dimen.padding_extra_large)
            ),
            modifier = modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimensionResource(R.dimen.padding_extra_large))
                .fillMaxWidth(0.7f)
                .onGloballyPositioned { coordinates ->
                    val heightPx = coordinates.size.height
                    controlHeight = with(density) { heightPx.toDp() }
                    overlayHeight(controlHeight)
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.padding_small))
            ) {
                IconButton(
                    enabled = viewerUiState.value.currentIndex != 0,
                    onClick = {
                        if (viewerUiState.value.currentIndex > 0) {
                            viewModel.updateUiStateIndex(-1)
                        }
                    }
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                }
                IconButton(
                    enabled = !(recipe.value!!.steps[viewerUiState.value.currentIndex].askable.isNullOrBlank()),
                    onClick = {
                        if (!cameraPermissionStatus.status.isGranted) {
                            toolMenuContent = {
                                PermissionCard(
                                    icon = Icons.Default.CameraAlt,
                                    onGrantClick = {
                                        changeMenuState( // menu
                                            currentState = toolMenuState,
                                            clickedMenuState = ToolMenuState.None,
                                            onShowMenuChange = { it ->
                                                viewModel.updateViewerUiState(viewerUiState.value.copy(isShowMenu = false))
                                            },
                                            newState = { it ->
                                                toolMenuState = it
                                            },
                                            newTitle = { it ->
                                                titleResId = it
                                            }
                                        )
                                        cameraPermissionStatus.launchPermissionRequest()
                                    },
                                    rationale = stringResource(R.string.camera_permission_needed),
                                    shouldShowRationale = cameraPermissionStatus.status.shouldShowRationale
                                )
                            }
                            changeMenuState(
                                currentState = toolMenuState,
                                clickedMenuState = ToolMenuState.CameraPermission,
                                onShowMenuChange = { it ->
                                    viewModel.updateViewerUiState(viewerUiState.value.copy(isShowMenu = it))
                                },
                                newState = { it ->
                                    toolMenuState = it
                                },
                                newTitle = { it ->
                                    titleResId = it
                                }
                            )
                        } else {
                            changeMenuState(
                                currentState = toolMenuState,
                                clickedMenuState = ToolMenuState.CameraView,
                                onShowMenuChange = { it ->
                                    viewModel.updateViewerUiState(viewerUiState.value.copy(isShowMenu = it))
                                },
                                newState = { it ->
                                    toolMenuState = it
                                },
                                newTitle = { it ->
                                    titleResId = it
                                }
                            )
                            toolMenuContent = {
                                Text(
                                    recipeUiState.value!!.steps[viewerUiState.value.currentIndex].askable!!,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium,
                                    fontStyle = FontStyle.Italic,
                                    modifier = modifier
                                        .padding(vertical = dimensionResource(R.dimen.padding_medium))
                                )
                                CameraView(
                                    viewerViewModel =  viewModel,
                                    onCapture = { viewModel.generateAskable() }
                                )
                            }
                        }
                    }
                ) {
                    Icon(Icons.Outlined.CameraAlt, null)
                }
                IconButton(
                    onClick = {
                        if (!micPermissionStatus.status.isGranted) { //show mic permission request
                            toolMenuContent = {
                                PermissionCard(
                                    shouldShowRationale = micPermissionStatus.status.shouldShowRationale,
                                    rationale = stringResource(R.string.mic_permission_needed),
                                    icon = Icons.Default.MicOff,
                                    onGrantClick = {
                                        changeMenuState(
                                            currentState = toolMenuState,
                                            clickedMenuState = ToolMenuState.None,
                                            onShowMenuChange = { it ->
                                                viewModel.updateViewerUiState(viewerUiState.value.copy(isShowMenu = false))
                                            },
                                            newState = { it ->
                                                toolMenuState = it
                                            },
                                            newTitle = { it ->
                                                titleResId = it
                                            }
                                        )
                                        micPermissionStatus.launchPermissionRequest()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                            changeMenuState(
                                currentState = toolMenuState,
                                clickedMenuState = ToolMenuState.RecordPermission,
                                onShowMenuChange = { it ->
                                    viewModel.updateViewerUiState(viewerUiState.value.copy(isShowMenu = it))
                                },
                                newState = { it ->
                                    toolMenuState = it
                                },
                                newTitle = { it ->
                                    titleResId = it
                                }
                            )
                        } else { //Granted
                            changeMenuState(
                                currentState = toolMenuState,
                                clickedMenuState = ToolMenuState.MicView,
                                onShowMenuChange = { it ->
                                    viewModel.updateViewerUiState(viewerUiState.value.copy(isShowMenu = it))
                                },
                                newState = { it ->
                                    toolMenuState = it
                                },
                                newTitle = { it ->
                                    titleResId = it
                                }
                            )
                            toolMenuContent = {
                                Mic(uiState = viewerUiState.value)
                            }
                            if(viewerUiState.value.isMicOn) {
                                viewModel.pause()
                            } else {
                                viewModel.initVosk(context)
                            }
                        }
                    },
                    modifier = modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (viewerUiState.value.isMicOn) MaterialTheme.colorScheme.primary else Color.Transparent)
                ) {
                    Icon(
                        Icons.Outlined.Mic,
                        null,
                        tint = micColor
                    )
                }
                IconButton(
                    onClick = {
                        //Set contents on menu container
                        titleResId = R.string.menu
                        toolMenuContent = {
                            Menu(
                                viewModel = viewModel,
                                viewerUiState = viewerUiState,
                                onShowExitDialog = onShowExitDialog
                            )
                        }
                        changeMenuState(
                            currentState = toolMenuState,
                            clickedMenuState = ToolMenuState.Menu,
                            clickedStateTitleResId = R.string.menu,
                            onShowMenuChange = { it ->
                                viewModel.updateViewerUiState(viewerUiState.value.copy(isShowMenu = it))
                            },
                            newState = { it ->
                                toolMenuState = it
                            },
                            newTitle = { it ->
                                titleResId = it
                            }
                        )
                    }
                ) {
                    Icon(Icons.Outlined.Menu, null)
                }


                if (viewerUiState.value.currentIndex < viewerUiState.value.size) {
                    IconButton(
                        onClick = {
                            if (viewerUiState.value.currentIndex < viewerUiState.value.size) {
                                viewModel.updateUiStateIndex(1)
                            }
                        },
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowForward, null)
                    }
                } else {
                    IconButton(
                        onClick = onShowExitDialog,
                        modifier = modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            stringResource(R.string.finish),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

private fun changeMenuState(
    currentState: ToolMenuState?,
    clickedMenuState: ToolMenuState? = null,
    clickedStateTitleResId: Int? = null,
    onShowMenuChange: (Boolean) -> Unit,
    newState: (ToolMenuState) -> Unit,
    newTitle: (Int?) -> Unit,
) {
    if (clickedMenuState == null || currentState == clickedMenuState) {
        onShowMenuChange(false)
        newState(ToolMenuState.None)
        newTitle(null)
    } else {
        onShowMenuChange(true)
        newState(clickedMenuState)
        newTitle(clickedStateTitleResId)
    }

}


@Composable
private fun ToolMenu(
    titleResId: Int?,
    modifier: Modifier = Modifier,
    closeMenu: () -> Unit,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    var toolMenuHeight by remember { mutableStateOf(Dp.Unspecified)}

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned{ coordinates ->
                toolMenuHeight = with(density) { coordinates.size.height.toDp() * 0.6f }
            }
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.padding_small)),
            modifier = modifier
                .heightIn(min = 0.dp, max = (toolMenuHeight))
                .fillMaxWidth(0.7f)
        ) {
            Box(
                modifier = modifier
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .zIndex(2f)
                ) {
                    if (titleResId != null) {
                        Text(
                            text = stringResource(id = titleResId),
                            fontWeight = FontWeight.Bold,
                            modifier = modifier
                                .padding(dimensionResource(R.dimen.padding_medium))
                        )
                    } else {
                        Text("")
                    }
                    IconButton(
                        onClick = closeMenu,
                    ) {
                        Icon(
                            Icons.Default.Close,
                            stringResource(R.string.close)
                        )
                    }
                }
                Column(
                    modifier = modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun Mic(
    uiState: ViewerUiState,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(uiState.transcript)
    }
}

@Composable
private fun Menu(
    viewModel: ViewerViewModel,
    viewerUiState: State<ViewerUiState>,
    onShowExitDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val calendar = Calendar.getInstance()
            val format = SimpleDateFormat("HH:mm:ss a", Locale.getDefault())
            currentTime = format.format(calendar.time)
            delay(1000)
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.padding_large))
    ) {
        Text(
            text = currentTime,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 32.sp.nonScaledSp,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.show_status_bars))
            Switch(
                checked = viewerUiState.value.isShowStatusBar,
                onCheckedChange = {
                    viewModel.updateViewerUiState(
                        viewerUiState.value.copy(
                            isShowStatusBar = it
                        )
                    )
                }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .fillMaxWidth()
        ) {

            Text(
                text = stringResource(R.string.mute_audio)
            )
            Switch(
                checked = viewerUiState.value.isReadAround,
                onCheckedChange = {
                    viewModel.updateViewerUiState(
                        viewerUiState.value.copy(
                            isReadAround = !viewerUiState.value.isReadAround
                        )
                    )
                }
            )
        }
        TextButton(
            onClick = onShowExitDialog,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.quit)
            )
        }
    }
}

@Composable
private fun PermissionCard(
    shouldShowRationale: Boolean,
    rationale: String,
    icon: ImageVector,
    onGrantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = dimensionResource(R.dimen.padding_large))
    ) {
        Icon(icon, null)
        Spacer(modifier = modifier.padding(dimensionResource(R.dimen.padding_small)))
        Text(text = rationale);
        Spacer(modifier = modifier.padding(dimensionResource(R.dimen.padding_small)))
        if (shouldShowRationale)
            OutlinedButton(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        addCategory(Intent.CATEGORY_DEFAULT)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    }
                    context.startActivity(intent)
                }
            ) {
                Text(stringResource(R.string.go_to_setting))
            }
        else
            Button(
                onClick = onGrantClick
            ) {
                Text(stringResource(R.string.allow_access))
            }
    }
}

@Composable
private fun ExitDialog(
    onExit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = { onCancel() }) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
                modifier = modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .fillMaxWidth()
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ExitToApp,
                    null,
                    modifier = modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .size(48.dp)
                )
                Text(
                    text = stringResource(R.string.exit_recipe),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = modifier
                        .fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = modifier
                        .fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            onCancel()
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            onExit()
                        }
                    ) {
                        Text(stringResource(R.string.exit))
                    }
                }
            }
        }
    }
}

data class RecipeViewer(
    val recipe: Recipe,
    val currentIndex: Int,
    val isMicOn: Boolean,
    val isReadAloud: Boolean
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecipeViewerPreview() {
    NavCookTheme {
        RecipeViewer(
            innerPadding = PaddingValues(0.dp),
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExitDialogPreview() {
    NavCookTheme {
        ExitDialog({}, {})
    }
}