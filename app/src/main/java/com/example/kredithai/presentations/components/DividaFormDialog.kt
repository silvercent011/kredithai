package com.example.kredithai.presentations.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.kredithai.data.db.DividaDB
import com.example.kredithai.data.models.DividaModel
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DividaFormDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    onSave: (DividaModel) -> Unit,
    db: DividaDB
) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = onClose,
            title = { Text(text = "Cadastrar Dívida") },
            text = { DividaForm(onSave = onSave) },
            confirmButton = {
                Button(onClick = onClose) {
                    Text("Fechar")
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DividaForm(
    onSave: (DividaModel) -> Unit
) {
    var nomeCompleto by remember { mutableStateOf(TextFieldValue("")) }
    var cpfCnpj by remember { mutableStateOf(TextFieldValue("")) }
    var telefone by remember { mutableStateOf(TextFieldValue("")) }
    var endereco by remember { mutableStateOf(TextFieldValue("")) }
    var valorDivida by remember { mutableStateOf(TextFieldValue("")) }
    var juros by remember { mutableStateOf(TextFieldValue("1")) }
    var sazonalidade by remember { mutableStateOf("mensal") }
    var descricao by remember { mutableStateOf(TextFieldValue("")) }

    var dataVencimento by rememberSaveable { mutableStateOf(System.currentTimeMillis() + 86400000) }
    var dataPagamento by rememberSaveable { mutableStateOf<Long?>(null) }

    var status by remember { mutableStateOf("pendente") }

    var nomeError by remember { mutableStateOf(false) }
    var cpfCnpjError by remember { mutableStateOf(false) }
    var valorDividaError by remember { mutableStateOf(false) }
    var jurosError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Nome", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = nomeCompleto.text,
            onValueChange = {
                nomeCompleto = TextFieldValue(it)
                if (nomeError) nomeError = false
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = nomeError,
            supportingText = {
                if (nomeError) Text("Nome é obrigatório")
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("CPF ou CNPJ", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = cpfCnpj.text,
            onValueChange = {
                val onlyDigits = it.filter { char -> char.isDigit() }
                cpfCnpj = TextFieldValue(onlyDigits)
                if (cpfCnpjError) cpfCnpjError = false
            },
            modifier = Modifier.fillMaxWidth(),
            isError = cpfCnpjError,
            supportingText = {
                if (cpfCnpjError) Text("CPF ou CNPJ é obrigatório")
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Valor da Dívida", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = valorDivida.text,
            onValueChange = {
                val filtered = it
                    .replace(",", ".")
                    .filterIndexed { index, c ->
                        c.isDigit() || (c == '.' && !valorDivida.text.replace(",", ".").drop(index + 1).contains('.'))
                    }

                valorDivida = TextFieldValue(filtered)
                if (valorDividaError) valorDividaError = false
            },
            modifier = Modifier.fillMaxWidth(),
            isError = valorDividaError,
            supportingText = {
                if (valorDividaError) Text("Informe um valor válido")
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )


        Spacer(modifier = Modifier.height(8.dp))

        Text("Taxa de Juros (%)", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = juros.text,
            onValueChange = {
                val onlyDigits = it.filter { char -> char.isDigit() }
                juros = TextFieldValue(onlyDigits)
                if (jurosError) jurosError = false
            },
            modifier = Modifier.fillMaxWidth(),
            isError = jurosError,
            supportingText = {
                if (jurosError) Text("Informe uma taxa de juros válida")
            }
        )


        Spacer(modifier = Modifier.height(8.dp))

        Text("Data de Vencimento", style = MaterialTheme.typography.bodyMedium)
        DatePickerField(
            initialDate = dataVencimento,
            onDateSelected = { dataVencimento = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Status da Dívida", style = MaterialTheme.typography.bodyMedium)
        StatusDropdown(
            currentStatus = status,
            onStatusSelected = { status = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Sazonalidade", style = MaterialTheme.typography.bodyMedium)
        SazonalidadeDropdown(
            currentValue = sazonalidade,
            onValueSelected = { sazonalidade = it }
        )

        Text("Descrição", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = descricao.text,
            onValueChange = { descricao = TextFieldValue(it) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (status == "paga") {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Data de Pagamento", style = MaterialTheme.typography.bodyMedium)
            DatePickerField(
                initialDate = dataPagamento ?: System.currentTimeMillis(),
                onDateSelected = { dataPagamento = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Validação
                val isNomeValido = nomeCompleto.text.isNotBlank()
                val isCpfCnpjValido = cpfCnpj.text.isNotBlank()
                val isValorValido = valorDivida.text.toDoubleOrNull() != null && valorDivida.text.toDouble() > 0
                val isJurosValido = juros.text.toIntOrNull() != null && juros.text.toInt() >= 0

                nomeError = !isNomeValido
                cpfCnpjError = !isCpfCnpjValido
                valorDividaError = !isValorValido
                jurosError = !isJurosValido

                val formValido = isNomeValido && isCpfCnpjValido && isValorValido && isJurosValido

                if (formValido) {
                    val divida = DividaModel(
                        nomeCompleto = nomeCompleto.text,
                        cpfCnpj = cpfCnpj.text,
                        telefone = telefone.text,
                        endereco = endereco.text,
                        valorDivida = valorDivida.text.toDouble(),
                        dataVencimento = dataVencimento,
                        status = status,
                        sazonalidade = sazonalidade,
                        dataPagamento = if (status == "paga") dataPagamento else null,
                        juros = juros.text.toInt(),
                        descricao = descricao.text
                    )
                    onSave(divida)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Dívida")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    initialDate: Long,
    onDateSelected: (Long) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed: Boolean by interactionSource.collectIsPressedAsState()

    var selectedDate by rememberSaveable {
        mutableStateOf(Date(initialDate).toFormattedString())
    }

    val context = LocalContext.current

    val calendar = Calendar.getInstance().apply { timeInMillis = initialDate }
    val year: Int = calendar.get(Calendar.YEAR)
    val month: Int = calendar.get(Calendar.MONTH)
    val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val newDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            selectedDate = "${month.toMonthName()} $dayOfMonth, $year"
            onDateSelected(newDate.timeInMillis)
        },
        year,
        month,
        day
    )

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        value = selectedDate,
        onValueChange = {},
        trailingIcon = {
            Icon(
                Icons.Filled.DateRange,
                contentDescription = "Selecionar data",
                modifier = Modifier.clickable { datePickerDialog.show() }
            )
        },
        interactionSource = interactionSource
    )

    if (isPressed) {
        datePickerDialog.show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusDropdown(
    currentStatus: String,
    onStatusSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("pendente", "paga", "atrasada")

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = currentStatus.replaceFirstChar { it.uppercaseChar() },
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            statusOptions.forEach { status ->
                DropdownMenuItem(
                    text = { Text(text = status.replaceFirstChar { it.uppercaseChar() }) },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SazonalidadeDropdown(
    currentValue: String,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("mensal", "anual", "semanal", "diária", "quinzenal")

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = currentValue.replaceFirstChar { it.uppercaseChar() },
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.replaceFirstChar { it.uppercaseChar() }) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun Int.toMonthName(): String {
    return DateFormatSymbols().months[this]
}

fun Date.toFormattedString(): String {
    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return simpleDateFormat.format(this)
}
