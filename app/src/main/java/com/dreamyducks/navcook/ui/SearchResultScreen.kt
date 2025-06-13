package com.dreamyducks.navcook.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamyducks.navcook.R
import com.dreamyducks.navcook.format.nonScaledSp

@Composable
fun SearchResultScreen(
    viewModel: NavCookViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val searchUiState = viewModel.searchUiState

    Box(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_medium))
            .fillMaxSize()
    ) {
        when (searchUiState) {
            is SearchUiState.Loading -> Loading()
            is SearchUiState.Success -> Success(recipes = listOf<Recipe>())
            is SearchUiState.Error -> Error()
        }
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
private fun Success(
    recipes: List<Recipe>,
    modifier: Modifier = Modifier
) {
    if(recipes.isNotEmpty()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            itemsIndexed(recipes) { index, item ->
                RecipeItem(
                    isFirstItem = index == 0,
                    isLastItem = index == recipes.lastIndex,
                    recipe = item
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
    recipe: Recipe,
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
                .padding(dimensionResource(R.dimen.padding_small))
        ) {
            Image(
                bitmap = recipe.thumbNailImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
            )
            Text(
                text = recipe.title,
                fontSize = 24.sp.nonScaledSp,
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


@Preview(showBackground = true)
@Composable
fun SearchResultPreview() {
    MaterialTheme {
        SearchResultScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultTopBar(
    viewModel: NavCookViewModel = NavCookViewModel(),
    onNavigationBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchInput.collectAsState()

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
                    fontWeight = FontWeight.Medium
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
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SearchResultTopBarPreview() {
    MaterialTheme {
        SearchResultTopBar(
            onNavigationBack = {}
        )
    }
}