package com.example.brewco.data

import com.example.brewco.data.models.WishlistItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WishlistManager private constructor() {
    private val _wishlistItems = MutableStateFlow<List<WishlistItem>>(emptyList())
    val wishlistItems: StateFlow<List<WishlistItem>> = _wishlistItems.asStateFlow()

    fun addItem(item: WishlistItem) {
        val currentItems = _wishlistItems.value.toMutableList()
        if (!currentItems.any { it.id == item.id }) {
            currentItems.add(item)
            _wishlistItems.value = currentItems
        }
    }

    fun removeItem(itemId: String) {
        val currentItems = _wishlistItems.value.toMutableList()
        currentItems.removeAll { it.id == itemId }
        _wishlistItems.value = currentItems
    }

    fun isItemInWishlist(itemId: String): Boolean {
        return _wishlistItems.value.any { it.id == itemId }
    }

    companion object {
        @Volatile
        private var instance: WishlistManager? = null

        fun getInstance(): WishlistManager {
            return instance ?: synchronized(this) {
                instance ?: WishlistManager().also { instance = it }
            }
        }
    }
} 