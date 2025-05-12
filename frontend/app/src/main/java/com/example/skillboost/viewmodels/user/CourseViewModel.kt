package com.example.skillboost.viewmodels.user

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillboost.data.CourseRepository
import com.example.skillboost.data.RetrofitInstance
import com.example.skillboost.data.UserRepository
import com.example.skillboost.models.Course
import kotlinx.coroutines.launch

class CoursesViewModel(
    private val repository: CourseRepository
) : ViewModel() {
    var courses = mutableStateOf<List<Course>>(emptyList())
        private set

    init {
        fetchCourses()
    }

    private fun fetchCourses() {
        viewModelScope.launch {
            courses.value = repository.fetchCourses()
        }
    }
}
class CoursesViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoursesViewModel::class.java)) {
            val courseRepository = CourseRepository(RetrofitInstance.api)
            @Suppress("UNCHECKED_CAST")
            return CoursesViewModel(courseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}