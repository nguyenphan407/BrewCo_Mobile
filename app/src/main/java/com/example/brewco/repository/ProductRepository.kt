package com.example.brewco.repository

import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.ProductCreateRequest
import com.example.brewco.data.dto.ProductDetailResponse
import com.example.brewco.data.dto.ProductResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val apiClient: ApiClient = ApiClient) {

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
        
        @Volatile
        private var instance: ProductRepository? = null
        
        fun getInstance(): ProductRepository {
            return instance ?: synchronized(this) {
                instance ?: ProductRepository().also { instance = it }
            }
        }
    }

    private fun <T> requireSuccess(response: retrofit2.Response<T>, errorPrefix: String): T {
        if (!response.isSuccessful) {
            throw IllegalStateException("$errorPrefix: ${response.code()}")
        }
        return response.body() ?: throw IllegalStateException("Phản hồi rỗng")
    }

    suspend fun getProducts(page: Int = 0, size: Int = DEFAULT_PAGE_SIZE): Result<List<ProductResponse>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.getProducts(page = page, size = size).execute()
            requireSuccess(response, "Lỗi lấy sản phẩm").data?.items.orEmpty()
        }
    }

    suspend fun getProductById(productId: String): Result<ProductDetailResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.getProductDetail(productId).execute()
            requireSuccess(response, "Lỗi lấy chi tiết sản phẩm")
        }
    }

    suspend fun getProductsByCategory(categoryId: Long, page: Int = 0, size: Int = DEFAULT_PAGE_SIZE): Result<List<ProductResponse>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.getProductsByCategory(categoryId, page, size).execute()
            requireSuccess(response, "Lỗi lấy sản phẩm theo danh mục").data?.items.orEmpty()
        }
    }

    suspend fun searchProducts(
        query: String,
        categoryId: Long? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): Result<List<ProductResponse>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.searchProducts(
                name = query,
                categoryId = categoryId,
                minPrice = minPrice,
                maxPrice = maxPrice
            ).execute()
            requireSuccess(response, "Lỗi tìm kiếm sản phẩm").data?.items.orEmpty()
        }
    }

    suspend fun createProduct(request: ProductCreateRequest): Result<ProductDetailResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.createProduct(request).execute()
            requireSuccess(response, "Lỗi tạo sản phẩm")
        }
    }

    suspend fun updateProduct(productId: String, request: ProductCreateRequest): Result<ProductDetailResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.updateProduct(productId, request).execute()
            requireSuccess(response, "Lỗi cập nhật sản phẩm")
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.deleteProduct(productId).execute()
            if (!response.isSuccessful) {
                throw IllegalStateException("Lỗi xóa sản phẩm: ${response.code()}")
            }
        }
    }

    suspend fun getMustTryProducts(): Result<List<ProductResponse>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.getMustTryProducts().execute()
            if (response.isSuccessful) {
                response.body().orEmpty()
            } else {
                throw IllegalStateException("Lỗi lấy sản phẩm nổi bật: ${response.code()}")
            }
        }
    }
}
