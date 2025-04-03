package com.example.kredithai.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.kredithai.data.models.DividaModel

@Dao
interface DividaDao {

    @Insert
    suspend fun insert(divida: DividaModel)

    @Update
    suspend fun update(divida: DividaModel)

    @Delete
    suspend fun delete(divida: DividaModel)

    @Query("SELECT * FROM dividas")
    suspend fun getAllDividas(): List<DividaModel>

    @Query("SELECT * FROM dividas WHERE uid = :id LIMIT 1")
    suspend fun getDividaById(id: Int): DividaModel?

    @Query("SELECT * FROM dividas WHERE status = :status")
    suspend fun getDividasByStatus(status: String): List<DividaModel>

    @Query("SELECT * FROM dividas WHERE data_vencimento < :currentTime")
    suspend fun getDividasVencidas(currentTime: Long): List<DividaModel>

    @Query("SELECT * FROM dividas WHERE status = 'paga'")
    suspend fun getDividasPagas(): List<DividaModel>

    @Query("SELECT * FROM dividas WHERE sazonalidade = :sazonalidade")
    suspend fun getDividasBySazonalidade(sazonalidade: String): List<DividaModel>
}
