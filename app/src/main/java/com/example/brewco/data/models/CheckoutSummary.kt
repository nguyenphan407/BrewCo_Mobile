package com.example.brewco.data.models

import androidx.annotation.Keep

@Keep
data class CheckoutSummary(
    val orderId: String?,
    val items: List<CartItem>
) {
    val totalPrice: Int = items.sumOf { it.price * it.quantity }
}
