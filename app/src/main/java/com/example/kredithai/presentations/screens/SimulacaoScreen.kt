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
    var multaAtraso by remember { mutableStateOf("2") } // 2% como padrão
    var dataPagamento by remember { mutableStateOf(System.currentTimeMillis()) }

    var showResultDialog by remember { mutableStateOf(false) }
    var simulacaoResult by remember { mutableStateOf<SimulacaoResult?>(null) }

    var valorError by remember { mutableStateOf<String?>(null) }
    var jurosError by remember { mutableStateOf<String?>(null) }
    var multaError by remember { mutableStateOf<String?>(null) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Simulação de Pagamento",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = valorOriginal,
                onValueChange = {
                    valorOriginal = it.filter { c -> c.isDigit() || c == '.' || c == ',' }
                    valorError = if (valorOriginal.isEmpty()) "Informe o valor" else null
                },
                label = { Text("Valor Original (R\$)") },
                modifier = Modifier.fillMaxWidth(),
                isError = valorError != null,
                supportingText = { valorError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: Data de Vencimento
            var showVencimentoPicker by remember { mutableStateOf(false) }
            val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

            OutlinedButton(
                onClick = { showVencimentoPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Vencimento: ${dateFormatter.format(Date(dataVencimento))}")
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

            // Campo: Taxa de Juros
            OutlinedTextField(
                value = taxaJuros,
                onValueChange = {
                    taxaJuros = it.filter { c -> c.isDigit() }
                    jurosError = if (taxaJuros.isEmpty()) "Informe a taxa" else null
                },
                label = { Text("Taxa de Juros Mensal (%)") },
                modifier = Modifier.fillMaxWidth(),
                isError = jurosError != null,
                supportingText = { jurosError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = multaAtraso,
                onValueChange = {
                    multaAtraso = it.filter { c -> c.isDigit() || c == '.' || c == ',' }
                    multaError = if (multaAtraso.isEmpty()) "Informe a multa" else null
                },
                label = { Text("Multa por Atraso (%)") },
                modifier = Modifier.fillMaxWidth(),
                isError = multaError != null,
                supportingText = { multaError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Data de Pagamento
            var showPagamentoPicker by remember { mutableStateOf(false) }

            Text("Data Prevista de Pagamento:", style = MaterialTheme.typography.labelMedium)
            OutlinedButton(
                onClick = { showPagamentoPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(dateFormatter.format(Date(dataPagamento)))
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
                    valorError = if (valorOriginal.isEmpty()) "Informe o valor" else null
                    jurosError = if (taxaJuros.isEmpty()) "Informe a taxa" else null
                    multaError = if (multaAtraso.isEmpty()) "Informe a multa" else null

                    if (valorError == null && jurosError == null && multaError == null) {
                        val input = SimulacaoInput(
                            valorOriginal = valorOriginal.replace(",", ".").toDouble(),
                            dataVencimento = dataVencimento,
                            taxaJuros = taxaJuros.toInt(),
                            multaAtraso = multaAtraso.replace(",", ".").toDouble() / 100 // Converte para decimal
                        )

                        simulacaoResult = SimulacaoCalculator.calcular(
                            input = input,
                            dataPagamento = dataPagamento
                        )
                        showResultDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = valorOriginal.isNotEmpty() && taxaJuros.isNotEmpty() && multaAtraso.isNotEmpty()
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
                    Button(onClick = { showResultDialog = false }) {
                        Text("Fechar")
                    }
                }
            )
        }
    }
}