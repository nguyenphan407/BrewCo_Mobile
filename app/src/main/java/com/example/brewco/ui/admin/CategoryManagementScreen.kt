package com.example.brewco.ui.admin

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.CategoryListResponse
import com.example.brewco.data.dto.CategoryRequest
import com.example.brewco.data.dto.CategoryResponse
import com.example.brewco.ui.theme.CafeBrown
import com.example.brewco.ui.theme.CafeOrange
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen() {
    var categories by remember { mutableStateOf<List<CategoryResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryResponse?>(null) }
    var nameInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }

    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    fun loadCategories() {
        isLoading = true
        errorMessage = null
        ApiClient.apiService.getCategories().enqueue(object : Callback<CategoryListResponse> {
            override fun onResponse(
                call: Call<CategoryListResponse>,
                response: Response<CategoryListResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    categories = response.body()?.data ?: emptyList()
                } else {
                    errorMessage = "Lỗi lấy danh mục: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<CategoryListResponse>, t: Throwable) {
                isLoading = false
                errorMessage = "Lỗi kết nối: ${t.localizedMessage}"
            }
        })
    }

    fun resetForm(category: CategoryResponse?) {
        editingCategory = category
        nameInput = category?.name ?: ""
        descriptionInput = category?.description ?: ""
        validationError = null
    }

    fun createCategory() {
        validationError = null
        val trimmedName = nameInput.trim()
        val trimmedDesc = descriptionInput.trim()
        if (trimmedName.isEmpty()) {
            validationError = "Tên danh mục không được để trống"
            return
        }

        isSubmitting = true
        ApiClient.apiService.createCategory(CategoryRequest(trimmedName, trimmedDesc))
            .enqueue(object : Callback<CategoryResponse> {
                override fun onResponse(
                    call: Call<CategoryResponse>,
                    response: Response<CategoryResponse>
                ) {
                    isSubmitting = false
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Tạo danh mục thành công", Toast.LENGTH_SHORT).show()
                        showDialog = false
                        loadCategories()
                    } else {
                        validationError = "Tạo thất bại (${response.code()})"
                    }
                }

                override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                    isSubmitting = false
                    validationError = "Lỗi kết nối: ${t.localizedMessage}"
                }
            })
    }

    fun updateCategory() {
        val current = editingCategory ?: return
        validationError = null
        val trimmedName = nameInput.trim()
        val trimmedDesc = descriptionInput.trim()
        if (trimmedName.isEmpty()) {
            validationError = "Tên danh mục không được để trống"
            return
        }

        isSubmitting = true
        ApiClient.apiService.updateCategory(current.id.toString(), CategoryRequest(trimmedName, trimmedDesc))
            .enqueue(object : Callback<CategoryResponse> {
                override fun onResponse(
                    call: Call<CategoryResponse>,
                    response: Response<CategoryResponse>
                ) {
                    isSubmitting = false
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                        showDialog = false
                        loadCategories()
                    } else {
                        validationError = "Cập nhật thất bại (${response.code()})"
                    }
                }

                override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                    isSubmitting = false
                    validationError = "Lỗi kết nối: ${t.localizedMessage}"
                }
            })
    }

    fun deleteCategory() {
        val current = editingCategory ?: return
        isSubmitting = true
        ApiClient.apiService.deleteCategory(current.id.toString()).enqueue(object : Callback<okhttp3.ResponseBody> {
            override fun onResponse(call: Call<okhttp3.ResponseBody>, response: Response<okhttp3.ResponseBody>) {
                isSubmitting = false
                showDeleteDialog = false
                if (response.isSuccessful) {
                    Toast.makeText(context, "Đã xoá danh mục", Toast.LENGTH_SHORT).show()
                    loadCategories()
                } else {
                    Toast.makeText(
                        context,
                        "Xoá thất bại (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<okhttp3.ResponseBody>, t: Throwable) {
                isSubmitting = false
                showDeleteDialog = false
                Toast.makeText(
                    context,
                    "Lỗi kết nối: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    LaunchedEffect(Unit) {
        loadCategories()
    }

    LaunchedEffect(searchQuery) {
        delay(300)
        debouncedQuery = searchQuery
    }

    androidx.compose.material3.Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin - Quản lý danh mục", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CafeBrown),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    resetForm(null)
                    showDialog = true
                },
                containerColor = CafeOrange,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm danh mục")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = errorMessage ?: "", color = Color.Red)
                    }
                }

                categories.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Chưa có danh mục", color = CafeBrown)
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Tìm kiếm danh mục") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )

                        val visibleCategories = categories.filter {
                            it.name.contains(debouncedQuery, ignoreCase = true) ||
                                    it.description.contains(debouncedQuery, ignoreCase = true)
                        }

                        if (visibleCategories.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Không tìm thấy danh mục", color = CafeBrown)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(visibleCategories) { category ->
                                    CategoryCard(
                                        category = category,
                                        onEdit = {
                                            resetForm(category)
                                            showDialog = true
                                        },
                                        onDelete = {
                                            resetForm(category)
                                            showDeleteDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        CategoryDialog(
            isEdit = editingCategory != null,
            name = nameInput,
            description = descriptionInput,
            isSubmitting = isSubmitting,
            validationError = validationError,
            onNameChange = { nameInput = it },
            onDescriptionChange = { descriptionInput = it },
            onDismiss = { showDialog = false },
            onSubmit = {
                if (editingCategory == null) createCategory() else updateCategory()
            }
        )
    }

    if (showDeleteDialog && editingCategory != null) {
        DeleteCategoryDialog(
            name = editingCategory?.name.orEmpty(),
            isSubmitting = isSubmitting,
            onDismiss = { showDeleteDialog = false },
            onConfirm = { deleteCategory() }
        )
    }
}

@Composable
private fun CategoryCard(
    category: CategoryResponse,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = category.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = CafeBrown
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.description,
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Chỉnh sửa",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Sửa")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = CafeOrange)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xoá",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Xoá")
                }
            }
        }
    }
}

@Composable
private fun CategoryDialog(
    isEdit: Boolean,
    name: String,
    description: String,
    isSubmitting: Boolean,
    validationError: String?,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = { Text(text = if (isEdit) "Chỉnh sửa danh mục" else "Thêm danh mục") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Tên danh mục") },
                    singleLine = true,
                    enabled = !isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Mô tả") },
                    enabled = !isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                )

                validationError?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = Color.Red, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = onSubmit, enabled = !isSubmitting) {
                Text(text = if (isEdit) "Lưu" else "Thêm")
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!isSubmitting) onDismiss() }) {
                Text("Hủy")
            }
        }
    )
}

@Composable
private fun DeleteCategoryDialog(
    name: String,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = { Text(text = "Xoá danh mục") },
        text = { Text(text = "Bạn có chắc chắn muốn xoá \"$name\"?") },
        confirmButton = {
            Button(onClick = onConfirm, enabled = !isSubmitting) {
                Text("Xoá")
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!isSubmitting) onDismiss() }) {
                Text("Hủy")
            }
        }
    )
}
