package com.dreamyducks.navcook

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
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
import com.dreamyducks.navcook.ui.NavCookViewModel
import com.dreamyducks.navcook.ui.WelcomeScreen
import com.dreamyducks.navcook.ui.homepage.Homepage
import com.dreamyducks.navcook.ui.homepage.HomepageBottomAppBar
import com.dreamyducks.navcook.ui.recipeOverview.RecipeOverviewScreen
import com.dreamyducks.navcook.ui.recipeViewer.RecipeViewer
import com.dreamyducks.navcook.ui.search.SearchResultScreen
import com.dreamyducks.navcook.ui.search.SearchScreen
import com.dreamyducks.navcook.ui.search.SearchTopAppBar


enum class NavCookScreen(@StringRes val title: Int) {
    Welcome(title = R.string.title_welcome),
    Homepage(title = R.string.title_homepage),
    RecipeOverview(title = R.string.title_recipe_overview),
    Search(title = R.string.title_search),
    SearchResult(title = R.string.title_search_result),
    RecipeViewer(title = R.string.title_recipe_viewer)
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
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            when (currentRoute) {
                NavCookScreen.Homepage.name -> CenterAlignedTopAppBar(
                    title = {
                        Text("Homepage")
                    },
                    modifier = modifier
                )

                NavCookScreen.Search.name -> SearchTopAppBar(
                    navigateUp = {
                        navController.popBackStack()
                    }
                )
            }
        },
        bottomBar = {
            when (currentRoute) {
                NavCookScreen.Homepage.name, NavCookScreen.Search.name, NavCookScreen.SearchResult.name ->
                    HomepageBottomAppBar(
                        currentRoute = currentRoute,
                        onNavigationClick = { destination ->
                            navController.navigate(
                                route = destination,
                            )
                        }
                    )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
                .fillMaxSize()
        ) {
            composable(route = NavCookScreen.Welcome.name) {
                WelcomeScreen(
                    innerPadding = innerPadding,
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
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                }
            ) {
                Homepage(
                    innerPadding = innerPadding,
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
                RecipeOverviewScreen(
                    innerPadding = innerPadding,
                    onStartClick = {
                        navController.navigate(NavCookScreen.RecipeViewer.name)
                    }
                )
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
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                }
            ) {
                SearchScreen(
                    innerPadding = innerPadding,
                    onNavigateToSearchResult = {
                        navController.navigate(NavCookScreen.SearchResult.name)
                    }
                )
            }
            composable(
                route = NavCookScreen.SearchResult.name,
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
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                }
            ) {
                SearchResultScreen(
                    innerPadding = innerPadding,
                    navigateToRecipeOverview = {
                        navController.navigate(NavCookScreen.RecipeOverview.name)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = NavCookScreen.RecipeViewer.name,
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
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                }
            ) {
                RecipeViewer(
                    innerPadding = innerPadding,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
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