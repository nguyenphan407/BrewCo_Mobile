package com.example.brewco.data.dto

import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

@Keep
data class VoucherDTO(
    val id: String,
    val code: String? = null,
    val title: String? = null,
    val description: String? = null,
    @SerializedName("discountPercentage")
    val discountPercentage: Double,
    @SerializedName("discountAmount")
    val discountAmount: Double? = null,
    val minOrderAmount: Double? = null,
    val maxDiscountAmount: Double? = null,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("endDate")
    val endDate: String,
    val quantity: Int? = null,
    val used: Int? = null,
    val active: Boolean? = true,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null
) {
    fun getFormattedEndDate(): String {
        return try {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatter.format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(endDate) ?: return endDate)
        } catch (e: Exception) {
            endDate
        }
    }

    fun getDiscountText(): String {
        return if (discountPercentage > 0) {
            "${discountPercentage.toInt()}% giảm giá"
        } else if (discountAmount != null && discountAmount > 0) {
            "Giảm ${discountAmount.toInt()}₫"
        } else {
            "Khuyến mãi"
        }
    }

    fun getVoucherTitle(): String= title ?: code ?: "Voucher"
}

@Keep
data class VoucherResponse(
    val id: String,
    val discountPercentage: Double,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
)
