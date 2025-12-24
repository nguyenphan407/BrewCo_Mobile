package com.example.brewco.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import com.example.brewco.ui.theme.HighlandWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotiScreen(
    onBackClick: () -> Unit = {}
) {

    val notifications = remember {
        listOf(
            NotificationItem(
                id = "welcome",
                title = "Chào bạn mới",
                message = "Chào mừng bạn đã trở thành viên của Brew Co, chúng tôi luôn mong muốn mang đến cho bạn những trải nghiệm tốt nhất!",
                timestamp = "10/04",
                isRead = false
            ),
            NotificationItem(
                id = "voucher",
                title = "Ưu đãi hôm nay",
                message = "Ghé mục Voucher để đổi Bean và nhận ưu đãi cho đơn hàng tiếp theo nhé.",
                timestamp = "11/04",
                isRead = false
            ),
            NotificationItem(
                id = "shipping",
                title = "Cập nhật giao hàng",
                message = "Đơn hàng của bạn đang được chuẩn bị. Cảm ơn bạn đã chờ!",
                timestamp = "12/04",
                isRead = false
            )
        )
    }

    var notificationsList by remember { mutableStateOf(notifications) }

    Scaffold(
        containerColor = HighlandWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Thông báo",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = HighlandWhite
                        )
                    }
                },
                actions = {

                    IconButton(
                        onClick = {
                            notificationsList = notificationsList.map { it.copy(isRead = true) }
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_checked),
                            contentDescription = "Đánh dấu tất cả là đã đọc",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HighlandRed
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(HighlandWhite)
        ) {
            items(notificationsList) { notification ->
                NotificationCard(
                    notification = notification,
                    onMarkAsRead = {
                        notificationsList = notificationsList.map {
                            if (it.id == notification.id) it.copy(isRead = true) else it
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onMarkAsRead: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onMarkAsRead() },
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFFFE8E8)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(HighlandRed.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_noti),
                    contentDescription = "Notification Icon",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandText
                    )

                    Text(
                        text = notification.timestamp,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = HighlandText.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false
)
