package com.dreamyducks.navcook.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.dreamyducks.navcook.R
import com.dreamyducks.navcook.data.Recipe
import com.dreamyducks.navcook.data.Step
import com.dreamyducks.navcook.ui.theme.NavCookTheme

@Composable
fun RecipeViewer(
    modifier: Modifier = Modifier,
    viewModel: NavCookViewModel = NavCookViewModel(),
    innerPadding: PaddingValues
) {
    val recipeUiState = Recipe(
        id = 0,
        title = "Pasta",
        thumbNailImage = R.drawable.pasta,
        ingredients = listOf<String>(
            "Pasta",
            "Tomato Paste",
            "Meat Ball"
        ),
        steps = listOf(
            Step(
                title = "Cut Tomato",
                image = R.drawable.pasta,
                ingredients = listOf<String>(
                    "Tomato",
                    "Salt"
                ),
                description = "Cut tomatoes and pour salt"
            )
        )

    )//viewModel.recipeUiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

    }
}

@Composable
private fun OverlayControl(
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxSize()
            .zIndex(2f)
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun RecipeViewerPreview() {
    NavCookTheme {
        RecipeViewer(
            innerPadding = PaddingValues(0.dp)
        )
    }
}