package com.example.brewco.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.brewco.ui.theme.BrewCoTheme
import com.example.brewco.ui.theme.CafeBeige
import com.example.brewco.ui.theme.CafeBrown
import com.example.brewco.ui.theme.CafeGrayText
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText

private data class MockUser(
    val email: String,
    val password: String,
    val fullName: String,
    val isAdmin: Boolean = false
)

private val mockUsers = listOf(
    MockUser(email = "gm.giaphu@gmail.com", password = "admin", fullName = "Gia Phu", isAdmin = true),
    MockUser(email = "barista@brewco.com", password = "espresso", fullName = "Barista Brew"),
    MockUser(email = "member@brewco.com", password = "coffeetime", fullName = "Cafe Member")
)

@Composable
fun LoginScreen(
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onLoginClick: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                .background(Color.White)
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
                        text = "Nhập địa chỉ gmail",
                        color = CafeGrayText,
                        fontSize = 18.sp
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
                            painter = painterResource(id = if (passwordVisible) R.drawable.eye else R.drawable.close_eye),
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
                            .background(if (rememberMe) CafeBrown else Color.Transparent, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rememberMe) {
                            Spacer(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(Color.White, RoundedCornerShape(2.5.dp))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Ghi nhớ tôi", color = HighlandText, fontSize = 12.sp)
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
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    val matchedUser = mockUsers.firstOrNull { user ->
                        user.email.equals(email.trim(), ignoreCase = true) && user.password == password
                    }
                    if (matchedUser != null) {
                        Toast.makeText(context, "Chào mừng trở lại ${matchedUser.fullName}!", Toast.LENGTH_SHORT).show()
                        onLoginClick(matchedUser.isAdmin)
                    } else {
                        Toast.makeText(context, "Email hoặc mật khẩu chưa đúng", Toast.LENGTH_SHORT).show()
                    }
                    isLoading = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HighlandRed),
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
                    Text(text = "Đăng Nhập", color = CafeBeige, fontSize = 16.sp)
                }
            }

            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Bạn chưa có tài khoản? ", color = HighlandText, fontSize = 12.sp)
                Text(
                    text = "Đăng ký ngay",
                    color = HighlandRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    BrewCoTheme {
        LoginScreen()
    }
}
