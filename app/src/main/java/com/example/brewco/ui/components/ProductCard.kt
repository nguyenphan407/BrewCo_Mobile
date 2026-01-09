package com.example.brewco.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.brewco.data.dto.ProductResponse

@Composable
fun ProductCard(product: ProductResponse) {
    // TODO: render product card
    Text(text = product.name)
}
