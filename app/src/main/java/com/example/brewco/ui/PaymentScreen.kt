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
    onNavigateToMain: () -> Unit = {}
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





private enum class PaymentMethod(val label: String, val iconRes: Int) {
    CASH("Tiền mặt", R.drawable.ic_cash),
    VNPAY("VNPAY", R.drawable.ic_vnpay)
}
