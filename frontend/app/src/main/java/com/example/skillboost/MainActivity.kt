package com.example.skillboost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.atry.CourseDetail
import com.example.skillboost.ui.admin.AddNewCourse
import com.example.skillboost.ui.admin.AdminProfileScreen
import com.example.skillboost.ui.admin.AdminScreen
import com.example.skillboost.ui.admin.Screen
import com.example.skillboost.ui.user.HomeScreen
import com.example.skillboost.ui.common.LoginScreen
import com.example.skillboost.ui.common.RoleSelectionScreen
import com.example.skillboost.ui.user.SearchScreen
import com.example.skillboost.ui.common.SignUpScreen
import com.example.skillboost.ui.common.LandingPageScreen
import com.example.skillboost.ui.common.SkillBoostTheme
import com.example.skillboost.ui.user.ProfileScreen
import com.example.skillboost.ui.user.StudentCoursesScreen
import com.example.skillboost.ui.user.StudentProfileScreen
import com.example.skillboost.ui.admin.AdminCourse
import com.example.skillboost.ui.admin.Screen.AddNewCourse

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkillBoostTheme {
                val navController = rememberNavController()
                Scaffold { innerPadding ->
                    SkillBoostApp(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SkillBoostApp(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Screen.LandingPage.route, modifier = modifier) {
        composable(Screen.LandingPage.route) {
            LandingPageScreen(navController)
        }
        composable(Screen.RoleSelection.route) { RoleSelectionScreen(navController) }
        composable(Screen.LoginScreen.route) { LoginScreen(navController) }
        composable(Screen.SignUpScreen.route) { SignUpScreen(navController) }
        composable(Screen.HomeScreen.route) { HomeScreen(navController) }
        composable(Screen.SearchScreen.route) { SearchScreen(navController) }
        composable(Screen.ProfileScreen.route) { ProfileScreen(navController) }
        composable(Screen.StudentCourse.route) { StudentCoursesScreen(navController) }
        composable(Screen.StudentProfileScreen.route) { StudentProfileScreen(navController) }
        composable(Screen.CourseDetailScreen.route) { CourseDetail(navController) }
        composable(Screen.CourseDetails.route) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")?.toInt() ?: 0
            Text("Course Details: $courseId") // Placeholder
        }
        composable(Screen.AdminCourse.route) { AdminCourse(navController)  }
        composable(Screen.AdminCourseDetails.route) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")?.toInt() ?: 0
            Text("Admin Course Details: $courseId") // Placeholder
        }
        composable(Screen.EditCourse.route) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")?.toInt() ?: 0
            Text("Edit Course: $courseId") // Placeholder
        }
        composable(Screen.AdminProfile.route) { AdminProfileScreen(navController, coursesCreated = 5) }
        composable(Screen.AdminScreen.route) { AdminScreen(navController) }
        composable(Screen.AddNewCourse.route) {AddNewCourse(navController = navController)
        }
    }
}