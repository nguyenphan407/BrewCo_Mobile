package com.example.brewco.ui

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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.brewco.R
import com.example.brewco.data.models.CartItem
import com.example.brewco.data.models.CheckoutSummary
import com.example.brewco.data.models.Voucher
import com.example.brewco.ui.mock.MockCartStore
import com.example.brewco.ui.mock.MockProfileStore
import com.example.brewco.ui.mock.MockVoucherStore
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import com.example.brewco.ui.theme.HighlandWhite
import com.example.brewco.utils.FormatUtils
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val scrollState = rememberScrollState()
    val profile = remember { MockProfileStore.profile }
    val entryIds = remember(checkoutSummary) { checkoutSummary.items.map { it.entryId } }

    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var showPaymentMethodSheet by remember { mutableStateOf(false) }
    var showVoucherSheet by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentError by remember { mutableStateOf<String?>(null) }

    val paymentMethodSheetState = rememberModalBottomSheetState()
    val voucherSheetState = rememberModalBottomSheetState()
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

    if (showVoucherSheet) {
        ModalBottomSheet(
            onDismissRequest = { showVoucherSheet = false },
            sheetState = voucherSheetState,
            containerColor = HighlandWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                SheetHeader(
                    title = "Chọn khuyến mãi",
                    onClose = {
                        coroutineScope.launch {
                            voucherSheetState.hide()
                            showVoucherSheet = false
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                MockVoucherStore.vouchers.forEach { voucher ->
                    VoucherOptionCard(
                        voucher = voucher,
                        isApplied = appliedVoucher?.id == voucher.id,
                        onSelect = {
                            onVoucherApplied(voucher)
                            coroutineScope.launch {
                                voucherSheetState.hide()
                                showVoucherSheet = false
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                TextButton(
                    onClick = {
                        onVoucherApplied(null)
                        coroutineScope.launch {
                            voucherSheetState.hide()
                            showVoucherSheet = false
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "Bỏ chọn", color = HighlandRed, fontWeight = FontWeight.Bold)
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
                name = profile.fullName ?: "Khách hàng",
                phone = profile.phoneNumber ?: "Chưa cập nhật",
                address = profile.company ?: "BrewCo Tower, TP. HCM"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SelectedProductsSection(checkoutSummary.items)

            Spacer(modifier = Modifier.height(16.dp))

            CostSummarySection(
                total = checkoutSummary.totalPrice,
                discountAmount = discountAmount,
                appliedVoucher = appliedVoucher,
                finalTotal = finalTotal,
                onSelectVoucher = { showVoucherSheet = true },
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
                        if (checkoutSummary.items.isEmpty()) {
                            paymentError = "Giỏ hàng đang trống"
                            return@Button
                        }
                        if (isProcessingPayment) return@Button

                        paymentError = null
                        isProcessingPayment = true
                        coroutineScope.launch {
                            delay(600)
                            MockCartStore.removeItems(entryIds)
                            onVoucherApplied(null)
                            isProcessingPayment = false
                            showSuccessDialog = true
                        }
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
                TextButton(onClick = { /* Mock screen */ }) {
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
private fun VoucherOptionCard(
    voucher: Voucher,
    isApplied: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isApplied) HighlandRed.copy(alpha = 0.1f) else HighlandWhite
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = voucher.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = HighlandText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Giảm ${voucher.discount} • HSD ${voucher.expiry}",
                fontSize = 13.sp,
                color = HighlandText.copy(alpha = 0.7f)
            )
        }
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
