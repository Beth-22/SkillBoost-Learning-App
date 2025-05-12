package com.example.skillboost.data

import com.example.skillboost.models.Course
import com.example.skillboost.models.AuthResponse
import com.example.skillboost.models.LoginRequest
import com.example.skillboost.models.RoleRequest
import com.example.skillboost.models.RoleResponse
import com.example.skillboost.models.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

// ApiService.kt
interface ApiService {

    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    /*@POST("auth/select-role")
    suspend fun selectRole(
        @Header("Authorization") token: String,
        @Body roleRequest: RoleRequest
    ): RoleResponse*/
    @POST("auth/select-role")
    suspend fun selectrole(
        @Header("Authorization") token: String,
        @Body roleRequest: RoleRequest
    ): Response<Unit> // ← add return type

    @GET("courses") // Adjust the URL endpoint as necessary
    suspend fun getCourses(): Response<List<Course>>

    @GET("user/courses") // Adjust to backend endpoint, e.g., "student/courses"
    suspend fun getUserCourses(
        @Header("Authorization") token: String
    ): Response<List<Course>>
}
