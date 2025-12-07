package com.example.brewco.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.data.AuthManager
import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.OrderResponse
import com.example.brewco.data.dto.UserProfileResponse
import com.example.brewco.ui.components.BottomNavBar
import com.example.brewco.ui.components.NavigationItem
import com.example.brewco.ui.theme.BrewCoTheme
import com.example.brewco.ui.theme.HighlandDarkRed
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import com.example.brewco.ui.theme.HighlandWhite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifferScreen(
    onBackClick: () -> Unit = {},
    onNavigationItemClick: (String) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onNavigateToNoti: () -> Unit = {}
) {
    val context = LocalContext.current
    val sharedPreferences = remember(context) {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }
    val authManager = remember { AuthManager.getInstance(context) }

    var orderCount by remember { mutableStateOf(0) }
    var beanCount by remember { mutableStateOf(0) }
    val voucherCount = 0
//    var fullName by remember { mutableStateOf(authManager.getSavedFullName() ?: "Người dùng") }
    var fullName by remember { mutableStateOf("Người dùng") }
    var email by remember { mutableStateOf(authManager.getSavedEmail().ifEmpty { "—" }) }

    // Giữ logic logout cũ: có token thì gọi API logout
    val handleLogout = {
        val token = sharedPreferences.getString("auth_token", null)
        if (token != null) {
            ApiClient.apiService.logout("Bearer $token").enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        sharedPreferences.edit().clear().apply()
                        Toast.makeText(context, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
                        onLogoutClick()
                    } else {
                        Toast.makeText(context, "Đăng xuất thất bại: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(context, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show()
            onLogoutClick()
        }
    }

    // Không mock: lấy số đơn + tính bean từ orders (tạm như CouponScreen)
    LaunchedEffect(Unit) {
        val token = sharedPreferences.getString("auth_token", null)
        if (token.isNullOrBlank()) {
            Log.w("DifferScreen", "Missing auth token for orders")
            return@LaunchedEffect
        }
        ApiClient.apiService.getMyOrders("Bearer $token").enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (!response.isSuccessful) return
                val orders = response.body()?.data?.content.orEmpty()
                orderCount = orders.size
                beanCount = orders.sumOf { it.totalPrice } / 1000
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Log.e("DifferScreen", "getMyOrders error", t)
            }
        })
    }

    LaunchedEffect(Unit) {
        val token = sharedPreferences.getString("auth_token", null)
        Log.e(
            "tokentest",
            "Error body = ${token}"
        )
        if (token.isNullOrBlank()) {
            Log.w("DifferScreen", "Missing auth token")
            return@LaunchedEffect
        }
        ApiClient.apiService.getCurrentUser("Bearer $token")
            .enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    if (!response.isSuccessful) {
                        Log.w("DifferScreen", "getCurrentUser error ${response.code()}")
                        Toast.makeText(context, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show()
                        return
                    }
                    Log.e(
                        "DifferScreen",
                        "Error body = ${response.body()}"
                    )
                    val profile = response.body() ?: return

                    fullName = profile.fullName
                    email = profile.email
                    authManager.saveUserInfo(profile.id, profile.fullName, profile.phoneNumber.orEmpty())
                    sharedPreferences.edit()
                        .putString("full_name", profile.fullName)
                        .putString("email", profile.email)
                        .apply()
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    Log.e("DifferScreen", "getCurrentUser failure", t)
                    Toast.makeText(context, "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_SHORT).show()
                }
            })
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
                    IconButton(onClick = { onNavigateToNoti() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_noti),
                            contentDescription = "Notifications",
                            tint = HighlandWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HighlandRed)
            )
        },
        bottomBar = {
            BottomNavBar(
                currentItem = NavigationItem.MORE,
                onNavigate = onNavigationItemClick
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                                brush = Brush.radialGradient(colors = listOf(HighlandRed, HighlandDarkRed)),
                                shape = androidx.compose.foundation.shape.CircleShape
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
                        StatItem(value = beanCount.toString(), label = "Bean", icon = R.drawable.coffee_beans)
                        Divider(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp),
                            color = HighlandText.copy(alpha = 0.2f)
                        )
                        StatItem(value = orderCount.toString(), label = "Đơn hàng", icon = R.drawable.invoice)
                        Divider(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp),
                            color = HighlandText.copy(alpha = 0.2f)
                        )
                        StatItem(value = voucherCount.toString(), label = "Voucher", icon = R.drawable.ic_voucher)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { onNavigationItemClick("user_info") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = HighlandRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Chỉnh sửa tài khoản", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = HighlandWhite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ModernMenuItem(
                        iconResId = R.drawable.ic_lichsudonhang,
                        title = "Lịch sử đơn hàng",
                        onClick = onHistoryClick
                    )

//                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
//
//                    ModernMenuItem(
//                        iconResId = R.drawable.ic_diachi,
//                        title = "Địa chỉ đã lưu",
//                        onClick = { /* TODO */ }
//                    )
//
//                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
//
//                    ModernMenuItem(
//                        iconResId = R.drawable.ic_danhgiadonhang,
//                        title = "Đánh giá đơn hàng",
//                        onClick = { /* TODO */ }
//                    )
//
//                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
//
//                    ModernMenuItem(
//                        iconResId = R.drawable.ic_lienhe,
//                        title = "Liên hệ và góp ý",
//                        onClick = { /* TODO */ }
//                    )
//
//                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
//
//                    ModernMenuItem(
//                        iconResId = R.drawable.ic_caidat,
//                        title = "Cài đặt",
//                        onClick = { /* TODO */ }
//                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { handleLogout() },
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighlandWhite
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatItem(
    value: String,
    label: String,
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
                .background(HighlandRed.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape),
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
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = HighlandText,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = HighlandText.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DifferScreenPreview() {
    BrewCoTheme {
        DifferScreen()
    }
}


