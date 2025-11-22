package com.example.brewco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewco.data.dto.CategoryResponse
import com.example.brewco.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Skeleton ViewModel cho màn quản lý danh mục (admin).
 * Logic load/CRUD sẽ được triển khai ở các phase sau.
 */
@Suppress("unused")
class AdminCategoryViewModel(
    private val categoryRepository: CategoryRepository = CategoryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminCategoryUiState())
    val uiState: StateFlow<AdminCategoryUiState> = _uiState

    fun loadCategories() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            // TODO: fetch categories using repository
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun setEditingCategory(category: CategoryResponse?) {
        _uiState.update { it.copy(editingCategory = category) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}

data class AdminCategoryUiState(
    val categories: List<CategoryResponse> = emptyList(),
    val editingCategory: CategoryResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
