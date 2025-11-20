package com.example.brewco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.brewco.ui.BookedScreen
import com.example.brewco.ui.ChangePasswordScreen
import com.example.brewco.ui.DifferScreen
import com.example.brewco.ui.ForgotPasswordScreen
import com.example.brewco.ui.HistoryScreen
import com.example.brewco.ui.LoginScreen
import com.example.brewco.ui.OTP_FGPassScreen
import com.example.brewco.ui.OTP_SignUpScreen
import com.example.brewco.ui.SignUpScreen
import com.example.brewco.ui.SplashScreen
import com.example.brewco.ui.UserInfScreen
import com.example.brewco.ui.admin.AdminScreen
import com.example.brewco.ui.theme.BrewCoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BrewCoTheme {
                var currentScreen by remember { mutableStateOf(Screen.Splash) }
                var emailForOtp by remember { mutableStateOf("") }

                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) togetherWith
                                fadeOut(animationSpec = tween(500))
                    },
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {
                        Screen.Splash -> SplashScreen(
                            onSplashFinished = { currentScreen = Screen.Login }
                        )
                        Screen.Login -> LoginScreen(
                            onForgotPasswordClick = { currentScreen = Screen.ForgotPassword },
                            onSignUpClick = { currentScreen = Screen.SignUp },
                            onLoginClick = { isAdmin ->
                                currentScreen = if (isAdmin) Screen.Admin else Screen.Order
                            }
                        )
                        Screen.ForgotPassword -> ForgotPasswordScreen(
                            onBackToLogin = { currentScreen = Screen.Login },
                            onSubmitEmail = {
                                emailForOtp = it
                                currentScreen = Screen.OtpForgot
                            }
                        )
                        Screen.SignUp -> SignUpScreen(
                            onBackClick = { currentScreen = Screen.Login },
                            onSignUpSubmit = {
                                emailForOtp = it
                                currentScreen = Screen.OtpSignUp
                            },
                            onNavigateToOTP = {
                                emailForOtp = it
                                currentScreen = Screen.OtpSignUp
                            }
                        )
                        Screen.OtpForgot -> OTP_FGPassScreen(
                            emailAddress = emailForOtp,
                            onBackClick = { currentScreen = Screen.ForgotPassword },
                            onVerifyOtp = {
                                currentScreen = Screen.ChangePassword
                            }
                        )
                        Screen.ChangePassword -> ChangePasswordScreen(
                            email = emailForOtp,
                            onBackClick = { currentScreen = Screen.OtpForgot },
                            onChangePasswordSubmit = {
                                currentScreen = Screen.Login
                            }
                        )
                        Screen.OtpSignUp -> OTP_SignUpScreen(
                            emailAddress = emailForOtp,
                            onBackClick = { currentScreen = Screen.SignUp },
                            onVerifyOtp = {
                                currentScreen = Screen.Login
                            }
                        )
                        Screen.Admin -> AdminScreen(
                            onBackClick = { currentScreen = Screen.Login }
                        )
                        Screen.Order -> BookedScreen(
                            onNavigationItemClick = { route ->
                                currentScreen = when (route) {
                                    "home" -> Screen.Home
                                    "order" -> Screen.Order
                                    "rewards" -> Screen.Rewards
                                    "differ" -> Screen.Differ
                                    else -> Screen.Order
                                }
                            }
                        )
                        Screen.Differ -> DifferScreen(
                            onNavigationItemClick = { route ->
                                currentScreen = when (route) {
                                    "home" -> Screen.Home
                                    "order" -> Screen.Order
                                    "rewards" -> Screen.Rewards
                                    "differ" -> Screen.Differ
                                    // support both spellings
                                    "user_info", "userinfo" -> Screen.UserInfo
                                    else -> Screen.Differ
                                }
                            },
                            onHistoryClick = { currentScreen = Screen.History },
                            onLogoutClick = { currentScreen = Screen.Login }
                        )
                        Screen.UserInfo -> UserInfScreen(
                            onBackClick = { currentScreen = Screen.Differ }
                        )
                        Screen.History -> HistoryScreen(
                            onBackClick = { currentScreen = Screen.Differ }
                        )
                        Screen.Home -> BookedScreen(
                            onNavigationItemClick = { route ->
                                currentScreen = when (route) {
                                    "home" -> Screen.Home
                                    "order" -> Screen.Order
                                    "rewards" -> Screen.Rewards
                                    "differ" -> Screen.Differ
                                    else -> Screen.Home
                                }
                            }
                        )
                        Screen.Rewards -> DifferScreen(
                            onNavigationItemClick = { route ->
                                currentScreen = when (route) {
                                    "home" -> Screen.Home
                                    "order" -> Screen.Order
                                    "rewards" -> Screen.Rewards
                                    "differ" -> Screen.Differ
                                    "user_info", "userinfo" -> Screen.UserInfo
                                    else -> Screen.Rewards
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    enum class Screen {
        Splash,
        Login,
        ForgotPassword,
        SignUp,
        OtpForgot,
        ChangePassword,
        OtpSignUp,
        // main app screens
        Home,
        Order,
        Rewards,
        Differ,
        UserInfo,
        History,
        // admin
        Admin
    }
}