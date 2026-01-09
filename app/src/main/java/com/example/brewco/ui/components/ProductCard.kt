package com.example.brewco.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.brewco.data.dto.ProductResponse
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import com.example.brewco.ui.theme.HighlandWhite
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductCard(
    product: ProductResponse,
    onClick: (ProductResponse) -> Unit = {},
    showNewBadge: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(product) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            if (showNewBadge) {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(HighlandRed, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = "NEW",
                        color = HighlandWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = HighlandText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatPrice(product.price),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = HighlandRed
            )
        }
    }
}

private fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${formatter.format(price)}đ"
}
