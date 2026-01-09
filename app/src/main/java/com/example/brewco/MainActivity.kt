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
import com.example.brewco.ui.ForgotPasswordScreen
import com.example.brewco.ui.LoginScreen
import com.example.brewco.ui.SignUpScreen
import com.example.brewco.ui.SplashScreen
import com.example.brewco.ui.theme.BrewCoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BrewCoTheme {
                var currentScreen by remember { mutableStateOf(Screen.Splash) }

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
                            onLoginClick = { _ -> }
                        )
                        Screen.ForgotPassword -> ForgotPasswordScreen(
                            onBackToLogin = { currentScreen = Screen.Login },
                            onSubmitEmail = {
                                currentScreen = Screen.Login
                            }
                        )
                        Screen.SignUp -> SignUpScreen(
                            onBackClick = { currentScreen = Screen.Login },
                            onSignUpSubmit = {
                                currentScreen = Screen.Login
                            },
                            onNavigateToOTP = {
                                currentScreen = Screen.Login
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
        SignUp
    }
}