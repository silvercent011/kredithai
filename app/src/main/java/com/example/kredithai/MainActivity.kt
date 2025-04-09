package com.example.kredithai

import CustomTopAppBar
import android.app.Activity
import android.content.Intent
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.kredithai.navigation.AppNavHost
import com.example.kredithai.navigation.Routes
import com.example.kredithai.presentations.components.AppBottomBar
import com.example.kredithai.presentations.components.DividaFormDialog
import com.example.kredithai.ui.theme.KredithaiTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.room.Room
import com.example.kredithai.data.db.DividaDB
import com.example.kredithai.data.models.DividaModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val db = Room.databaseBuilder(
            applicationContext,
            DividaDB::class.java, "database-dividas"
        ).build()

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
                AppRoot(db)
            }
        }
    }
}

@Composable
fun AppRoot(db:DividaDB) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    var isDialogOpen by remember { mutableStateOf(false) }
    val dividaDAO = db.dividaDao()

    suspend fun inserirDivida(divida:DividaModel) {
        dividaDAO.insert(divida)
    }

    val coroutineScope = rememberCoroutineScope()
    val fullScreenRoutes = listOf(Routes.SETTINGS)

    if (currentRoute in fullScreenRoutes) {
        AppNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize(),
            db = db
        )
    } else {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            CustomTopAppBar(
                title = when (currentRoute) {
                    Routes.HOME -> "Kredithai"
                    Routes.DIVIDAS -> "Dívidas"
                    Routes.HISTORICO -> "Histórico"
                    Routes.SIMULACAO -> "Simulação"
                    else -> "Adicionar pagamento"
                },
                currentRoute = currentRoute,
                navController = navController,
                onPersonClick = {
                    navController.navigate("settings")
                }
            )
        },
        bottomBar = { AppBottomBar(navController) },
        floatingActionButton = {
            if (currentRoute == Routes.HOME || currentRoute == Routes.DIVIDAS) {
                FloatingActionButton(
                    onClick = {
                       // navController.navigate(Routes.CADASTRO)
                        isDialogOpen = true
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Filled.Add, "Adicionar")
                }
            }
        }

    ) { innerPadding ->

        if (isDialogOpen) {
            DividaFormDialog(
                isOpen = isDialogOpen,
                onClose = { isDialogOpen = false }, // Fecha o modal
                onSave = { divida ->
                    // Aqui você poderia salvar a dívida no banco de dados usando o DAO
                    coroutineScope.launch {
                        inserirDivida(divida) // Call the suspend function
                        isDialogOpen = false
                    }
                },
                db
            )
        }

        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),db)



    }}
}