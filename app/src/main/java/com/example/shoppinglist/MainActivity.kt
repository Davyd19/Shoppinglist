package com.example.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shoppinglist.components.ItemInput
import com.example.shoppinglist.components.SearchInput
import com.example.shoppinglist.components.ShoppingList
import com.example.shoppinglist.components.Title
import com.example.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

// Screen Routes
sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Shopping List")
    object About : Screen("about", "About App")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val screens = listOf(
        Screen.Home,
        Screen.About
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("App Menu", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                screens.forEach { screen ->
                    NavigationDrawerItem(
                        icon = {
                            if (screen.route == Screen.Home.route) Icon(Icons.Default.Home, contentDescription = null)
                            else Icon(Icons.Default.Info, contentDescription = null)
                        },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        val title = screens.find { it.route == currentDestination?.route }?.title ?: "Shopping List"
                        Text(title)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    screens.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                if (screen.route == Screen.Home.route) Icon(Icons.Default.Home, contentDescription = null)
                                else Icon(Icons.Default.Info, contentDescription = null)
                            },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(
                    Screen.Home.route,
                    exitTransition = {
                        slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) +
                                fadeOut(animationSpec = tween(300))
                    },
                    popEnterTransition = {
                        slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(300)) +
                                fadeIn(animationSpec = tween(300))
                    }
                ) {
                    ShoppingListScreen()
                }

                composable(
                    Screen.About.route,
                    enterTransition = {
                        slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) +
                                fadeIn(animationSpec = tween(300))
                    },
                    popExitTransition = {
                        slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(300)) +
                                fadeOut(animationSpec = tween(300))
                    }
                ) {
                    AboutScreen()
                }
            }
        }
    }
}


@Composable
fun ShoppingListScreen() {
    var newItemText by rememberSaveable { mutableStateOf("") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val shoppingItems = remember { mutableStateListOf<String>() }

    val filteredItems by remember(searchQuery, shoppingItems) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                shoppingItems
            } else {
                shoppingItems.filter { it.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Title()
        ItemInput(
            text = newItemText,
            onTextChange = { newItemText = it },
            onAddItem = {
                if (newItemText.isNotBlank()) {
                    shoppingItems.add(0, newItemText)
                    newItemText = ""
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        SearchInput(
            query = searchQuery,
            onQueryChange = { searchQuery = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        ShoppingList(items = filteredItems)
    }
}

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("About This App", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("This is a simple shopping list application created using Jetpack Compose to demonstrate modern Android development practices, including navigation.")
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    ShoppingListTheme {
        MainApp()
    }
}