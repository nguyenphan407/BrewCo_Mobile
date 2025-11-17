package com.example.brewco.data.api

import com.example.brewco.data.dto.CategoryListResponse
import com.example.brewco.data.dto.CategoryRequest
import com.example.brewco.data.dto.CategoryResponse
import com.example.brewco.data.dto.ProductCreateRequest
import com.example.brewco.data.dto.ProductDetailResponse
import com.example.brewco.data.dto.ProductListResponse
import com.example.brewco.data.dto.ProductResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Chỉ giữ các endpoint phục vụ BookedScreen + admin product/category.
 */
interface ApiService {
    // Must try products
    @GET("api/products/must-try")
    fun getMustTryProducts(): Call<List<ProductResponse>>

    // Product detail
    @GET("api/products/id/{productId}")
    fun getProductDetail(@Path("productId") productId: String): Call<com.example.brewco.data.dto.ProductDetailResponse>

    // Products
    @GET("api/products")
    fun getProductsPaginated(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String? = null
    ): Call<ProductListResponse>

    @GET("api/products")
    fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String? = null
    ): Call<ProductListResponse>

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
    fun createProduct(@Body request: ProductCreateRequest): Call<ProductResponse>

    @PUT("api/products/id/{productId}")
    fun updateProduct(
        @Path("productId") productId: String,
        @Body request: ProductCreateRequest
    ): Call<ProductResponse>

    @DELETE("api/products/id/{productId}")
    fun deleteProduct(@Path("productId") productId: String): Call<ResponseBody>

    // Categories
    @GET("api/categories")
    fun getCategories(): Call<CategoryListResponse>

    @POST("api/categories")
    fun createCategory(@Body request: CategoryRequest): Call<CategoryResponse>

    @PUT("api/categories/{id}")
    fun updateCategory(
        @Path("id") categoryId: String,
        @Body request: CategoryRequest
    ): Call<CategoryResponse>

    @DELETE("api/categories/{id}")
    fun deleteCategory(@Path("id") categoryId: String): Call<ResponseBody>
}
