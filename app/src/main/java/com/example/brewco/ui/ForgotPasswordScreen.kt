package com.example.brewco.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.ui.theme.BrewCoTheme
import com.example.brewco.ui.theme.CafeBeige
import com.example.brewco.ui.theme.CafeBrown
import com.example.brewco.ui.theme.CafeButtonBackground
import com.example.brewco.ui.theme.CafeGrayText
import com.example.brewco.ui.theme.CafeLoginBackground
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit = {},
    onSubmitEmail: (String) -> Unit = {}
) {
    var emailAddress by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CafeLoginBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
                .background(HighlandRed)
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp)
                .weight(1.2f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(CafeLoginBackground)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Chào mừng bạn đến với",
                    color = HighlandText,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Brew Co",
                    color = HighlandRed,
                    fontSize = 48.sp,
                    fontFamily = FontFamily(Font(R.font.agbalumo_regular)),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 20.dp, bottom = 30.dp)
                )

                Text(
                    text = "Quên mật khẩu",
                    color = HighlandText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                val annotatedString = buildAnnotatedString {
                    append("Nhớ mật khẩu? ")
                    pushStringAnnotation(tag = "login", annotation = "login")
                    withStyle(SpanStyle(color = HighlandRed, fontWeight = FontWeight.Bold)) {
                        append("Đăng nhập tại đây")
                    }
                    pop()
                }

                Text(
                    text = annotatedString,
                    color = CafeBrown,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .clickable(onClick = onBackToLogin)
                )

                OutlinedTextField(
                    value = emailAddress,
                    onValueChange = { emailAddress = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = {
                        Text("Nhập địa chỉ gmail", color = CafeGrayText, fontSize = 18.sp)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(6.dp),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (emailAddress.isBlank()) {
                            Toast.makeText(context, "Vui lòng nhập địa chỉ email", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        Toast.makeText(
                            context,
                            "Mã xác nhận đã được gửi tới $emailAddress (mock)",
                            Toast.LENGTH_SHORT
                        ).show()
                        onSubmitEmail(emailAddress)
                        isLoading = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CafeButtonBackground),
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
                        Text(text = "XÁC NHẬN", color = CafeBeige, fontSize = 16.sp)
                    }
                }
            }

            Text(
                text = "×",
                color = Color.Black,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 20.dp)
                    .clickable(onClick = onBackToLogin)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    BrewCoTheme {
        ForgotPasswordScreen()
    }
}
