package com.example.brewco.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.ScrollState
import com.example.brewco.R
import com.example.brewco.data.dto.ProductResponse
import com.example.brewco.ui.CategoryItem
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import com.example.brewco.ui.theme.HighlandWhite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.ScrollState

@Composable
fun CategoryButton(
	category: CategoryItem,
	scrollState: ScrollState,
	sectionRefs: Map<String, MutableState<Int>>,
	coroutineScope: CoroutineScope,
	animationDuration: Int = 300
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.width(80.dp)
			.clickable {
				coroutineScope.launch {
					sectionRefs[category.id]?.value?.let { position ->
						val distance = position - scrollState.value
						scrollState.animateScrollBy(
							value = distance.toFloat(),
							animationSpec = androidx.compose.animation.core.tween(durationMillis = animationDuration)
						)
					}
				}
			}
	) {
		Image(
			painter = painterResource(id = category.imageRes),
			contentDescription = category.title,
			modifier = Modifier
				.size(60.dp)
				.clip(RoundedCornerShape(12.dp)),
			contentScale = ContentScale.Crop
		)

		Spacer(modifier = Modifier.height(4.dp))

		Text(
			text = category.title,
			fontSize = 12.sp,
			textAlign = TextAlign.Center,
			color = HighlandText,
			lineHeight = 14.sp
		)
	}
}

@Composable
fun CategorySheetItem(
	category: CategoryItem,
	onClick: () -> Unit
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.width(100.dp)
			.clickable(onClick = onClick)
	) {
		Image(
			painter = painterResource(id = category.imageRes),
			contentDescription = category.title,
			modifier = Modifier
				.size(70.dp)
				.clip(RoundedCornerShape(12.dp)),
			contentScale = ContentScale.Crop
		)

		Spacer(modifier = Modifier.height(6.dp))

		Text(
			text = category.title.replace("\n", " "),
			fontSize = 12.sp,
			textAlign = TextAlign.Center,
			color = HighlandText,
			lineHeight = 14.sp
		)
	}
}

@Composable
fun SearchDialogWithResults(
	searchQuery: String,
	onSearchQueryChange: (String) -> Unit,
	searchResults: List<ProductResponse>,
	isLoading: Boolean,
	onDismiss: () -> Unit,
	onProductClick: (ProductResponse) -> Unit
) {
	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(
			dismissOnBackPress = true,
			dismissOnClickOutside = true,
			usePlatformDefaultWidth = false
		)
	) {
		Surface(
			modifier = Modifier
				.fillMaxWidth()
				.fillMaxHeight(0.9f)
				.padding(16.dp),
			shape = RoundedCornerShape(16.dp),
			color = HighlandWhite
		) {
			Column(
				modifier = Modifier.padding(16.dp)
			) {
				Text(
					text = "Tìm kiếm",
					fontSize = 20.sp,
					fontWeight = FontWeight.Bold,
					color = HighlandText
				)

				Spacer(modifier = Modifier.height(16.dp))

				OutlinedTextField(
					value = searchQuery,
					onValueChange = onSearchQueryChange,
					modifier = Modifier.fillMaxWidth(),
					placeholder = { Text("Nhập tên sản phẩm...") },
					colors = OutlinedTextFieldDefaults.colors(
						focusedBorderColor = HighlandRed,
						unfocusedBorderColor = HighlandText.copy(alpha = 0.3f)
					)
				)

				Spacer(modifier = Modifier.height(16.dp))

				Column(
					modifier = Modifier
						.weight(1f)
						.verticalScroll(rememberScrollState())
				) {
					when {
						isLoading -> {
							Box(
								modifier = Modifier
									.fillMaxWidth()
									.padding(32.dp),
								contentAlignment = Alignment.Center
							) {
								CircularProgressIndicator(color = HighlandRed)
							}
						}
						searchQuery.isBlank() -> {
							Text(
								text = "Nhập từ khóa để tìm kiếm sản phẩm",
								fontSize = 14.sp,
								color = HighlandText.copy(alpha = 0.6f),
								modifier = Modifier.padding(16.dp)
							)
						}
						searchResults.isEmpty() -> {
							Text(
								text = "Không tìm thấy sản phẩm nào",
								fontSize = 14.sp,
								color = HighlandText.copy(alpha = 0.6f),
								modifier = Modifier.padding(16.dp)
							)
						}
						else -> {
							Text(
								text = "Tìm thấy ${searchResults.size} sản phẩm",
								fontSize = 14.sp,
								fontWeight = FontWeight.Medium,
								color = HighlandText,
								modifier = Modifier.padding(bottom = 8.dp)
							)

							searchResults.forEach { product ->
								ProductCard(
									product = product,
									onClick = { onProductClick(product) }
								)
							}
						}
					}
				}

				Spacer(modifier = Modifier.height(16.dp))

				Button(
					onClick = onDismiss,
					modifier = Modifier.fillMaxWidth(),
					colors = ButtonDefaults.buttonColors(
						containerColor = HighlandRed
					)
				) {
					Text("Đóng", color = HighlandWhite)
				}
			}
		}
	}
}

@Composable
fun SearchDialog(
	searchQuery: String,
	onSearchQueryChange: (String) -> Unit,
	onDismiss: () -> Unit
) {
	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(
			dismissOnBackPress = true,
			dismissOnClickOutside = true
		)
	) {
		Surface(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			shape = RoundedCornerShape(16.dp),
			color = HighlandWhite
		) {
			Column(
				modifier = Modifier.padding(16.dp)
			) {
				Text(
					text = "Tìm kiếm",
					fontSize = 20.sp,
					fontWeight = FontWeight.Bold,
					color = HighlandText
				)

				Spacer(modifier = Modifier.height(16.dp))

				OutlinedTextField(
					value = searchQuery,
					onValueChange = onSearchQueryChange,
					modifier = Modifier.fillMaxWidth(),
					placeholder = { Text("Nhập tên sản phẩm...") },
					colors = OutlinedTextFieldDefaults.colors(
						focusedBorderColor = HighlandRed,
						unfocusedBorderColor = HighlandText.copy(alpha = 0.3f)
					)
				)

				Spacer(modifier = Modifier.height(16.dp))

				Button(
					onClick = onDismiss,
					modifier = Modifier.fillMaxWidth(),
					colors = ButtonDefaults.buttonColors(
						containerColor = HighlandRed
					)
				) {
					Text("Đóng", color = HighlandWhite)
				}
			}
		}
	}
}
