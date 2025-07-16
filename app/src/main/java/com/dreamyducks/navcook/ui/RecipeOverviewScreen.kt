@file:OptIn(ExperimentalMaterial3Api::class)

package com.dreamyducks.navcook.ui

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dreamyducks.navcook.R
import com.dreamyducks.navcook.format.nonScaledSp

@Composable
fun RecipeOverviewScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    viewModel: NavCookViewModel = NavCookViewModel()
) {
    val recipe = viewModel.recipeUiState.collectAsState()

    val density = LocalDensity.current
    val statusBarHeight = with(density) { WindowInsets.statusBars.getTop(density).toDp() }

    var descriptionLineCount by remember { mutableIntStateOf(0) }
    var imagePixelHeight by remember { mutableIntStateOf(0) }
    val isBigThumbnail = remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState.value) {
        Log.d("MainActivity", "Scroll amount ${scrollState.value}")
    }

    isBigThumbnail.value = when (scrollState.value) {
        in 0..imagePixelHeight -> {
            true
        }

        else -> {
            false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            Image(
                painter = painterResource(recipe.value!!.thumbNailImage),
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
                        imagePixelHeight = coordinates.size.height
                    },
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
                    text = recipe.value!!.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 36.sp.nonScaledSp,
                    color = Color(0xfff6f6f6)
                )
            }
        }
        Column(
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                text = "You're going to make a choice today that will have a direct impact on where you are five years from now. The truth is, you'll make choice like that every day of your life. The problem is that on most days, you won't know the choice you make will have such a huge impact on your life in the future. So if you want to end up in a certain place in the future, you need to be careful of the choices you make today.\n" +
                        "You're going to make a choice today that will have a direct impact on where you are five years from now. The truth is, you'll make choice like that every day of your life. The problem is that on most days, you won't know the choice you make will have such a huge impact on your life in the future. So if you want to end up in a certain place in the future, you need to be careful of the choices you make today.\n" +
                        "Love isn't always a ray of sunshine. That's what the older girls kept telling her when she said she had found the perfect man. She had thought this was simply bitter talk on their part since they had been unable to find true love like hers. But now she had to face the fact that they may have been right. Love may not always be a ray of sunshine. That is unless they were referring to how the sun can burn.\n" +
                        "The thing that's great about this job is the time sourcing the items involves no traveling. I just look online to buy it. It's really as simple as that. While everyone else is searching for what they can sell, I sit in front of my computer and buy better stuff for less money and spend a fraction of the time doing it.",
                onTextLayout = { textLayoutResult ->
                    descriptionLineCount = textLayoutResult.lineCount
                },
                overflow = TextOverflow.Ellipsis,
                maxLines = 5
            )
            if (descriptionLineCount >= 5) {
                Text(
                    text = stringResource(R.string.read_more),
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = modifier
                        .clickable(
                            onClick = {}
                        )
                )
            }

            Text(
                text = stringResource(R.string.ingredients),
                style = MaterialTheme.typography.titleLarge
            )

            Column {
                recipe.value!!.ingredients?.forEach { ingredient ->
                    Text(
                        text = ingredient
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecipeOverviewScreenPreview() {
    MaterialTheme {
        RecipeOverviewScreen(
            innerPadding = PaddingValues(0.dp)
        )
    }
}

