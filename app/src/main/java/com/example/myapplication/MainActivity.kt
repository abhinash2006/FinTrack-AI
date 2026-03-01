package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myapplication.ui.AuthScreen
import com.example.myapplication.ui.ExpenseScreen
import com.example.myapplication.ui.ExpenseViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Logic: Keep splash screen visible until user data is fetched
        var keepSplashScreen = true
        window.decorView.postDelayed({ keepSplashScreen = false }, 2000)

        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        enableEdgeToEdge()

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            val authLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = viewModel.authRepository.handleSignInResult(task)
                viewModel.onSignInResult(account)
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                val user by viewModel.user.collectAsState()

                if (user == null) {
                    AuthScreen(onGoogleSignIn = {
                        authLauncher.launch(viewModel.authRepository.getSignInIntent())
                    })
                } else {
                    ExpenseScreen(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }
}