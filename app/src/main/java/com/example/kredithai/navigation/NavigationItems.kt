package com.example.kredithai.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    companion object {
        fun getBottomNavigationItems() = listOf(
            NavigationItem("Home", Icons.Default.Home, Routes.HOME),
            NavigationItem("Dívidas", Icons.Default.Payments, Routes.DIVIDAS),
            NavigationItem("Histórico", Icons.Default.History, Routes.HISTORICO),
            NavigationItem("Simulação.", Icons.Default.AttachMoney, Routes.SIMULACAO)
        )
    }
}