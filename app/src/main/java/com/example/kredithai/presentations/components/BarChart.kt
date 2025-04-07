package com.example.kredithai.presentations.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

data class BarChartItem(
    val label: String,
    val value: Double,
    val color: Color
)

@Composable
fun BarChart(
    items: List<BarChartItem>,
    maxValue: Double
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { item ->
            val percentage = (item.value / maxValue).coerceIn(0.0, 1.0)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(80.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                ) {
                    // Barra de fundo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )

                    // Barra de valor
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(percentage.toFloat())
                            .height(24.dp)
                            .background(
                                color = item.color,
                                shape = RoundedCornerShape(4.dp)
                            )
                    ) {
                        // Texto dentro da barra
                        if (percentage > 0.2) {
                            Text(
                                text = String.format("%.1f%%", percentage * 100),
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 8.dp)
                            )
                        }
                    }

                    // Texto fora da barra para barras pequenas
                    if (percentage <= 0.2) {
                        Text(
                            text = String.format("%.1f%%", percentage * 100),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 8.dp)
                        )
                    }
                }

                val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                Text(
                    text = formatter.format(item.value),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}