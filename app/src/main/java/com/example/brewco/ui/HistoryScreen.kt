package com.example.brewco.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.ui.theme.*

/* -------------------- DATA -------------------- */

data class OrderHistoryItem(
    val id: String,
    val date: String,
    val items: List<String>,
    val status: OrderStatus
)

enum class OrderStatus {
    DELIVERING,
    DELIVERED,
    CANCELLED
}

/* -------------------- MOCK DATA -------------------- */

private val mockOrders = listOf(
    OrderHistoryItem(
        id = "A10293",
        date = "12/01/2026",
        items = listOf(
            "2 Latte",
            "1 Cappuccino"
        ),
        status = OrderStatus.DELIVERED
    ),
    OrderHistoryItem(
        id = "A10281",
        date = "10/01/2026",
        items = listOf(
            "1 Espresso",
            "1 Croissant"
        ),
        status = OrderStatus.DELIVERING
    ),
    OrderHistoryItem(
        id = "A10270",
        date = "08/01/2026",
        items = listOf(
            "1 Americano"
        ),
        status = OrderStatus.CANCELLED
    )
)

/* -------------------- SCREEN -------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBackClick: () -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var orderHistory by remember { mutableStateOf<List<OrderHistoryItem>>(emptyList()) }

    /* giả lập load dữ liệu */
    LaunchedEffect(Unit) {
        isLoading = true
        kotlinx.coroutines.delay(800)
        orderHistory = mockOrders
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lịch sử đơn hàng",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = HighlandWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HighlandRed
                )
            )
        },
        containerColor = HighlandWhite
    ) { paddingValues ->
        when {
            isLoading -> LoadingState(paddingValues)
            error != null -> ErrorState(
                message = error!!,
                paddingValues = paddingValues,
                onRetry = {
                    error = null
                    isLoading = true
                }
            )
            else -> OrderList(
                orders = orderHistory,
                paddingValues = paddingValues
            )
        }
    }
}

/* -------------------- STATES -------------------- */

@Composable
private fun LoadingState(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = HighlandRed)
    }
}

@Composable
private fun ErrorState(
    message: String,
    paddingValues: PaddingValues,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = HighlandText)
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = HighlandRed),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Thử lại", color = HighlandWhite, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun OrderList(
    orders: List<OrderHistoryItem>,
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
    ) {
        items(orders) { order ->
            OrderHistoryCard(order)
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

/* -------------------- CARD -------------------- */

@Composable
fun OrderHistoryCard(order: OrderHistoryItem) {
    val statusColor = when (order.status) {
        OrderStatus.DELIVERED -> Color(0xFF4CAF50)
        OrderStatus.CANCELLED -> HighlandRed
        OrderStatus.DELIVERING -> Color(0xFFFF9800)
    }

    val statusText = when (order.status) {
        OrderStatus.DELIVERED -> "Đã giao hàng"
        OrderStatus.CANCELLED -> "Đã hủy"
        OrderStatus.DELIVERING -> "Đang giao"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(70.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = statusColor.copy(alpha = 0.2f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_shipper),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .background(
                            HighlandRed.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.date,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandRed
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .background(statusColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandWhite
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                order.items.forEach {
                    Text(
                        text = "• $it",
                        fontSize = 14.sp,
                        color = HighlandText,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Mã đơn: #${order.id}",
                    fontSize = 12.sp,
                    color = HighlandText.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/* -------------------- PREVIEW -------------------- */

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    BrewCoTheme {
        HistoryScreen()
    }
}
