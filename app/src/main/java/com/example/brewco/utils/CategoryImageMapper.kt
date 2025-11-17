package com.example.brewco.utils

import com.example.brewco.R

/**
 * Map tên danh mục từ API sang drawable có sẵn.
 * Khi thêm danh mục mới, cập nhật bảng dưới đây.
 */
object CategoryImageMapper {
    // Sẽ cập nhật sau khi thêm drawable thực tế; hiện dùng placeholder ic_launcher_foreground
    private val mapping: Map<String, Int> = emptyMap()

    fun getImageResource(name: String): Int {
        return mapping[name] ?: R.drawable.ic_launcher_foreground
    }
}
