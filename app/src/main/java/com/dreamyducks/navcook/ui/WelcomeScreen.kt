package com.dreamyducks.navcook.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dreamyducks.navcook.R
import com.dreamyducks.navcook.format.nonScaledSp
import com.dreamyducks.navcook.ui.theme.NavCookTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun WelcomeScreen(
    innerPadding: PaddingValues,
    onNavigateToHomepage: () -> Unit, modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var cardWidth by remember { mutableStateOf(0.dp) }
    var targetOffsetX by remember { mutableStateOf(0.dp) }
    val animatedOffsetX by animateDpAsState(
        targetValue = targetOffsetX,
        animationSpec = tween(durationMillis = 1000),
    )
    var isVisible2 by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()


    Box(
        modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Column(
            modifier = modifier.padding(horizontal = dimensionResource(R.dimen.padding_medium))
        ) {
            AnimatedVisibility(
                visible = isVisible2, exit = slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth - 100 },
                    animationSpec = tween(durationMillis = 1000)
                )
            ) {
                Text(
                    text = "Welcome",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 60.sp.nonScaledSp,
                    modifier = modifier.padding(
                            vertical = dimensionResource(R.dimen.padding_extra_large)
                        )
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .offset { IntOffset(x = animatedOffsetX.roundToPx(), y = 0 ) }
                    .onGloballyPositioned { coordinates ->
                        cardWidth = with(density) { (coordinates.size.width).toDp() }
                    }) {

            }

            Spacer(modifier.weight(1f))

            AnimatedVisibility(
                visible = isVisible2, exit = slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth - 100 },
                    animationSpec = tween(durationMillis = 1000)
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = modifier
                        .padding(vertical = dimensionResource(R.dimen.padding_medium))
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                targetOffsetX = -cardWidth - 100.dp
                                delay(500L) //Wait for the first animation to end

                                isVisible2 = false

                                delay(700L) //Wait for the second animation to end

                                //navigate to homepage
                                onNavigateToHomepage()
                            }
                        }, modifier
                            .fillMaxHeight(0.6f)
                            .fillMaxWidth(0.4f)
                    ) {
                        Text(
                            text = "START", fontSize = 24.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun WelcomeScreenPreview() {
    NavCookTheme {
        WelcomeScreen(
            PaddingValues(0.dp),
            {}
        )
    }
}