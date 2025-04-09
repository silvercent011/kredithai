import com.example.kredithai.data.models.SimulacaoInput
import java.util.concurrent.TimeUnit

object SimulacaoCalculator {
    fun calcular(
        input: SimulacaoInput,
        dataPagamento: Long
    ): SimulacaoResult {

        val diasAtraso = if (dataPagamento > input.dataVencimento) {
            TimeUnit.MILLISECONDS.toDays(dataPagamento - input.dataVencimento).toInt()
        } else {
            0
        }

        val isMultaPercentual = input.multaAtraso < 1.0

        val valorMulta = if (diasAtraso > 0) {
            if (isMultaPercentual) {
                input.valorOriginal * input.multaAtraso
            } else {
                input.multaAtraso // valor fixo
            }
        } else {
            0.0
        }

        val valorJuros = if (diasAtraso > 0) {
            input.valorOriginal * (Math.pow(1 + input.taxaJuros / 100.0, diasAtraso / 30.0) - 1)
        } else {
            0.0
        }

        val valorTotal = input.valorOriginal + valorJuros + valorMulta

        return SimulacaoResult(
            valorOriginal = input.valorOriginal,
            valorJuros = valorJuros,
            valorMulta = valorMulta,
            valorTotal = valorTotal,
            diasAtraso = diasAtraso,
            dataVencimento = input.dataVencimento,
            dataPagamento = dataPagamento
        )
    }
}

data class SimulacaoResult(
    val valorOriginal: Double,
    val valorJuros: Double,
    val valorMulta: Double,
    val valorTotal: Double,
    val diasAtraso: Int,
    val dataVencimento: Long,
    val dataPagamento: Long
)