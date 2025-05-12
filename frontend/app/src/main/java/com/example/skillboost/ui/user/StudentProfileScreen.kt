package com.example.skillboost.ui.user

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.skillboost.R

@Composable
fun StudentProfileScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigation(navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF7F4FC)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(123.dp)
                        .background(Color(0xFF9C6ADE))
                )

                Spacer(modifier = Modifier.height(84.dp))

                Image(
                    painter = painterResource(id = R.drawable.headshot_portrait),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Alice Wang",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFF746868)
                )

                Text(
                    text = "@alicedev",
                    fontSize = 16.sp,
                    color = Color(0xFF746868)
                )

                Button(
                    onClick = {
                        navController.navigate("ProfileScreen")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C6ADE)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(30.dp)
                ) {
                    Text("Edit Profile")
                }

                // Achievements
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 15.dp)
                        .clickable {
                            Log.d("StudentProfile", "Achievements clicked")
                            navController.navigate("achievements")
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1E7FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_trophy),
                            contentDescription = "Trophy Icon",
                            tint = Color(0xFF9C6ADE),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Achievements",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF746868)
                        )
                    }
                }

                // Logout
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 8.dp)
                        .clickable {
                            Log.d("StudentProfile", "User logged out")
                            navController.navigate("LoginScreen")
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1E7FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout Icon",
                            tint = Color(0xFF9C6ADE),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Logout",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF746868)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp)) // Bottom padding
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewStudentProfileScreen() {
    val mockNavController = rememberNavController()
    StudentProfileScreen(navController = mockNavController)
}
