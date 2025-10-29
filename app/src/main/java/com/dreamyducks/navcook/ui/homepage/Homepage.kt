package com.dreamyducks.navcook.ui.homepage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.dreamyducks.navcook.R
import com.dreamyducks.navcook.data.database.recentRecipes.RecentRecipe
import com.dreamyducks.navcook.data.database.searchQueries.Query
import com.dreamyducks.navcook.data.navigationItems
import com.dreamyducks.navcook.format.nonScaledSp
import com.dreamyducks.navcook.ui.theme.NavCookTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Homepage(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    homepageViewModel: HomepageViewModel = viewModel(factory = HomepageViewModel.Factory),
    onGoSearch: () -> Unit,
    navigateToOverview: () -> Unit,
    navigateToSearch: () -> Unit,
) {
    val context = LocalContext.current
    val homepageUiState by homepageViewModel.homepageUiState.collectAsState()
    val coroutine = rememberCoroutineScope()
    var isStartClicked = remember { mutableStateOf(false) }
    val queries by homepageViewModel.queriesState.collectAsState()
    val uniqueQueries = queries.distinctBy { it.query }
    val recentRecipes by homepageViewModel.recentRecipesState.collectAsState()
    val uniqueRecent = recentRecipes.distinctBy { it.id }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (isStartClicked.value) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.75f))
                    .fillMaxSize()
                    .zIndex(2f)
                    .pointerInput(Unit) {

                    }
            ) {
                ContainedLoadingIndicator(
                    modifier = modifier
                        .size(120.dp)
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            HelloWord(
                userName = ""
            )
            Box(
                modifier = modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Search(
                    onSearchClick = navigateToSearch
                )
            }
            TodaysRecipe(
                uiState = homepageUiState,
                onStartClick = {
                    homepageViewModel.onGetRecipeDetail()
                    navigateToOverview()
                }
            )
            if(uniqueRecent.isNotEmpty()) {
                RecentRecipes(
                    recipes = uniqueRecent,
                    onRecipeClick = { id ->
                        isStartClicked.value = true

                        coroutine.launch(Dispatchers.Main) {
                            val isSuccess = homepageViewModel.onGetRecipeById(id, context)

                            if (isSuccess) {
                                navigateToOverview()
                            } else {
                                isStartClicked.value = false
                            }
                        }

                    }
                )
            }
            RecentSearch(
                queries = uniqueQueries,
                onGoSearch = onGoSearch,
            )
        }
    }
}

@Composable
private fun HelloWord(
    userName: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.hello, userName),
        fontSize = 32.sp.nonScaledSp,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
fun Search(
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .clickable(
                onClick = { onSearchClick() }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(50.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.search_recipe),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TodaysRecipe(
    uiState: HomepageUiState,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val recipeState = uiState.todaysRecipeState
    var isButtonEnable = remember { mutableStateOf(true) }

    Card(
        shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.todays_recipe),
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        )
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            when (recipeState) {
                is TodaysRecipeState.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        CircularWavyProgressIndicator(
                            modifier = modifier
                                .padding(dimensionResource(R.dimen.padding_extra_large))
                        )
                    }
                }

                is TodaysRecipeState.Success -> {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(recipeState.recipe.thumbnail)
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
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularWavyProgressIndicator()
                                }
                            }
                        },
                        contentScale = ContentScale.Crop,
                        modifier = modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .drawWithCache {
                                val gradient = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Black
                                    ),
                                    startY = size.height / 7,
                                    endY = size.height
                                )
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(gradient, blendMode = BlendMode.Multiply)
                                }
                            }
                            .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_large)))
                    )

                    Column(
                        modifier = modifier
                            .padding(dimensionResource(R.dimen.padding_medium))
                    ) {
                        Text(
                            text = uiState.todaysRecipeState.recipe.title,
                            maxLines = 1,
                            fontSize = 28.sp.nonScaledSp,
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Button(
                                onClick = {
                                    onStartClick()
                                    isButtonEnable.value = false
                                },
                                enabled = isButtonEnable.value
                            ) {
                                Text(
                                    "Start"
                                )
                            }
                        }
                    }
                }

                is TodaysRecipeState.Error -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RecentRecipes(
    recipes: List<RecentRecipe>,
    onRecipeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(dimensionResource((R.dimen.padding_medium))),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.recent_visit),
            fontSize = 24.sp.nonScaledSp,
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        )
        Card(
            colors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = modifier
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small))
                    .horizontalScroll(rememberScrollState())
            ){
                if(recipes.size != 0) {
                    for (recipe in recipes) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(context = LocalContext.current)
                                .data(recipe.thumbnail)
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
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularWavyProgressIndicator()
                                    }
                                }
                            },
                            contentScale = ContentScale.Crop,
                            modifier = modifier
                                .height(88.dp)
                                .aspectRatio(1f / 1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(
                                    onClick = {
                                        onRecipeClick(recipe.id)
                                    }
                                )
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun RecentSearch(
    queries: List<Query>,
    onGoSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.recent_search),
            fontSize = 24.sp.nonScaledSp,
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        )
        if(queries.isEmpty()) {
            Text(
                text = stringResource(R.string.no_recent_search),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp.nonScaledSp,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.padding_large))
            )
            Spacer(modifier = modifier.padding(dimensionResource(R.dimen.padding_small)))
            Button(
                onClick = onGoSearch,
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(dimensionResource(R.dimen.padding_medium))
            ) {
                Text(
                    text = stringResource(R.string.go_to_search)
                )
            }
        } else {
            for (query in queries) {
                Card(
                    shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                    modifier = modifier
                        .padding(dimensionResource(R.dimen.padding_small))
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier
                            .padding(dimensionResource(R.dimen.padding_small))
                    ) {
                        Icon(imageVector = Icons.Default.SavedSearch, null)
                        Text(
                            text = query.query,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomepageBottomAppBar(
    currentRoute: String,
    onNavigationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar {
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.title)) },
                onClick = {
                    if (item.route != currentRoute) {
                        onNavigationClick(item.route)
                    }
                },
                selected = item.route == currentRoute
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomepageBottomBarPreview() {
    NavCookTheme {
        HomepageBottomAppBar(
            currentRoute = "",
            onNavigationClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    NavCookTheme {
        Homepage(
            innerPadding = PaddingValues(0.dp),
            navigateToOverview = {},
            onGoSearch = {},
            navigateToSearch = {}
        )
    }
}