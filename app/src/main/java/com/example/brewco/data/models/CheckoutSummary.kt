package com.example.brewco.data.models

data class CheckoutSummary(
    val orderId: String?,
    val items: List<CartItem>
) {
    val totalPrice: Int = items.sumOf { it.price * it.quantity }
}
