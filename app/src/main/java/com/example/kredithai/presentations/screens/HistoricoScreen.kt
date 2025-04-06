package com.example.kredithai.presentations.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kredithai.data.db.DividaDB
import com.example.kredithai.data.models.DividaModel
import com.example.kredithai.presentations.components.DividaDetailModal
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Divider
import androidx.compose.foundation.layout.size
import com.example.kredithai.presentations.components.HistoricoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoScreen(
    navController: NavHostController,
    db: DividaDB
) {
    var dividas by remember { mutableStateOf<List<DividaModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var sortAscending by remember { mutableStateOf(false) }
    var selectedDivida by remember { mutableStateOf<DividaModel?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(Unit) {
        dividas = db.dividaDao().getAllDividas()
        isLoading = false
    }

    val filteredDividas = remember(dividas, searchQuery, sortAscending) {
        val filtered = if (searchQuery.isBlank()) {
            dividas
        } else {
            dividas.filter {
                it.nomeCompleto?.contains(searchQuery, ignoreCase = true) ?: false
            }
        }

        if (sortAscending) {
            filtered.sortedBy { it.dataVencimento }
        } else {
            filtered.sortedByDescending { it.dataVencimento }
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {},
                    active = false,
                    onActiveChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {}

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "${filteredDividas.size} itens",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Botão de ordenação com label
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { sortAscending = !sortAscending }
                    ) {
                        Text(
                            text = "Data",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (sortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = "Ordenar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Divider()
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
                filteredDividas.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) {
                                "Nenhuma dívida encontrada"
                            } else {
                                "Nenhuma dívida encontrada para \"$searchQuery\""
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (searchQuery.isNotBlank()) {
                            TextButton(onClick = { searchQuery = "" }) {
                                Text("Limpar busca")
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredDividas) { divida ->
                            HistoricoItem(
                                divida = divida,
                                dateFormat = dateFormat,
                                onItemClick = { selectedDivida = divida }
                            )
                        }
                    }
                }
            }

            // Modal de detalhes
            selectedDivida?.let { divida ->
                DividaDetailModal(
                    divida = divida,
                    onDismiss = { selectedDivida = null }
                )
            }
        }
    }
}