package com.example.brewco.repository

import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.ProductCreateRequest
import com.example.brewco.data.dto.ProductResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val apiClient: com.example.brewco.data.api.ApiClient = ApiClient) {

    suspend fun getProducts(page: Int = 0, size: Int = 20): Result<List<ProductResponse>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.getProducts(page = page, size = size).execute()
            if (response.isSuccessful) {
                response.body()?.data?.items ?: emptyList()
            } else {
                throw IllegalStateException("Lỗi lấy sản phẩm: ${response.code()}")
            }
        }
    }

    suspend fun getProductsByCategory(categoryId: Long, page: Int = 0, size: Int = 20): Result<List<ProductResponse>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.getProductsByCategory(categoryId, page, size).execute()
            if (response.isSuccessful) {
                response.body()?.data?.items ?: emptyList()
            } else {
                throw IllegalStateException("Lỗi lấy sản phẩm theo danh mục: ${response.code()}")
            }
        }
    }

    suspend fun searchProducts(query: String): Result<List<ProductResponse>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.searchProducts(name = query).execute()
            if (response.isSuccessful) {
                response.body()?.data?.items ?: emptyList()
            } else {
                throw IllegalStateException("Lỗi tìm kiếm sản phẩm: ${response.code()}")
            }
        }
    }

    suspend fun createProduct(request: ProductCreateRequest): Result<ProductResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.createProduct(request).execute()
            if (response.isSuccessful) {
                response.body() ?: throw IllegalStateException("Phản hồi rỗng")
            } else {
                throw IllegalStateException("Lỗi tạo sản phẩm: ${response.code()}")
            }
        }
    }

    suspend fun updateProduct(productId: String, request: ProductCreateRequest): Result<ProductResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.updateProduct(productId, request).execute()
            if (response.isSuccessful) {
                response.body() ?: throw IllegalStateException("Phản hồi rỗng")
            } else {
                throw IllegalStateException("Lỗi cập nhật sản phẩm: ${response.code()}")
            }
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
}
