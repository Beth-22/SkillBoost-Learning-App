package com.example.skillboost.viewmodels.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillboost.data.CourseRepository
import com.example.skillboost.data.RetrofitInstance
import com.example.skillboost.models.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() { private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    private val courseRepository = CourseRepository(RetrofitInstance.api)

    init {
        fetchCourses()
    }

    private fun fetchCourses() {
        viewModelScope.launch {
            _courses.value = courseRepository.fetchCourses()
        }
    }
}