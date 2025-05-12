package com.example.skillboost.viewmodels.user

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillboost.R
import com.example.skillboost.data.CourseRepository
import com.example.skillboost.data.UserRepository
import com.example.skillboost.models.Course
import com.example.skillboost.models.CourseStatus
import kotlinx.coroutines.launch

class StudentCoursesViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _courses = mutableStateListOf<CourseStatus>()
    val courses: List<CourseStatus> = _courses

    init {
        fetchUserCourses()
    }

    private fun fetchUserCourses() {
        viewModelScope.launch {
            val userCourses = courseRepository.fetchUserCourses()
            _courses.clear()
            _courses.addAll(userCourses.map { mapToCourseStatus(it) })
        }
    }

    private fun mapToCourseStatus(course: Course): CourseStatus {
        // Map image to local drawable resource
        val imageRes = when (course.title.lowercase()) {
            "fullstack web development" -> R.drawable.rec_1
            "youtube for beginners" -> R.drawable.rec_2
            "kotlin app development" -> R.drawable.rec_3
            "economics essentials" -> R.drawable.rec_4
            "intro to applied math" -> R.drawable.rec_5
            else -> R.drawable.rec_1 // Default image
        }

        // Capitalize status for UI (e.g., "enrolled" → "Enroll")
        val status = course.status.replaceFirstChar { it.uppercase() }

        return CourseStatus(
            title = course.title,
            description = course.description,
            status = status,
            imageRes = imageRes
        )
    }

    fun deleteCourse(course: CourseStatus) {
        _courses.remove(course)
        // TODO: Call backend API to delete course enrollment if needed
    }
}
class StudentCoursesViewModelFactory(
    private val courseRepository: CourseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentCoursesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudentCoursesViewModel(courseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}