package com.example.brewco.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.brewco.R
import com.example.brewco.data.AuthManager
import com.example.brewco.data.CartManager
import com.example.brewco.data.OrderStatusUpdater
import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.PaymentRequest
import com.example.brewco.data.dto.PaymentResponse
import com.example.brewco.data.dto.UserProfileResponse
import com.example.brewco.data.models.CartItem
import com.example.brewco.data.models.CheckoutSummary
import com.example.brewco.data.models.Voucher
import com.example.brewco.ui.mock.MockCartStore
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import com.example.brewco.ui.theme.HighlandWhite
import com.example.brewco.utils.FormatUtils
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    checkoutSummary: CheckoutSummary,
    appliedVoucher: Voucher? = null,
    onVoucherApplied: (Voucher?) -> Unit = {},
    onBackClick: () -> Unit = {},
    onNavigateToMain: () -> Unit = {},
    onSelectVoucher: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val authManager = remember { AuthManager.getInstance(context) }
    val cartManager = remember { CartManager.getInstance() }
    val checkoutEntryIds = remember(checkoutSummary) { checkoutSummary.items.map { it.entryId } }
    val targetOrderIds = remember(checkoutSummary) { checkoutSummary.targetOrderIds }
    val primaryOrderId = remember(targetOrderIds) { targetOrderIds.firstOrNull() }
    val paymentReferenceId = checkoutSummary.paymentReferenceId

    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var showPaymentMethodSheet by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentError by remember { mutableStateOf<String?>(null) }
    var orderNote by remember { mutableStateOf("") }
    var userProfile by remember { mutableStateOf<UserProfileResponse?>(null) }
    var isLoadingProfile by remember { mutableStateOf(true) }
    var profileError by remember { mutableStateOf<String?>(null) }

    val paymentMethodSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val discountPercent = remember(appliedVoucher) {
        appliedVoucher?.discount?.replace("%", "")?.trim()?.toDoubleOrNull()?.coerceAtLeast(0.0)
            ?: 0.0
    }
    val discountAmount = remember(discountPercent, checkoutSummary.totalPrice) {
        val raw = ((checkoutSummary.totalPrice * discountPercent) / 100.0).roundToInt()
        raw.coerceIn(0, checkoutSummary.totalPrice)
    }
    val finalTotal = remember(discountAmount, checkoutSummary.totalPrice) {
        checkoutSummary.totalPrice - discountAmount
    }
    val payableAmount = remember(finalTotal) { finalTotal.coerceAtLeast(0) }

    LaunchedEffect(Unit) {
        val token = authManager.getAuthToken()
        if (token.isNullOrBlank()) {
            profileError = "Vui lòng đăng nhập để tiếp tục"
            isLoadingProfile = false
            return@LaunchedEffect
        }

        ApiClient.apiService.getCurrentUser("Bearer $token")
            .enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    isLoadingProfile = false
                    if (response.isSuccessful) {
                        userProfile = response.body()
                        profileError = null
                    } else {
                        profileError = "Không thể tải thông tin người dùng"
                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    isLoadingProfile = false
                    profileError = "Lỗi kết nối: ${t.localizedMessage}"
                    Log.e("PaymentScreen", "Load profile error", t)
                }
            })
    }

            val displayName = userProfile?.fullName ?: authManager.getSavedFullName() ?: "Khách hàng"
            val displayPhone = userProfile?.phoneNumber ?: authManager.getSavedPhone() ?: "Chưa cập nhật"
            val displayAddress = userProfile?.company ?: "BrewCo Tower, TP. HCM"

            fun updateOrdersSequentially(
                token: String,
                orderIds: List<String>,
                onComplete: (Boolean) -> Unit
            ) {
                if (orderIds.isEmpty()) {
                    onComplete(false)
                    return
                }

                fun updateAt(index: Int) {
                    if (index >= orderIds.size) {
                        onComplete(true)
                        return
                    }
                    val orderId = orderIds[index]
                    OrderStatusUpdater.updateWithToken(
                        token = token,
                        orderId = orderId,
                        status = OrderStatusUpdater.STATUS_DONE
                    ) { success ->
                        if (success) {
                            updateAt(index + 1)
                        } else {
                            onComplete(false)
                        }
                    }
                }

                updateAt(0)
            }

    if (showSuccessDialog) {
        PaymentSuccessDialog(
            onDismiss = {
                showSuccessDialog = false
                onNavigateToMain()
            }
        )
    }

    if (showPaymentMethodSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaymentMethodSheet = false },
            sheetState = paymentMethodSheetState,
            containerColor = HighlandWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                SheetHeader(
                    title = "Phương thức thanh toán",
                    onClose = {
                        coroutineScope.launch {
                            paymentMethodSheetState.hide()
                            showPaymentMethodSheet = false
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PaymentMethod.values().forEach { method ->
                    PaymentOptionRow(
                        method = method,
                        isSelected = selectedPaymentMethod == method,
                        onSelect = {
                            selectedPaymentMethod = method
                            coroutineScope.launch {
                                paymentMethodSheetState.hide()
                                showPaymentMethodSheet = false
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Xác nhận đơn hàng",
                            color = HighlandText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = HighlandText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HighlandWhite)
            )
        },
        containerColor = HighlandWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            DeliveryInfoSection(
                name = displayName,
                phone = displayPhone,
                address = displayAddress
            )

            if (isLoadingProfile) {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(24.dp),
                    color = HighlandRed,
                    strokeWidth = 2.dp
                )
            }

            profileError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = HighlandRed,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SelectedProductsSection(checkoutSummary.items)

            Spacer(modifier = Modifier.height(16.dp))

            OrderNoteSection(
                note = orderNote,
                onNoteChange = { orderNote = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CostSummarySection(
                total = checkoutSummary.totalPrice,
                discountAmount = discountAmount,
                appliedVoucher = appliedVoucher,
                finalTotal = finalTotal,
                onSelectVoucher = onSelectVoucher,
                onRemoveVoucher = { onVoucherApplied(null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentMethodSection(
                selectedMethod = selectedPaymentMethod,
                onSelectPaymentMethod = { showPaymentMethodSheet = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Button(
                    onClick = {
                        if (isProcessingPayment) return@Button

                        if (checkoutSummary.items.isEmpty()) {
                            paymentError = "Giỏ hàng đang trống"
                            return@Button
                        }

                        val authToken = authManager.getAuthToken()?.takeIf { it.isNotBlank() }
                        if (authToken == null) {
                            paymentError = "Vui lòng đăng nhập lại"
                            return@Button
                        }

                        val orderId = primaryOrderId?.takeIf { it.isNotBlank() }
                            ?: paymentReferenceId?.takeIf { it.isNotBlank() }
                            ?: run {
                                paymentError = "Không xác định được mã đơn hàng"
                                return@Button
                            }
                        val orderIdsForStatus = targetOrderIds
                        if (orderIdsForStatus.isEmpty()) {
                            paymentError = "Không xác định được mã đơn hàng"
                            return@Button
                        }

                        fun cleanupAfterPayment() {
                            MockCartStore.removeItems(checkoutEntryIds)
                            cartManager.removeItems(checkoutEntryIds)
                            appliedVoucher?.let { voucher ->
                                ApiClient.apiService.deleteVoucher(
                                    "Bearer $authToken",
                                    voucher.id
                                ).enqueue(object : Callback<Void> {
                                    override fun onResponse(
                                        call: Call<Void>,
                                        response: Response<Void>
                                    ) {
                                        if (!response.isSuccessful) {
                                            Log.w(
                                                "PaymentScreen",
                                                "Delete voucher failed ${response.code()}"
                                            )
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        Log.e("PaymentScreen", "Delete voucher error", t)
                                    }
                                })
                            }
                            onVoucherApplied(null)
                        }

                        paymentError = null
                        isProcessingPayment = true

                        if (selectedPaymentMethod == PaymentMethod.CASH) {
                            updateOrdersSequentially(
                                token = authToken,
                                orderIds = orderIdsForStatus
                            ) { success ->
                                isProcessingPayment = false
                                if (success) {
                                    cleanupAfterPayment()
                                    showSuccessDialog = true
                                } else {
                                    paymentError = "Không thể cập nhật trạng thái đơn hàng"
                                }
                            }
                            return@Button
                        }

                        val paymentRequest = PaymentRequest(
                            amount = payableAmount,
                            orderId = orderId,
                            orderInfo = if (!checkoutSummary.comboId.isNullOrBlank()) {
                                "Combo ${checkoutSummary.comboId} (${orderIdsForStatus.joinToString()})"
                            } else {
                                "BrewCo order $orderId"
                            }
                        )

                        ApiClient.apiService.payWithVnpay(paymentRequest)
                            .enqueue(object : Callback<PaymentResponse> {
                                override fun onResponse(
                                    call: Call<PaymentResponse>,
                                    response: Response<PaymentResponse>
                                ) {
                                    isProcessingPayment = false
                                    if (response.isSuccessful) {
                                        val paymentResponse = response.body()
                                        val successCode = paymentResponse?.code?.lowercase()
                                        if (successCode == "00" || successCode == "ok") {
                                            cleanupAfterPayment()
                                            val paymentLink = paymentResponse?.paymentUrl
                                            if (!paymentLink.isNullOrBlank()) {
                                                try {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentLink))
                                                    context.startActivity(intent)
                                                } catch (ex: Exception) {
                                                    paymentError = "Không thể mở liên kết thanh toán"
                                                    Log.e("PaymentScreen", "Open payment url error", ex)
                                                }
                                            } else {
                                                paymentError = "Không nhận được liên kết thanh toán"
                                            }
                                        } else {
                                            paymentError = "Thanh toán thất bại: ${paymentResponse?.message ?: "Lỗi không xác định"}"
                                        }
                                    } else {
                                        paymentError = "Thanh toán thất bại (${response.code()}): ${response.message()}"
                                    }
                                }

                                override fun onFailure(
                                    call: Call<PaymentResponse>,
                                    t: Throwable
                                ) {
                                    isProcessingPayment = false
                                    paymentError = "Lỗi thanh toán: ${t.localizedMessage}"
                                    Log.e("PaymentScreen", "Payment Failure", t)
                                }
                            })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HighlandRed),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isProcessingPayment
                ) {
                    if (isProcessingPayment) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = HighlandWhite,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (selectedPaymentMethod == PaymentMethod.CASH) "Đặt hàng" else "Thanh toán",
                            color = HighlandWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                paymentError?.let { message ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        color = HighlandRed,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(HighlandRed.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DeliveryInfoSection(
    name: String,
    phone: String,
    address: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = HighlandRed.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Giao hàng tận nơi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighlandText
                )
                TextButton(onClick = {  }) {
                    Text(text = "Thay đổi", color = HighlandRed, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "$name | $phone",
                fontSize = 15.sp,
                color = HighlandText
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = address,
                fontSize = 14.sp,
                color = HighlandText.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Thời gian giao dự kiến: 15 - 30 phút",
                fontSize = 14.sp,
                color = HighlandText
            )
        }
    }
}

@Composable
private fun SelectedProductsSection(items: List<CartItem>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = HighlandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sản phẩm đã chọn",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighlandText
                )
                Text(text = "${items.size} món", color = HighlandText.copy(alpha = 0.7f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cup_of_cf),
                        contentDescription = "Cup",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "x${item.quantity} ${item.productName}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = HighlandText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.size,
                            fontSize = 13.sp,
                            color = HighlandText.copy(alpha = 0.7f)
                        )
                    }
                    Text(
                        text = FormatUtils.formatPrice(item.price * item.quantity),
                        fontSize = 14.sp,
                        color = HighlandText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderNoteSection(
    note: String,
    onNoteChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = HighlandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Ghi chú đơn hàng",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HighlandText
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ví dụ: Giao trước 12h, không ống hút nhựa...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HighlandRed,
                    unfocusedBorderColor = HighlandText.copy(alpha = 0.3f)
                ),
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun CostSummarySection(
    total: Int,
    discountAmount: Int,
    appliedVoucher: Voucher?,
    finalTotal: Int,
    onSelectVoucher: () -> Unit,
    onRemoveVoucher: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = HighlandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Tổng quan chi phí",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HighlandText
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow(label = "Thành tiền", value = FormatUtils.formatPrice(total))
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            SummaryRow(label = "Phí giao hàng", value = FormatUtils.formatPrice(0))
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSelectVoucher)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Khuyến mãi", fontSize = 15.sp, color = HighlandText)
                    Text(
                        text = appliedVoucher?.title ?: "Chưa áp dụng",
                        fontSize = 13.sp,
                        color = HighlandText.copy(alpha = 0.7f)
                    )
                }
                if (appliedVoucher != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "-${FormatUtils.formatPrice(discountAmount)}",
                            color = HighlandRed,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onRemoveVoucher) {
                            Text(text = "Bỏ", color = HighlandRed)
                        }
                    }
                } else {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = HighlandText.copy(alpha = 0.4f)
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            SummaryRow(
                label = "Số tiền thanh toán",
                value = FormatUtils.formatPrice(finalTotal.coerceAtLeast(0)),
                emphasize = true
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, emphasize: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (emphasize) 16.sp else 14.sp,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal,
            color = HighlandText
        )
        Text(
            text = value,
            fontSize = if (emphasize) 18.sp else 14.sp,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Medium,
            color = if (emphasize) HighlandRed else HighlandText
        )
    }
}

@Composable
private fun PaymentMethodSection(
    selectedMethod: PaymentMethod,
    onSelectPaymentMethod: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = HighlandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Thanh toán",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HighlandText
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectPaymentMethod() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = selectedMethod.iconRes),
                        contentDescription = "Method",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = selectedMethod.label,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = HighlandText
                    )
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = HighlandText.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
private fun PaymentOptionRow(
    method: PaymentMethod,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = HighlandRed,
                unselectedColor = HighlandText.copy(alpha = 0.4f)
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Image(
            painter = painterResource(id = method.iconRes),
            contentDescription = method.label,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = method.label,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = HighlandText
        )
    }
}

@Composable
private fun SheetHeader(
    title: String,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = HighlandText
        )
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Đóng",
                tint = HighlandText
            )
        }
    }
}

@Composable
private fun PaymentSuccessDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HighlandWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đóng",
                        tint = HighlandRed
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Đặt hàng thành công!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighlandText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Đơn hàng của bạn đang được chuẩn bị.",
                    fontSize = 15.sp,
                    color = HighlandText.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = HighlandRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Về trang chủ", color = HighlandWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private enum class PaymentMethod(val label: String, val iconRes: Int) {
    CASH("Tiền mặt", R.drawable.ic_cash),
    VNPAY("VNPAY", R.drawable.ic_vnpay)
}
