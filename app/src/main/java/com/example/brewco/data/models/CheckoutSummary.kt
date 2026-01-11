package com.example.brewco.data.models

import androidx.annotation.Keep

@Keep
data class CheckoutSummary(
    val orderId: String?,
    val items: List<CartItem>,
    val comboId: String? = null,
    val comboOrderIds: List<String> = emptyList()
) {
    val totalPrice: Int = items.sumOf { it.price * it.quantity }


    val targetOrderIds: List<String> = when {
        comboOrderIds.isNotEmpty() -> comboOrderIds
        !orderId.isNullOrBlank() -> listOf(orderId)
        else -> emptyList()
    }

   
    val paymentReferenceId: String? = comboId ?: orderId
}
