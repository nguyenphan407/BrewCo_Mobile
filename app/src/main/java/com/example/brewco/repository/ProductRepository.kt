package com.example.brewco.repository

import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.ProductCreateRequest
import com.example.brewco.data.dto.ProductDetailResponse
import com.example.brewco.data.dto.ProductResponse
import com.example.brewco.data.models.ErrorMapper
import com.example.brewco.data.models.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ProductRepository(private val apiClient: ApiClient = ApiClient) {

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
        
        @Volatile
        private var instance: ProductRepository? = null

        private const val CACHE_MAX_PAGE = 1 // chỉ cache trang đầu để đơn giản
        

    @Volatile
    private var mustTryCache: List<ProductResponse>? = null

    @Volatile
    private var categoryProductCache: MutableMap<Long, List<ProductResponse>> = mutableMapOf()
        fun getInstance(): ProductRepository {
            return instance ?: synchronized(this) {
                instance ?: ProductRepository().also { instance = it }
            }
        }
    }

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
                NetworkResult.Error(response.code(), ErrorMapper.toMessage(response.code()))
            }
        } catch (e: Exception) {
            NetworkResult.Error(message = ErrorMapper.toMessage(throwable = e), throwable = e)
        }
    }

    suspend fun getProducts(page: Int = 0, size: Int = DEFAULT_PAGE_SIZE): NetworkResult<List<ProductResponse>> =
        withContext(Dispatchers.IO) {
            executeRequest(
                errorPrefix = "Lỗi lấy sản phẩm",
                call = { apiClient.apiService.getProducts(page = page, size = size).execute() },
                mapper = { it.data?.items.orEmpty() }
            )
        }

    suspend fun getProductById(productId: String): NetworkResult<ProductDetailResponse> = withContext(Dispatchers.IO) {
        executeRequest(
            errorPrefix = "Lỗi lấy chi tiết sản phẩm",
            call = { apiClient.apiService.getProductDetail(productId).execute() },
            mapper = { it }
        )
    }

    suspend fun getProductsByCategory(
        categoryId: Long,
        page: Int = 0,
        size: Int = DEFAULT_PAGE_SIZE
    ): NetworkResult<List<ProductResponse>> = withContext(Dispatchers.IO) {
        categoryProductCache[categoryId]?.takeIf { page <= CACHE_MAX_PAGE }?.let {
            return@withContext NetworkResult.Success(it)
        }

        val result = executeRequest(
            errorPrefix = "Lỗi lấy sản phẩm theo danh mục",
            call = { apiClient.apiService.getProductsByCategory(categoryId, page, size).execute() },
            mapper = { it.data?.items.orEmpty() }
        )

        if (result is NetworkResult.Success && page <= CACHE_MAX_PAGE) {
            categoryProductCache[categoryId] = result.data
        }

        result
    }

    suspend fun searchProducts(
        query: String,
        categoryId: Long? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): NetworkResult<List<ProductResponse>> = withContext(Dispatchers.IO) {
        executeRequest(
            errorPrefix = "Lỗi tìm kiếm sản phẩm",
            call = {
                apiClient.apiService.searchProducts(
                    name = query,
                    categoryId = categoryId,
                    minPrice = minPrice,
                    maxPrice = maxPrice
                ).execute()
            },
            mapper = { it.data?.items.orEmpty() }
        )
    }

    suspend fun createProduct(request: ProductCreateRequest): NetworkResult<ProductDetailResponse> =
        withContext(Dispatchers.IO) {
            executeRequest(
                errorPrefix = "Lỗi tạo sản phẩm",
                call = { apiClient.apiService.createProduct(request).execute() },
                mapper = { it }
            )
        }

    suspend fun updateProduct(productId: String, request: ProductCreateRequest): NetworkResult<ProductDetailResponse> =
        withContext(Dispatchers.IO) {
            executeRequest(
                errorPrefix = "Lỗi cập nhật sản phẩm",
                call = { apiClient.apiService.updateProduct(productId, request).execute() },
                mapper = { it }
            )
        }

    suspend fun deleteProduct(productId: String): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        executeRequest(
            errorPrefix = "Lỗi xóa sản phẩm",
            call = { apiClient.apiService.deleteProduct(productId).execute() },
            mapper = { }
        )
    }

    suspend fun getMustTryProducts(forceRefresh: Boolean = false): NetworkResult<List<ProductResponse>> = withContext(Dispatchers.IO) {
        mustTryCache?.takeIf { !forceRefresh }?.let { return@withContext NetworkResult.Success(it) }

        val result = executeRequest(
            errorPrefix = "Lỗi lấy sản phẩm nổi bật",
            call = { apiClient.apiService.getMustTryProducts().execute() },
            mapper = { it.orEmpty() }
        )

        if (result is NetworkResult.Success) {
            mustTryCache = result.data
        }

        result
    }
}
