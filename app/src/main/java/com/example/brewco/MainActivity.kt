package com.example.brewco

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.brewco.data.AuthManager
import com.example.brewco.data.OrderStatusUpdater
import com.example.brewco.data.models.CheckoutSummary
import com.example.brewco.data.models.VnpayCallbackResult
import com.example.brewco.data.models.Voucher
import com.example.brewco.ui.*
import com.example.brewco.ui.theme.BrewCoTheme

class MainActivity : ComponentActivity() {
    private var vnpayResult by mutableStateOf<VnpayCallbackResult?>(null)
    private var pendingVnpayOrderId: String? = null

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Xử lý deeplink khi khởi động
        handleDeepLink(intent)

        setContent {
            BrewCoTheme {
                var showSplash by remember { mutableStateOf(true) }
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
                var previousScreen by remember { mutableStateOf<Screen>(Screen.Login) }
                var emailForOtp by remember { mutableStateOf("") }
                var otpToken by remember { mutableStateOf("") }
                var productId by remember { mutableStateOf("") }
                var selectedOrder: CheckoutSummary? by remember { mutableStateOf(null) }
                var selectedVoucher: Voucher? by remember { mutableStateOf(null) }

                // Get AuthManager instance
                val authManager = remember { AuthManager.getInstance(this@MainActivity) }
                val deepLinkResult = vnpayResult
                val composeContext = LocalContext.current

                LaunchedEffect(deepLinkResult) {
                    deepLinkResult?.let {
                        selectedOrder = null
                        selectedVoucher = null
                        if (it.isSuccess) {
                            val orderIdForSync = pendingVnpayOrderId
                                ?: it.txnRef
                                ?: it.orderInfo
                            if (!orderIdForSync.isNullOrBlank()) {
                                OrderStatusUpdater.updateWithContext(
                                    context = composeContext,
                                    orderId = orderIdForSync,
                                    status = OrderStatusUpdater.STATUS_DONE
                                )
                                pendingVnpayOrderId = null
                            } else {
                                Log.w("Deeplink", "Không tìm được orderId trong callback VNPAY")
                            }
                        }
                        currentScreen = Screen.PaymentResult
                    }
                }

                AnimatedContent(
                    targetState = showSplash,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(800)) togetherWith
                                fadeOut(animationSpec = tween(800))
                    },
                    label = "splash_transition"
                ) { isSplashVisible ->
                    if (isSplashVisible) {
                        SplashScreen(onSplashFinished = { showSplash = false })
                    } else {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                when {
                                    targetState == Screen.Main && initialState == Screen.Login -> {
                                        (slideInHorizontally(animationSpec = tween(900)) { width -> width } +
                                                fadeIn(animationSpec = tween(900))) togetherWith
                                                (slideOutHorizontally(animationSpec = tween(900)) { width -> -width } +
                                                        fadeOut(animationSpec = tween(900)))
                                    }
                                    initialState == Screen.Login && targetState == Screen.Main -> {
                                        fadeIn(animationSpec = tween(800)) togetherWith
                                                fadeOut(animationSpec = tween(800))
                                    }
                                    targetState == Screen.Main && initialState == Screen.ProductDetail -> {
                                        fadeIn(animationSpec = tween(150)) togetherWith
                                                fadeOut(animationSpec = tween(150))
                                    }
                                    else -> {
                                        fadeIn(animationSpec = tween(500)) togetherWith
                                                fadeOut(animationSpec = tween(500))
                                    }
                                }
                            },
                            label = "screen_transition"
                        ) { screen ->
                            when (screen) {
                                Screen.Login -> LoginScreen(
                                    onForgotPasswordClick = { currentScreen = Screen.ForgotPassword },
                                    onSignUpClick = { currentScreen = Screen.SignUp },
                                    onLoginClick = {
                                        if (authManager.getSavedEmail() == "admin@example.com") {
                                            currentScreen = Screen.Admin
                                        } else {
                                            currentScreen = Screen.Main
                                        }

                                    }
                                )
                                Screen.ForgotPassword -> ForgotPasswordScreen(
                                    onBackToLogin = { currentScreen = Screen.Login },
                                    onSubmitEmail = { email ->
                                        emailForOtp = email
                                        currentScreen = Screen.OtpVerification
                                    }
                                )
                                Screen.OtpVerification -> OTP_FGPassScreen(
                                    emailAddress = emailForOtp,
                                    onBackClick = { currentScreen = Screen.ForgotPassword },
                                    onVerifyOtp = { otp ->
                                        otpToken = otp
                                        currentScreen = Screen.ChangePassword
                                    }
                                )
                                Screen.ChangePassword -> ChangePasswordScreen(
                                    email = emailForOtp,
                                    onBackClick = { currentScreen = Screen.OtpVerification },
                                    onChangePasswordSubmit = {
                                        currentScreen = Screen.Login
                                    }
                                )
                                Screen.SignUp -> SignUpScreen(
                                    onBackClick = { currentScreen = Screen.Login },
                                    onSignUpSubmit = { email ->
                                        emailForOtp = email
                                        currentScreen = Screen.OtpSignUp
                                    },
                                    onNavigateToOTP = { email ->
                                        emailForOtp = email
                                        currentScreen = Screen.OtpSignUp
                                    }
                                )
                                Screen.OtpSignUp -> OTP_SignUpScreen(
                                    emailAddress = emailForOtp,
                                    onBackClick = { currentScreen = Screen.SignUp },
                                    onVerifyOtp = { otp ->
                                        currentScreen = Screen.Login
                                    }
                                )
                                Screen.Admin -> AdminScreen(
                                    onLogoutClick = {
                                        currentScreen = Screen.Login
                                    }
                                )
                                Screen.Main -> MainScreen(
                                    onNotificationClick = { /* TODO */ },
                                    onMenuClick = { /* TODO */ },
                                    onNavigate = { destination ->
                                        when {
                                            destination == "differ" -> currentScreen = Screen.Differ
                                            destination == "order" -> currentScreen = Screen.Booked
                                            destination == "orders" -> currentScreen = Screen.Cart
                                            destination == "rewards" -> currentScreen = Screen.Coupon
                                            destination.startsWith("product/") -> {
                                                productId = destination.removePrefix("product/")
                                                currentScreen = Screen.ProductDetail
                                            }
                                            else -> { /* Handle other navigation */ }
                                        }
                                    },
                                    onNavigateToNoti = {
                                        previousScreen = Screen.Main
                                        currentScreen = Screen.Noti
                                    },
                                )
                                Screen.ProductDetail -> {
                                    val productDetails = when (productId) {
                                        "xoai_granola" -> Triple(
                                            "Smoothie Xoài Nhiệt Đới Granola",
                                            "65.000đ",
                                            R.drawable.xoai_granola
                                        )
                                        "phuc_bon_tu_granola" -> Triple(
                                            "Smoothie Phúc Bồn Tử Granola",
                                            "65.000đ",
                                            R.drawable.phuc_bon_tu_granola
                                        )
                                        "oolong_tu_quy_vai" -> Triple(
                                            "Oolong Tứ Quý Vải",
                                            "59.000đ",
                                            R.drawable.oolong_tu_quy_vai
                                        )
                                        "oolong_kim_quat_tran_chau" -> Triple(
                                            "Oolong Tứ Quý Kim Quất Trân Châu",
                                            "59.000đ",
                                            R.drawable.oolong_kim_quat_tran_chau
                                        )
                                        "tra_sua_oolong_tu_quy_suong_sao" -> Triple(
                                            "Trà Sữa Oolong Tứ Quý Sương Sáo",
                                            "55.000đ",
                                            R.drawable.tra_sua_oolong_tu_quy_suong_sao
                                        )
                                        else -> Triple("Sản phẩm", "0đ", R.drawable.xoai_granola)
                                    }
                                    PrdScreen(
                                        productId = productId,
                                        onBackClick = { currentScreen = Screen.Main },
                                        onViewCart = { currentScreen = Screen.Cart },
                                        onNavigateToMain = {
                                            currentScreen = Screen.Main
                                        }
                                    )
                                }
                                Screen.Differ -> DifferScreen(
                                    onBackClick = { currentScreen = Screen.Main },
                                    onNavigationItemClick = { destination ->
                                        when (destination) {
                                            "home" -> currentScreen = Screen.Main
                                            "order" -> currentScreen = Screen.Booked
                                            "rewards" -> currentScreen = Screen.Coupon
                                            "user_info" -> currentScreen = Screen.UserInfo
                                            else -> currentScreen = Screen.Differ
                                        }
                                    },
                                    onLogoutClick = {
                                        authManager.clearLoginCredentials()
                                        currentScreen = Screen.Login
                                    },
                                    onHistoryClick = {
                                        currentScreen = Screen.History
                                    },
                                    onNavigateToNoti = {
                                        previousScreen = Screen.Differ
                                        currentScreen = Screen.Noti
                                    }
                                )
                                Screen.UserInfo -> UserInfScreen(
                                    onBackClick = { currentScreen = Screen.Differ }
                                )
                                Screen.Cart -> CartScreen(
                                    onBackClick = { currentScreen = Screen.Main },
                                    onNavigateToPayment = { order ->
                                        selectedOrder = order
                                        selectedVoucher = null
                                        pendingVnpayOrderId = order.orderId
                                            ?: order.items.firstOrNull()?.orderId
                                        currentScreen = Screen.Payment
                                    }
                                )
                                Screen.Payment -> {
                                    selectedOrder?.let { order ->
                                        PaymentScreen(
                                            checkoutSummary = order,
                                            appliedVoucher = selectedVoucher,
                                            onVoucherApplied = { voucher -> selectedVoucher = voucher },
                                            onBackClick = { currentScreen = Screen.Cart },
                                            onNavigateToMain = {
                                                currentScreen = Screen.Main
                                            },
                                            onSelectVoucher = {
                                                currentScreen = Screen.SelectVoucher
                                            }
                                        )
                                    } ?: run {
                                        currentScreen = Screen.Cart
                                    }
                                }
                                Screen.PaymentResult -> {
                                    val result = deepLinkResult
                                    if (result != null) {
                                        PaymentResultScreen(
                                            result = result,
                                            onBackHome = {
                                                vnpayResult = null
                                                currentScreen = Screen.Main
                                            }
                                        )
                                    } else {
                                        currentScreen = Screen.Main
                                    }
                                }
                                Screen.SelectVoucher -> SelectVoucherScreen(
                                    onBackClick = { currentScreen = Screen.Payment },
                                    onVoucherSelect = { voucher ->
                                        selectedVoucher = voucher
                                        currentScreen = Screen.Payment
                                    }
                                )
                                Screen.Coupon -> CouponScreen(
                                    onNavigationItemClick = { destination ->
                                        when (destination) {
                                            "home" -> currentScreen = Screen.Main
                                            "order" -> currentScreen = Screen.Booked
                                            "rewards" -> currentScreen = Screen.Coupon
                                            "differ" -> currentScreen = Screen.Differ
                                            else -> { /* Handle other navigation */ }
                                        }
                                    }
                                )
                                Screen.Booked -> BookedScreen(
                                    onBackClick = { currentScreen = Screen.Main },
                                    onNavigationItemClick = { destination ->
                                        when (destination) {
                                            "home" -> currentScreen = Screen.Main
                                            "order" -> currentScreen = Screen.Booked
                                            "rewards" -> currentScreen = Screen.Coupon
                                            "differ" -> currentScreen = Screen.Differ
                                            else -> { /* Handle other navigation */ }
                                        }
                                    },
                                    onFavoritesClick = {
                                        currentScreen = Screen.WishList
                                    }
                                )
                                Screen.History -> HistoryScreen(
                                    onBackClick = { currentScreen = Screen.Differ }
                                )
                                Screen.WishList -> WishListScreen(
                                    onBackClick = { currentScreen = Screen.Booked }
                                )
                                Screen.Noti -> NotiScreen(
                                    onBackClick = { currentScreen = previousScreen }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Xử lý deeplink khi ứng dụng đã chạy
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        if (intent?.action != Intent.ACTION_VIEW) return
        val uri = intent.data ?: return
        parseVnpayResult(uri)?.let { result ->
            Log.d("Deeplink", "Received deeplink: $uri")
            if (result.isSuccess) {
                pendingVnpayOrderId = result.txnRef ?: result.orderInfo
            }
            vnpayResult = result
        }
    }

    private fun parseVnpayResult(uri: Uri): VnpayCallbackResult? {
        if (uri.scheme != "yourapp" || uri.host != "payment" || uri.path?.startsWith("/callback") != true) {
            return null
        }

        val responseCode = uri.getQueryParameter("vnp_ResponseCode") ?: uri.getQueryParameter("status")
        val amountVnd = uri.getQueryParameter("vnp_Amount")
            ?.toLongOrNull()
            ?.div(100)
            ?.toInt()
        val message = when (responseCode) {
            "00" -> "Giao dịch thành công"
            "07" -> "Giao dịch nghi ngờ gian lận. Vui lòng liên hệ hỗ trợ."
            "09", "10", "11" -> "Giao dịch không thành công do lỗi kết nối."
            "24" -> "Khách hàng đã hủy giao dịch."
            else -> "Thanh toán chưa hoàn tất."
        }

        return VnpayCallbackResult(
            responseCode = responseCode,
            orderInfo = uri.getQueryParameter("vnp_OrderInfo"),
            amountVnd = amountVnd,
            transactionNo = uri.getQueryParameter("vnp_TransactionNo")
                ?: uri.getQueryParameter("transaction_id"),
            txnRef = uri.getQueryParameter("vnp_TxnRef"),
            bankCode = uri.getQueryParameter("vnp_BankCode"),
            payDate = uri.getQueryParameter("vnp_PayDate"),
            message = message
        )
    }

    // Enum để quản lý các màn hình
    enum class Screen {
        Login,
        ForgotPassword,
        OtpVerification,
        ChangePassword,
        SignUp,
        OtpSignUp,
        Main,
        Differ,
        ProductDetail,
        Cart,
        Payment,
        PaymentResult,
        Coupon,
        Booked,
        Admin,
        UserInfo,
        History,
        WishList,
        Noti,
        SelectVoucher
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        BrewCoTheme {
            Greeting("BrewCo")
        }
    }
}