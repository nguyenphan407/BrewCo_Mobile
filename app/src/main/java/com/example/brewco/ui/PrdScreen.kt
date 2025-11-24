package com.example.brewco.ui

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.brewco.R
import com.example.brewco.data.dto.ProductResponse
import com.example.brewco.ui.mock.MockCartStore
import com.example.brewco.ui.mock.MockCatalog
import com.example.brewco.ui.mock.MockProductDetail
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import com.example.brewco.ui.theme.HighlandWhite
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrdScreen(
    productId: String,
    onBackClick: () -> Unit = {},
    onViewCart: () -> Unit = {},
    onNavigateToMain: () -> Unit = {}
) {
    val detail = remember(productId) { MockCatalog.getDetail(productId) }
    val product = detail?.product

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Không tìm thấy sản phẩm", color = HighlandRed)
        }
        return
    }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var isInWishlist by remember { mutableStateOf(false) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(1) }
    var isAddingToCart by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun handleAddToCart(item: ProductResponse) {
        if (isAddingToCart) return
        coroutineScope.launch {
            isAddingToCart = true
            delay(300)
            MockCartStore.addProduct(item, quantity)
            isAddingToCart = false
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        SuccessDialog(
            onDismiss = {
                showSuccessDialog = false
                onNavigateToMain()
            },
            onViewCart = {
                showSuccessDialog = false
                onViewCart()
            }
        )
    }

    Scaffold(
        containerColor = HighlandWhite,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = HighlandWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isInWishlist = !isInWishlist }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_love),
                            contentDescription = "Yêu thích",
                            tint = if (isInWishlist) Color(0xFFFF6F6F) else HighlandWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HighlandRed)
            )
        },
        bottomBar = {
            BottomSummaryBar(
                totalPrice = product.price * quantity,
                isLoading = isAddingToCart,
                onAddToCart = { handleAddToCart(product) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderImage(detail)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighlandText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatPrice(product.price * quantity),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighlandRed
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (detail.tastingNotes.isNotEmpty()) {
                    TastingNotes(notes = detail.tastingNotes)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = "Mô tả sản phẩm",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighlandText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = product.description,
                    fontSize = 14.sp,
                    color = HighlandText.copy(alpha = 0.85f),
                    lineHeight = 20.sp,
                    maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis
                )

                if (product.description.length > 100) {
                    TextButton(onClick = { isDescriptionExpanded = !isDescriptionExpanded }) {
                        Text(
                            text = if (isDescriptionExpanded) "Thu gọn" else "Xem thêm",
                            color = HighlandRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Số lượng",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighlandText
                )

                Spacer(modifier = Modifier.height(12.dp))

                QuantitySelector(
                    quantity = quantity,
                    onDecrease = { if (quantity > 1) quantity-- },
                    onIncrease = { quantity++ }
                )
            }
        }
    }
}

@Composable
private fun HeaderImage(detail: MockProductDetail) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(HighlandRed.copy(alpha = 0.05f))
    ) {
        AsyncImage(
            model = detail.product.imageUrl,
            contentDescription = detail.product.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopStart)
                .background(HighlandRed, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (detail.isNew) "NEW" else "HOT",
                color = HighlandWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TastingNotes(notes: List<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        notes.forEach { note ->
            Box(
                modifier = Modifier
                    .background(HighlandRed.copy(alpha = 0.1f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = note, color = HighlandRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(HighlandRed.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        IconButton(onClick = onDecrease, enabled = quantity > 1) {
            Text(
                text = "−",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = HighlandRed
            )
        }

        Text(
            text = quantity.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = HighlandText,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        IconButton(onClick = onIncrease) {
            Text(
                text = "+",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = HighlandRed
            )
        }
    }
}

@Composable
private fun BottomSummaryBar(
    totalPrice: Double,
    isLoading: Boolean,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tổng cộng",
                    fontSize = 14.sp,
                    color = HighlandText.copy(alpha = 0.7f)
                )
                Text(
                    text = formatPrice(totalPrice),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighlandRed
                )
            }

            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HighlandRed),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = HighlandWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cart),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Thêm vào giỏ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandWhite
                    )
                }
            }
        }
    }
}

@Composable
private fun SuccessDialog(
    onDismiss: () -> Unit,
    onViewCart: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(24.dp))
                .background(HighlandWhite)
                .padding(24.dp)
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Đóng",
                    tint = HighlandRed
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(8.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_cart),
                    contentDescription = "Success",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Đã thêm vào giỏ hàng!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighlandText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onViewCart,
                        colors = ButtonDefaults.buttonColors(containerColor = HighlandRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Xem giỏ hàng", color = HighlandWhite, fontWeight = FontWeight.Bold)
                    }

                    TextButton(onClick = onDismiss) {
                        Text("Tiếp tục mua sắm", color = HighlandRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${formatter.format(price)}đ"
}
