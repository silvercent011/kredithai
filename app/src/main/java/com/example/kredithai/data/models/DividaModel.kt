package com.example.kredithai.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dividas")
data class DividaModel(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,

    @ColumnInfo(name = "nome_completo") val nomeCompleto: String?,
    @ColumnInfo(name = "cpf_cnpj") val cpfCnpj: String?,
    @ColumnInfo(name = "telefone") val telefone: String?,
    @ColumnInfo(name = "endereco") val endereco: String?,
    @ColumnInfo(name = "valor_divida") val valorDivida: Double,
    @ColumnInfo(name = "data_vencimento") val dataVencimento: Long, // Armazenando como timestamp (data em milissegundos)
    @ColumnInfo(name = "status") val status: String, // "pendente", "paga", "atrasada"
    @ColumnInfo(name = "sazonalidade") val sazonalidade: String, // mensal, anual, semanal, diária, quinzenal, etc.
    @ColumnInfo(name = "data_pagamento") val dataPagamento: Long? = null, // Data do pagamento, pode ser nula se a dívida não foi paga
    @ColumnInfo(name = "descricao") val descricao: String? = null, // Detalhes adicionais sobre a dívida
    @ColumnInfo(name = "juros") val juros: Int? = 0, // Taxa de juros da dívida
)
