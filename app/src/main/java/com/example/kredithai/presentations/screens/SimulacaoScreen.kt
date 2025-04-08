import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kredithai.data.models.SimulacaoInput
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulacaoValoresScreen(
    navController: NavHostController
) {
    var valorOriginal by remember { mutableStateOf("") }
    var dataVencimento by remember { mutableStateOf(System.currentTimeMillis()) }
    var taxaJuros by remember { mutableStateOf("5") } // 5% como padrão
    var multaAtraso by remember { mutableStateOf("2.0") } // 2% como padrão

    var valorPagamento by remember { mutableStateOf("") }
    var dataPagamento by remember { mutableStateOf(System.currentTimeMillis()) }

    var showResultDialog by remember { mutableStateOf(false) }
    var simulacaoResult by remember { mutableStateOf<SimulacaoResult?>(null) }

    Scaffold(

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Informe os dados para simulação",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Seção: Dados do Débito
            Text(
                text = "Dados do Débito",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = valorOriginal,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                        valorOriginal = newValue
                    }
                },
                label = { Text("Valor Original (R\$)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Seletor de data de vencimento
            var showVencimentoPicker by remember { mutableStateOf(false) }
            val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

            OutlinedButton(
                onClick = { showVencimentoPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Data de Vencimento: ${dateFormatter.format(Date(dataVencimento))}")
            }

            if (showVencimentoPicker) {
                val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dataVencimento)
                DatePickerDialog(
                    onDismissRequest = { showVencimentoPicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    dataVencimento = it
                                }
                                showVencimentoPicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = taxaJuros,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                        taxaJuros = newValue
                    }
                },
                label = { Text("Taxa de Juros Mensal (%)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = multaAtraso,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                        multaAtraso = newValue
                    }
                },
                label = { Text("Multa por Atraso (R\$ ou %)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Seção: Dados do Pagamento
            Text(
                text = "Dados do Pagamento",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = valorPagamento,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                        valorPagamento = newValue
                    }
                },
                label = { Text("Valor a Pagar (R\$)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Seletor de data de pagamento
            var showPagamentoPicker by remember { mutableStateOf(false) }

            OutlinedButton(
                onClick = { showPagamentoPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Data de Pagamento: ${dateFormatter.format(Date(dataPagamento))}")
            }

            if (showPagamentoPicker) {
                val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dataPagamento)
                DatePickerDialog(
                    onDismissRequest = { showPagamentoPicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    dataPagamento = it
                                }
                                showPagamentoPicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val input = SimulacaoInput(
                        valorOriginal = valorOriginal.toDoubleOrNull() ?: 0.0,
                        dataVencimento = dataVencimento,
                        taxaJuros = taxaJuros.toIntOrNull() ?: 0,
                        multaAtraso = multaAtraso.toDoubleOrNull() ?: 0.0
                    )

                    simulacaoResult = SimulacaoCalculator.calcular(
                        input = input,
                        valorPagamento = valorPagamento.toDoubleOrNull() ?: 0.0,
                        dataPagamento = dataPagamento
                    )

                    showResultDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = valorOriginal.isNotEmpty() && valorPagamento.isNotEmpty()
            ) {
                Text("Calcular Simulação")
            }
        }

        // Diálogo de Resultado
        if (showResultDialog && simulacaoResult != null) {
            AlertDialog(
                onDismissRequest = { showResultDialog = false },
                title = { Text("Resultado da Simulação") },
                text = {
                    Column {
                        Text("Valor Original: R$ ${"%.2f".format(simulacaoResult!!.valorOriginal)}")
                        Text("Valor Informado: R$ ${"%.2f".format(simulacaoResult!!.valorPagamento)}")

                        if (simulacaoResult!!.diasAtraso > 0) {
                            Text("Dias em Atraso: ${simulacaoResult!!.diasAtraso}")
                            Text("Juros: R$ ${"%.2f".format(simulacaoResult!!.valorJuros)}")
                            Text("Multa: R$ ${"%.2f".format(simulacaoResult!!.valorMulta)}")
                        } else {
                            Text("Pagamento realizado sem atraso")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Total a Pagar: R$ ${"%.2f".format(simulacaoResult!!.valorTotal)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showResultDialog = false }
                    ) {
                        Text("Fechar")
                    }
                }
            )
        }
    }
}