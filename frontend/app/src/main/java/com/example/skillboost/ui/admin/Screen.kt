package com.example.skillboost.ui.admin

sealed class Screen(val route: String) {
    data object LandingPage : Screen("landingPage")
    data object RoleSelection : Screen("roleSelection")
    data object LoginScreen : Screen("loginScreen")
    data object SignUpScreen : Screen("signUpScreen")
    data object HomeScreen : Screen("homeScreen")
    data object SearchScreen : Screen("searchScreen")
    data object ProfileScreen : Screen("profileScreen")
    data object StudentCourse : Screen("studentCourse")
    data object StudentProfileScreen : Screen("studentProfileScreen")
    data object CourseDetails : Screen("courseDetails/{courseId}") {
        fun createRoute(courseId: String) = "courseDetails/$courseId"
    }
    data object AdminCourseDetails : Screen("adminCourseDetails/{courseId}") {
        fun createRoute(courseId: String) = "adminCourseDetails/$courseId"
    }
    data object EditCourse : Screen("editCourse/{courseId}") {
        fun createRoute(courseId: String) = "editCourse/$courseId"
    }
    data object AdminCourse : Screen("adminCourse")
    data object AdminScreen : Screen("adminScreen")
    data object AddNewCourse : Screen("addNewCourse")
    data object AdminProfile : Screen("adminProfileScreen")
}