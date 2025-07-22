package com.dreamyducks.navcook.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
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
        OverlayControl(
            innerPadding = innerPadding,
            micState = {
                Log.d("MainActivity", "is microphone active: $it")
            }
        )
    }
}

@Composable
private fun OverlayControl(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    micState: (Boolean) -> Unit,
) {
    var isMicActive by remember { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxSize()
            .zIndex(2f)
            .padding(innerPadding)
    ) {
        Card(
            shape = RoundedCornerShape(dimensionResource(R.dimen.padding_extra_large)),
            modifier = modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.padding_extra_large))
                .padding(horizontal = 50.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.padding_small))
            ) {
                IconButton(
                    onClick = {}
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
                        .background(if(isMicActive) MaterialTheme.colorScheme.primary else Color.Transparent)
                ) {
                    Icon(Icons.Outlined.Mic, null, tint = if(isMicActive) MaterialTheme.colorScheme.onPrimary else LocalContentColor.current)
                }
                IconButton(
                    onClick = {}
                ) {
                    Icon(Icons.Outlined.Menu, null)
                }
                IconButton(
                    onClick = {}
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowForward, null)
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
            innerPadding = PaddingValues(0.dp)
        )
    }
}