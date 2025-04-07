package com.example.kredithai.presentations.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kredithai.data.models.DividaModel
import com.example.kredithai.data.dao.DividaDao
import com.example.kredithai.presentations.components.BarChart
import com.example.kredithai.presentations.components.BarChartItem
import com.example.kredithai.presentations.components.MetricCard
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    navController: NavHostController,
    dividaDao: DividaDao
) {
    val scope = rememberCoroutineScope()

    var todasDividas by remember { mutableStateOf<List<DividaModel>>(emptyList()) }
    var dividasPendentes by remember { mutableStateOf<List<DividaModel>>(emptyList()) }
    var dividasAtrasadas by remember { mutableStateOf<List<DividaModel>>(emptyList()) }
    var dividasPagas by remember { mutableStateOf<List<DividaModel>>(emptyList()) }

    val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    LaunchedEffect(key1 = true) {
        scope.launch {
            todasDividas = dividaDao.getAllDividas()
            dividasPendentes = dividaDao.getDividasByStatus("pendente")
            dividasPagas = dividaDao.getDividasByStatus("paga")

            val currentTime = System.currentTimeMillis()
            dividasAtrasadas = dividaDao.getDividasVencidas(currentTime)
                .filter { it.status != "paga" }
        }
    }

    val totalDividas = todasDividas.size
    val valorTotal = todasDividas.sumOf { it.valorDivida }
    val valorPendente = dividasPendentes.sumOf { it.valorDivida }
    val valorAtrasado = dividasAtrasadas.sumOf { it.valorDivida }
    val valorPago = dividasPagas.sumOf { it.valorDivida }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Total de Dívidas",
                value = totalDividas.toString(),
                icon = Icons.Default.AccountBalance,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Valor Total",
                value = formatoMoeda.format(valorTotal),
                icon = Icons.Default.AttachMoney,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Pendentes",
                value = formatoMoeda.format(valorPendente),
                subtitle = "${dividasPendentes.size} dívidas",
                icon = Icons.Default.Payment,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Atrasadas",
                value = formatoMoeda.format(valorAtrasado),
                subtitle = "${dividasAtrasadas.size} dívidas",
                icon = Icons.Default.Warning,
                color = Color(0xFFE53935),
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Distribuição de Valores",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                BarChart(
                    items = listOf(
                        BarChartItem("Pendente", valorPendente, MaterialTheme.colorScheme.tertiary),
                        BarChartItem("Atrasado", valorAtrasado, Color(0xFFE53935)),
                        BarChartItem("Pago", valorPago, Color(0xFF4CAF50))
                    ),
                    maxValue = valorTotal
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Próximos Vencimentos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val currentTime = System.currentTimeMillis()
                val proximosVencimentos = dividasPendentes
                    .filter { it.dataVencimento > currentTime }
                    .sortedBy { it.dataVencimento }
                    .take(3)

                if (proximosVencimentos.isEmpty()) {
                    Text(
                        text = "Nenhum vencimento próximo encontrado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    proximosVencimentos.forEach { divida ->
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val dataFormatada = dateFormat.format(Date(divida.dataVencimento))

                        ListItem(
                            headlineContent = {
                                Text(text = divida.nomeCompleto ?: "Sem nome")
                            },
                            supportingContent = {
                                Text(text = "Vencimento: $dataFormatada")
                            },
                            trailingContent = {
                                Text(
                                    text = formatoMoeda.format(divida.valorDivida),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Default.Payment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                        Divider()
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Análise de Sazonalidade",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val sazonalidadeMap = todasDividas
                    .groupBy { it.sazonalidade }
                    .mapValues { it.value.size }

                val totalItems = sazonalidadeMap.values.sum().toFloat()

                sazonalidadeMap.entries.sortedByDescending { it.value }.forEach { (sazonalidade, count) ->
                    val percentagem = (count / totalItems) * 100

                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = sazonalidade.capitalize(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "$count (${String.format("%.1f", percentagem)}%)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        LinearProgressIndicator(
                            progress = { count / totalItems },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .padding(top = 4.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
            }
        }
    }
}





fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}