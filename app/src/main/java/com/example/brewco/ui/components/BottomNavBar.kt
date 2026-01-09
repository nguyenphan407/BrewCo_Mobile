package com.example.brewco.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandWhite

enum class NavigationItem {
    HOME, ORDER, REWARDS, MORE
}

@Composable
fun BottomNavBar(
    currentItem: NavigationItem,
    onNavigate: (String) -> Unit
) {
    Surface(
        color = HighlandRed,
        shape = RectangleShape,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavBarItem(
                iconRes = R.drawable.ic_home,
                label = "Trang chủ",
                isActive = currentItem == NavigationItem.HOME,
                onClick = { onNavigate("home") }
            )

            NavBarItem(
                iconRes = R.drawable.ic_booked,
                label = "Đặt hàng",
                isActive = currentItem == NavigationItem.ORDER,
                onClick = { onNavigate("order") }
            )

            NavBarItem(
                iconRes = R.drawable.ic_voucher,
                label = "Ưu đãi",
                isActive = currentItem == NavigationItem.REWARDS,
                onClick = { onNavigate("rewards") }
            )

            NavBarItem(
                iconRes = R.drawable.ic_differ,
                label = "Khác",
                isActive = currentItem == NavigationItem.MORE,
                onClick = { onNavigate("differ") }
            )
        }
    }
}

@Composable
private fun NavBarItem(
    iconRes: Int,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(HighlandWhite)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = HighlandWhite,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )

        if (isActive) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(32.dp)
                    .height(3.dp)
                    .background(
                        color = HighlandWhite,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}
