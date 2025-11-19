package com.example.brewco.ui

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.ui.components.BottomNavBar
import com.example.brewco.ui.components.NavigationItem
import com.example.brewco.ui.components.CategoryButton
import com.example.brewco.ui.components.CategorySheetItem
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import com.example.brewco.ui.theme.HighlandWhite
import kotlinx.coroutines.launch

data class CategoryItem(
    val id: String,
    val title: String,
    val imageRes: Int,
    val categoryId: Int? = null
)

data class Product(
    val id: String,
    val name: String,
    val price: String,
    val imageRes: Int,
    val isNew: Boolean = false
)

data class CollectionBanner(
    val id: String,
    val imageRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookedScreen(
    onNavigationItemClick: (String) -> Unit = {},
    onFavoritesClick: () -> Unit = {}
) {
    var showSearchDialog by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val scrollState = rememberScrollState()

    val categories = listOf(
        CategoryItem("mon_moi", "Món Mới\nPhải Thử", R.drawable.mon_moi_phai_thu, 1),
        CategoryItem("cloud_tea", "Trà Sữa -\nCloudTea", R.drawable.cloud_tea, 2),
        CategoryItem("cloud_fee", "Cà Phê -\nCloudFee", R.drawable.cloud_fee, 3),
        CategoryItem("mon_nong", "Món Nóng", R.drawable.hot_fee, 4),
        CategoryItem("hi_tea", "Trà Trái Cây\n- HiTea", R.drawable.hi_tea, 5),
        CategoryItem("take_away", "Cà Phê -\nTrà Đóng Gói", R.drawable.take_away_fee, 6)
    )

    val collections = listOf(
        CollectionBanner("b1", R.drawable.bst_1),
        CollectionBanner("b2", R.drawable.bst_2),
        CollectionBanner("b3", R.drawable.bst_3)
    )

    if (showCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCategorySheet = false },
            sheetState = sheetState,
            containerColor = HighlandWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HighlandWhite)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Danh mục sản phẩm",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandText,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    androidx.compose.material3.IconButton(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                                showCategorySheet = false
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(28.dp)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = HighlandText
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        categories.take(3).forEach { category ->
                            CategorySheetItem(category = category, onClick = { showCategorySheet = false })
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        categories.takeLast(3).forEach { category ->
                            CategorySheetItem(category = category, onClick = { showCategorySheet = false })
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showCategorySheet = true }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_menu),
                            contentDescription = "Logo",
                            modifier = Modifier.size(32.dp),
                            colorFilter = ColorFilter.tint(HighlandWhite)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Danh mục",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = HighlandWhite
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier.padding(end = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = HighlandWhite,
                                    shape = RoundedCornerShape(size = 20.dp)
                                )
                                .clickable { showSearchDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = "Search",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.padding(end = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = HighlandWhite,
                                    shape = RoundedCornerShape(size = 20.dp)
                                )
                                .clickable { onFavoritesClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_love),
                                contentDescription = "Favorites",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HighlandRed
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                currentItem = NavigationItem.ORDER,
                onNavigate = onNavigationItemClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(HighlandWhite)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.take(4).forEach { category ->
                        CategoryButton(
                            category = category,
                            scrollState = scrollState,
                            sectionRefs = emptyMap(),
                            coroutineScope = coroutineScope,
                            animationDuration = 1000
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.takeLast(2).forEach { category ->
                        CategoryButton(
                            category = category,
                            scrollState = scrollState,
                            sectionRefs = emptyMap(),
                            coroutineScope = coroutineScope,
                            animationDuration = 1000
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    Text(
                        text = "Bộ sưu tập",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(collections) { collection ->
                            Image(
                                painter = painterResource(id = collection.imageRes),
                                contentDescription = "Collection Banner",
                                modifier = Modifier
                                    .height(200.dp)
                                    .fillParentMaxWidth(1f)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
