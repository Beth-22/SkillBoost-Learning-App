package com.example.skillboost.ui.user

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.skillboost.R
import com.example.skillboost.models.Course
import com.example.skillboost.viewmodels.user.HomeViewModel
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        Log.d("SkillBoost", "HomeScreen rendered")
    }

    Scaffold(
        bottomBar = { BottomNavigation(navController) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreenContent(viewModel, navController)
        }
    }
}

@Composable
fun HomeScreenContent(viewModel: HomeViewModel, navController: NavController) {
    val courses by viewModel.courses.collectAsState()
    LaunchedEffect(courses) {
        Log.d("SkillBoost", "Courses loaded: ${courses.size}")
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Purple Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(elevation = 8.dp)
                .zIndex(1f),
            color = Color(0xFF9B6ED8)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SkillBoost",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Main Content (Scrollable)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Welcome to SkillBoost!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Courses for you",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (courses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No courses available")
                    }
                }
            } else {

            }
        }
    }
}

@Composable
fun ScrollableCourseCategory(title: String, courses: List<Course>, navController: NavController) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(courses) { course ->
                CourseItem(course = course, navController = navController)
            }
        }
    }
}

@Composable
fun CourseItem(course: Course, navController: NavController) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5), // lightColor
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    )
    {
        Column(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            AsyncImage(
                model = course.image,
                contentDescription = course.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder_image),
                error = painterResource(R.drawable.placeholder_image),
                onError = { Log.e("SkillBoost", "Failed to load image: ${course.image}") }
            )
            Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Credits: ${course.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { navController.navigate("StudentCourse") },
                        modifier = Modifier.width(120.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Enroll")
                    }

                }

            }
        }
    }
}