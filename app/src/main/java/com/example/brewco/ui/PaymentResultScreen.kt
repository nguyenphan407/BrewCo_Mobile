package com.example.brewco.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
//import androidx.compose.material.icons.filled.Error
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.data.models.VnpayCallbackResult
import com.example.brewco.ui.theme.CafeBrown
import com.example.brewco.ui.theme.HighlandWhite
import com.example.brewco.utils.FormatUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PaymentResultScreen(
    result: VnpayCallbackResult,
    onBackHome: () -> Unit
) {
    val accentColor = if (result.isSuccess) Color(0xFF2E7D32) else Color(0xFFC62828)
    val icon = if (result.isSuccess) Icons.Default.CheckCircle else Icons.Default.CheckCircle
    val title = if (result.isSuccess) "Thanh toán thành công" else "Thanh toán không thành công"
    val subtitle = if (result.isSuccess) {
        "Cafe Brewco đã nhận được thanh toán của bạn qua VNPAY. Đơn hàng sẽ được xử lý ngay."
    } else {
        result.message.ifBlank { "Vui lòng thử lại hoặc chọn phương thức khác." }
    }

    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HighlandWhite)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .size(96.dp),
            tint = accentColor
        )
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = CafeBrown,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = CafeBrown.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            val dateTime = runCatching {
                val raw = result.payDate?.trim()
                raw?.let { if (it.isBlank()) null else LocalDateTime.parse(raw, formatter) }
            }.getOrNull()

            Column(modifier = Modifier.padding(20.dp)) {
                DetailRow(
                    label = "Mã giao dịch",
                    value = result.transactionNo ?: result.txnRef ?: "Không có"
                )
                DetailRow(
                    label = "Đơn hàng",
                    value = result.orderInfo ?: "Không xác định"
                )
                DetailRow(
                    label = "Số tiền",
                    value = result.amountVnd?.let { FormatUtils.formatPrice(it) } ?: "Không rõ"
                )
                DetailRow(
                    label = "Ngân hàng",
                    value = result.bankCode ?: "VNPAY"
                )
                DetailRow(
                    label = "Thời gian",
                    value = dateTime?.format(displayFormatter) ?: "Chưa cung cấp"
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onBackHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CafeBrown),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (result.isSuccess) "Về trang chủ" else "Thử lại",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = CafeBrown.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = CafeBrown
        )
    }
}