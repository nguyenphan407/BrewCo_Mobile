package com.example.brewco.data

import android.content.Context
import android.util.Log
import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.CreateOrderResponse
import com.example.brewco.data.dto.UpdateOrderStatusRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object OrderStatusUpdater {
    const val STATUS_WAIT_PAYMENT = 0
    const val STATUS_WAIT_DELIVERY = 1
    const val STATUS_DONE = 2
    const val STATUS_CANCELLED = 3

    private const val AUTH_PREFS = "auth_prefs"
    private const val AUTH_TOKEN_KEY = "auth_token"

    fun updateWithContext(
        context: Context,
        orderId: String,
        status: Int,
        onResult: (Boolean) -> Unit = {}
    ) {
        val prefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
        val token = prefs.getString(AUTH_TOKEN_KEY, null)
        if (token.isNullOrBlank()) {
            Log.w("OrderStatusUpdater", "Missing auth token, cannot update status")
            onResult(false)
            return
        }
        updateWithToken(token, orderId, status, onResult)
    }

    fun updateWithToken(
        token: String,
        orderId: String,
        status: Int,
        onResult: (Boolean) -> Unit = {}
    ) {
        ApiClient.apiService.updateOrderStatus(
            token = "Bearer $token",
            orderId = orderId,
            request = UpdateOrderStatusRequest(status)
        ).enqueue(object : Callback<CreateOrderResponse> {
            override fun onResponse(
                call: Call<CreateOrderResponse>,
                response: Response<CreateOrderResponse>
            ) {
                val body = response.body()
                val matchesRequestedStatus = body?.data?.orderStatus == status
                val success = response.isSuccessful && (body?.error == false || matchesRequestedStatus)
                if (!success) {
                    Log.w(
                        "OrderStatusUpdater",
                        "Status update ambiguous (code=${response.code()}, error=${body?.error}), assuming ${if (matchesRequestedStatus) "success" else "failure"}"
                    )
                }
                onResult(success || matchesRequestedStatus)
            }

            override fun onFailure(call: Call<CreateOrderResponse>, t: Throwable) {
                Log.e("OrderStatusUpdater", "Update status error", t)
                onResult(false)
            }
        })
    }
}