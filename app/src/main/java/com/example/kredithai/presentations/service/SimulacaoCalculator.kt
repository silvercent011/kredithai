import com.example.kredithai.data.models.SimulacaoInput
import java.util.concurrent.TimeUnit

object SimulacaoCalculator {
    fun calcular(
        input: SimulacaoInput,
        valorPagamento: Double,
        dataPagamento: Long
    ): SimulacaoResult {
        val diasAtraso = if (dataPagamento > input.dataVencimento) {
            TimeUnit.MILLISECONDS.toDays(dataPagamento - input.dataVencimento).toInt()
        } else {
            0
        }

        // Calcular multa (pode ser valor fixo ou porcentagem)
        val valorMulta = if (input.multaAtraso > 1) {
            input.multaAtraso // assume valor fixo se > 1
        } else {
            valorPagamento * input.multaAtraso // assume porcentagem se <= 1
        }

        // Calcular juros (ao dia)
        val jurosDiario = input.taxaJuros.toDouble() / 30 / 100 // converte juros mensal para diário
        val valorJuros = if (diasAtraso > 0) {
            valorPagamento * jurosDiario * diasAtraso
        } else {
            0.0
        }

        val valorTotal = valorPagamento + valorJuros + if (diasAtraso > 0) valorMulta else 0.0

        return SimulacaoResult(
            valorOriginal = input.valorOriginal,
            valorPagamento = valorPagamento,
            valorJuros = valorJuros,
            valorMulta = if (diasAtraso > 0) valorMulta else 0.0,
            valorTotal = valorTotal,
            diasAtraso = diasAtraso,
            dataVencimento = input.dataVencimento,
            dataPagamento = dataPagamento
        )
    }
}

data class SimulacaoResult(
    val valorOriginal: Double,
    val valorPagamento: Double,
    val valorJuros: Double,
    val valorMulta: Double,
    val valorTotal: Double,
    val diasAtraso: Int,
    val dataVencimento: Long,
    val dataPagamento: Long
)