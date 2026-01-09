package com.example.brewco.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed class NavigationItem(val route: String) {
    data object ORDER : NavigationItem("order")
    data object ADMIN : NavigationItem("admin")
}

@Composable
fun BottomNavBar(currentItem: NavigationItem, onNavigate: (NavigationItem) -> Unit) {
    // TODO: implement navigation bar
    Text(text = "BottomNav placeholder: ${currentItem.route}")
}
