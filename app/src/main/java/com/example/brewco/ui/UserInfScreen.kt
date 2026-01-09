package com.example.brewco.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.ui.theme.*

/**
 * INITIAL VERSION
 * -----------------------
 * - Chưa dùng API
 * - Chưa load dữ liệu thật
 * - Chưa validate input
 * - UI skeleton
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfScreen(
    onBackClick: () -> Unit,
    onUpdateInfoClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {}
) {

    /* =========================
     * Mock state
     * ========================= */

    var name by remember { mutableStateOf("Nguyễn Văn A") }
    var email by remember { mutableStateOf("nguyenvana@gmail.com") }
    var birthDate by remember { mutableStateOf("01/01/2000") }
    var gender by remember { mutableStateOf("Nam") }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }

    var expandedGenderDropdown by remember { mutableStateOf(false) }

    val genderOptions = listOf("Nam", "Nữ", "Khác")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cập nhật thông tin",
                        color = HighlandWhite,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
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
                .background(HighlandWhite)
                .verticalScroll(rememberScrollState())
        ) {

            /* =========================
             * Header
             * ========================= */

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HighlandRed)
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.coffee_beans),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
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

            /* =========================
             * Personal Info (Init)
             * ========================= */

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    SectionTitle("Thông tin cá nhân")

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Họ và tên") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                        OutlinedTextField(
                            value = birthDate,
                            onValueChange = { },
                            label = { Text("Ngày sinh") },
                            modifier = Modifier.weight(1f),
                            readOnly = true,
                            shape = RoundedCornerShape(12.dp)
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
                                label = { Text("Giới tính") },
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandedGenderDropdown
                                    )
                                },
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = expandedGenderDropdown,
                                onDismissRequest = {
                                    expandedGenderDropdown = false
                                }
                            ) {
                                genderOptions.forEach {
                                    DropdownMenuItem(
                                        text = { Text(it) },
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

            /* =========================
             * Security (Init)
             * ========================= */

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    SectionTitle("Bảo mật")

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Mật khẩu hiện tại") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (showCurrentPassword)
                            VisualTransformation.None
                        else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    showCurrentPassword = !showCurrentPassword
                                }
                            ) {
                                Icon(
                                    painter = painterResource(
                                        if (showCurrentPassword)
                                            R.drawable.eye
                                        else R.drawable.close_eye
                                    ),
                                    contentDescription = null
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Mật khẩu mới") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (showNewPassword)
                            VisualTransformation.None
                        else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    showNewPassword = !showNewPassword
                                }
                            ) {
                                Icon(
                                    painter = painterResource(
                                        if (showNewPassword)
                                            R.drawable.eye
                                        else R.drawable.close_eye
                                    ),
                                    contentDescription = null
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            /* =========================
             * Actions
             * ========================= */

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
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HighlandRed
                    )
                ) {
                    Text(
                        text = "Cập nhật thông tin",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onDeleteAccountClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = null,
                        tint = HighlandRed
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Xóa tài khoản",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

/* =========================
 * Small components
 * ========================= */

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = HighlandRed
    )
}

@Preview(showBackground = true)
@Composable
fun UserInfScreenInitPreview() {
    BrewCoTheme {
        UserInfScreen(onBackClick = {})
    }
}
