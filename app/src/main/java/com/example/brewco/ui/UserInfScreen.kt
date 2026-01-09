package com.example.brewco.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfScreen(
    onBackClick: () -> Unit,
    onUpdateInfoClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {}
) {
    /* -------- UI State (mock / init tối thiểu) -------- */

    var name by remember { mutableStateOf("Nguyễn Văn A") }
    var email by remember { mutableStateOf("nguyenvana@email.com") }
    var birthDate by remember { mutableStateOf("01/01/1995") }
    var gender by remember { mutableStateOf("Nam") }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }

    /* -------- Date Picker (thuần UI) -------- */

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val datePickerDialog = DatePickerDialog(
        context,
        R.style.DatePickerTheme,
        { _, year, month, day ->
            calendar.set(year, month, day)
            birthDate = dateFormatter.format(calendar.time)
        },
        calendar.get(Calendar.YEAR) - 18,
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.maxDate = System.currentTimeMillis()
    }

    /* -------- Gender Dropdown -------- */

    var expandedGenderDropdown by remember { mutableStateOf(false) }
    val genderOptions = listOf("Nam", "Nữ", "Khác")

    /* ================= UI ================= */

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cập nhật thông tin",
                        color = HighlandWhite,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = HighlandWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HighlandRed
                )
            )
        },
        containerColor = HighlandWhite
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            /* -------- Header -------- */

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HighlandRed)
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.coffee_beans),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(HighlandWhite, CircleShape)
                            .padding(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = null,
                            tint = HighlandRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighlandWhite
                )
            }

            Spacer(Modifier.height(16.dp))

            /* -------- Personal Info -------- */

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    Text(
                        "Thông tin cá nhân",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandRed
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Họ và tên") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HighlandRed,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = HighlandRed,
                            focusedLabelColor = HighlandRed,
                            focusedTextColor = HighlandText,
                            unfocusedTextColor = HighlandText
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HighlandRed,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = HighlandRed,
                            focusedLabelColor = HighlandRed,
                            focusedTextColor = HighlandText,
                            unfocusedTextColor = HighlandText
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                        OutlinedTextField(
                            value = birthDate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ngày sinh") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HighlandRed,
                                unfocusedBorderColor = Color.LightGray,
                                cursorColor = HighlandRed,
                                focusedLabelColor = HighlandRed,
                                focusedTextColor = HighlandText,
                                unfocusedTextColor = HighlandText
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { datePickerDialog.show() }
                        )

                        ExposedDropdownMenuBox(
                            expanded = expandedGenderDropdown,
                            onExpandedChange = {
                                expandedGenderDropdown = !expandedGenderDropdown
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = gender,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Giới tính") },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = HighlandRed,
                                    unfocusedBorderColor = Color.LightGray,
                                    cursorColor = HighlandRed,
                                    focusedLabelColor = HighlandRed,
                                    focusedTextColor = HighlandText,
                                    unfocusedTextColor = HighlandText
                                ),
                                modifier = Modifier.menuAnchor(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandedGenderDropdown
                                    )
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = expandedGenderDropdown,
                                onDismissRequest = {
                                    expandedGenderDropdown = false
                                }
                            ) {
                                genderOptions.forEach {
                                    DropdownMenuItem(
                                        text = { Text(it, color = CafeBrown) },
                                        onClick = {
                                            gender = it
                                            expandedGenderDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            /* -------- Security -------- */

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    Text(
                        "Bảo mật",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlandRed
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Mật khẩu hiện tại") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HighlandRed,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = HighlandRed,
                            focusedLabelColor = HighlandRed,
                            focusedTextColor = HighlandText,
                            unfocusedTextColor = HighlandText
                        ),
                        visualTransformation =
                            if (showCurrentPassword)
                                VisualTransformation.None
                            else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton {
                                showCurrentPassword = !showCurrentPassword
                            }
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Mật khẩu mới") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HighlandRed,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = HighlandRed,
                            focusedLabelColor = HighlandRed,
                            focusedTextColor = HighlandText,
                            unfocusedTextColor = HighlandText
                        ),
                        visualTransformation =
                            if (showNewPassword)
                                VisualTransformation.None
                            else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton {
                                showNewPassword = !showNewPassword
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            /* -------- Actions -------- */

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    onClick = onUpdateInfoClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HighlandRed)
                ) {
                    Text("Cập nhật thông tin", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onDeleteAccountClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = null,
                        tint = HighlandRed
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Xóa tài khoản", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun ColumnScope.IconButton(function2: () -> Unit) {}


@Preview(showBackground = true)
@Composable
fun UserInfScreenPreview() {
    BrewCoTheme {
        UserInfScreen(onBackClick = {})
    }
}
