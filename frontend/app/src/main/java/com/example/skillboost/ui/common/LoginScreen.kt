package com.example.skillboost.ui.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.skillboost.data.UserRepository
import com.example.skillboost.ui.admin.Screen
import com.example.skillboost.viewmodels.common.LoginState
import com.example.skillboost.viewmodels.common.LoginViewModel

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(UserRepository)
    )
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        // Purple header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color(0xFF9B6ED8)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SkillBoost",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        // Login form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF9B6ED8),
                    unfocusedBorderColor = Color(0xFF884FD0),
                    focusedLabelColor = Color(0xFF9B6ED8),
                    cursorColor = Color(0xFF884FD0)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF9B6ED8),
                    unfocusedBorderColor = Color(0xFF884FD0),
                    focusedLabelColor = Color(0xFF9B6ED8),
                    cursorColor = Color(0xFF884FD0)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Observe login state
            val loginState by viewModel.loginState.collectAsState()
            Log.d("SkillBoost", "Login state: $loginState")
            when (loginState) {
                is LoginState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is LoginState.Success -> {
                    LaunchedEffect(Unit) {
                        val token = UserRepository.getToken()
                        val role = UserRepository.getUserRole()
                        Log.d("SkillBoost", "Login success - token: $token, role: $role")
                        if (token == null) {
                            Log.e("SkillBoost", "No token after login, navigating to login")
                            navController.navigate(Screen.LoginScreen.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        } else {
                            val destination = when (role) {
                                "student" -> Screen.HomeScreen.route
                                "instructor" -> Screen.AdminCourse.route
                                else -> Screen.RoleSelection.route
                            }
                            Log.d("SkillBoost", "Navigating to: $destination")
                            navController.navigate(destination) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        viewModel.clearLoginState()
                    }
                }
                is LoginState.Error -> {
                    Text(
                        text = (loginState as LoginState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                is LoginState.Idle -> {}
            }

            Button(
                onClick = {
                    viewModel.login(email, password)
                },
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF884FD0),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Log In")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { navController.navigate(Screen.SignUpScreen.route) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Don't have an account? Sign up")
            }
        }
    }
}

class LoginViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}