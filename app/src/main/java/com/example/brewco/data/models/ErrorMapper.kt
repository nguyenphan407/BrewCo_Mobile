package com.example.brewco.data.models

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorMapper {
    fun toMessage(code: Int? = null, throwable: Throwable? = null): String {
        return when {
            throwable is UnknownHostException -> "Không thể kết nối máy chủ"
            throwable is SocketTimeoutException -> "Kết nối hết thời gian chờ"
            throwable is IOException -> "Lỗi mạng, vui lòng thử lại"
            code in 500..599 -> "Máy chủ đang gặp sự cố"
            code == 401 -> "Phiên đăng nhập đã hết hạn"
            code == 403 -> "Bạn không có quyền thực hiện thao tác này"
            code == 404 -> "Không tìm thấy dữ liệu"
            else -> throwable?.message ?: "Đã có lỗi xảy ra"
        }
    }
}
