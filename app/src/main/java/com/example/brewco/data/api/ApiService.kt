package com.example.brewco.data.api

import com.example.brewco.data.dto.CategoryListResponse
import com.example.brewco.data.dto.CategoryRequest
import com.example.brewco.data.dto.CategoryResponse
import com.example.brewco.data.dto.CreateOrderRequest
import com.example.brewco.data.dto.CreateOrderResponse
import com.example.brewco.data.dto.ForgotPasswordRequest
import com.example.brewco.data.dto.LoginRequest
import com.example.brewco.data.dto.LoginResponse
import com.example.brewco.data.dto.OrderResponse
import com.example.brewco.data.dto.PaymentRequest
import com.example.brewco.data.dto.PaymentResponse
import com.example.brewco.data.dto.RegisterRequest
import com.example.brewco.data.dto.ResendOtpRequest
import com.example.brewco.data.dto.ResetPasswordRequest
import com.example.brewco.data.dto.UpdateOrderStatusRequest
import com.example.brewco.data.dto.ProductCreateRequest
import com.example.brewco.data.dto.ProductDetailResponse
import com.example.brewco.data.dto.ProductListResponse
import com.example.brewco.data.dto.ProductResponse
import com.example.brewco.data.dto.UserProfileResponse
import com.example.brewco.data.dto.VoucherRequest
import com.example.brewco.data.dto.VoucherResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * BrewCo API Service - All endpoints
 */
interface ApiService {
    // Auth endpoints
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
    
    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<Void>

    @GET("api/auth/email-verification/{id}")
    fun verifyOtp(@Path("id") id: String): Call<Void>
    
    @POST("api/auth/resend-otp")
    fun resendOtp(@Body request: ResendOtpRequest): Call<Void>
    
    @GET("api/auth/reset-password/{id}")
    fun forgotPassword(@Path("id") id: String): Call<Void>
    
    @POST("api/auth/reset-password/{email}")
    fun resetPassword(
        @Path("email") email: String,
        @Body request: ResetPasswordRequest
    ): Call<Void>
    
    @GET("api/auth/logout")
    fun logout(@Header("Authorization") token: String): Call<Void>

    @GET("api/account/me")
    fun getCurrentUser(@Header("Authorization") token: String): Call<UserProfileResponse>

    // Must try products
    @GET("api/products/must-try")
    fun getMustTryProducts(): Call<List<ProductResponse>>

    // Product detail
    @GET("api/products/id/{productId}")
    fun getProductDetail(@Path("productId") productId: String): Call<ProductDetailResponse>

    // Products
    @GET("api/products")
    fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String? = null
    ): Call<ProductListResponse>

    @GET("api/products")
    fun getProductsPaginated(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String? = null
    ): Call<ProductListResponse>

    // Legacy overload for admin filter (params + pageable JSON strings)
    @GET("api/products")
    fun getProducts(
        @Query("params") params: String,
        @Query("pageable") pageable: String
    ): Call<ResponseBody>

    @GET("api/products/category/{categoryId}")
    fun getProductsByCategory(
        @Path("categoryId") categoryId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Call<ProductListResponse>

    @GET("api/products/search")
    fun searchProducts(
        @Query("name") name: String? = null,
        @Query("categoryId") categoryId: Long? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Call<ProductListResponse>

    @POST("api/products")
    fun createProduct(@Body request: ProductCreateRequest): Call<ProductDetailResponse>

    @PUT("api/products/id/{productId}")
    fun updateProduct(
        @Path("productId") productId: String,
        @Body request: ProductCreateRequest
    ): Call<ProductDetailResponse>

    @DELETE("api/products/id/{productId}")
    fun deleteProduct(@Path("productId") productId: String): Call<ResponseBody>

    // Categories
    @GET("api/categories")
    fun getCategories(): Call<CategoryListResponse>

    @GET("api/categories/{id}")
    fun getCategoryDetail(@Path("id") categoryId: String): Call<CategoryResponse>

    @POST("api/categories")
    fun createCategory(@Body request: CategoryRequest): Call<CategoryResponse>

    @PUT("api/categories/{id}")
    fun updateCategory(
        @Path("id") categoryId: String,
        @Body request: CategoryRequest
    ): Call<CategoryResponse>

    @DELETE("api/categories/{id}")
    fun deleteCategory(@Path("id") categoryId: String): Call<ResponseBody>

    // Order endpoints
    @POST("api/order")
    fun createOrder(
        @Header("Authorization") token: String,
        @Body request: CreateOrderRequest
    ): Call<CreateOrderResponse>

    @GET("api/order/me")
    fun getMyOrders(
        @Header("Authorization") token: String
    ): Call<OrderResponse>

    @POST("api/order/{orderId}/status")
    fun updateOrderStatus(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String,
        @Body request: UpdateOrderStatusRequest
    ): Call<CreateOrderResponse>

    // Payment endpoints
    @POST("api/payment/vnpay")
    fun payWithVnpay(@Body paymentRequest: PaymentRequest): Call<PaymentResponse>

    // Voucher endpoints
    @GET("api/vouchers")
    fun getVouchers(): Call<List<VoucherResponse>>

    @GET("api/vouchers/{id}")
    fun getVoucherDetail(@Path("id") voucherId: String): Call<VoucherResponse>

    @POST("api/vouchers")
    fun createVoucher(
        @Header("Authorization") token: String,
        @Body request: VoucherRequest
    ): Call<VoucherResponse>

    @PUT("api/vouchers/{id}")
    fun updateVoucher(
        @Header("Authorization") token: String,
        @Path("id") voucherId: String,
        @Body request: VoucherRequest
    ): Call<VoucherResponse>

    @DELETE("api/vouchers/{id}")
    fun deleteVoucher(
        @Header("Authorization") token: String,
        @Path("id") voucherId: String
    ): Call<Void>
}
