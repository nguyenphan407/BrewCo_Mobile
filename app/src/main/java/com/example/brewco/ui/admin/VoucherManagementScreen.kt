package com.example.brewco.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.brewco.ui.theme.CafeBeige
import com.example.brewco.ui.theme.CafeBrown
import com.example.brewco.ui.theme.CafeButtonBackground

data class VoucherFormState(
    val id: String? = null,
    val discountPercentage: String = "",
    val startDate: String = "",
    val endDate: String = ""
)

@Composable
fun VoucherDetailDialog(
    isVisible: Boolean,
    isEditing: Boolean = false,
    formState: VoucherFormState,
    isSubmitting: Boolean,
    onFormChange: (VoucherFormState) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    if (!isVisible) return

    Dialog(
        onDismissRequest = {
            if (!isSubmitting) {
                onDismiss()
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "Chỉnh sửa voucher" else "Tạo voucher mới",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = CafeBrown
                    )

                    IconButton(onClick = onDismiss, enabled = !isSubmitting) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = CafeBrown
                        )
                    }
                }

                if (isEditing && !formState.id.isNullOrEmpty()) {
                    OutlinedTextField(
                        value = formState.id,
                        onValueChange = {},
                        enabled = false,
                        label = { Text("ID voucher") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = CafeBrown.copy(alpha = 0.4f),
                            disabledLabelColor = CafeBrown.copy(alpha = 0.6f),
                            disabledTextColor = CafeBrown
                        )
                    )
                }

                OutlinedTextField(
                    value = formState.discountPercentage,
                    onValueChange = { onFormChange(formState.copy(discountPercentage = it)) },
                    label = { Text("Giảm giá (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    supportingText = {
                        Text(
                            text = "Theo tài liệu API chỉ hỗ trợ giảm theo phần trăm.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                )

                Text(
                    text = "Hiệu lực",
                    fontWeight = FontWeight.SemiBold,
                    color = CafeBrown
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = formState.startDate,
                        onValueChange = { onFormChange(formState.copy(startDate = it)) },
                        label = { Text("Ngày bắt đầu") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("dd/MM/yyyy") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = formState.endDate,
                        onValueChange = { onFormChange(formState.copy(endDate = it)) },
                        label = { Text("Ngày kết thúc") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("dd/MM/yyyy") },
                        singleLine = true
                    )
                }

                Text(
                    text = "Hệ thống backend sẽ tự sinh mã voucher (UUID).",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    color = Color.Gray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, enabled = !isSubmitting) {
                        Text(text = "Hủy", color = CafeBrown)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onSave,
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CafeButtonBackground,
                            contentColor = CafeBeige
                        )
                    ) {
                        Text(text = if (isEditing) "Cập nhật" else "Thêm mới")
                    }
                }
            }
        }
    }
}