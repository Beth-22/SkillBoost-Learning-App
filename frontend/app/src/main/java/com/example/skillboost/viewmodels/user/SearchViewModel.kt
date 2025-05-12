package com.example.skillboost.viewmodels.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillboost.data.CourseRepository
import com.example.skillboost.data.RetrofitInstance
import com.example.skillboost.models.Course
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: CourseRepository = CourseRepository(RetrofitInstance.api)) : ViewModel() {
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadCourses()
    }

    private fun loadCourses() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val courseList = repository.fetchCourses()
                _courses.value = courseList
            } catch (e: Exception) {
                Log.e("SkillBoost", "Error loading courses: ${e.message}")
                _courses.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        Log.d("SkillBoost", "ViewModel search query: $query")
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Debounce to prevent excessive searches
            _isLoading.value = true
            try {
                if (query.isBlank()) {
                    _courses.value = repository.fetchCourses()
                } else {
                    val course = repository.getCourseByName(query)
                    _courses.value = if (course != null) listOf(course) else emptyList()
                }
            } catch (e: Exception) {
                Log.e("SkillBoost", "Error searching courses: ${e.message}")
                _courses.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class SearchViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}