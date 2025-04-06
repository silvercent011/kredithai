package com.example.kredithai.presentations.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kredithai.data.db.DividaDB
import com.example.kredithai.presentations.components.DividaCard
import kotlinx.coroutines.Dispatchers
import com.example.kredithai.data.models.DividaModel
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DividasScreen(
    navController: NavController,
    db: DividaDB,
    modifier: Modifier = Modifier
) {
    var dividas by remember { mutableStateOf<List<DividaModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            // Executa a consulta em uma thread de IO
            val result = withContext(Dispatchers.IO) {
                db.dividaDao().getAllDividas()
            }
            dividas = result
        } catch (e: Exception) {
            error = "Erro ao carregar dívidas: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    error != null -> {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    dividas.isEmpty() -> {
                        Text(
                            text = "Nenhuma dívida cadastrada",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp)
                        ) {
                            items(dividas) { divida ->
                                DividaCard(
                                    divida = divida,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    onClick = {
                                        // Navega para detalhes da dívida
                                        // navController.navigate("${Routes.DIVIDAS_DETAILS}/${divida.uid}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

