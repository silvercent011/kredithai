package com.example.kredithai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.kredithai.ui.theme.KredithaiTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KredithaiTheme {
                AppRoot()
            }
        }
    }
}


data class NavigationItem(
    val title: String, val icon: ImageVector, val route: String
)

@Preview
@Composable
fun AppRoot() {

    val items = listOf(
        NavigationItem(
            title = "Home", icon = Icons.Default.Home, route = "home"
        ), NavigationItem(
            title = "Dividas", icon = Icons.Default.Payments, route = "dividas"
        ), NavigationItem(
            title = "Histórico", icon = Icons.Default.History, route = "historico"
        ), NavigationItem(
            title = "Config.", icon = Icons.Default.Settings, route = "config"
        )
    )

    val navController = rememberNavController()

    val navGraph = navController.createGraph(startDestination = "home") {
        composable("home") { HomePage() }
        composable("dividas") { DividasPage() }
        composable("historico") { HistoricoPage() }
        composable("config") { ConfigPage() }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),


        bottomBar = {
            NavigationBar {

                val selectedNavigationIndex = rememberSaveable {
                    mutableIntStateOf(0)
                }

                items.forEachIndexed { index, navigationItem ->

                    NavigationBarItem(
                        label = { Text(navigationItem.title) },
                        icon = {
                            Icon(
                                navigationItem.icon, contentDescription = navigationItem.title
                            )
                        },
                        selected = selectedNavigationIndex.intValue == index,
                        onClick = {
                            selectedNavigationIndex.intValue = index
                            navController.navigate(navigationItem.route)
                        },
                    )
                }
            }
        }

    ) { innerPadding ->


        NavHost(navController, navGraph, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun HomePage() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Home Screen", style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Composable
fun DividasPage() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Dividas Screen", style = MaterialTheme.typography.headlineLarge
        )
    }
}


@Composable
fun HistoricoPage() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Histórico Screen", style = MaterialTheme.typography.headlineLarge
        )
    }
}


@Composable
fun ConfigPage() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Config Screen", style = MaterialTheme.typography.headlineLarge
        )
    }
}

