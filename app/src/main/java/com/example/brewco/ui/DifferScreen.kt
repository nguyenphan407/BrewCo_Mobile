package com.example.brewco.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.ui.theme.*

/**
 * MOCK VERSION
 * --------------------
 * UI giống production
 * Không dùng API
 * Data được mock
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifferScreen(
    onNavigationItemClick: (String) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onNavigateToNoti: () -> Unit = {}
) {

    /* =========================
     * Mock states
     * ========================= */

    var fullName by remember { mutableStateOf("Nguyễn Văn A") }
    var email by remember { mutableStateOf("nguyenvana@gmail.com") }

    var orderCount by remember { mutableStateOf(0) }
    var beanCount by remember { mutableStateOf(0) }
    val voucherCount = 2

    /**
     * Giả lập load data giống API
     */
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(600)
        orderCount = 12
        beanCount = 345
    }

    val initials = fullName
        .trim()
        .split("\\s+".toRegex())
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
        .joinToString("")
        .ifEmpty { "U" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tài khoản",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandWhite
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToNoti) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_noti),
                            contentDescription = null,
                            tint = HighlandWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HighlandRed
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            /* =========================
             * Profile Card
             * ========================= */

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        HighlandRed,
                                        HighlandDarkRed
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = HighlandWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = fullName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandText
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = email,
                        fontSize = 14.sp,
                        color = HighlandText.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("Bean", beanCount.toString(), R.drawable.coffee_beans)
                        VerticalDivider()
                        StatItem("Đơn hàng", orderCount.toString(), R.drawable.invoice)
                        VerticalDivider()
                        StatItem("Voucher", voucherCount.toString(), R.drawable.ic_voucher)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { onNavigationItemClick("user_info") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = HighlandRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Chỉnh sửa tài khoản",
                            color = HighlandWhite,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            /* =========================
             * Menu section
             * ========================= */

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column {
                    ModernMenuItem(
                        iconResId = R.drawable.ic_lichsudonhang,
                        title = "Lịch sử đơn hàng",
                        onClick = onHistoryClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            /* =========================
             * Logout
             * ========================= */

            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HighlandRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logout),
                    contentDescription = null,
                    tint = HighlandWhite,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Đăng xuất",
                    color = HighlandWhite,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/* =====================================================
 * Components
 * ===================================================== */

@Composable
private fun VerticalDivider() {
    Divider(
        modifier = Modifier
            .height(40.dp)
            .width(1.dp),
        color = HighlandText.copy(alpha = 0.2f)
    )
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = HighlandText
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = HighlandText.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ModernMenuItem(
    iconResId: Int,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    HighlandRed.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = title,
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(HighlandRed)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = HighlandText
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = HighlandText.copy(alpha = 0.4f)
        )
    }
}

/* =====================================================
 * Preview
 * ===================================================== */

@Preview(showBackground = true)
@Composable
fun DifferScreenMockPreview() {
    BrewCoTheme {
        DifferScreen()
    }
}
