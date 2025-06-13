package com.dreamyducks.navcook.data

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.dreamyducks.navcook.NavCookScreen
import com.dreamyducks.navcook.R

data class NavigationItem(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
)

val navigationItems = listOf(
    NavigationItem(title = R.string.title_homepage, icon = Icons.Default.Home, route = NavCookScreen.Homepage.name),
    NavigationItem(title = R.string.title_search, icon = Icons.Default.Search, route = NavCookScreen.Search.name)
)