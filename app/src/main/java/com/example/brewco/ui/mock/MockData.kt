package com.example.brewco.ui.mock

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.brewco.data.dto.ProductResponse
import com.example.brewco.data.dto.UserProfileResponse
import com.example.brewco.data.models.CartItem
import com.example.brewco.data.models.Voucher
import java.util.UUID

data class MockProductDetail(
    val product: ProductResponse,
    val isNew: Boolean = false,
    val tastingNotes: List<String> = emptyList()
)

object MockCatalog {
    private val details = listOf(
        MockProductDetail(
            product = ProductResponse(
                id = "prd_xoai_granola",
                name = "Smoothie Xoài Nhiệt Đới Granola",
                description = "Sinh tố xoài kết hợp granola giòn tan, mang đến trải nghiệm vừa mát lạnh vừa bổ dưỡng cho ngày mới.",
                price = 65000.0,
                imageUrl = "https://images.unsplash.com/photo-1464306076886-da185f6a9d12?auto=format&fit=crop&w=600&q=80",
                categoryId = 1,
                categoryName = "Món mới"
            ),
            isNew = true,
            tastingNotes = listOf("Xoài", "Granola")
        ),
        MockProductDetail(
            product = ProductResponse(
                id = "prd_phuc_bon_tu",
                name = "Smoothie Phúc Bồn Tử Granola",
                description = "Vị chua ngọt của phúc bồn tử kết hợp cùng granola giúp cân bằng năng lượng và tạo cảm giác sảng khoái.",
                price = 65000.0,
                imageUrl = "https://images.unsplash.com/photo-1470337458703-46ad1756a187?auto=format&fit=crop&w=600&q=80",
                categoryId = 1,
                categoryName = "Món mới"
            ),
            isNew = true,
            tastingNotes = listOf("Phúc bồn tử", "Granola")
        ),
        MockProductDetail(
            product = ProductResponse(
                id = "prd_cloud_tea",
                name = "CloudTea Oolong Sương Sáo",
                description = "Trà sữa béo mịn đi cùng sương sáo thơm mát, phù hợp cho mọi tâm trạng.",
                price = 55000.0,
                imageUrl = "https://images.unsplash.com/photo-1498804103079-a6351b050096?auto=format&fit=crop&w=600&q=80",
                categoryId = 2,
                categoryName = "CloudTea"
            ),
            tastingNotes = listOf("Trà oolong", "Sương sáo", "Kem cheese")
        ),
        MockProductDetail(
            product = ProductResponse(
                id = "prd_cloud_fee",
                name = "CloudFee Caramel",
                description = "Cà phê robusta pha lạnh kết hợp caramel cháy nhẹ mang lại hậu vị ngọt ngào.",
                price = 59000.0,
                imageUrl = "https://images.unsplash.com/photo-1511920170033-f8396924c348?auto=format&fit=crop&w=600&q=80",
                categoryId = 3,
                categoryName = "CloudFee"
            ),
            tastingNotes = listOf("Caramel", "Cà phê lạnh")
        ),
        MockProductDetail(
            product = ProductResponse(
                id = "prd_cold_brew",
                name = "Cold Brew Cam Sả",
                description = "Cold brew đậm đà kết hợp cam vàng và sả tươi mang lại cảm giác sảng khoái.",
                price = 52000.0,
                imageUrl = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=600&q=80",
                categoryId = 4,
                categoryName = "Món nóng"
            )
        ),
        MockProductDetail(
            product = ProductResponse(
                id = "prd_hi_tea",
                name = "Hi-Tea Vải Nhài",
                description = "Trà trái cây vị vải, chút hương nhài cùng topping thạch trái cây house-made.",
                price = 57000.0,
                imageUrl = "https://images.unsplash.com/photo-1432107294467-7fe5e74cbb34?auto=format&fit=crop&w=600&q=80",
                categoryId = 5,
                categoryName = "Hi-Tea"
            ),
            tastingNotes = listOf("Vải", "Hoa nhài")
        ),
        MockProductDetail(
            product = ProductResponse(
                id = "prd_packaged",
                name = "CloudFee Đóng Chai",
                description = "Phiên bản đóng chai tiện lợi, giữ trọn hương vị để bạn mang đi bất kỳ đâu.",
                price = 45000.0,
                imageUrl = "https://images.unsplash.com/photo-1481391032119-d89fee407e44?auto=format&fit=crop&w=600&q=80",
                categoryId = 6,
                categoryName = "Đóng gói"
            )
        )
    )

    private val detailMap = details.associateBy { it.product.id }

    fun featuredProducts(): List<ProductResponse> = details.filter { it.isNew }.map { it.product }

    fun productsByCategory(categoryId: Long): List<ProductResponse> = details
        .filter { it.product.categoryId == categoryId }
        .map { it.product }

    fun getDetail(productId: String): MockProductDetail? = detailMap[productId]

    fun allProducts(): List<ProductResponse> = details.map { it.product }
}

object MockCartStore {
    private val cartItems = mutableStateListOf<CartItem>().apply {
        addAll(defaultItems())
    }

    fun observeCartItems(): SnapshotStateList<CartItem> = cartItems

    fun addProduct(product: ProductResponse, quantity: Int) {
        val existingIndex = cartItems.indexOfFirst { it.id == product.id }
        if (existingIndex >= 0) {
            val current = cartItems[existingIndex]
            cartItems[existingIndex] = current.copy(quantity = current.quantity + quantity)
        } else {
            cartItems += CartItem(
                id = product.id,
                productName = product.name,
                size = "L",
                quantity = quantity,
                price = product.price.toInt(),
                toppings = listOf("Trân châu trắng"),
                note = "Không ống hút nhựa",
                entryId = UUID.randomUUID().toString()
            )
        }
    }

    fun updateQuantity(entryId: String, newQuantity: Int) {
        val index = cartItems.indexOfFirst { it.entryId == entryId }
        if (index >= 0) {
            val current = cartItems[index]
            cartItems[index] = current.copy(quantity = newQuantity.coerceAtLeast(1))
        }
    }

    fun removeItems(entryIds: Collection<String>) {
        if (entryIds.isEmpty()) return
        val set = entryIds.toSet()
        cartItems.removeAll { it.entryId in set }
    }

    private fun defaultItems(): List<CartItem> {
        return MockCatalog.featuredProducts().take(2).mapIndexed { idx, product ->
            CartItem(
                id = product.id,
                productName = product.name,
                size = if (idx % 2 == 0) "M" else "L",
                quantity = idx + 1,
                price = product.price.toInt(),
                toppings = if (idx % 2 == 0) listOf("Thạch trái cây") else listOf("Kem cheese"),
                note = if (idx % 2 == 0) "Ít đá" else "Không đường",
                entryId = UUID.randomUUID().toString()
            )
        }
    }
}

object MockProfileStore {
    val profile = UserProfileResponse(
        id = "mock-user",
        email = "mock@brewco.com",
        fullName = "BrewCo Mockingbird",
        company = "BrewCo Tower",
        phoneNumber = "0909 123 456",
        roles = listOf("CUSTOMER"),
        emailVerifiedAt = null,
        blockedAt = null,
        createdAt = "2024-01-01",
        updatedAt = "2024-01-01"
    )
}

object MockVoucherStore {
    val vouchers = listOf(
        Voucher(
            id = "voucher_1",
            title = "Giảm 20% đơn giao tận nơi",
            type = "DELIVERY",
            discount = "20%",
            expiry = "31/12/2025"
        ),
        Voucher(
            id = "voucher_2",
            title = "Giảm 10% đơn mang đi",
            type = "PICKUP",
            discount = "10%",
            expiry = "30/06/2026"
        )
    )
}
