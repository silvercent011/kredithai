package com.example.kredithai.presentations.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kredithai.navigation.NavigationItem

@Composable
fun AppBottomBar(navController: NavController) {
    val items = NavigationItem.getBottomNavigationItems()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        val selectedIndex = rememberSaveable { mutableIntStateOf(0) }

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    selectedIndex.intValue = index
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}