package com.example.kredithai.data.models

data class SimulacaoInput(
    val valorOriginal: Double = 0.0,
    val dataVencimento: Long = System.currentTimeMillis(),
    val taxaJuros: Int = 0,
    val multaAtraso: Double = 0.0
)
