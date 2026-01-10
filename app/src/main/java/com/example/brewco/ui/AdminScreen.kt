package com.example.brewco.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.ui.theme.*

/* -------------------- MOCK MODELS -------------------- */

data class AdminProduct(
    val id: String,
    val name: String,
    val price: Int,
    val category: String
)

private val mockProducts = listOf(
    AdminProduct("P01", "Latte", 45000, "Coffee"),
    AdminProduct("P02", "Cappuccino", 48000, "Coffee"),
    AdminProduct("P03", "Croissant", 32000, "Bakery")
)

/* -------------------- ADMIN SCREEN -------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onBackClick: () -> Unit = {}
) {
    val tabs = listOf("DASHBOARD", "PRODUCTS", "VOUCHERS", "CATEGORIES")
    var selectedTab by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Admin Panel",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, null, tint = HighlandWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HighlandRed
                )
            )
        },
        floatingActionButton = {
            if (selectedTab != 0) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = HighlandRed
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        },
        containerColor = HighlandWhite
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = HighlandWhite,
                contentColor = HighlandRed
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            showAddDialog = false
                        },
                        text = {
                            Text(title, fontWeight = FontWeight.Bold)
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> DashboardTab()
                1 -> ProductTab(mockProducts)
                2 -> PlaceholderTab("Voucher management")
                3 -> PlaceholderTab("Category management")
            }
        }
    }
}

/* -------------------- DASHBOARD -------------------- */

@Composable
fun DashboardTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Dashboard", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardCard("Orders", "128")
            DashboardCard("Revenue", "18.5M")
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardCard("Products", "42")
            DashboardCard("Pending", "6")
        }
    }
}

@Composable
fun DashboardCard(title: String, value: String) {
    Card(
        modifier = Modifier
            //.weight(1f)
            .height(100.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, color = HighlandText)
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/* -------------------- PRODUCTS -------------------- */

@Composable
fun ProductTab(products: List<AdminProduct>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(products) { product ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(product.name, fontWeight = FontWeight.Bold)
                    Text("Category: ${product.category}")
                    Text("Price: ${product.price}đ", color = HighlandRed)
                }
            }
        }
    }
}

/* -------------------- PLACEHOLDER -------------------- */

@Composable
fun PlaceholderTab(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(title, color = HighlandText)
    }
}

/* -------------------- PREVIEW -------------------- */

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    BrewCoTheme {
        AdminScreen()
    }
}
