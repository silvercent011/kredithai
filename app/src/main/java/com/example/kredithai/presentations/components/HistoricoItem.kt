package com.example.kredithai.presentations.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kredithai.data.models.DividaModel
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun HistoricoItem(
    divida: DividaModel,               // Recebe os dados da dívida
    dateFormat: SimpleDateFormat,      // Formatador de datas
    onItemClick: () -> Unit,           // Callback para clique
    modifier: Modifier = Modifier      // Modificador padrão
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onItemClick          // Abre o modal ao clicar
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Linha superior: Nome e Valor
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = divida.nomeCompleto ?: "Sem nome",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "R$ %.2f".format(divida.valorDivida),
                    style = MaterialTheme.typography.titleMedium,
                    color = when (divida.status) {
                        "paga" -> Color(0xFF4CAF50)       // Verde para pago
                        "atrasada" -> Color(0xFFF44336)   // Vermelho para atrasado
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Linha inferior: Data e Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormat.format(Date(divida.dataVencimento)),
                    style = MaterialTheme.typography.bodySmall
                )

                // Badge de status colorido
                Badge(
                    containerColor = when (divida.status) {
                        "paga" -> Color(0xFFE8F5E9)
                        "atrasada" -> Color(0xFFFFEBEE)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = when (divida.status) {
                        "paga" -> Color(0xFF2E7D32)
                        "atrasada" -> Color(0xFFC62828)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                ) {
                    Text(divida.status.capitalize())
                }
            }
        }
    }
}