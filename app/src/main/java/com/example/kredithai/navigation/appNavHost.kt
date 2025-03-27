package com.example.kredithai.navigation

import CadastroScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kredithai.presentations.screens.*

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) { HomeScreen() }
        composable(Routes.DIVIDAS) { DividasScreen() }
        composable(Routes.HISTORICO) { HistoricoScreen() }
        composable(Routes.CONFIG) { ConfigScreen() }
        composable(Routes.CADASTRO) { CadastroScreen() }
    }
}
