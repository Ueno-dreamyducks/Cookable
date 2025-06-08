package com.dreamyducks.navcook

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dreamyducks.navcook.ui.Homepage
import com.dreamyducks.navcook.ui.HomepageBottomAppBar
import com.dreamyducks.navcook.ui.NavCookViewModel
import com.dreamyducks.navcook.ui.RecipeOverviewScreen
import com.dreamyducks.navcook.ui.SearchScreen
import com.dreamyducks.navcook.ui.SearchTopAppBar
import com.dreamyducks.navcook.ui.WelcomeScreen


enum class NavCookScreen(@StringRes val title: Int) {
    Welcome(title = R.string.title_welcome),
    Homepage(title = R.string.title_homepage),
    RecipeOverview(title = R.string.title_recipe_overview),
    Search(title = R.string.title_search)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavCookApp(
    modifier: Modifier = Modifier,
    navCookViewModel: NavCookViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current

    val startDestination = if (isFirstOpen(context)) {
        NavCookScreen.Welcome.name
    } else {
        NavCookScreen.Homepage.name
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()


    Scaffold(
        topBar = {
            when (currentBackStackEntry?.destination?.route) {
                NavCookScreen.Homepage.name -> CenterAlignedTopAppBar(
                    title = {
                        Text("Homepage")
                    },
                    modifier = modifier
                )
                NavCookScreen.Search.name -> SearchTopAppBar({})
            }
        },
        bottomBar = {
            when (currentBackStackEntry?.destination?.route) {
                NavCookScreen.Homepage.name -> HomepageBottomAppBar()
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            composable(route = NavCookScreen.Welcome.name) {
                WelcomeScreen(
                    onNavigateToHomepage = {
                        navController.navigate(NavCookScreen.Homepage.name)
                    }
                )
            }
            composable(
                route = NavCookScreen.Homepage.name,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                }
            ) {
                Homepage(
                    viewModel = navCookViewModel,
                    navigateToOverview = {
                        navController.navigate(NavCookScreen.RecipeOverview.name)
                    },
                    navigateToSearch = {
                        navController.navigate(NavCookScreen.Search.name)
                    }
                )
            }
            composable(route = NavCookScreen.RecipeOverview.name) {
                RecipeOverviewScreen()
            }
            composable(
                route = NavCookScreen.Search.name,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                },
                exitTransition = {
                    ExitTransition.None
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                }
            ) {
                SearchScreen(
                    viewModel = navCookViewModel
                )
            }
        }
    }
}

private fun isFirstOpen(context: Context): Boolean {
    val prefs = context.getSharedPreferences(
        context.getString(R.string.is_first_preference_key),
        Context.MODE_PRIVATE
    )
    val isFirstOpen = prefs.getBoolean(context.getString(R.string.is_first_preference_key), true)

    if (isFirstOpen) {
        prefs.edit() {
            putBoolean(context.getString(R.string.is_first_preference_key), false)
        }
    }

    return isFirstOpen
}