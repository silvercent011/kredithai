package com.example.kredithai.navigation

import CadastroScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kredithai.data.db.DividaDB
import com.example.kredithai.presentations.screens.*

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier, db: DividaDB) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) { HomeScreen(navController, db.dividaDao()) }
        composable(Routes.DIVIDAS) {
            DividasScreen(
                navController = navController,
                db = db
            )
        }
        composable(Routes.HISTORICO) { HistoricoScreen(navController, db) }
        composable(Routes.CONFIG) { ConfigScreen(navController) }
        composable(Routes.CADASTRO) { CadastroScreen(navController) }
    }
}