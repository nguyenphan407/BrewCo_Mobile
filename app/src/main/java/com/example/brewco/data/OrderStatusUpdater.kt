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

    private const val TAG = "OrderStatusUpdater"

    private fun bearer(rawToken: String) = "Bearer $rawToken"

    fun updateWithContext(
        context: Context,
        orderId: String,
        status: Int,
        onResult: (Boolean) -> Unit = {}
    ) {
        val token = AuthManager.getInstance(context).getAuthToken()
        if (token.isNullOrBlank()) {
            Log.w(TAG, "Missing auth token, cannot update status")
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
            token = bearer(token),
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
                        TAG,
                        "Status update ambiguous (code=${response.code()}, error=${body?.error}), assuming ${if (matchesRequestedStatus) "success" else "failure"}"
                    )
                }
                onResult(success || matchesRequestedStatus)
            }

            override fun onFailure(call: Call<CreateOrderResponse>, t: Throwable) {
                Log.e(TAG, "Update status error", t)
                onResult(false)
            }
        })
    }
}
