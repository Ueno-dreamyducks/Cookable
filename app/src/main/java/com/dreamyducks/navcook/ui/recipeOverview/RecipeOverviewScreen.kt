@file:OptIn(ExperimentalMaterial3Api::class)

package com.dreamyducks.navcook.ui.recipeOverview

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dreamyducks.navcook.R
import com.dreamyducks.navcook.format.nonScaledSp
import com.dreamyducks.navcook.network.Recipe

@Composable
fun RecipeOverviewScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    onStartClick: () -> Unit,
    overviewViewModel: OverviewViewModel = viewModel()
) {
    val density = LocalDensity.current
    val recipe = overviewViewModel.recipe.collectAsState()
    //val statusBarHeight = with(density) { WindowInsets.statusBars.getTop(density).toDp() }

    var imagePixelHeight by remember { mutableIntStateOf(0) }
    var isBigThumbnail by remember { mutableStateOf(true) }

    var controlHeight by remember { mutableStateOf(0.dp) }

    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState.value) {
        Log.d("MainActivity", "Scroll amount ${scrollState.value}")
    }

    isBigThumbnail = when (scrollState.value) {
        in 0..imagePixelHeight -> {
            true
        }

        else -> {
            false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        OverlayControl(
            controlHeight = { px ->
                controlHeight = with(density) { px.toDp() + 8.dp }
            },
            onStartClick = onStartClick,
            isBigImage = isBigThumbnail,
            innerPadding = innerPadding,
            modifier = modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .zIndex(2f)
        )
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            BigPicture(
                recipe = recipe.value,
                imagePixelHeight = { height -> imagePixelHeight = height }
            )
            Column(
                modifier = modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .padding(bottom = controlHeight)
            ) {
                Description()
                Ingredients(
                    recipe = recipe.value
                )
            }
        }
    }
}

@Composable
private fun OverlayControl(
    controlHeight: (Int) -> Unit,
    isBigImage: Boolean,
    onStartClick: () -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .background(if (isBigImage) Color.Transparent else MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = dimensionResource(R.dimen.padding_small)
                    )
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = dimensionResource(R.dimen.padding_small))
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    controlHeight(coordinates.size.height)
                },
        ) {
            Button(
                onClick = onStartClick,
                shape = RoundedCornerShape(dimensionResource(R.dimen.padding_small)),
                modifier = modifier
                    .weight(3f)
            ) {
                Text("Start", modifier = modifier.padding(dimensionResource(R.dimen.padding_small)))
            }
            Button(
                onClick = {},
                shape = RoundedCornerShape(dimensionResource(R.dimen.padding_small)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                modifier = modifier
                    .weight(1f)
            ) {
                Icon(
                    Icons.Default.Menu,
                    null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = modifier.padding(dimensionResource(R.dimen.padding_small))
                )
            }
        }
    }
}

@Composable
private fun BigPicture(
    modifier: Modifier = Modifier,
    recipe: Recipe?,
    imagePixelHeight: (Int) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(recipe!!.thumbnail)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .drawWithCache {
                    val gradient = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = size.height / 10,
                        endY = size.height
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(gradient, blendMode = BlendMode.Multiply)
                    }
                }
                .onGloballyPositioned { coordinates ->
                    //imagePixelHeight = coordinates.size.height
                    imagePixelHeight(coordinates.size.height)
                }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 36.sp.nonScaledSp,
                color = Color(0xfff6f6f6)
            )
        }
    }
}

@Composable
private fun Description(
    modifier: Modifier = Modifier
) {
    var descriptionLineCount by remember { mutableIntStateOf(0) }

    var isDescriptionExpand by remember { mutableStateOf(false) }

    Text(
        text = "You're going to make a choice today that will have a direct impact on where you are five years from now. The truth is, you'll make choice like that every day of your life. The problem is that on most days, you won't know the choice you make will have such a huge impact on your life in the future. So if you want to end up in a certain place in the future, you need to be careful of the choices you make today.\n" +
                "You're going to make a choice today that will have a direct impact on where you are five years from now. The truth is, you'll make choice like that every day of your life. The problem is that on most days, you won't know the choice you make will have such a huge impact on your life in the future. So if you want to end up in a certain place in the future, you need to be careful of the choices you make today.\n" +
                "Love isn't always a ray of sunshine. That's what the older girls kept telling her when she said she had found the perfect man. She had thought this was simply bitter talk on their part since they had been unable to find true love like hers. But now she had to face the fact that they may have been right. Love may not always be a ray of sunshine. That is unless they were referring to how the sun can burn.\n" +
                "The thing that's great about this job is the time sourcing the items involves no traveling. I just look online to buy it. It's really as simple as that. While everyone else is searching for what they can sell, I sit in front of my computer and buy better stuff for less money and spend a fraction of the time doing it.",
        onTextLayout = { textLayoutResult ->
            descriptionLineCount = textLayoutResult.lineCount
        },
        overflow = TextOverflow.Ellipsis,
        maxLines = if (isDescriptionExpand) Int.MAX_VALUE else 5,
        modifier = modifier
            .animateContentSize()
    )
    if (descriptionLineCount >= 5) {
        if (isDescriptionExpand) {
            Text(
                text = stringResource(R.string.show_less),
                color = MaterialTheme.colorScheme.tertiary,
                modifier = modifier
                    .clickable(
                        onClick = {
                            isDescriptionExpand = false
                        }
                    )
            )
        } else {
            Text(
                text = stringResource(R.string.read_more),
                color = MaterialTheme.colorScheme.tertiary,
                modifier = modifier
                    .clickable(
                        onClick = {
                            isDescriptionExpand = true
                        }
                    )
            )
        }
    }
}

@Composable
private fun Ingredients(
    recipe: Recipe?,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.ingredients),
        fontSize = 28.sp.nonScaledSp,

        style = MaterialTheme.typography.titleLarge
    )

    Column {
        recipe!!.ingredients?.forEach { ingredient ->
            IngredientWithCheckbox(ingredient)
        }
    }
}

@Composable
private fun IngredientWithCheckbox(
    ingredient: String,
    modifier: Modifier = Modifier
) {
    var checked by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable {
                checked = !checked
            }
            .fillMaxWidth()
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it },
            modifier = modifier
        )
        Text(
            text = ingredient,
            style = LocalTextStyle.current.copy(textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecipeOverviewScreenPreview() {
    MaterialTheme {
        RecipeOverviewScreen(
            innerPadding = PaddingValues(0.dp),
            onStartClick = {}
        )
    }
}

