// components/DividaCard.kt
package com.example.kredithai.presentations.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kredithai.data.models.DividaModel
import java.text.SimpleDateFormat
import java.util.*

@Preview(showBackground = true)
@Composable
fun DividaCardPreview() {
    // Criando dados mockados para o preview
    val mockDivida = DividaModel(
        nomeCompleto = "João da Silva",
        cpfCnpj = "123.456.789-00",
        telefone = "(11) 98765-4321",
        endereco = "Rua Exemplo, 123",
        valorDivida = 1250.75,
        dataVencimento = System.currentTimeMillis() + (86400000 * 7), // 7 dias no futuro
        status = "pendente",
        sazonalidade = "mensal",
        juros = 5,
        descricao = "Empréstimo pessoal contratado em Janeiro/2023"
    )

    MaterialTheme {
        DividaCard(
            divida = mockDivida,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun DividaCard(
    divida: DividaModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val statusColor = when (divida.status.lowercase()) {
        "paga" -> Color(0xFF4CAF50)
        "atrasada" -> Color(0xFFF44336)
        else -> MaterialTheme.colorScheme.primary
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val vencimento = dateFormat.format(Date(divida.dataVencimento))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = divida.nomeCompleto ?: "Sem nome",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "CPF/CNPJ: ${divida.cpfCnpj ?: "Não informado"}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )

            RowInfo(label = "Valor:", value = "R$ %.2f".format(divida.valorDivida))
            RowInfo(label = "Vencimento:", value = vencimento)
            RowInfo(label = "Status:", value = divida.status, valueColor = statusColor)
            RowInfo(label = "Juros:", value = "${divida.juros}%")

            divida.descricao?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun RowInfo(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}