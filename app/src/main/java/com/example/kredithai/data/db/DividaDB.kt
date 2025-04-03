package com.example.kredithai.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.kredithai.data.dao.DividaDao
import com.example.kredithai.data.models.DividaModel

@Database(entities = [DividaModel::class], version = 1)
abstract class DividaDB : RoomDatabase() {
    abstract fun dividaDao(): DividaDao
}