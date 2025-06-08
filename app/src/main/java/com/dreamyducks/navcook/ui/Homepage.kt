package com.dreamyducks.navcook.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamyducks.navcook.R
import com.dreamyducks.navcook.format.nonScaledSp
import com.dreamyducks.navcook.ui.theme.NavCookTheme

@Composable
fun Homepage(
    modifier: Modifier = Modifier,
    viewModel: NavCookViewModel = viewModel(),
    navigateToOverview: () -> Unit,
    navigateToSearch: () -> Unit,
) {
    val searchInput by viewModel.searchInput.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = modifier
                .fillMaxWidth()
        ) {
            HelloWord(
                userName = "UserName"
            ) //Hello word

            Box(
                modifier = modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Search(
                    onSearchClick = navigateToSearch
                )
            }

            //Today's recipe

            OutlinedButton(
                onClick = {
                    navigateToOverview()
                }
            ) {
                Text("test")
            }

            OutlinedButton(
                onClick = {
                    viewModel.tts(
                        context = context
                    )
                }
            ) {
                Text("Speech test")
            }
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

@Composable
private fun TodaysRecipe() {

}

@Composable
fun HomepageBottomAppBar(
    modifier: Modifier = Modifier
) {
    BottomAppBar {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = modifier
                .fillMaxWidth()
        ) {
            BottomBarItem(
                icon = Icons.Outlined.Search,
                description = "Search"
            )
            BottomBarItem(
                icon = Icons.Outlined.Menu,
                description = "Menu"
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                onClick = {}
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Text(
            text = description
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomepageBottomBarPreview() {
    NavCookTheme {
        HomepageBottomAppBar()
    }
}

@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    NavCookTheme {
        Homepage(
            navigateToOverview = {},
            navigateToSearch = {}
        )
    }
}