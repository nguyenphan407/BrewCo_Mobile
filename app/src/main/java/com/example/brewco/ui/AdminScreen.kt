package com.example.brewco.ui

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.brewco.R
import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.CategoryListResponse
import com.example.brewco.data.dto.CategoryRequest
import com.example.brewco.data.dto.CategoryResponse
import com.example.brewco.data.dto.OrderResponse
import com.example.brewco.data.dto.VoucherRequest
import com.example.brewco.data.dto.VoucherResponse
import com.example.brewco.ui.admin.DeleteConfirmationDialog
import com.example.brewco.ui.admin.ProductManagementContent
import com.example.brewco.ui.admin.VoucherDetailDialog
import com.example.brewco.ui.admin.VoucherFormState
import com.example.brewco.utils.FormatUtils
import com.example.brewco.ui.theme.CafeBeige
import com.example.brewco.ui.theme.CafeBrown
import com.example.brewco.ui.theme.CafeButtonBackground
import com.example.brewco.ui.theme.CafeLightBrown
import com.example.brewco.ui.theme.CafeLoginBackground
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onLogoutClick: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("DASHBOARD", "SẢN PHẨM", "VOUCHER", "DANH MỤC")

    // Dialog states
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showProductDialog by remember { mutableStateOf(false) }
    var showVoucherDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Café UIT",
                        fontFamily = FontFamily(Font(R.font.agbalumo_regular)),
                        fontSize = 26.sp,
                        color = CafeBrown
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Đăng xuất",
                            tint = CafeBrown
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = CafeBeige
                )
            )
        },
        floatingActionButton = {
            if (selectedTabIndex != 0) { // Không hiển thị FAB ở tab Dashboard
                FloatingActionButton(
                    onClick = {
                        when (selectedTabIndex) {
                            1 -> showProductDialog = true // Tab Sản phẩm
                            2 -> showVoucherDialog = true // Tab Voucher
                            3 -> showCategoryDialog = true // Tab Danh mục
                        }
                    },
                    containerColor = CafeButtonBackground,
                    contentColor = CafeBeige,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm mới"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(CafeLoginBackground)
        ) {
            // Tab Bar
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = CafeBeige,
                contentColor = CafeBrown,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        height = 2.dp,
                        color = CafeBrown
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            // Nội dung tab
            when (selectedTabIndex) {
                0 -> DashboardContent()
                1 -> ProductManagementContent(
                    showAddDialog = showProductDialog,
                    onAddDialogDismiss = { showProductDialog = false }
                )
                2 -> VoucherManagementContent(
                    showAddDialog = showVoucherDialog,
                    onAddDialogDismiss = { showVoucherDialog = false }
                )
                3 -> CategoryManagementContent(
                    showAddDialog = showCategoryDialog,
                    onAddDialogDismiss = { showCategoryDialog = false }
                )
            }
        }
    }
}

data class DashboardMetrics(
    val processedOrders: Int = 0,
    val revenue: Int = 0,
    val productCount: Int = 0,
    val pendingOrders: Int = 0
)

@Composable
fun DashboardContent() {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE) }
    var metrics by remember { mutableStateOf(DashboardMetrics()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun refreshDashboard() {
        val token = sharedPreferences.getString("auth_token", null)
        if (token.isNullOrBlank()) {
            metrics = DashboardMetrics()
            isLoading = false
            errorMessage = "Vui lòng đăng nhập admin để xem thống kê."
            return
        }

        isLoading = true
        var pendingCalls = 2

        fun onCallFinished() {
            pendingCalls -= 1
            if (pendingCalls <= 0) {
                isLoading = false
            }
        }

        ApiClient.apiService.getMyOrders("Bearer $token").enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    val orders = response.body()?.data?.content.orEmpty()
                    val processedOrders = orders.count { it.orderStatus == 1 || it.orderStatus == 2 }
                    val revenue = orders.filter { it.orderStatus == 1 || it.orderStatus == 2 }
                        .sumOf { it.totalPrice }
                    val pendingOrders = orders.count { it.orderStatus == 0 }
                    metrics = metrics.copy(
                        processedOrders = processedOrders,
                        revenue = revenue,
                        pendingOrders = pendingOrders
                    )
                } else {
                    Log.e("Dashboard", "Lấy đơn hàng thất bại ${response.code()}")
                    errorMessage = "Không thể tải đơn hàng (${response.code()})"
                }
                onCallFinished()
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Log.e("Dashboard", "Lỗi tải đơn hàng", t)
                errorMessage = "Lỗi tải đơn hàng: ${t.localizedMessage}"
                onCallFinished()
            }
        })

        val params = JSONObject()
        val pageable = JSONObject().apply {
            put("page", 0)
            put("size", 1)
            val sortArr = JSONArray()
            sortArr.put("name")
            put("sort", sortArr)
        }

        ApiClient.apiService.getProducts(params.toString(), pageable.toString()).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val bodyString = response.body()?.string()
                        val dataObj = bodyString?.let { JSONObject(it).optJSONObject("data") }
                        val total = dataObj?.optInt("total")
                            ?: dataObj?.optInt("size")
                            ?: dataObj?.optJSONArray("items")?.length()
                            ?: 0
                        metrics = metrics.copy(productCount = total)
                    } catch (ex: Exception) {
                        Log.e("Dashboard", "Lỗi phân tích sản phẩm", ex)
                        errorMessage = "Lỗi phân tích dữ liệu sản phẩm: ${ex.localizedMessage}"
                    }
                } else {
                    Log.e("Dashboard", "Lấy sản phẩm thất bại ${response.code()}")
                    errorMessage = "Không thể tải sản phẩm (${response.code()})"
                }
                onCallFinished()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Dashboard", "Lỗi tải sản phẩm", t)
                errorMessage = "Lỗi tải sản phẩm: ${t.localizedMessage}"
                onCallFinished()
            }
        })
    }

    LaunchedEffect(Unit) {
        refreshDashboard()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            errorMessage = null
        }
    }

    val ordersValue = if (isLoading) "..." else metrics.processedOrders.toString()
    val revenueValue = if (isLoading) "..." else FormatUtils.formatPrice(metrics.revenue)
    val productValue = if (isLoading) "..." else metrics.productCount.toString()
    val pendingValue = if (isLoading) "..." else metrics.pendingOrders.toString()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tổng quan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CafeBrown
                )

                IconButton(onClick = { refreshDashboard() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Làm mới",
                        tint = CafeBrown
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardCard(
                    title = "Đơn đã xử lý",
                    value = ordersValue,
                    backgroundColor = CafeBeige,
                    modifier = Modifier.weight(1f)
                )

                DashboardCard(
                    title = "Doanh thu",
                    value = revenueValue,
                    backgroundColor = Color(0xFFF5E6CC),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardCard(
                    title = "Sản phẩm hiện có",
                    value = productValue,
                    backgroundColor = Color(0xFFE6CCCC),
                    modifier = Modifier.weight(1f)
                )

                DashboardCard(
                    title = "Đơn chờ thanh toán",
                    value = pendingValue,
                    backgroundColor = Color(0xFFCCE6D4),
                    modifier = Modifier.weight(1f)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Đơn hàng 7 ngày gần đây",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CafeBrown,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val dailyOrders = listOf(8, 12, 5, 15, 10, 7, 18)
                    val maxValue = dailyOrders.maxOrNull() ?: 1
                    val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        dailyOrders.forEachIndexed { index, count ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = days[index],
                                    color = CafeBrown,
                                    fontSize = 14.sp,
                                    modifier = Modifier.width(30.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(24.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFE8E8E8))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(count.toFloat() / maxValue)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CafeButtonBackground)
                                    ) {}
                                }

                                Text(
                                    text = count.toString(),
                                    color = CafeBrown,
                                    fontSize = 14.sp,
                                    modifier = Modifier.width(30.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CafeButtonBackground)
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    value: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = CafeBrown
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                color = CafeBrown.copy(alpha = 0.8f)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VoucherManagementContent(
    showAddDialog: Boolean = false,
    onAddDialogDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE) }

    var vouchers by remember { mutableStateOf<List<VoucherResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var editingVoucher by remember { mutableStateOf<VoucherResponse?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var voucherPendingDeletion by remember { mutableStateOf<VoucherResponse?>(null) }
    var formState by remember { mutableStateOf(VoucherFormState()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val displayFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }

    fun Double.toInputString(): String {
        val formatted = String.format(Locale.US, "%.2f", this)
        return formatted.trimEnd('0').trimEnd('.')
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseDate(value: String, endOfDay: Boolean = false): LocalDateTime? {
        return try {
            val date = LocalDate.parse(value, displayFormatter)
            if (endOfDay) {
                date.atTime(LocalTime.of(23, 59, 59))
            } else {
                date.atStartOfDay()
            }
        } catch (ex: DateTimeParseException) {
            null
        }
    }

    fun refreshVouchers() {
        isLoading = true
        ApiClient.apiService.getVouchers().enqueue(object : Callback<List<VoucherResponse>> {
            override fun onResponse(
                call: Call<List<VoucherResponse>>,
                response: Response<List<VoucherResponse>>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    vouchers = response.body().orEmpty()
                } else {
                    Log.e("VoucherAdmin", "load vouchers failed ${response.code()}")
                    errorMessage = "Không thể tải voucher (${response.code()})"
                }
            }

            override fun onFailure(call: Call<List<VoucherResponse>>, t: Throwable) {
                isLoading = false
                Log.e("VoucherAdmin", "load vouchers error", t)
                errorMessage = t.message
            }
        })
    }

    fun dismissDialog() {
        showAddEditDialog = false
        editingVoucher = null
        onAddDialogDismiss()
    }

    fun openCreateDialog() {
        isEditing = false
        editingVoucher = null
        formState = VoucherFormState(
            discountPercentage = "",
            startDate = displayFormatter.format(LocalDate.now()),
            endDate = displayFormatter.format(LocalDate.now().plusDays(7))
        )
        showAddEditDialog = true
    }

    fun openEditDialog(voucher: VoucherResponse) {
        isEditing = true
        editingVoucher = voucher
        formState = VoucherFormState(
            id = voucher.id,
            discountPercentage = voucher.discountPercentage.toInputString(),
            startDate = voucher.startDate.format(displayFormatter),
            endDate = voucher.endDate.format(displayFormatter)
        )
        showAddEditDialog = true
    }

    fun resolveToken(): String? {
        val token = sharedPreferences.getString("auth_token", null)
        return if (token.isNullOrBlank()) {
            errorMessage = "Vui lòng đăng nhập admin để thao tác voucher."
            null
        } else {
            "Bearer $token"
        }
    }

    fun submitVoucher() {
        val discount = formState.discountPercentage.replace(',', '.').toDoubleOrNull()
        if (discount == null || discount <= 0) {
            errorMessage = "Giá trị giảm phải là số dương."
            return
        }

        val startDate = parseDate(formState.startDate)
        val endDate = parseDate(formState.endDate, endOfDay = true)

        if (startDate == null || endDate == null) {
            errorMessage = "Ngày không hợp lệ. Định dạng dd/MM/yyyy."
            return
        }

        if (endDate.isBefore(startDate)) {
            errorMessage = "Ngày kết thúc phải sau ngày bắt đầu."
            return
        }

        val token = resolveToken() ?: return
        val request = VoucherRequest(
            discountPercentage = discount,
            startDate = startDate,
            endDate = endDate
        )

        isSubmitting = true
        val call = if (isEditing && editingVoucher != null) {
            ApiClient.apiService.updateVoucher(token, editingVoucher!!.id, request)
        } else {
            ApiClient.apiService.createVoucher(token, request)
        }

        call.enqueue(object : Callback<VoucherResponse> {
            override fun onResponse(
                call: Call<VoucherResponse>,
                response: Response<VoucherResponse>
            ) {
                isSubmitting = false
                if (response.isSuccessful) {
                    Toast.makeText(
                        context,
                        if (isEditing) "Đã cập nhật voucher" else "Đã tạo voucher",
                        Toast.LENGTH_SHORT
                    ).show()
                    dismissDialog()
                    refreshVouchers()
                } else {
                    Log.e("VoucherAdmin", "save voucher failed ${response.code()}")
                    errorMessage = "Thao tác thất bại (${response.code()})"
                }
            }

            override fun onFailure(call: Call<VoucherResponse>, t: Throwable) {
                isSubmitting = false
                Log.e("VoucherAdmin", "save voucher error", t)
                errorMessage = t.message
            }
        })
    }

    fun deleteVoucher() {
        val voucher = voucherPendingDeletion ?: return
        val token = resolveToken() ?: return
        isDeleting = true
        ApiClient.apiService.deleteVoucher(token, voucher.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                isDeleting = false
                showDeleteDialog = false
                voucherPendingDeletion = null
                if (response.isSuccessful) {
                    Toast.makeText(context, "Đã xóa voucher", Toast.LENGTH_SHORT).show()
                    refreshVouchers()
                } else {
                    Log.e("VoucherAdmin", "delete voucher failed ${response.code()}")
                    errorMessage = "Không thể xóa voucher (${response.code()})"
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                isDeleting = false
                showDeleteDialog = false
                voucherPendingDeletion = null
                Log.e("VoucherAdmin", "delete voucher error", t)
                errorMessage = t.message
            }
        })
    }

    LaunchedEffect(Unit) {
        refreshVouchers()
    }

    LaunchedEffect(showAddDialog) {
        if (showAddDialog) {
            openCreateDialog()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            errorMessage = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CafeButtonBackground)
                }
            }

            vouchers.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Chưa có voucher nào",
                        color = CafeBrown,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(vouchers, key = { it.id }) { voucher ->
                        VoucherItem(
                            voucher = voucher,
                            onEditClick = { openEditDialog(voucher) },
                            onDeleteClick = {
                                voucherPendingDeletion = voucher
                                showDeleteDialog = true
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        if (isSubmitting || isDeleting) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CafeButtonBackground.copy(alpha = 0.8f))
            }
        }
    }

    if (showAddEditDialog) {
        VoucherDetailDialog(
            isVisible = true,
            isEditing = isEditing,
            formState = formState,
            isSubmitting = isSubmitting,
            onFormChange = { formState = it },
            onDismiss = {
                if (!isSubmitting) {
                    dismissDialog()
                }
            },
            onSave = { submitVoucher() }
        )
    }

    if (showDeleteDialog && voucherPendingDeletion != null) {
        DeleteConfirmationDialog(
            isVisible = true,
            itemName = voucherPendingDeletion?.id ?: "",
            onDismiss = {
                showDeleteDialog = false
                voucherPendingDeletion = null
            },
            onConfirm = { deleteVoucher() }
        )
    }
}

@Composable
fun CategoryManagementContent(
    showAddDialog: Boolean = false,
    onAddDialogDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    var categories by remember { mutableStateOf<List<CategoryResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryResponse?>(null) }
    var initialDescription by remember { mutableStateOf("") }
    var initialName by remember { mutableStateOf("") }
    var pendingDelete by remember { mutableStateOf<CategoryResponse?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    fun refreshCategories() {
        isLoading = true
        ApiClient.apiService.getCategories().enqueue(object : Callback<CategoryListResponse> {
            override fun onResponse(
                call: Call<CategoryListResponse>,
                response: Response<CategoryListResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    categories = response.body()?.data.orEmpty()
                } else {
                    Log.e("CategoryAdmin", "load categories failed ${response.code()}")
                    errorMessage = "Không thể tải danh mục (${response.code()})"
                }
            }

            override fun onFailure(call: Call<CategoryListResponse>, t: Throwable) {
                isLoading = false
                Log.e("CategoryAdmin", "load categories error", t)
                errorMessage = t.message
            }
        })
    }

    fun closeDialog(resetExternalFlag: Boolean = false) {
        showAddEditDialog = false
        editingCategory = null
        initialName = ""
        initialDescription = ""
        if (resetExternalFlag || showAddDialog) {
            onAddDialogDismiss()
        }
    }

    fun submitCategory(name: String, description: String) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            errorMessage = "Tên danh mục không được để trống."
            return
        }

        val request = CategoryRequest(
            name = trimmedName,
            description = description.trim()
        )
        isSubmitting = true
        val call = if (editingCategory == null) {
            ApiClient.apiService.createCategory(request)
        } else {
            ApiClient.apiService.updateCategory(editingCategory!!.id.toString(), request)
        }

        call.enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                isSubmitting = false
                if (response.isSuccessful) {
                    Toast.makeText(
                        context,
                        if (editingCategory == null) "Đã tạo danh mục" else "Đã cập nhật danh mục",
                        Toast.LENGTH_SHORT
                    ).show()
                    closeDialog(resetExternalFlag = true)
                    refreshCategories()
                } else {
                    Log.e("CategoryAdmin", "save category failed ${response.code()}")
                    errorMessage = "Không thể lưu danh mục (${response.code()})"
                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                isSubmitting = false
                Log.e("CategoryAdmin", "save category error", t)
                errorMessage = t.message
            }
        })
    }

    fun deleteCategory(category: CategoryResponse) {
        isDeleting = true
        ApiClient.apiService.deleteCategory(category.id.toString()).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                isDeleting = false
                showDeleteDialog = false
                pendingDelete = null
                if (response.isSuccessful) {
                    Toast.makeText(context, "Đã xóa danh mục", Toast.LENGTH_SHORT).show()
                    refreshCategories()
                } else {
                    Log.e("CategoryAdmin", "delete category failed ${response.code()}")
                    errorMessage = "Không thể xóa danh mục (${response.code()})"
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                isDeleting = false
                showDeleteDialog = false
                pendingDelete = null
                Log.e("CategoryAdmin", "delete category error", t)
                errorMessage = t.message
            }
        })
    }

    LaunchedEffect(Unit) {
        refreshCategories()
    }

    LaunchedEffect(showAddDialog) {
        if (showAddDialog) {
            editingCategory = null
            initialName = ""
            initialDescription = ""
            showAddEditDialog = true
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            errorMessage = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CafeButtonBackground)
                }
            }

            categories.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Chưa có danh mục nào",
                        color = CafeBrown,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories, key = { it.id }) { category ->
                        CategoryItem(
                            name = category.name,
                            description = category.description,
                            onEditClick = {
                                editingCategory = category
                                initialName = category.name
                                initialDescription = category.description
                                showAddEditDialog = true
                            },
                            onDeleteClick = {
                                pendingDelete = category
                                showDeleteDialog = true
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        if (isSubmitting || isDeleting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CafeButtonBackground)
            }
        }
    }

    if (showAddEditDialog) {
        CategoryDialog(
            isEditing = editingCategory != null,
            categoryName = initialName,
            categoryDescription = initialDescription,
            isSubmitting = isSubmitting,
            onDismiss = {
                if (!isSubmitting) {
                    closeDialog(resetExternalFlag = true)
                }
            },
            onSave = { name, description ->
                initialName = name
                initialDescription = description
                submitCategory(name, description)
            }
        )
    }

    if (showDeleteDialog && pendingDelete != null) {
        DeleteConfirmationDialog(
            isVisible = true,
            itemName = pendingDelete?.name ?: "",
            onDismiss = {
                showDeleteDialog = false
                pendingDelete = null
            },
            onConfirm = {
                pendingDelete?.let { deleteCategory(it) }
            }
        )
    }
}

@Composable
fun CategoryItem(
    name: String,
    description: String? = null,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tên danh mục
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Medium,
                    color = CafeBrown,
                    fontSize = 16.sp
                )

                if (!description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        color = CafeLightBrown,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Nút chỉnh sửa
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = CafeBeige,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Chỉnh sửa",
                    tint = CafeBrown,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Nút xóa
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = Color(0xFFFFEBEE),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryDialog(
    isEditing: Boolean = false,
    categoryName: String = "",
    categoryDescription: String = "",
    isSubmitting: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(categoryName) }
    var description by remember { mutableStateOf(categoryDescription) }

    Dialog(onDismissRequest = onDismiss) {
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
                    .padding(20.dp)
            ) {
                Text(
                    text = if (isEditing) "Chỉnh Sửa Danh Mục" else "Thêm Danh Mục Mới",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = CafeBrown
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên danh mục") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSubmitting,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CafeBrown,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = CafeBrown
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    enabled = !isSubmitting,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CafeBrown,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = CafeBrown
                    ),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isSubmitting) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CafeBrown
                        )
                    ) {
                        Text("Hủy")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (name.isNotEmpty()) {
                                onSave(name, description)
                            }
                        },
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CafeButtonBackground
                        )
                    ) {
                        Text(
                            text = if (isEditing) "Cập Nhật" else "Thêm Mới",
                            color = CafeBeige
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(
    name: String,
    price: String,
    imageRes: Int,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hình ảnh sản phẩm
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CafeBeige)
            ) {
                Icon(
                    painter = painterResource(id = imageRes),
                    contentDescription = name,
                    modifier = Modifier
                        .size(70.dp)
                        .padding(8.dp),
                    tint = Color.Unspecified
                )
            }

            // Thông tin sản phẩm
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    color = CafeBrown,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = price,
                    color = CafeLightBrown,
                    fontSize = 14.sp
                )
            }

            // Nút chỉnh sửa
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = CafeBeige,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Chỉnh sửa",
                    tint = CafeBrown,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Nút xóa
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = Color(0xFFFFEBEE),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VoucherItem(
    voucher: VoucherResponse,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val codeLabel = remember(voucher.id) { voucher.id.uppercase(Locale.getDefault()) }
    val discountLabel = remember(voucher.discountPercentage) {
        val formatted = String.format(Locale.US, "%.2f", voucher.discountPercentage)
        formatted.trimEnd('0').trimEnd('.')
    }
    val validityRange = remember(voucher.startDate to voucher.endDate) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        "${voucher.startDate.format(formatter)} - ${voucher.endDate.format(formatter)}"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(110.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CafeButtonBackground.copy(alpha = 0.08f))
                    .border(
                        width = 1.dp,
                        color = CafeButtonBackground,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(vertical = 18.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$discountLabel%",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = CafeButtonBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "GIẢM GIÁ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CafeBrown
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Mã voucher",
                            fontSize = 12.sp,
                            color = CafeBrown.copy(alpha = 0.6f)
                        )
                        Text(
                            text = codeLabel,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CafeBrown
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VoucherActionButton(
                            label = "Sửa",
                            icon = Icons.Default.Edit,
                            backgroundColor = CafeBeige,
                            contentColor = CafeBrown,
                            onClick = onEditClick
                        )
                        VoucherActionButton(
                            label = "Xóa",
                            icon = Icons.Default.Delete,
                            backgroundColor = Color(0xFFFFEBEE),
                            contentColor = Color(0xFFE53935),
                            onClick = onDeleteClick
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = CafeBeige.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Giảm $discountLabel% cho các đơn hàng đủ điều kiện.",
                    color = CafeBrown,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hiệu lực: $validityRange",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun VoucherActionButton(
    label: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(38.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    isVisible: Boolean,
    itemName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Xác nhận xóa",
                color = CafeBrown,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Bạn có chắc chắn muốn xóa \"$itemName\"?",
                color = Color.DarkGray
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373)
                )
            ) {
                Text("Xóa")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CafeBrown
                )
            ) {
                Text("Hủy")
            }
        },
        containerColor = Color.White
    )
}