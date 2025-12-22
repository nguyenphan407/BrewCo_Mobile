package com.example.brewco.ui

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.OrderResponse
import com.example.brewco.data.dto.VoucherResponse
import com.example.brewco.ui.components.BottomNavBar
import com.example.brewco.ui.components.NavigationItem
import com.example.brewco.ui.theme.BrewCoTheme
import com.example.brewco.ui.theme.HighlandDarkRed
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import com.example.brewco.ui.theme.HighlandWhite
import java.time.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class VoucherItem(
    val id: String,
    val code: String? = null,
    val titleText: String,
    val discountText: String,
    val description: String? = null,
    val expiryDate: String,
    val startDate: String? = null,
    val imageRes: Int,
    val hasFreeship: Boolean = false,
    val minOrderAmount: Double? = null,
    val maxDiscountAmount: Double? = null,
    val quantity: Int? = null,
    val used: Int? = null,
    val isUsed: Boolean = false
)

data class ExchangeItem(
    val id: String,
    val title: String,
    val description: String,
    val beanCost: Int,
    val imageRes: Int,
    val isAvailable: Boolean = true
)

data class HistoryItem(
    val id: String,
    val title: String,
    val date: String,
    val amount: String,
    val isPositive: Boolean,
    val type: HistoryType
)

data class BeanBalance(
    val current: Int,
    val expiringSoon: Int
)

enum class HistoryType {
    EARN, EXCHANGE, EXPIRE, BONUS
}

@Composable
fun CouponScreen(
    onNavigationItemClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val authManager = remember { AuthManager.getInstance(context) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Voucher của tôi", "Đổi Bean", "Lịch sử")
    val snackbarHostState = remember { SnackbarHostState() }

    var isLoading by remember { mutableStateOf(false) }
    val isProcessing = false

    var vouchers by remember { mutableStateOf<List<VoucherItem>>(emptyList()) }
    var exchangeItems by remember { mutableStateOf<List<ExchangeItem>>(emptyList()) }
    var historyItems by remember { mutableStateOf<List<HistoryItem>>(emptyList()) }
    var beanBalance by remember { mutableStateOf(BeanBalance(current = 0, expiringSoon = 0)) }


    val voucherImages = listOf(R.drawable.vc_1, R.drawable.vc_2, R.drawable.vc_3)

    LaunchedEffect(Unit) {
        isLoading = true
        val token = authManager.getAuthToken()
        val ordersCall = token?.let { ApiClient.apiService.getMyOrders("Bearer $it") }
        if (ordersCall == null) {
            Log.e("CouponScreen", "Missing auth token for getMyOrders")
        }

        val vouchersCall = ApiClient.apiService.getVouchers()


        ordersCall?.enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (!response.isSuccessful) {
                    Log.e("CouponScreen", "getMyOrders failed: ${response.code()} ${response.message()}")
                    return
                }
                val orders = response.body()?.data?.content.orEmpty()


                val beanEarned = orders.sumOf { it.totalPrice } / 1000
                beanBalance = beanBalance.copy(current = beanEarned)

                historyItems = orders.map { order ->
                    val idShort = order.id.takeLast(6)
                    val earned = (order.totalPrice / 1000).coerceAtLeast(0)
                    HistoryItem(
                        id = order.id,
                        title = "Tích điểm đơn hàng #$idShort",
                        date = "—",
                        amount = "+ $earned Bean",
                        isPositive = true,
                        type = HistoryType.EARN
                    )
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Log.e("CouponScreen", "getMyOrders error", t)
            }
        })


        vouchersCall.enqueue(object : Callback<List<VoucherResponse>> {
            override fun onResponse(
                call: Call<List<VoucherResponse>>,
                response: Response<List<VoucherResponse>>
            ) {
                isLoading = false
                if (!response.isSuccessful) {
                    Log.e("CouponScreen", "getVouchers failed: ${response.code()} ${response.message()}")
                    return
                }
                val voucherList = response.body().orEmpty()
                val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                vouchers = voucherList.mapIndexed { idx, voucher ->
                    val discountText =
                        if (voucher.discountPercentage % 1.0 == 0.0) "${voucher.discountPercentage.toInt()}%"
                        else "${voucher.discountPercentage}%"
                    VoucherItem(
                        id = voucher.id,
                        titleText = "Giảm $discountText cho đơn hàng",
                        discountText = discountText,
                        expiryDate = "Hết hạn ${voucher.endDate.toLocalDate().format(dateFormatter)}",
                        startDate = voucher.startDate.toLocalDate().format(dateFormatter),
                        imageRes = voucherImages[idx % voucherImages.size],
                        hasFreeship = false,
                        isUsed = false
                    )
                }
                Log.d("CouponScreen", "Loaded ${vouchers.size} vouchers")
            }

            override fun onFailure(call: Call<List<VoucherResponse>>, t: Throwable) {
                isLoading = false
                Log.e("CouponScreen", "getVouchers error", t)
            }
        })
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HighlandRed)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Ưu đãi & Điểm thưởng",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = HighlandWhite,
                            modifier = Modifier.semantics {
                                contentDescription = "Màn hình ưu đãi và điểm thưởng"
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        AnimatedBeanCounter(beanCount = beanBalance.current)
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = HighlandRed,
                    contentColor = HighlandWhite,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentSize(Alignment.BottomStart)
                                    .offset(x = tabPositions[selectedTab].left)
                                    .width(tabPositions[selectedTab].width)
                                    .height(3.dp)
                                    .padding(horizontal = 20.dp)
                                    .background(
                                        HighlandWhite,
                                        RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                                    )
                            )
                        }
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "Tab $title"
                            }
                        )
                    }
                }
            }
        },
        bottomBar = {
            BottomNavBar(
                currentItem = NavigationItem.REWARDS,
                onNavigate = onNavigationItemClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp),
                    color = HighlandRed
                )
            } else {
                AnimatedContent(
                    targetState = selectedTab,
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        0 -> VoucherTab(
                            vouchers = vouchers,
                            onUseVoucher = {  },
                            isProcessing = isProcessing,
                            paddingValues = paddingValues
                        )
                        1 -> ExchangeBeanTab(
                            beanBalance = beanBalance,
                            exchangeItems = exchangeItems,
                            onExchange = { _, _ ->  },
                            isProcessing = isProcessing,
                            paddingValues = paddingValues
                        )
                        2 -> HistoryTab(
                            historyItems = historyItems,
                            paddingValues = paddingValues
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedBeanCounter(
    beanCount: Int,
    modifier: Modifier = Modifier
) {
    val animatedCount = remember { Animatable(beanCount.toFloat()) }

    LaunchedEffect(beanCount) {
        animatedCount.animateTo(
            targetValue = beanCount.toFloat(),
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.coffee_beans),
            contentDescription = "Bean icon",
            modifier = Modifier
                .size(18.dp)
                .graphicsLayer {
                    rotationZ = (animatedCount.value % 360)
                },
            colorFilter = ColorFilter.tint(HighlandWhite)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "${animatedCount.value.toInt()} Bean",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = HighlandWhite
        )
    }
}

@Composable
fun VoucherTab(
    vouchers: List<VoucherItem>,
    onUseVoucher: (String) -> Unit,
    isProcessing: Boolean,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Voucher khả dụng",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = HighlandText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (vouchers.isEmpty()) {
            EmptyState(
                message = "Bạn chưa có voucher nào",
                icon = Icons.Default.Info
            )
        } else {
            vouchers.forEachIndexed { index, voucher ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300, delayMillis = index * 50)) +
                        slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    ModernVoucherCard(
                        voucher = voucher,
                        onUseClick = { onUseVoucher(voucher.id) },
                        isProcessing = isProcessing
                    )
                }

                if (index < vouchers.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ExchangeBeanTab(
    beanBalance: BeanBalance,
    exchangeItems: List<ExchangeItem>,
    onExchange: (String, Int) -> Unit,
    isProcessing: Boolean,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(HighlandRed, HighlandDarkRed)
                        )
                    )
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Bean hiện có",
                        fontSize = 14.sp,
                        color = HighlandWhite.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val animatedBean = remember { Animatable(beanBalance.current.toFloat()) }
                    LaunchedEffect(beanBalance.current) {
                        animatedBean.animateTo(
                            targetValue = beanBalance.current.toFloat(),
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }

                    Text(
                        text = "${animatedBean.value.toInt()} Bean",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandWhite
                    )

                    if (beanBalance.expiringSoon > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${beanBalance.expiringSoon} Bean sắp hết hạn",
                            fontSize = 11.sp,
                            color = Color(0xFFFFEB3B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.coffee_beans),
                    contentDescription = "Bean icon",
                    modifier = Modifier.size(60.dp),
                    colorFilter = ColorFilter.tint(HighlandWhite.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Đổi Bean lấy ưu đãi",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = HighlandText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (exchangeItems.isEmpty()) {
            EmptyState(
                message = "Chưa có quà để đổi",
                icon = Icons.Default.Info
            )
        } else {
            exchangeItems.forEachIndexed { index, item ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300, delayMillis = index * 50)) +
                        slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    ExchangeItemCard(
                        item = item,
                        currentBeans = beanBalance.current,
                        onExchange = { onExchange(item.id, item.beanCost) },
                        isProcessing = isProcessing
                    )
                }

                if (index < exchangeItems.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun HistoryTab(
    historyItems: List<HistoryItem>,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Lịch sử Bean",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = HighlandText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (historyItems.isEmpty()) {
            EmptyState(
                message = "Chưa có lịch sử giao dịch",
                icon = Icons.Default.Info
            )
        } else {
            historyItems.forEachIndexed { index, item ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300, delayMillis = index * 50)) +
                        slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    HistoryItemCard(item = item)
                }

                if (index < historyItems.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = HighlandText.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = HighlandText.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ModernVoucherCard(
    voucher: VoucherItem,
    onUseClick: () -> Unit,
    isProcessing: Boolean
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .semantics { contentDescription = "Voucher ${voucher.titleText}, giảm ${voucher.discountText}" },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Image(
                    painter = painterResource(id = voucher.imageRes),
                    contentDescription = "Hình ảnh voucher",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(HighlandRed, RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = voucher.discountText,
                        color = HighlandWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                if (voucher.hasFreeship) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "FREESHIP",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }


                Text(
                    text = voucher.titleText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = HighlandText,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))


                voucher.description?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = HighlandText.copy(alpha = 0.7f),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }


                voucher.code?.let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF0F0F0), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mã: ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = HighlandText
                        )
                        Text(
                            text = it,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HighlandRed,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    if (voucher.minOrderAmount != null && voucher.minOrderAmount > 0) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Đơn tối thiểu",
                                fontSize = 10.sp,
                                color = HighlandText.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${String.format("%.0f", voucher.minOrderAmount)}₫",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HighlandText
                            )
                        }
                    }


                    if (voucher.maxDiscountAmount != null && voucher.maxDiscountAmount > 0) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Giảm tối đa",
                                fontSize = 10.sp,
                                color = HighlandText.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${String.format("%.0f", voucher.maxDiscountAmount)}₫",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HighlandText
                            )
                        }
                    }


                    if (voucher.quantity != null && voucher.used != null) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Còn lại",
                                fontSize = 10.sp,
                                color = HighlandText.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${voucher.quantity!! - (voucher.used ?: 0)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if ((voucher.quantity!! - (voucher.used ?: 0)) > 0) Color(0xFF4CAF50) else Color(0xFFFF6B6B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF3CD), RoundedCornerShape(6.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "",
                        fontSize = 12.sp
                    )
                    Text(
                        text = voucher.expiryDate,
                        fontSize = 11.sp,
                        color = HighlandText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ExchangeItemCard(
    item: ExchangeItem,
    currentBeans: Int,
    onExchange: () -> Unit,
    isProcessing: Boolean
) {
    val canAfford = currentBeans >= item.beanCost
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "exchange_card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .semantics { contentDescription = "${item.title}, cần ${item.beanCost} Bean" },
        colors = CardDefaults.cardColors(
            containerColor = if (!canAfford) Color.Gray.copy(alpha = 0.1f) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            if (canAfford) HighlandRed.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = "Icon ${item.title}",
                        modifier = Modifier.size(28.dp),
                        colorFilter = if (!canAfford) ColorFilter.tint(Color.Gray) else null
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (canAfford) HighlandText else HighlandText.copy(alpha = 0.5f)
                    )
                    if (item.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.description,
                            fontSize = 11.sp,
                            color = HighlandText.copy(alpha = 0.5f),
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.coffee_beans),
                            contentDescription = "Bean icon",
                            modifier = Modifier.size(14.dp),
                            colorFilter = if (!canAfford) ColorFilter.tint(Color.Gray) else null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.beanCost} Bean",
                            fontSize = 13.sp,
                            color = if (canAfford) HighlandRed else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Button(
                onClick = onExchange,
                enabled = canAfford && !isProcessing && item.isAvailable,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HighlandRed,
                    disabledContainerColor = HighlandRed.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = HighlandWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Đổi", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(item: HistoryItem) {
    val icon = when (item.type) {
        HistoryType.EARN -> Icons.Default.Add
        HistoryType.EXCHANGE -> Icons.Default.ShoppingCart
        HistoryType.EXPIRE -> Icons.Default.Warning
        HistoryType.BONUS -> Icons.Default.Star
    }

    val iconColor = when (item.type) {
        HistoryType.EARN -> Color(0xFF4CAF50)
        HistoryType.EXCHANGE -> HighlandRed
        HistoryType.EXPIRE -> Color(0xFFFF9800)
        HistoryType.BONUS -> Color(0xFF2196F3)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = HighlandText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.date,
                        fontSize = 12.sp,
                        color = HighlandText.copy(alpha = 0.6f)
                    )
                }
            }

            Text(
                text = item.amount,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.isPositive) Color(0xFF4CAF50) else HighlandRed
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CouponScreenPreview() {
    BrewCoTheme {
        CouponScreen()
    }
}
