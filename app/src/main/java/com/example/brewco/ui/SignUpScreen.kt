package com.example.brewco.ui

import android.app.DatePickerDialog
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.ui.theme.BrewCoTheme
import com.example.brewco.ui.theme.CafeBrown
import com.example.brewco.ui.theme.CafeButtonBackground
import com.example.brewco.ui.theme.CafeGrayText
import com.example.brewco.ui.theme.CafeLoginBackground
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onBackClick: () -> Unit = {},
    onSignUpSubmit: (String) -> Unit = {},
    onNavigateToOTP: (String) -> Unit = {}
) {
    var emailAddress by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val genderOptions = listOf("Nam", "Nữ", "Khác")
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val validateAndUpdateDate = { input: String ->
        try {
            if (input.isEmpty()) {
                birthday = input
            } else {
                val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply { isLenient = false }
                df.parse(input)
                birthday = input
            }
        } catch (_: Exception) {
        }
    }

    val datePickerDialog = DatePickerDialog(
        context,
        R.style.DatePickerTheme,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            birthday = dateFormatter.format(calendar.time)
        },
        currentYear - 18,
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.datePicker.apply {
        maxDate = System.currentTimeMillis()
        updateDate(currentYear - 18, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }   

    val isFormValid = emailAddress.isNotEmpty() &&
        phoneNumber.isNotEmpty() &&
        fullName.isNotEmpty() &&
        birthday.isNotEmpty() &&
        gender.isNotEmpty() &&
        password.isNotEmpty() &&
        password == confirmPassword

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CafeLoginBackground)
    ) {
        Text(
            text = "×",
            color = Color.Black,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable(onClick = onBackClick)
                .padding(16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Chào mừng bạn đến với",
                color = CafeBrown,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Brew Co",
                color = HighlandRed,
                fontSize = 48.sp,
                fontFamily = FontFamily(Font(R.font.agbalumo_regular)),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = buildAnnotatedString {
                    append("Điền các thông tin sau để trở thành\nthành viên của ")
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily(Font(R.font.agbalumo_regular)),
                            color = HighlandRed
                        )
                    ) {
                        append("Brew Co")
                    }
                    append(" bạn nhé!")
                },
                color = HighlandText,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            OutlinedTextField(
                value = emailAddress,
                onValueChange = { emailAddress = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = {
                    Text("Nhập địa chỉ gmail", color = CafeGrayText, fontSize = 16.sp)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                singleLine = true,
                shape = RoundedCornerShape(6.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = {
                    Text("Nhập số điện thoại", color = CafeGrayText, fontSize = 16.sp)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                leadingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(text = "+84", color = Color.Black, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 4.dp))
                        HorizontalDivider(
                            modifier = Modifier
                                .height(24.dp)
                                .width(1.dp),
                            color = Color.LightGray
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(6.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = { Text("Nhập họ và tên", color = CafeGrayText, fontSize = 16.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                singleLine = true,
                shape = RoundedCornerShape(6.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = birthday,
                onValueChange = { validateAndUpdateDate(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = { Text("Chọn ngày sinh (DD/MM/YYYY)", color = CafeGrayText, fontSize = 16.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                singleLine = true,
                shape = RoundedCornerShape(6.dp),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_my_calendar),
                        contentDescription = "Select date",
                        tint = CafeGrayText,
                        modifier = Modifier.clickable {
                            focusManager.clearFocus()
                            datePickerDialog.show()
                        }
                    )
                }
            )

            Text(
                text = "Định dạng: Ngày/Tháng/Năm (VD: 15/05/2004)",
                color = CafeGrayText,
                fontSize = 11.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 4.dp, top = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .menuAnchor(),
                    placeholder = { Text("Chọn giới tính", color = CafeGrayText, fontSize = 16.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option) },
                            onClick = {
                                gender = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = { Text("Nhập mật khẩu", color = CafeGrayText, fontSize = 16.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(6.dp),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Image(
                            painter = painterResource(id = if (passwordVisible) R.drawable.eye else R.drawable.close_eye),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = { Text("Xác nhận mật khẩu", color = CafeGrayText, fontSize = 16.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (isFormValid) {
                        onSignUpSubmit(emailAddress)
                    }
                }),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(6.dp),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Image(
                            painter = painterResource(id = if (confirmPasswordVisible) R.drawable.eye else R.drawable.close_eye),
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )

            if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
                Text(
                    text = "Mật khẩu không khớp",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (isFormValid) {
                        Toast.makeText(context, "Đăng ký thành công (mock)!", Toast.LENGTH_SHORT).show()
                        onSignUpSubmit(emailAddress)
                        onNavigateToOTP(emailAddress)
                    } else {
                        Toast.makeText(context, "Vui lòng kiểm tra lại thông tin", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CafeButtonBackground),
                enabled = isFormValid
            ) {
                Text(text = "Xác nhận", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Đã có tài khoản? ", color = HighlandText, fontSize = 12.sp)
                Text(
                    text = "Đăng nhập",
                    color = HighlandRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onBackClick() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpScreenPreview() {
    BrewCoTheme {
        SignUpScreen()
    }
}
