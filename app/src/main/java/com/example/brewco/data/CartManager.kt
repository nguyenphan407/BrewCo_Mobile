package com.example.brewco.data

import com.example.brewco.data.models.CartItem
import com.example.brewco.data.models.Order
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class CartManager private constructor() {
    private val currentItems: SnapshotStateList<CartItem> = mutableStateListOf()
    private val orders: SnapshotStateList<Order> = mutableStateListOf()
    private var nextOrderId = 1

    fun addToCart(item: CartItem) {
        val existingIndex = currentItems.indexOfFirst { it.isSameLineAs(item) }
        if (existingIndex >= 0) {
            val existing = currentItems[existingIndex]
            val merged = existing.copy(quantity = existing.quantity + item.quantity)
            currentItems[existingIndex] = merged
        } else {
            currentItems.add(item)
        }
    }

    fun updateQuantity(entryId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItems(listOf(entryId))
            return
        }
        val index = currentItems.indexOfFirst { it.entryId == entryId }
        if (index >= 0) {
            currentItems[index] = currentItems[index].copy(quantity = newQuantity)
        }
    }

    fun incrementQuantity(entryId: String) {
        val index = currentItems.indexOfFirst { it.entryId == entryId }
        if (index >= 0) {
            val item = currentItems[index]
            currentItems[index] = item.copy(quantity = item.quantity + 1)
        }
    }

    fun decrementQuantity(entryId: String) {
        val index = currentItems.indexOfFirst { it.entryId == entryId }
        if (index >= 0) {
            val item = currentItems[index]
            if (item.quantity > 1) {
                currentItems[index] = item.copy(quantity = item.quantity - 1)
            } else {
                currentItems.removeAt(index)
            }
        }
    }

    fun getCartItems(): List<CartItem> = currentItems.toList()
    
    fun observeCartItems(): SnapshotStateList<CartItem> = currentItems

    fun replaceCartItems(newItems: List<CartItem>) {
        currentItems.clear()
        currentItems.addAll(newItems)
    }

    fun removeItems(entryIds: Collection<String>) {
        if (entryIds.isEmpty()) return
        val idSet = entryIds.toSet()
        currentItems.removeAll { it.entryId in idSet }
    }

    fun removeItem(entryId: String) {
        currentItems.removeAll { it.entryId == entryId }
    }

    fun getTotalItemCount(): Int = currentItems.sumOf { it.quantity }

    fun getTotalPrice(): Int = currentItems.sumOf { it.price * it.quantity }

    fun isEmpty(): Boolean = currentItems.isEmpty()

    fun getItemByEntryId(entryId: String): CartItem? {
        return currentItems.find { it.entryId == entryId }
    }

    fun createOrderDraft(customerName: String, phoneNumber: String): Order {
        val itemsSnapshot = currentItems.toList()
        val total = itemsSnapshot.sumOf { it.price * it.quantity }
        return Order(
            id = nextOrderId++,
            customerName = customerName,
            phoneNumber = phoneNumber,
            items = itemsSnapshot,
            totalAmount = total
        )
    }

    fun addOrder(order: Order) {
        orders.add(order)
    }

    fun getOrders(): List<Order> = orders.toList()
    
    fun getOrderCount(): Int = orders.size
    
    fun clearCart() {
        currentItems.clear()
    }

    fun clearAllOrders() {
        orders.clear()
        nextOrderId = 1
    }

    private fun CartItem.isSameLineAs(other: CartItem): Boolean {
        return id == other.id &&
            size == other.size &&
            toppings == other.toppings &&
            note == other.note
    }

    companion object {
        @Volatile
        private var instance: CartManager? = null

        fun getInstance(): CartManager {
            return instance ?: synchronized(this) {
                instance ?: CartManager().also { instance = it }
            }
        }
    }
}
