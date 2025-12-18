package com.example.brewco.data

import com.example.brewco.data.models.WishlistItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WishlistManager private constructor() {
    private val _wishlistItems = MutableStateFlow<List<WishlistItem>>(emptyList())
    val wishlistItems: StateFlow<List<WishlistItem>> = _wishlistItems.asStateFlow()

    fun addItem(item: WishlistItem) {
        _wishlistItems.update { current ->
            if (current.any { it.id == item.id }) current else current + item
        }
    }

    fun removeItem(itemId: String) {
        _wishlistItems.update { current -> current.filterNot { it.id == itemId } }
    }

    fun toggleItem(item: WishlistItem) {
        if (isItemInWishlist(item.id)) {
            removeItem(item.id)
        } else {
            addItem(item)
        }
    }

    fun isItemInWishlist(itemId: String): Boolean = _wishlistItems.value.any { it.id == itemId }

    fun getItemCount(): Int = _wishlistItems.value.size

    fun isEmpty(): Boolean = _wishlistItems.value.isEmpty()

    fun getItemById(itemId: String): WishlistItem? = _wishlistItems.value.find { it.id == itemId }

    fun clear() {
        _wishlistItems.value = emptyList()
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
