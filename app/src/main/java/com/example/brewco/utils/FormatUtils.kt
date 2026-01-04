package com.example.brewco.utils

object FormatUtils {
    fun formatPrice(price: Int): String {
        val priceStr = price.toString()
        return when {
            priceStr.length > 3 -> "${priceStr.substring(0, priceStr.length - 3)}.${priceStr.substring(priceStr.length - 3)}đ"
            else -> "${priceStr}đ"
        }
    }
} 