package com.example.brewco.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.brewco.R
import com.example.brewco.data.AuthManager
import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.CategoryListResponse
import com.example.brewco.data.dto.CategoryResponse
import com.example.brewco.data.dto.ProductResponse
import com.example.brewco.data.dto.ProductListResponse
import com.example.brewco.data.dto.UserProfileResponse
import com.example.brewco.ui.components.BottomNavBar
import com.example.brewco.ui.components.NavigationItem
import com.example.brewco.ui.theme.BrewCoTheme
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import com.example.brewco.ui.theme.HighlandWhite
import com.example.brewco.utils.FormatUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNotificationClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onNavigateToNoti: () -> Unit = {}
) {
    var userName by remember { mutableStateOf("") }
    val context = LocalContext.current
    val authManager = remember { AuthManager.getInstance(context) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun loadUserProfile() {
        val token = authManager.getAuthToken()
        if (token.isNullOrBlank()) {
            errorMessage = "Vui lòng đăng nhập lại"
            return
        }
        isLoading = true
        ApiClient.apiService.getCurrentUser("Bearer $token")
            .enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val profile = response.body()
                        if (profile != null) {
                            userName = profile.fullName
                            authManager.saveUserInfo(profile.id, profile.fullName, profile.phoneNumber.orEmpty())
                            errorMessage = null
                        } else {
                            errorMessage = "Không có dữ liệu người dùng"
                        }
                    } else {
                        errorMessage = "Không thể tải thông tin (${response.code()})"
                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    isLoading = false
                    errorMessage = t.localizedMessage ?: "Lỗi kết nối"
                }
            })
    }


    val mustTryProducts = remember { mutableStateListOf<ProductResponse>() }


    var categoriesFromApi by remember { mutableStateOf<List<CategoryResponse>>(emptyList()) }

    LaunchedEffect(true) {
        loadUserProfile()

        ApiClient.apiService.getProductsPaginated(page = 0, size = 20, sort = null)
            .enqueue(object : Callback<ProductListResponse> {
                override fun onResponse(
                    call: Call<ProductListResponse>,
                    response: Response<ProductListResponse>
                ) {
                    val items = response.body()?.data?.items.orEmpty()
                    mustTryProducts.clear()
                    mustTryProducts.addAll(items)
                }

                override fun onFailure(call: Call<ProductListResponse>, t: Throwable) {
                    Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
                }
            })
    }

    LaunchedEffect(Unit) {
        ApiClient.apiService.getCategories().enqueue(object : Callback<CategoryListResponse> {
            override fun onResponse(
                call: Call<CategoryListResponse>,
                response: Response<CategoryListResponse>
            ) {
                if (!response.isSuccessful) return
                categoriesFromApi = response.body()?.data.orEmpty()
            }

            override fun onFailure(call: Call<CategoryListResponse>, t: Throwable) {

            }
        })
    }

    val adImages = listOf(
        R.drawable.ad_1,
        R.drawable.ad_2,
        R.drawable.ad_3,
        R.drawable.ad_4,
        R.drawable.ad_5,
        R.drawable.ad_6,
        R.drawable.ad_7
    )


    val categoryChips = remember(categoriesFromApi) {
        listOf(null to "Tất cả") + categoriesFromApi.map { it.id to it.name }
    }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }


    val filteredProducts by remember(mustTryProducts) {
        derivedStateOf { mustTryProducts.toList() }
    }

    Scaffold(
        topBar = {

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = HighlandWhite,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Chào, $userName",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = HighlandText
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(HighlandRed.copy(alpha = 0.1f), CircleShape)
                                .clickable { onNavigate("rewards") },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_coupon),
                                contentDescription = "Vouchers",
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(HighlandRed.copy(alpha = 0.1f), CircleShape)
                                .clickable { onNavigateToNoti() },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_noti),
                                contentDescription = "Notifications",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomNavBar(
                currentItem = NavigationItem.HOME,
                onNavigate = onNavigate
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate("orders") },
                containerColor = HighlandRed,
                contentColor = HighlandWhite
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.invoice),
                    contentDescription = "Orders",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(8.dp))


            val pagerState = rememberPagerState(pageCount = { adImages.size })
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                while (true) {
                    delay(3500)
                    val nextPage = (pagerState.currentPage + 1) % adImages.size
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            page = nextPage,
                            animationSpec = tween(durationMillis = 600)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = adImages[page]),
                            contentDescription = "Promotion",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(adImages.size) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .width(if (pagerState.currentPage == index) 20.dp else 6.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (pagerState.currentPage == index) HighlandWhite else HighlandWhite.copy(alpha = 0.5f)
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                item {
                    ServiceIconCard(
                        icon = R.drawable.shipping,
                        label = "Giao hàng",
                        onClick = { onNavigate("order") }
                    )
                }
                item {
                    ServiceIconCard(
                        icon = R.drawable.take_away,
                        label = "Mang đi",
                        onClick = { onNavigate("order") }
                    )
                }
                item {
                    ServiceIconCard(
                        icon = R.drawable.coffee_beans,
                        label = "Đổi Bean",
                        onClick = { onNavigate("rewards") }
                    )
                }
                item {
                    ServiceIconCard(
                        icon = R.drawable.ic_gift,
                        label = "Ưu đãi",
                        onClick = { onNavigate("rewards") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoryChips) { (id, name) ->
                    CategoryChip(
                        text = name,
                        selected = id == selectedCategoryId,
                        onClick = {
                            selectedCategoryId = id
                            if (id == null) {

                                ApiClient.apiService.getProductsPaginated(page = 0, size = 20, sort = null)
                                    .enqueue(object : Callback<ProductListResponse> {
                                        override fun onResponse(
                                            call: Call<ProductListResponse>,
                                            response: Response<ProductListResponse>
                                        ) {
                                            val items = response.body()?.data?.items.orEmpty()
                                            mustTryProducts.clear()
                                            mustTryProducts.addAll(items)
                                        }

                                        override fun onFailure(call: Call<ProductListResponse>, t: Throwable) {
                                            Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                            } else {

                                ApiClient.apiService.getProductsByCategory(categoryId = id.toLong(), page = 0, size = 20)
                                    .enqueue(object : Callback<ProductListResponse> {
                                        override fun onResponse(
                                            call: Call<ProductListResponse>,
                                            response: Response<ProductListResponse>
                                        ) {
                                            val items = response.body()?.data?.items.orEmpty()
                                            mustTryProducts.clear()
                                            mustTryProducts.addAll(items)
                                        }

                                        override fun onFailure(call: Call<ProductListResponse>, t: Throwable) {
                                            Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Món Mới Phải Thử",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandText
                    )

                    TextButton(
                        onClick = { onNavigate("order") },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .background(HighlandRed, shape = RoundedCornerShape(20.dp))
                    ) {
                        Text(
                            text = "Xem tất cả",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))


                val productChunks = filteredProducts.chunked(2)
                if (filteredProducts.isEmpty()) {
                    Text(
                        text = "Không có sản phẩm trong danh mục này",
                        color = HighlandText.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                productChunks.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { product ->
                            ProductItem(
                                imageUrl = product.imageUrl,
                                name = product.name,
                                price = FormatUtils.formatPrice(product.price.toInt()),
                                isNew = true,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate("product/${product.id}") }
                            )
                        }

                        if (rowItems.size == 1) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ProductItem(
    imageUrl: String,
    name: String,
    price: String,
    isNew: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            )

            if (isNew) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                        .background(Color(0xFFFF3333), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "NEW",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            color = Color(0xFF543310),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = price,
                color = Color(0xFF543310),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF74512D), CircleShape)
                    .clickable {  },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.button_plus),
                    contentDescription = "Add to cart",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
@Composable
fun ServiceIconCard(
    icon: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable(onClick = onClick)
    ) {

        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = HighlandText,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) HighlandRed else Color.White,
        shadowElevation = if (selected) 4.dp else 2.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            color = if (selected) HighlandWhite else HighlandText,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BrewCoTheme {
        MainScreen()
    }
}
