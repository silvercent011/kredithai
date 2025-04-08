package com.example.kredithai.navigation

import CadastroScreen
import SimulacaoValoresScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.kredithai.data.db.DividaDB
import com.example.kredithai.data.models.DividaModel
import com.example.kredithai.presentations.screens.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    db: DividaDB
) {
    val dividaDao = db.dividaDao()
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                navController = navController,
                dividaDao = db.dividaDao()
            )
        }
        composable(Routes.DIVIDAS) {
            DividasScreen(
                navController = navController,
                db = db
            )
        }
        composable(Routes.HISTORICO) {
            HistoricoScreen(
                navController = navController,
                db = db
            )
        }
        composable(Routes.SIMULACAO) {
            SimulacaoValoresScreen(navController) // Tela de simulação independente
        }

        composable(
            route = "${Routes.DIVIDAS_DETAILS}/{dividaId}",
            arguments = listOf(navArgument("dividaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val dividaId = backStackEntry.arguments?.getInt("dividaId") ?: 0
            var divida by remember { mutableStateOf<DividaModel?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(dividaId) {
                divida = withContext(Dispatchers.IO) {
                    dividaDao.getDividaById(dividaId)
                }
                isLoading = false
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (divida != null) {
                EditDividaScreen(
                    divida = divida!!,
                    navController = navController,
                    db = db
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Dívida não encontrada")
                }
            }
        }
    }
}