package com.example.kredithai.presentations.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kredithai.data.db.DividaDB
import com.example.kredithai.data.models.DividaModel
import com.example.kredithai.navigation.Routes
import com.example.kredithai.presentations.components.DividaCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@Composable
fun DividasScreen(
    navController: NavController,
    db: DividaDB,
    modifier: Modifier = Modifier
) {
    var dividas by remember { mutableStateOf<List<DividaModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var mesSelecionado by remember { mutableStateOf("Todos") }
    val meses = listOf(
        "Todos", "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )

    val scope = rememberCoroutineScope()

    suspend fun carregarDividas() {
        try {
            isLoading = true
            withContext(Dispatchers.IO) {
                db.dividaDao().atualizarStatusAtrasadas(System.currentTimeMillis())
            }
            val todas = withContext(Dispatchers.IO) {
                db.dividaDao().getDividasPendentesOuAtrasadas()
            }

            dividas = if (mesSelecionado == "Todos") {
                todas
            } else {
                val indexMes = meses.indexOf(mesSelecionado).takeIf { it >= 0 }?.minus(1) ?: -1
                if (indexMes < 0) {
                    todas
                } else {
                    todas.filter { divida ->
                        val cal = Calendar.getInstance().apply {
                            timeInMillis = divida.dataVencimento
                        }
                        cal.get(Calendar.MONTH) == indexMes
                    }
                }
            }
        } catch (e: Exception) {
            error = "Erro ao carregar dívidas: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(mesSelecionado) {
        carregarDividas()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var expanded by remember { mutableStateOf(false) }
                Box {
                    TextButton(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .height(32.dp)
                            .defaultMinSize(minWidth = 1.dp)
                    ) {
                        Text(
                            text = mesSelecionado,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        meses.forEach { mes ->
                            DropdownMenuItem(
                                text = { Text(mes) },
                                onClick = {
                                    mesSelecionado = mes
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(dividas) { divida ->
                            DividaCard(
                                divida = divida,
                                modifier = Modifier.padding(vertical = 4.dp),
                                onClick = {
                                    navController.navigate("${Routes.DIVIDAS_DETAILS}/${divida.uid}")
                                },
                                onDelete = { dividaToDelete ->
                                    scope.launch {
                                        withContext(Dispatchers.IO) {
                                            db.dividaDao().delete(dividaToDelete)
                                        }
                                        carregarDividas()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}