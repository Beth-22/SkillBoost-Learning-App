package com.example.skillboost.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.skillboost.R
import com.example.skillboost.ui.common.CourseCard
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.skillboost.data.CourseRepository
import com.example.skillboost.data.RetrofitInstance
import com.example.skillboost.viewmodels.user.StudentCoursesViewModel
import com.example.skillboost.viewmodels.user.StudentCoursesViewModelFactory

@Composable
fun StudentCoursesScreen(
    navController: NavHostController, viewModel: StudentCoursesViewModel = viewModel(
        factory = StudentCoursesViewModelFactory(CourseRepository(RetrofitInstance.api))
    )
) {
    Scaffold(
        bottomBar = { BottomNavigation(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Header()

            Spacer(modifier = Modifier.height(16.dp))

            // Courses List
            CourseList(
                courses = viewModel.courses,
                onDelete = { viewModel.deleteCourse(it) }
            )
        }
    }
}

@Composable
fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(0xFF9B6ED8)),
        contentAlignment = Alignment.TopStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp, top = 14.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.circular_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Courses",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun CourseList(courses: List<com.example.skillboost.models.CourseStatus>, onDelete: (com.example.skillboost.models.CourseStatus) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(courses) { course ->
            CourseCard(course = course, onDelete = { onDelete(course) })
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
