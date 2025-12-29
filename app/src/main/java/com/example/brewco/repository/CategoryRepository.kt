package com.example.brewco.repository

import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.CategoryRequest
import com.example.brewco.data.dto.CategoryResponse
import com.example.brewco.data.models.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class CategoryRepository(private val apiClient: ApiClient = ApiClient) {

    private fun <T, R> executeRequest(
        errorPrefix: String,
        call: () -> Response<T>,
        mapper: (T) -> R
    ): NetworkResult<R> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    NetworkResult.Success(mapper(body))
                } else {
                    NetworkResult.Error(response.code(), "Phản hồi rỗng")
                }
            } else {
                NetworkResult.Error(response.code(), "$errorPrefix: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(message = "$errorPrefix: ${e.message}", throwable = e)
        }
    }

    suspend fun getCategories(): NetworkResult<List<CategoryResponse>> = withContext(Dispatchers.IO) {
        executeRequest(
            errorPrefix = "Lỗi lấy danh mục",
            call = { apiClient.apiService.getCategories().execute() },
            mapper = { it.data.orEmpty() }
        )
    }

    suspend fun createCategory(request: CategoryRequest): NetworkResult<CategoryResponse> = withContext(Dispatchers.IO) {
        executeRequest(
            errorPrefix = "Lỗi tạo danh mục",
            call = { apiClient.apiService.createCategory(request).execute() },
            mapper = { it }
        )
    }

    suspend fun updateCategory(categoryId: String, request: CategoryRequest): NetworkResult<CategoryResponse> =
        withContext(Dispatchers.IO) {
            executeRequest(
                errorPrefix = "Lỗi cập nhật danh mục",
                call = { apiClient.apiService.updateCategory(categoryId, request).execute() },
                mapper = { it }
            )
        }

    suspend fun deleteCategory(categoryId: String): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        executeRequest(
            errorPrefix = "Lỗi xóa danh mục",
            call = { apiClient.apiService.deleteCategory(categoryId).execute() },
            mapper = { }
        )
    }
}
