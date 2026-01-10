package com.example.brewco.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.data.models.CartItem
import com.example.brewco.data.models.CheckoutSummary
import com.example.brewco.ui.mock.MockCartStore
import com.example.brewco.ui.theme.BrewCoTheme
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import com.example.brewco.ui.theme.HighlandWhite
import com.example.brewco.utils.FormatUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit = {},
    onNavigateToPayment: (CheckoutSummary) -> Unit = {}
) {
    val cartItems = MockCartStore.observeCartItems()
    var selectedEntryIds by remember { mutableStateOf(setOf<String>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var removingEntryId by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val selectedItems = cartItems.filter { selectedEntryIds.contains(it.entryId) }
    val totalPrice = selectedItems.sumOf { it.price * it.quantity }
    val allSelected = cartItems.isNotEmpty() && selectedEntryIds.size == cartItems.size

    fun handleRemove(cartItem: CartItem) {
        removingEntryId = cartItem.entryId
        coroutineScope.launch {
            delay(250)
            MockCartStore.removeItems(listOf(cartItem.entryId))
            selectedEntryIds = selectedEntryIds - cartItem.entryId
            removingEntryId = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Giỏ hàng",
                        color = HighlandWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = HighlandWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HighlandRed)
            )
        },
        containerColor = HighlandWhite
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                cartItems.isEmpty() -> {
                    EmptyCartState(modifier = Modifier.padding(paddingValues))
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable(enabled = cartItems.isNotEmpty()) {
                                    selectedEntryIds = if (allSelected) emptySet() else cartItems.map { it.entryId }.toSet()
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = allSelected,
                                    onCheckedChange = {
                                        selectedEntryIds = if (it) cartItems.map { item -> item.entryId }.toSet() else emptySet()
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Chọn tất cả",
                                    fontSize = 15.sp,
                                    color = HighlandText,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "${selectedItems.size}/${cartItems.size} món",
                                color = HighlandText.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }

                       
                    }
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = HighlandRed,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun EmptyCartState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_cart),
            contentDescription = "Empty Cart",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 24.dp)
        )
        Text(
            text = "Giỏ hàng trống",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = HighlandText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Thêm sản phẩm vào giỏ hàng để tiếp tục!",
            fontSize = 16.sp,
            color = HighlandText.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartScreenPreview() {
    BrewCoTheme {
        CartScreen()
    }
}
