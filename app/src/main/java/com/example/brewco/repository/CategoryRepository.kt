package com.example.brewco.repository

import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.CategoryRequest
import com.example.brewco.data.dto.CategoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository(private val apiClient: com.example.brewco.data.api.ApiClient = ApiClient) {

    suspend fun getCategories(): Result<List<CategoryResponse>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.getCategories().execute()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw IllegalStateException("Lỗi lấy danh mục: ${response.code()}")
            }
        }
    }

    suspend fun createCategory(request: CategoryRequest): Result<CategoryResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.createCategory(request).execute()
            if (response.isSuccessful) {
                response.body() ?: throw IllegalStateException("Phản hồi rỗng")
            } else {
                throw IllegalStateException("Lỗi tạo danh mục: ${response.code()}")
            }
        }
    }

    suspend fun updateCategory(categoryId: String, request: CategoryRequest): Result<CategoryResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.updateCategory(categoryId, request).execute()
            if (response.isSuccessful) {
                response.body() ?: throw IllegalStateException("Phản hồi rỗng")
            } else {
                throw IllegalStateException("Lỗi cập nhật danh mục: ${response.code()}")
            }
        }
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.deleteCategory(categoryId).execute()
            if (!response.isSuccessful) {
                throw IllegalStateException("Lỗi xóa danh mục: ${response.code()}")
            }
        }
    }
}
