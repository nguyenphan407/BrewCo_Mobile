package com.example.brewco.repository

import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.CategoryRequest
import com.example.brewco.data.dto.CategoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository(private val apiClient: ApiClient = ApiClient) {

    private fun <T> requireSuccess(response: retrofit2.Response<T>, errorPrefix: String): T {
        if (!response.isSuccessful) {
            throw IllegalStateException("$errorPrefix: ${response.code()}")
        }
        return response.body() ?: throw IllegalStateException("Phản hồi rỗng")
    }

    suspend fun getCategories(): Result<List<CategoryResponse>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.getCategories().execute()
            requireSuccess(response, "Lỗi lấy danh mục").data.orEmpty()
        }
    }

    suspend fun createCategory(request: CategoryRequest): Result<CategoryResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.createCategory(request).execute()
            requireSuccess(response, "Lỗi tạo danh mục")
        }
    }

    suspend fun updateCategory(categoryId: String, request: CategoryRequest): Result<CategoryResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiClient.apiService.updateCategory(categoryId, request).execute()
            requireSuccess(response, "Lỗi cập nhật danh mục")
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
