package com.example.skillboost.viewmodels.admin

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillboost.data.CourseRepository
import com.example.skillboost.data.UserRepository
import com.example.skillboost.models.Course
import kotlinx.coroutines.launch

data class AdminCourse(
    val id: String = "",
    val title: String,
    val description: String,
    val image: String = "/images/default-course.jpg",
    val instructor: String? = null,
    val content: List<Content>? = null
) {
    data class Content(
        val id: String,
        val type: String,
        val title: String,
        val url: String
    )
}

class AdminCoursesViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _courses = mutableStateListOf<AdminCourse>()
    val courses: List<AdminCourse> = _courses

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage.value

    init {
        fetchUserCourses()
    }

    fun fetchUserCourses() {
        viewModelScope.launch {
            try {
                val token = UserRepository.getToken() ?: throw Exception("No token available")
                val fetchedCourses = courseRepository.fetchCourses()
                _courses.clear()
                _courses.addAll(fetchedCourses.map { course ->
                    AdminCourse(
                        id = course.id.toString(),
                        title = course.title,
                        description = course.description,
                        image = course.image ?: "/images/default-course.jpg"
                    )
                })
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            }
        }
    }

    fun addCourse(course: AdminCourse) {
        _courses.add(course)
    }

    fun deleteCourse(course: AdminCourse) {
        viewModelScope.launch {
            try {
                val success = courseRepository.deleteCourse(course.id)
                if (success) {
                    _courses.remove(course)
                    _errorMessage.value = null
                    Log.d("AdminVM", "Course deleted successfully")
                } else {
                    _errorMessage.value = "Failed to delete course"
                    Log.e("AdminVM", "Failed to delete course")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting course: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
class AdminCoursesViewModelFactory(
    private val context: Context,
    private val courseRepository: CourseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminCoursesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminCoursesViewModel(courseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}