package com.example.kredithai.presentations.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kredithai.data.models.DividaModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDividaContent(
    divida: DividaModel,
    onSave: (DividaModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var editedDivida by remember { mutableStateOf(divida) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val currentDate = System.currentTimeMillis()

    // Atualiza status baseado nas datas
    fun atualizarStatus(): String = when {
        editedDivida.dataPagamento != null -> "paga"
        currentDate > editedDivida.dataVencimento -> "atrasada"
        else -> "pendente"
    }

    // Recalcula status toda vez que a data for alterada
    LaunchedEffect(editedDivida.dataPagamento) {
        editedDivida = editedDivida.copy(status = atualizarStatus())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status automático
        OutlinedTextField(
            value = editedDivida.status.replaceFirstChar { it.uppercase() },
            onValueChange = {},
            label = { Text("Status") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        // Data de pagamento
        OutlinedTextField(
            value = editedDivida.dataPagamento?.let { dateFormat.format(Date(it)) } ?: "Não pago",
            onValueChange = {},
            label = { Text("Data de Pagamento") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Definir data de pagamento")
                }
            }
        )

        // Data de vencimento (apenas visual)
        OutlinedTextField(
            value = dateFormat.format(Date(editedDivida.dataVencimento)),
            onValueChange = {},
            label = { Text("Data de Vencimento") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onSave(editedDivida) },
            modifier = Modifier.fillMaxWidth(),
            enabled = editedDivida != divida
        ) {
            Text("Salvar Alterações")
        }
    }

    // Date Picker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = editedDivida.dataPagamento ?: currentDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        editedDivida = editedDivida.copy(
                            dataPagamento = it,
                            status = atualizarStatus()
                        )
                    }
                    showDatePicker = false
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
