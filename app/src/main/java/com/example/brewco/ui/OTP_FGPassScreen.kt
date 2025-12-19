package com.example.brewco.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.data.api.ApiClient
import com.example.brewco.data.dto.ResendOtpRequest
import com.example.brewco.ui.components.OtpTextField
import com.example.brewco.ui.theme.CafeBrown
import com.example.brewco.ui.theme.CafeLoginBackground
import com.example.brewco.ui.theme.BrewCoTheme
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

@Composable
fun OTP_FGPassScreen(
    emailAddress: String = "example@gmail.com",
    onBackClick: () -> Unit = {},
    onVerifyOtp: (String) -> Unit = {}
) {
    var otpValue by remember { mutableStateOf("") }
    var timeRemaining by remember { mutableStateOf(120) }
    var isResendEnabled by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current


    LaunchedEffect(key1 = true) {
        while (timeRemaining > 0) {
            delay(1000)
            timeRemaining--
        }
        isResendEnabled = true
    }


    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val timeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)


    val handleVerifyOtp = {
        if (otpValue.length == 6) {
            isLoading = true

            ApiClient.apiService.verifyOtp(otpValue).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Xác thực OTP thành công!", Toast.LENGTH_SHORT).show()
                        onVerifyOtp(otpValue)
                    } else {
                        Toast.makeText(context, "Xác thực OTP thành công!", Toast.LENGTH_SHORT).show()
                        onVerifyOtp(otpValue)

                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    isLoading = false
                    Toast.makeText(context, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show()
        }
    }


    val handleResendOtp = {
        if (isResendEnabled || timeRemaining <= 0) {
            isLoading = true
            val request = ResendOtpRequest(email = emailAddress)

            ApiClient.apiService.resendOtp(request).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Đã gửi lại mã OTP", Toast.LENGTH_SHORT).show()
                        timeRemaining = 120
                        isResendEnabled = false
                    } else {
                        Toast.makeText(context, "Không thể gửi lại mã OTP: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    isLoading = false
                    Toast.makeText(context, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
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
                text = "Xác nhận Mã OTP",
                color = HighlandRed,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Mã xác thực OTP đã được gửi đến",
                color = HighlandText,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = emailAddress,
                color = HighlandRed,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "Nhập mã để tiếp tục",
                color = CafeBrown,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))


            OtpTextField(
                otpText = otpValue,
                onOtpTextChange = { value, isFilled ->
                    otpValue = value
                }
            )

            Spacer(modifier = Modifier.height(24.dp))


            Text(
                text = "Thời gian còn lại: $timeString",
                color = if (timeRemaining > 0) HighlandText else HighlandRed,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Không nhận được mã OTP? ",
                    color = HighlandText,
                    fontSize = 14.sp
                )
                Text(
                    text = "Gửi lại",
                    color = if (isResendEnabled || timeRemaining <= 0) HighlandRed else Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(enabled = isResendEnabled || timeRemaining <= 0) {
                        handleResendOtp()
                    }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))


            Button(
                onClick = { handleVerifyOtp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HighlandRed
                ),
                enabled = otpValue.length == 6 && !isLoading
            ) {
                Text(
                    text = "Xác nhận",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OTP_FGPassScreenPreview() {
    BrewCoTheme {
        OTP_FGPassScreen()
    }
}
