package com.example.brewco.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.ResetPasswordRequest
import com.example.brewco.ui.theme.CafeBrown
import com.example.brewco.ui.theme.CafeLoginBackground
import com.example.brewco.ui.theme.BrewCoTheme
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ChangePasswordScreen(
    email: String = "",
    onBackClick: () -> Unit = {},
    onChangePasswordSubmit: () -> Unit = {}
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current


    val passwordsMatch = password == confirmPassword && password.isNotEmpty()


    val handleResetPassword = handleReset@ {
        if (passwordsMatch) {
            if (email.isEmpty()) {
                Toast.makeText(context, "Email không hợp lệ, vui lòng thử lại", Toast.LENGTH_SHORT).show()
                return@handleReset
            }

            isLoading = true
            val request = ResetPasswordRequest(
                email = email,
                password = password,
                passwordConfirm = confirmPassword
            )

            ApiClient.apiService.resetPassword(email, request).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show()
                        onChangePasswordSubmit()
                    } else {
                        Toast.makeText(context, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show()
                        onChangePasswordSubmit()






                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    isLoading = false
                    Toast.makeText(context, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CafeLoginBackground),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "×",
            color = Color.Black,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable(onClick = onBackClick)
                .padding(32.dp)
        )


        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = CafeBrown
            )
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Chào mừng bạn đến với",
                color = CafeBrown,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = "Brew Co",
                color = HighlandRed,
                fontSize = 48.sp,
                fontFamily = FontFamily(Font(R.font.agbalumo_regular)),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))


            Text(
                text = "Đổi mật khẩu",
                color = HighlandText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(42.dp))


            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                label = { Text("Mật khẩu mới") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HighlandRed,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = HighlandRed,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = HighlandRed,
                    focusedTextColor = HighlandText,
                    unfocusedTextColor = HighlandText,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading,
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Image(
                            painter = painterResource(
                                id = if (passwordVisible) R.drawable.eye else R.drawable.close_eye
                            ),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                label = { Text("Xác nhận mật khẩu") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HighlandRed,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = HighlandRed,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = HighlandRed,
                    focusedTextColor = HighlandText,
                    unfocusedTextColor = HighlandText,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (passwordsMatch && !isLoading) {
                            handleResetPassword()
                        }
                    }
                ),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading,
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Image(
                            painter = painterResource(
                                id = if (confirmPasswordVisible) R.drawable.eye else R.drawable.close_eye
                            ),
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )


            if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (passwordsMatch) "✓ Mật khẩu khớp" else "✗ Mật khẩu không khớp",
                    color = if (passwordsMatch) Color(0xFF4CAF50) else HighlandRed,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = { handleResetPassword() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HighlandRed,
                    contentColor = Color.White
                ),
                enabled = passwordsMatch && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Đổi mật khẩu",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordScreenPreview() {
    BrewCoTheme {
        ChangePasswordScreen()
    }
}
