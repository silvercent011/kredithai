package com.example.kredithai.presentations.components
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.kredithai.data.db.DividaDB
import com.example.kredithai.data.models.DividaModel

@Composable
fun DividaFormDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    onSave: (DividaModel) -> Unit,
    db:DividaDB
) {

    if (isOpen) {
        AlertDialog(
            onDismissRequest = { onClose() },
            title = {
                Text(text = "Cadastrar Dívida")
            },
            text = {
                DividaForm(onSave = onSave)
            },
            confirmButton = {
                Button(
                    onClick = {
                        val divida = DividaModel(
                            nomeCompleto = "Fulano de Tal",
                            cpfCnpj = "123.456.789-10",
                            telefone = "(11) 98765-4321",
                            endereco = "Rua Exemplo, 123",
                            valorDivida = 1000.0,
                            dataVencimento = System.currentTimeMillis() + 86400000, // 1 dia à frente
                            status = "pendente",
                            sazonalidade = "mensal",
                            juros = 5
                        )
                        onSave(divida) // Envia para salvar
                        onClose() // Fecha o Modal
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                Button(onClick = { onClose() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun DividaForm(
    onSave: (DividaModel) -> Unit
) {
    var nomeCompleto by remember { mutableStateOf(TextFieldValue("")) }
    var cpfCnpj by remember { mutableStateOf(TextFieldValue("")) }
    var telefone by remember { mutableStateOf(TextFieldValue("")) }
    var endereco by remember { mutableStateOf(TextFieldValue("")) }
    var valorDivida by remember { mutableStateOf(TextFieldValue("")) }
    var juros by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Campo: Nome Completo
        Text("Nome Completo")
        BasicTextField(
            value = nomeCompleto,
            onValueChange = { nomeCompleto = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // Campo: CPF/CNPJ
        Text("CPF ou CNPJ")
        BasicTextField(
            value = cpfCnpj,
            onValueChange = { cpfCnpj = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // Campo: Telefone
        Text("Telefone")
        BasicTextField(
            value = telefone,
            onValueChange = { telefone = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // Campo: Endereço
        Text("Endereço")
        BasicTextField(
            value = endereco,
            onValueChange = { endereco = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // Campo: Valor da Dívida
        Text("Valor da Dívida")
        BasicTextField(
            value = valorDivida,
            onValueChange = { valorDivida = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // Campo: Juros
        Text("Taxa de Juros (%)")
        BasicTextField(
            value = juros,
            onValueChange = { juros = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Criando o modelo de dívida e chamando a função onSave
                val divida = DividaModel(
                    nomeCompleto = nomeCompleto.text,
                    cpfCnpj = cpfCnpj.text,
                    telefone = telefone.text,
                    endereco = endereco.text,
                    valorDivida = valorDivida.text.toDoubleOrNull() ?: 0.0,
                    dataVencimento = System.currentTimeMillis() + 86400000, // 1 dia à frente
                    status = "pendente",
                    sazonalidade = "mensal",
                    juros = juros.text.toIntOrNull() ?: 0
                )
                onSave(divida)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Dívida")
        }
    }
}
