package com.example.skillboost.data

import com.example.skillboost.models.Course
import com.example.skillboost.data.UserRepository
import com.example.skillboost.data.ApiService


class CourseRepository(private val apiService: ApiService) {

    suspend fun fetchCourses(): List<Course> {
        return try {
            val response = apiService.getCourses()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList() // Handle error case (can show a message in UI)
            }
        } catch (e: Exception) {
            emptyList() // Handle network failure
        }
    }

    suspend fun getCourseByName(name: String): Course? {
        val courses = fetchCourses()
        return courses.find { it.title.equals(name, ignoreCase = true) }
    }

    suspend fun fetchUserCourses(): List<Course> {
        return try {
            val token = UserRepository.getToken() ?: throw Exception("No token available")
            val response = apiService.getUserCourses("Bearer $token")
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                println("Failed to fetch user courses: HTTP ${response.code()}, ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error fetching user courses: ${e.message}")
            emptyList()
        }
    }
}