package com.example.kredithai

import CustomTopAppBar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.kredithai.navigation.AppNavHost
import com.example.kredithai.navigation.Routes
import com.example.kredithai.presentations.components.AppBottomBar
import com.example.kredithai.ui.theme.KredithaiTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        val splashScreen = installSplashScreen()
//        splashScreen.setKeepOnScreenCondition {true}
//
//        CoroutineScope(Dispatchers.Main).launch {
//        delay(3000L)
//          splashScreen.setKeepOnScreenCondition {false}
//        }

        setContent {
            KredithaiTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopAppBar(
                title = when (currentRoute) {
                    Routes.HOME -> "Home"
                    Routes.DIVIDAS -> "Dívidas"
                    Routes.HISTORICO -> "Histórico"
                    Routes.CONFIG -> "Configurações"
                    else -> "App"
                },
                currentRoute = currentRoute,
                navController = navController,
                onPersonClick = {

                }
            )
        },
        bottomBar = { AppBottomBar(navController) },
        floatingActionButton = {
            if (currentRoute == Routes.HOME || currentRoute == Routes.DIVIDAS) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Routes.CADASTRO)
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Filled.Add, "Adicionar")
                }
            }
        }

    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding))
    }
}