package com.example.brewco.utils

import com.example.brewco.R

/**
 * Map tên danh mục từ API sang drawable có sẵn.
 * Khi thêm danh mục mới, cập nhật bảng dưới đây.
 */
object CategoryImageMapper {
    /**
     * Map category name từ API về drawable resource
     * Sử dụng pattern matching để xử lý các tên category khác nhau
     */
    fun getImageResource(categoryName: String): Int {
        return when {
            // Cà phê
            categoryName.contains("cà phê", ignoreCase = true) ||
            categoryName.contains("coffee", ignoreCase = true) ||
            categoryName.contains("cloudfee", ignoreCase = true) -> R.drawable.cloud_fee

            // Trà sữa
            categoryName.contains("trà sữa", ignoreCase = true) ||
            categoryName.contains("milk tea", ignoreCase = true) ||
            categoryName.contains("cloudtea", ignoreCase = true) -> R.drawable.cloud_tea

            // Trà trái cây / HiTea
            categoryName.contains("trà trái cây", ignoreCase = true) ||
            categoryName.contains("hi tea", ignoreCase = true) ||
            categoryName.contains("hitea", ignoreCase = true) ||
            categoryName.contains("fruit tea", ignoreCase = true) -> R.drawable.hi_tea

            // Món nóng
            categoryName.contains("nóng", ignoreCase = true) ||
            categoryName.contains("hot", ignoreCase = true) -> R.drawable.hot_fee

            // Món mới / Must try
            categoryName.contains("mới", ignoreCase = true) ||
            categoryName.contains("new", ignoreCase = true) ||
            categoryName.contains("must try", ignoreCase = true) -> R.drawable.mon_moi_phai_thu

            // Take away / Đóng gói
            categoryName.contains("đóng gói", ignoreCase = true) ||
            categoryName.contains("take away", ignoreCase = true) ||
            categoryName.contains("takeaway", ignoreCase = true) -> R.drawable.take_away_fee

            // Default fallback
            else -> R.drawable.cloud_fee
        }
    }

    /**
     * Map category ID từ backend về drawable resource
     * Sử dụng mapping cố định cho các ID đã biết
     */
    fun getImageResourceById(categoryId: Int): Int {
        return when (categoryId) {
            1 -> R.drawable.mon_moi_phai_thu
            2 -> R.drawable.cloud_tea
            3 -> R.drawable.cloud_fee
            4 -> R.drawable.hot_fee
            5 -> R.drawable.hi_tea
            6 -> R.drawable.take_away_fee
            else -> R.drawable.cloud_fee
        }
    }
}
