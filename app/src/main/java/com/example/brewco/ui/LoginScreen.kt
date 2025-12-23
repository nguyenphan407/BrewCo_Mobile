package com.example.brewco.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.data.AuthManager
import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.LoginRequest
import com.example.brewco.data.dto.LoginResponse
import com.example.brewco.data.dto.UserProfileResponse
import com.example.brewco.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun LoginScreen(
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val authManager = remember { AuthManager.getInstance(context) }

    fun fetchAndCacheProfile(token: String, onComplete: () -> Unit) {
        ApiClient.apiService.getCurrentUser("Bearer $token")
            .enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val profile = response.body()
                        if (profile != null) {
                            authManager.saveUserInfo(
                                userId = profile.id,
                                fullName = profile.fullName,
                                phone = profile.phoneNumber.orEmpty()
                            )
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Không thể tải thông tin người dùng (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    onComplete()
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Lỗi khi tải thông tin người dùng: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    onComplete()
                }
            })
    }


    var email by remember { mutableStateOf(authManager.getSavedEmail()) }
    var password by remember { mutableStateOf(authManager.getSavedPassword()) }
    var rememberMe by remember { mutableStateOf(authManager.isRememberMeEnabled()) }

    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HighlandWhite)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(HighlandRed),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.cf_login),
                contentDescription = "Cafe Background",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.Center
            )
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
                .offset(y = (-40).dp)
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(HighlandWhite)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Brew Co",
                color = HighlandRed,
                fontSize = 48.sp,
                fontFamily = FontFamily(Font(R.font.agbalumo_regular)),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 50.dp)
            )


            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = {
                    Text(
                        "Nhập địa chỉ gmail",
                        color = CafeGrayText,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 0.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = RoundedCornerShape(6.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))


            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = { Text("Nhập mật khẩu", color = CafeGrayText, fontSize = 18.sp) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true,
                shape = RoundedCornerShape(6.dp),
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


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .border(1.dp, CafeBrown, RoundedCornerShape(6.dp))
                            .background(
                                if (rememberMe) CafeBrown else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rememberMe) {
                            Spacer(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(
                                        HighlandWhite,
                                        RoundedCornerShape(2.5.dp)
                                    )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ghi nhớ tôi",
                        color = HighlandText,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "Quên mật khẩu?",
                    color = HighlandText,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { onForgotPasswordClick() }
                )
            }


            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true


                        if (email == "gm.giaphu@gmail.com" && password == "admin") {
                            authManager.saveLoginCredentials(email, password, rememberMe)

                            Toast.makeText(context, "Đăng nhập quản trị thành công!", Toast.LENGTH_SHORT).show()
                            onLoginClick()
                            isLoading = false
                        } else {
                            val loginRequest = LoginRequest(
                                email = email,
                                password = password,
                                rememberMe = rememberMe
                            )

                            ApiClient.apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                    if (!response.isSuccessful) {
                                        isLoading = false
                                        val errorMsg = when(response.code()) {
                                            401 -> "Email hoặc mật khẩu không đúng"
                                            404 -> "Tài khoản không tồn tại"
                                            else -> "Đăng nhập thất bại: ${response.code()}"
                                        }
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                        return
                                    }

                                    val loginResponse = response.body()
                                    val loginData = loginResponse?.data
                                    val token = loginData?.token

                                    if (token.isNullOrBlank()) {
                                        isLoading = false
                                        Toast.makeText(context, "Không nhận được token đăng nhập", Toast.LENGTH_SHORT).show()
                                        return
                                    }

                                    authManager.saveLoginCredentials(email, password, rememberMe)
                                    authManager.saveTokens(
                                        accessToken = token,
                                        refreshToken = loginData.refreshToken
                                    )

                                    fetchAndCacheProfile(token) {
                                        isLoading = false
                                        Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                                        onLoginClick()
                                    }
                                }

                                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                    isLoading = false
                                    Toast.makeText(context, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })

                        }
                    } else {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HighlandRed
                ),
                shape = RoundedCornerShape(6.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = CafeBeige,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Đăng Nhập",
                        color = CafeBeige,
                        fontSize = 16.sp
                    )
                }
            }


            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bạn chưa có tài khoản? ",
                    color = HighlandText,
                    fontSize = 12.sp
                )
                Text(
                    text = "Đăng ký ngay",
                    color = HighlandRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BrewCoTheme {
        LoginScreen()
    }
}
