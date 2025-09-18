package com.dreamyducks.navcook.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dreamyducks.navcook.R
import com.dreamyducks.navcook.format.nonScaledSp
import com.dreamyducks.navcook.network.SearchResult
import kotlinx.coroutines.launch

@Composable
fun SearchResultScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    searchResultViewModel: SearchResultViewModel = viewModel(factory = SearchResultViewModel.Factory),
    onNavigateBack: () -> Unit,
    navigateToRecipeOverview: () -> Unit,
) {
    val searchResult = searchResultViewModel.searchResult.collectAsState()
    var topBarHeight by remember { mutableStateOf<Dp>(0.dp) }
    val coroutine = rememberCoroutineScope()
    val searchResultScreen = searchResultViewModel.searchResultScreen

    LaunchedEffect(Unit) {
        searchResultViewModel.updateScreen(SearchResultScreen.Result)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        SearchResultTopBar(
            searchQuery = searchResultViewModel.searchQuery.collectAsState().value,
            onNavigationBack = onNavigateBack,
            height = {
                topBarHeight = it
            }
        )
        Box(
            modifier = modifier
                .padding(innerPadding)
                .padding(top = topBarHeight)
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            when(searchResultScreen) {
                is SearchResultScreen.Result ->
                    Success(
                        recipes = searchResult.value,
                        onRecipeClick = { recipeId ->
                            coroutine.launch {
                                searchResultViewModel.onGetRecipeDetail(id = recipeId)
                                navigateToRecipeOverview()
                            }
                        }
                    )
                is SearchResultScreen.LoadingRecipe ->
                    Loading()
            }
        }
    }
}

@Composable
private fun Success(
    recipes: List<SearchResult>,
    onRecipeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (recipes.isNotEmpty()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            itemsIndexed(recipes) { index, item ->
                RecipeItem(
                    isFirstItem = index == 0,
                    isLastItem = index == recipes.lastIndex,
                    recipe = item,
                    onRecipeClick = onRecipeClick
                )
            }
        }
    } else { //Result found
        Card(
            modifier = modifier
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
                    .fillMaxSize()
            ) {
                Icon(
                    Icons.Default.SearchOff,
                    contentDescription = null,
                    modifier = modifier
                        .size(120.dp)
                        .padding(dimensionResource(R.dimen.padding_large))
                )
                Text(
                    text = stringResource(R.string.no_recipe_found),
                    fontSize = 32.sp.nonScaledSp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun RecipeItem(
    isFirstItem: Boolean,
    isLastItem: Boolean,
    recipe: SearchResult,
    onRecipeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(
            topStart = if (isFirstItem) dimensionResource(R.dimen.padding_large) else dimensionResource(
                R.dimen.padding_small
            ),
            topEnd = if (isFirstItem) dimensionResource(R.dimen.padding_large) else dimensionResource(
                R.dimen.padding_small
            ),
            bottomStart = if (isLastItem) dimensionResource(R.dimen.padding_large) else dimensionResource(
                R.dimen.padding_small
            ),
            bottomEnd = if (isLastItem) dimensionResource(R.dimen.padding_large) else dimensionResource(
                R.dimen.padding_small
            )
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = modifier
                .clickable(
                    onClick = {
                        onRecipeClick(recipe.id)
                    }
                )
                .padding(dimensionResource(R.dimen.padding_small))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(recipe.thumbnail)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
            )
            Text(
                text = recipe.title,
                fontSize = 20.sp.nonScaledSp,
                fontWeight = FontWeight.Bold,
                modifier = modifier
                    .weight(4f)
            )
        }
    }
}

@Composable
private fun Error(
    modifier: Modifier = Modifier
) {

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


@Preview(showBackground = true)
@Composable
fun SearchResultPreview() {
    MaterialTheme {
        SearchResultScreen(
            innerPadding = PaddingValues(0.dp),
            navigateToRecipeOverview = {},
            onNavigateBack = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultTopBar(
    searchQuery: String,
    onNavigationBack: () -> Unit,
    height: (Dp) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
                Text(
                    text = searchQuery,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigationBack
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                val heightPx = coordinates.size.height
                height(with(density) { heightPx.toDp() })
            }
    )
}

@Preview(showBackground = true)
@Composable
fun SearchResultTopBarPreview() {
    MaterialTheme {
        SearchResultTopBar(
            searchQuery = "",
            onNavigationBack = {},
            height = {}
        )
    }
}