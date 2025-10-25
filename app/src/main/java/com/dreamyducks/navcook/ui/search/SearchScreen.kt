package com.dreamyducks.navcook.ui.search

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.outlined.Dangerous
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamyducks.navcook.R
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
    onNavigateToSearchResult: () -> Unit,
) {
    val fieldFocus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val searchUiState = searchViewModel.searchUiState

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
            .padding(innerPadding)
    ) {
        DisposableEffect(Unit) {
            onDispose {
                searchViewModel.resetSearchUiState()
            }
        }
        when (searchUiState) {
            is SearchUiState.Loading -> Box(modifier = modifier.padding(dimensionResource(R.dimen.padding_medium))) { Loading() }
            is SearchUiState.Success -> {
                onNavigateToSearchResult()
            }

            is SearchUiState.NoInternet -> {
                NoInternet(
                    onSearch = {
                        coroutineScope.launch {
                            searchViewModel.onSearch()
                        }
                    }
                )
            }

            is SearchUiState.Error -> {
                Error(
                    onSearch = {
                        coroutineScope.launch {
                            searchViewModel.onSearch()
                        }
                    }
                )
            }

            else -> Column(
                modifier = modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
            ) {
                SearchSection(
                    searchViewModel = searchViewModel,
                    onSearch = {
                        coroutineScope.launch {
                            searchViewModel.onSearch()
                        }
                    },
                    fieldFocus = fieldFocus
                )
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                ) {
                    GenreItem(
                        onGenreClick = {},
                        genreHeader = stringResource(R.string.main_dish),
                        genreImage = R.drawable.pasta, //<a href="https://unsplash.com/ja/%E5%86%99%E7%9C%9F/%E7%99%BD%E3%81%84%E9%99%B6%E5%99%A8%E3%81%AE%E5%99%A8%E3%81%AB%E9%BA%BA%E3%82%92%E3%81%AE%E3%81%9B%E3%81%9F%E3%83%A1%E3%82%BF%E3%83%9C%E3%83%BC%E3%83%AB-AUAuEgUxg5Q?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash">Unsplash</a>の<a href="https://unsplash.com/ja/@ninjason?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash">Jason Leung</a>が撮影した写真
                        modifier = modifier
                            .weight(1f)
                    )
                    GenreItem(
                        onGenreClick = {},
                        genreHeader = stringResource(R.string.vegetarian),
                        genreImage = R.drawable.vegetarian, //<a href="https://unsplash.com/ja/%E5%86%99%E7%9C%9F/%E3%82%B9%E3%83%A9%E3%82%A4%E3%82%B9%E3%81%97%E3%81%9F%E3%82%AA%E3%83%AC%E3%83%B3%E3%82%B8%E3%83%95%E3%83%AB%E3%83%BC%E3%83%84%E3%81%A8%E3%82%B0%E3%83%AA%E3%83%BC%E3%83%B3%E3%83%96%E3%83%AD%E3%83%83%E3%82%B3%E3%83%AA%E3%83%BC-rWcza92CapI?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash">Unsplash</a>の<a href="https://unsplash.com/ja/@nate_dumlao?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash">Nathan Dumlao</a>が撮影した写真
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
                        genreHeader = stringResource(R.string.soup),
                        genreImage = R.drawable.soup, //<a href="https://unsplash.com/ja/%E5%86%99%E7%9C%9F/%E3%81%8A%E7%9A%BF%E3%81%AB%E7%9B%9B%E3%82%89%E3%82%8C%E3%81%9F%E6%BF%83%E5%8E%9A%E3%81%AA%E3%82%B9%E3%83%BC%E3%83%97%E3%81%AE%E4%B8%BC-k5VGs9qQubc?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash">Unsplash</a>の<a href="https://unsplash.com/ja/@foodwithaview?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash">Jenn Kosar</a>が撮影した写真
                        modifier = modifier
                            .weight(1f)
                    )
                    GenreItem(
                        onGenreClick = {},
                        genreHeader = stringResource(R.string.sweet),
                        genreImage = R.drawable.cupcake, //<a href="https://unsplash.com/ja/%E5%86%99%E7%9C%9F/%E7%99%BD%E3%81%A8%E8%B5%A4%E3%81%AE%E3%82%AB%E3%83%83%E3%83%97%E3%82%B1%E3%83%BC%E3%82%AD%E3%81%AE%E4%B8%8A%E3%81%AB%E7%99%BD%E3%81%84%E3%82%A2%E3%82%A4%E3%82%B7%E3%83%B3%E3%82%B0-MJPr6nOdppw?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash">Unsplash</a>の<a href="https://unsplash.com/ja/@luisanazl?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash">luisana zerpa</a>が撮影した写真
                        modifier = modifier
                            .weight(1f) //a
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    MaterialTheme {
        SearchScreen(
            innerPadding = PaddingValues(0.dp),
            onNavigateToSearchResult = {},
        )
    }
}

@Composable
private fun SearchSection(
    searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
    onSearch: () -> Unit,
    fieldFocus: FocusRequester,
    modifier: Modifier = Modifier,
) {
    val searchQuery = searchViewModel.searchQuery.collectAsState()

    val history = searchViewModel.queriesState.collectAsState()
    val uniqueQueries = history.value.distinctBy { it.query }

    var isFocused by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        fieldFocus.requestFocus()
    }

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
            value = searchQuery.value,
            onValueChange = searchViewModel::updateSearchQuery,
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
                }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            maxLines = 1,
            modifier = modifier
                .focusRequester(fieldFocus)
                .onFocusChanged { event ->
                    isFocused = event.isFocused
                }
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        if (isFocused) { //Show recent searches
            if (history.value.isNotEmpty()) { //check if recent search present
                for(query in uniqueQueries) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    searchViewModel.updateSearchQuery(query.query)
                                    onSearch()
                                }
                            )
                            .padding(
                                horizontal = dimensionResource(R.dimen.padding_medium),
                                vertical = dimensionResource(R.dimen.padding_small)
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.SavedSearch, null)
                        Text(
                            text = query.query
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_large))
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = modifier
                            .size(40.dp)
                    )
                    Text(
                        text = stringResource(R.string.no_recent_search),
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchSectionPreview() {
    MaterialTheme {
        SearchSection(
            fieldFocus = FocusRequester(),
            onSearch = {}
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
                                startY = size.height / 3,
                                endY = size.height
                            )
                            onDrawWithContent {
                                drawContent()
                                drawRect(gradient, blendMode = BlendMode.Multiply)
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Loading(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            ContainedLoadingIndicator(
                modifier = modifier
                    .size(120.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun NoInternet(
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.padding_small))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.padding_medium),
                Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Icon(
                Icons.Default.SignalWifiOff,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = modifier
                    .size(60.dp)
            )
            Text(
                text = stringResource(R.string.no_internet),
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            )
            OutlinedButton(
                onClick = onSearch
            ) {
                Text(
                    text = stringResource(R.string.try_again),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoInternetPreview() {
    MaterialTheme {
        NoInternet({})
    }
}

@Composable
private fun Error(
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.padding_small))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.padding_medium),
                Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Icon(
                Icons.Outlined.Dangerous,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = modifier
                    .size(60.dp)
            )
            Text(
                text = stringResource(R.string.an_error_occurred),
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            )
            OutlinedButton(
                onClick = onSearch
            ) {
                Text(
                    text = stringResource(R.string.try_again),
                )
            }
        }
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