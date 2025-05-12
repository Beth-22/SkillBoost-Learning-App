package com.example.skillboost.ui.common

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.skillboost.R
import com.example.skillboost.data.UserRepository
import com.example.skillboost.ui.admin.Screen
import com.example.skillboost.viewmodels.common.RoleSelectionState
import com.example.skillboost.viewmodels.common.RoleSelectionViewModel

@Composable
fun RoleSelectionScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: RoleSelectionViewModel = viewModel(
        factory = RoleSelectionViewModelFactory(UserRepository)
    )
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            is RoleSelectionState.Success -> {
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
                viewModel.resetState()
            }
            is RoleSelectionState.InvalidToken -> {
                Toast.makeText(context, "Session expired, please log in again", Toast.LENGTH_LONG).show()
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
                viewModel.resetState()
            }
            is RoleSelectionState.NetworkError -> {
                Toast.makeText(context, (state as RoleSelectionState.NetworkError).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            is RoleSelectionState.Error -> {
                Toast.makeText(context, (state as RoleSelectionState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> { /* Idle or Loading, no action */ }
        }
    }

    RoleSelectionContent(
        onStudentClick = { viewModel.selectRole("student") },
        onTeacherClick = { viewModel.selectRole("instructor") },
        isLoading = state is RoleSelectionState.Loading,
        errorMessage = (state as? RoleSelectionState.Error)?.message
    )
}

@Composable
fun RoleSelectionContent(
    onStudentClick: () -> Unit,
    onTeacherClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF9B6ED8))
    ) {
        // Background Decorations
        Image(
            painter = painterResource(id = R.drawable.circular_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .padding(horizontal = 42.dp, vertical = 58.dp)
                .size(175.dp)
                .align(Alignment.TopStart),
            contentScale = ContentScale.Fit
        )

        Icon(
            painter = painterResource(id = R.drawable.ellipse_3),
            contentDescription = "Ellipse_top_right",
            tint = Color(0xFFE3CDFB),
            modifier = Modifier
                .size(230.dp)
                .offset(x = 230.dp, y = (-10).dp)
        )

        Icon(
            painter = painterResource(id = R.drawable.ellipse_2),
            contentDescription = "Ellipse_middle_left",
            tint = Color(0xFFE3CDFB),
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-50).dp, y = 210.dp)
        )

        Icon(
            painter = painterResource(id = R.drawable.ellipse_1),
            contentDescription = "Ellipse_bottom_right",
            tint = Color(0xFFE3CDFB),
            modifier = Modifier
                .size(300.dp)
                .offset(x = 130.dp, y = 640.dp)
        )

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Skill\nBoost",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 36.sp,
                    textAlign = TextAlign.Center
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_double_arrow),
                    contentDescription = "Arrow",
                    tint = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer { rotationZ = -20f }
                )
            }

            Spacer(modifier = Modifier.height(120.dp))

            Text(
                text = "I am a:",
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onStudentClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                enabled = !isLoading
            ) {
                Text("Student", color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onTeacherClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                enabled = !isLoading
            ) {
                Text("Teacher", color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRoleSelectionContent() {
    RoleSelectionContent(
        onStudentClick = {},
        onTeacherClick = {},
        isLoading = false,
        errorMessage = null
    )
}

class RoleSelectionViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoleSelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoleSelectionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}