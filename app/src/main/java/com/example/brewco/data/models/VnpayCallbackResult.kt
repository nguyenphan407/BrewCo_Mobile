package com.example.brewco.data.models

import androidx.annotation.Keep

@Keep
data class VnpayCallbackResult(
    val responseCode: String?,
    val orderInfo: String?,
    val amountVnd: Int?,
    val transactionNo: String?,
    val txnRef: String?,
    val bankCode: String?,
    val payDate: String?,
    val message: String
) {
    val isSuccess: Boolean
        get() = responseCode == "00"
}
