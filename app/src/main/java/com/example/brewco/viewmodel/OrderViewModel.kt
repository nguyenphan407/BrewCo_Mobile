package com.example.brewco.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.CategoryListResponse
import com.example.brewco.data.dto.CategoryResponse
import com.example.brewco.data.dto.ProductListResponse
import com.example.brewco.data.dto.ProductResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class OrderUiState(
    val isLoading: Boolean = false,
    val categories: List<CategoryResponse> = emptyList(),
    val allProducts: List<ProductResponse> = emptyList(),
    val productsByCategory: Map<Long, List<ProductResponse>> = emptyMap(),
    val mustTryProducts: List<ProductResponse> = emptyList(),
    val searchResults: List<ProductResponse> = emptyList(),
    val errorMessage: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val hasMorePages: Boolean = false
)

class OrderViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "OrderViewModel"
        private const val PAGE_SIZE = 20
    }

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        loadCategories()
        loadMustTryProducts()
        loadAllProducts()

        // Always load products for fixed category IDs (2-6) to populate sections
        val fixedCategoryIds = listOf(2L, 3L, 4L, 5L, 6L)
        fixedCategoryIds.forEach { categoryId ->
            loadProductsByCategory(categoryId)
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            ApiClient.apiService.getCategories().enqueue(object : Callback<CategoryListResponse> {
                override fun onResponse(
                    call: Call<CategoryListResponse>,
                    response: Response<CategoryListResponse>
                ) {
                    if (response.isSuccessful) {
                        val categories = response.body()?.data ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            categories = categories,
                            isLoading = false,
                            errorMessage = null
                        )
                        Log.d(TAG, "Loaded ${categories.size} categories from API")
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Không thể tải danh mục: ${response.code()}"
                        )
                        Log.e(TAG, "Failed to load categories: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<CategoryListResponse>, t: Throwable) {
                    Log.e(TAG, "Failed to load categories", t)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Lỗi kết nối: ${t.message}"
                    )
                }
            })
        }
    }

    fun loadMustTryProducts() {
        viewModelScope.launch {
            ApiClient.apiService.getMustTryProducts().enqueue(object : Callback<List<ProductResponse>> {
                override fun onResponse(
                    call: Call<List<ProductResponse>>,
                    response: Response<List<ProductResponse>>
                ) {
                    if (response.isSuccessful) {
                        val products = response.body() ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            mustTryProducts = products,
                            errorMessage = null
                        )
                        Log.d(TAG, "Loaded ${products.size} must-try products")
                    } else {
                        Log.e(TAG, "Failed to load must-try products: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<ProductResponse>>, t: Throwable) {
                    Log.e(TAG, "Failed to load must-try products", t)
                }
            })
        }
    }

    fun loadAllProducts(page: Int = 0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            ApiClient.apiService.getProductsPaginated(page, PAGE_SIZE).enqueue(object : Callback<ProductListResponse> {
                override fun onResponse(
                    call: Call<ProductListResponse>,
                    response: Response<ProductListResponse>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()?.data
                        if (data != null) {
                            val currentProducts = if (page == 0) emptyList() else _uiState.value.allProducts
                            _uiState.value = _uiState.value.copy(
                                allProducts = currentProducts + data.items,
                                currentPage = data.page,
                                totalPages = data.pages,
                                hasMorePages = data.page < data.pages - 1,
                                isLoading = false,
                                errorMessage = null
                            )
                            Log.d(TAG, "Loaded ${data.items.size} products, page ${data.page}/${data.pages}")
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Không thể tải sản phẩm: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<ProductListResponse>, t: Throwable) {
                    Log.e(TAG, "Failed to load products", t)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Lỗi kết nối: ${t.message}"
                    )
                }
            })
        }
    }

    fun loadProductsByCategory(categoryId: Long, page: Int = 0) {
        viewModelScope.launch {
            ApiClient.apiService.getProductsByCategory(categoryId, page, PAGE_SIZE)
                .enqueue(object : Callback<ProductListResponse> {
                    override fun onResponse(
                        call: Call<ProductListResponse>,
                        response: Response<ProductListResponse>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()?.data
                            if (data != null) {
                                val currentMap = _uiState.value.productsByCategory.toMutableMap()
                                val currentProducts = if (page == 0) emptyList() else currentMap[categoryId] ?: emptyList()
                                currentMap[categoryId] = currentProducts + data.items

                                _uiState.value = _uiState.value.copy(
                                    productsByCategory = currentMap,
                                    errorMessage = null
                                )
                                Log.d(TAG, "Loaded ${data.items.size} products for category $categoryId")
                            }
                        } else {
                            Log.e(TAG, "Failed to load products for category $categoryId: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<ProductListResponse>, t: Throwable) {
                        Log.e(TAG, "Failed to load products for category $categoryId", t)
                    }
                })
        }
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            ApiClient.apiService.searchProducts(name = query, page = 0, size = 50)
                .enqueue(object : Callback<ProductListResponse> {
                    override fun onResponse(
                        call: Call<ProductListResponse>,
                        response: Response<ProductListResponse>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()?.data
                            if (data != null) {
                                _uiState.value = _uiState.value.copy(
                                    searchResults = data.items,
                                    isLoading = false,
                                    errorMessage = null
                                )
                                Log.d(TAG, "Search found ${data.items.size} products")
                            }
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = "Không thể tìm kiếm: ${response.code()}"
                            )
                        }
                    }

                    override fun onFailure(call: Call<ProductListResponse>, t: Throwable) {
                        Log.e(TAG, "Failed to search products", t)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Lỗi tìm kiếm: ${t.message}"
                        )
                    }
                })
        }
    }

    fun clearSearchResults() {
        _uiState.value = _uiState.value.copy(searchResults = emptyList())
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (!currentState.isLoading && currentState.hasMorePages) {
            loadAllProducts(currentState.currentPage + 1)
        }
    }
}
