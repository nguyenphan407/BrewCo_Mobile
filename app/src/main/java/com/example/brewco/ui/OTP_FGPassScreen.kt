package com.example.brewco.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.ui.components.OtpTextField
import com.example.brewco.ui.theme.BrewCoTheme
import com.example.brewco.ui.theme.CafeBrown
import com.example.brewco.ui.theme.CafeLoginBackground
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandText
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OTP_FGPassScreen(
    emailAddress: String = "example@gmail.com",
    onBackClick: () -> Unit = {},
    onVerifyOtp: (String) -> Unit = {}
) {
    var otpValue by remember { mutableStateOf("") }
    var isResendEnabled by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var timerKey by remember { mutableIntStateOf(0) }
    var timeRemaining by remember { mutableIntStateOf(120) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(timerKey) {
        timeRemaining = 120
        isResendEnabled = false
        while (timeRemaining > 0) {
            delay(1000)
            timeRemaining--
        }
        isResendEnabled = true
    }

    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val timeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    fun handleVerifyOtp() {
        when {
            otpValue.length != OTP_DEMO_CODE.length -> {
                Toast.makeText(context, "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show()
            }
            otpValue != OTP_DEMO_CODE -> {
                Toast.makeText(context, "Mã OTP chưa đúng, thử lại nhé", Toast.LENGTH_SHORT).show()
            }
            else -> {
                coroutineScope.launch {
                    isLoading = true
                    delay(600)
                    isLoading = false
                    Toast.makeText(context, "Xác thực thành công!", Toast.LENGTH_SHORT).show()
                    onVerifyOtp(otpValue)
                }
            }
        }
    }

    fun handleResendOtp() {
        if (!isResendEnabled) return
        coroutineScope.launch {
            isLoading = true
            delay(600)
            isLoading = false
            timerKey++
            Toast.makeText(
                context,
                "OTP mới đã được gửi (mock): $OTP_DEMO_CODE",
                Toast.LENGTH_SHORT
            ).show()
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
            horizontalAlignment = Alignment.CenterHorizontally
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

            Spacer(modifier = Modifier.height(24.dp))

            OtpTextField(
                otpText = otpValue,
                onOtpTextChange = { value, _ -> otpValue = value }
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Không nhận được mã OTP? ",
                    color = HighlandText,
                    fontSize = 14.sp
                )
                Text(
                    text = "Gửi lại",
                    color = if (isResendEnabled) HighlandRed else Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(enabled = isResendEnabled) { handleResendOtp() }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { handleVerifyOtp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HighlandRed),
                enabled = otpValue.length == OTP_DEMO_CODE.length && !isLoading
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
private fun OTP_FGPassScreenPreview() {
    BrewCoTheme {
        OTP_FGPassScreen()
    }
}
