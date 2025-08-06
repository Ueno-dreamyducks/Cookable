package com.dreamyducks.navcook.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Flatware
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Flatware
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.dreamyducks.navcook.R
import com.dreamyducks.navcook.data.Recipe
import com.dreamyducks.navcook.data.Step
import com.dreamyducks.navcook.format.nonScaledSp
import com.dreamyducks.navcook.ui.theme.NavCookTheme
import kotlinx.coroutines.delay


@Composable
fun RecipeViewer(
    modifier: Modifier = Modifier,
    viewModel: NavCookViewModel = NavCookViewModel(),
    innerPadding: PaddingValues,
    onNavigateBack: () -> Unit
) {
    val recipeUiState = Recipe(
        id = 0,
        title = "Pasta",
        thumbNailImage = R.drawable.pasta,
        ingredients = listOf(
            "Pasta",
            "Tomato Paste",
            "Meat Ball"
        ),
        steps = listOf(
            Step(
                title = "Cut Tomato",
                image = R.drawable.pasta,
                ingredients = listOf(
                    "Tomato",
                    "Salt"
                ),
                description = "Cut tomatoes and pour salt"
            ),
            Step(
                title = "No image step Extra long title ",
                image = null,
                ingredients = listOf(
                    "Bacon",
                    "Tomato Paste"
                ),
                description = "Cut tomatoes and pour salt"
            )
        )

    )//viewModel.recipeUiState.collectAsState()

    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var currentStep by remember { mutableStateOf(recipeUiState.steps!![currentStepIndex]) }
    var audioScript by remember { mutableStateOf("") }
    var isChangingStep by remember { mutableStateOf(false) }
    var isMute by remember { mutableStateOf(false) }

    var overlayHeight by remember { mutableStateOf(0.dp) }

    BackHandler {
        showExitDialog = true
    }

    LaunchedEffect(currentStepIndex) {
        isChangingStep = true
        delay(100)
        currentStep = recipeUiState.steps!![currentStepIndex]
        isChangingStep = false

        audioScript = currentStep.title //Build audio script
        viewModel.textToSpeech(context = context, text = audioScript, volume = if(isMute) 0.0f else 0.8f)
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
        OverlayControl(  //Control on bottom of screen
            innerPadding = innerPadding,
            stepSize = recipeUiState.steps!!.size - 1,
            currentIndex = currentStepIndex,
            isChangingStep = isChangingStep,
            overlayHeight = { dp ->
                overlayHeight = dp
            },
            onStepBack = {
                if (currentStepIndex > 0) {
                    currentStepIndex -= 1
                }
            },
            onStepForward = {
                if (currentStepIndex < recipeUiState.steps.size - 1) {
                    currentStepIndex += 1
                }
                Log.d("MainActivity", "current index num. : $currentStepIndex")
            },
            micState = {
                Log.d("MainActivity", "is microphone active: $it")
            },
            onMuteCheckChange = { isMute = it },
            onNavigateBack = onNavigateBack
        )

        Column( //Viewer contents
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = overlayHeight)
                .animateContentSize()
        ) {
            if (currentStep.image != null) { //step image
                Image(
                    painter = painterResource(currentStep.image!!),
                    contentDescription = null,
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
                            .weight(1f, fill = false)
                    )
                    IconButton(
                        onClick = {
                            viewModel.textToSpeech(context = context, text = audioScript, volume = if(isMute) 0.0f else 0.8f)
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
                    text = currentStep.description
                )
            }
        }
    }
}

@Composable
private fun OverlayControl(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    stepSize: Int,
    currentIndex: Int,
    isChangingStep: Boolean,
    overlayHeight: (Dp) -> Unit,
    onStepBack: () -> Unit,
    onStepForward: () -> Unit,
    micState: (Boolean) -> Unit,
    onMuteCheckChange: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
) {
    var isMicActive by remember { mutableStateOf(false) }
    var isMute by remember { mutableStateOf(false) }

    val micColor by animateColorAsState(
        targetValue = if (isMicActive) MaterialTheme.colorScheme.onPrimary else LocalContentColor.current,
        animationSpec = tween(1000),
        label = "Mic background color animate"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isChangingStep) MaterialTheme.colorScheme.surface else Color.Transparent,
        animationSpec = tween(300)
    )

    var isShowMenu by remember { mutableStateOf(false) }
    var toolMenuContent by remember { mutableStateOf<@Composable () -> Unit>({ Text("Initial Menu") }) } //set composes to show when menu container show up

    val density = LocalDensity.current
    var controlHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(isMute) {
        onMuteCheckChange(isMute)
    }

    Box(
        modifier
            .fillMaxSize()
            .zIndex(2f)
            .background(backgroundColor)
            .padding(innerPadding)
    ) {
        AnimatedVisibility( //expanded menu
            isShowMenu,
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
            ) {
                ToolMenu(
                    modifier = modifier
                        .align(Alignment.BottomCenter)

                ) {
                    toolMenuContent()
                }
            }
        }
        Card(
            shape = RoundedCornerShape(dimensionResource(R.dimen.padding_extra_large)),
            modifier = modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.padding_extra_large))
                .padding(horizontal = 50.dp)
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
                    enabled = currentIndex != 0,
                    onClick = onStepBack
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                }
                IconButton(
                    onClick = {}
                ) {
                    Icon(Icons.Outlined.CameraAlt, null)
                }
                IconButton(
                    onClick = {
                        isMicActive = !isMicActive
                        micState(isMicActive)
                    },
                    modifier = modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (isMicActive) MaterialTheme.colorScheme.primary else Color.Transparent)
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
                        toolMenuContent = {
                            Column(
                                modifier = modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(R.string.menu),
                                    fontWeight = FontWeight.Bold
                                )
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
                                        checked = isMute,
                                        onCheckedChange = {
                                            Log.d("MainActivity", "new value: $it")
                                            isMute = it
                                        }
                                    )
                                }
                                TextButton(
                                    onClick = onNavigateBack,
                                    modifier = modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(R.string.quit)
                                    )
                                }
                            }
                        }
                        isShowMenu = !isShowMenu

                    }
                ) {
                    Icon(Icons.Outlined.Menu, null)
                }


                IconButton(
                    onClick = onStepForward,
                    enabled = currentIndex < stepSize
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowForward, null)
                }
            }
        }

    }
}

@Composable
private fun ToolMenu(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.padding_small)),
        modifier = modifier
            .fillMaxWidth(0.7f)
    ) {
        Column(
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            content()
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
                    fontWeight = FontWeight.Medium,
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