package com.example.kredithai.presentations.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.kredithai.data.db.DividaDB
import com.example.kredithai.data.models.DividaModel

@Composable
fun DividasScreen(db:DividaDB) {
    var dividas by remember { mutableStateOf<List<DividaModel>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()

    // Usando LaunchedEffect para executar a função de forma assíncrona
    LaunchedEffect(Unit) {
        // Executar a função suspend dentro da corrotina
        try {
            val dao = db.dividaDao()
            dividas = dao.getAllDividas()
        } catch (e: Exception) {
            Log.e("DividasScreen", "Erro ao buscar as dívidas", e)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = dividas.toString(), style = MaterialTheme.typography.headlineLarge
        )

    }
}