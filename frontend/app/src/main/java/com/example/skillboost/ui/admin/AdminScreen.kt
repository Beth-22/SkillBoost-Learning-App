package com.example.skillboost.ui.admin

import BottomNavigationAdmin
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.skillboost.R
import com.example.skillboost.viewmodel.admin.AdminProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun AdminScreen(
    navController: NavController,
    token: String,
    viewModel: AdminProfileViewModel = viewModel()
) {
    val profile = viewModel.profile // Access profile from ViewModel
    val isLoading = viewModel.isLoading // Access loading state from ViewModel
    val error = viewModel.errorMessage // Access error from ViewModel

    var name by remember { mutableStateOf("Sophia Laurent") }
    var email by remember { mutableStateOf("youremail@gmail.com") }
    var username by remember { mutableStateOf("@Sophia_design") }
    var password by remember { mutableStateOf("password") }
    var job by remember { mutableStateOf("Senior UI/UX Designer") }

    // Load profile when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadProfile(token)
    }

    // Set initial values from profile when it loads
    LaunchedEffect(profile) {
        profile?.let {
            name = it.username // or it.fullName if available separately
            email = it.email
            username = it.username
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationAdmin(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F4FC))
                .padding(16.dp)
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()), // ✅ Make content scrollable
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            navController.navigate(Screen.AdminProfile.route) // Navigate to Profile Screen
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C6ADE)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Edit Profile")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Image(
                        painter = rememberAsyncImagePainter(
                            model = "https://example.com/profile_image.jpg", // Change with actual URL or resource
                            placeholder = painterResource(id = R.drawable.sophia_image),
                            error = painterResource(id = R.drawable.sophia_image)
                        ),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    AdminField(label = "Name", value = name, onValueChange = { name = it })
                    AdminField(label = "Job", value = job, onValueChange = { job = it })
                    AdminField(label = "Email", value = email, onValueChange = { email = it })
                    AdminField(label = "Username", value = username, onValueChange = { username = it })
                    AdminField(label = "Password", value = password, onValueChange = { password = it }, isPassword = true)

                    if (!error.isNullOrEmpty()) {
                        Text(text = error, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.updateProfile(token, username, email) {
                                // Navigate back or show success
                                navController.navigate(Screen.AdminProfile.route)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C6ADE)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Save changes")
                    }
                }
            }
        }
    }
}

@Composable
fun AdminField(label: String, value: String, onValueChange: (String) -> Unit, isPassword: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF1E7FA))
                .padding(0.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAdminScreen() {
    val mockNavController = rememberNavController()
    AdminScreen(navController = mockNavController, token = "yourTokenHere")
}
