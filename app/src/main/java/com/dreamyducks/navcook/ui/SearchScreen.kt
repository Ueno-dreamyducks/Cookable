package com.dreamyducks.navcook.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamyducks.navcook.R

@Composable
fun SearchScreen(
    viewModel: NavCookViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            SearchSection(
                onSearch = {

                }
            )
            Row(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                GenreItem(
                    onGenreClick = {},
                    genreHeader = stringResource(R.string.bbq),
                    modifier = modifier
                        .weight(1f)
                )
                GenreItem(
                    onGenreClick = {},
                    genreHeader = stringResource(R.string.vegetarian),
                    genreImage = R.drawable.benjamin_jopen_rz3yhkssube_unsplash, //<a href="https://unsplash.com/ja/%E5%86%99%E7%9C%9F/%E9%BB%92%E3%81%84%E9%99%B6%E5%99%A8%E3%81%AE%E7%9A%BF%E3%81%AE%E4%B8%8A%E3%81%AE%E8%AA%BF%E7%90%86%E3%81%95%E3%82%8C%E3%81%9F%E9%A3%9F%E3%81%B9%E7%89%A9-rZ3YHKSSUbE?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash">Unsplash</a>の<a href="https://unsplash.com/ja/@benjopen?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash">Benjamin Jopen</a>が撮影した写真
                    modifier = modifier
                        .weight(1f)
                )
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                GenreItem(
                    onGenreClick = {},
                    genreHeader = stringResource(R.string.snack),
                    modifier = modifier
                        .weight(1f)
                )
                GenreItem(
                    onGenreClick = {},
                    genreHeader = stringResource(R.string.sweet),
                    modifier = modifier
                        .weight(1f)
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    MaterialTheme {
        SearchScreen(
        )
    }
}

@Composable
private fun SearchSection(
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .height(if (isFocused) 400.dp else Dp.Unspecified),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(if (isFocused) 20.dp else 50.dp)
    ) {
        TextField(
            value = value,
            onValueChange = { newStr ->
                value = newStr
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null
                )
            },
            placeholder = {
                Text(stringResource(R.string.search_recipe))
            },
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                    focusManager.clearFocus()
                }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            maxLines = 1,
            modifier = modifier
                .focusRequester(focusRequester)
                .onFocusChanged { event ->
                    isFocused = event.isFocused
                }
                .fillMaxWidth()
        )
    }
}

@Composable
private fun GenreItem(
    onGenreClick: () -> Unit,
    genreHeader: String,
    @DrawableRes genreImage: Int = R.drawable.ic_launcher_foreground,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
    ) {
        Card(
            onClick = {
                onGenreClick()
            },
            modifier = Modifier
                .height(200.dp)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Image(
                    painter = painterResource(genreImage),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithCache {
                        val gradient = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                            startY = size.height/3,
                            endY = size.height
                        )
                        onDrawWithContent {
                            drawContent()
                            drawRect(gradient,blendMode = BlendMode.Multiply)
                        }
                    }
                )
                Text(
                    text = genreHeader,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }
        }
    }
}

@Preview
@Composable
fun GenreItemPreview() {
    MaterialTheme {
        GenreItem(
            onGenreClick = {},
            genreHeader = "Vegetarian"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = {
                    navigateUp()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null
                )
            }
        }
    )
}