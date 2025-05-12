package com.example.skillboost.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.skillboost.models.AuthResponse
import com.example.skillboost.models.LoginRequest
import com.example.skillboost.models.RoleRequest
import com.example.skillboost.models.SignupRequest
import java.net.ConnectException
import java.net.SocketTimeoutException

object UserRepository {
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    }

    suspend fun signup(name: String, email: String, password: String): String? {
        return try {
            val response = RetrofitInstance.api.signup(SignupRequest(name, email, password))
            if (response.isSuccessful) {
                val data = response.body()!!
                saveAuthData(data.id, data.accessToken)
                Log.d("SkillBoost", "Signup successful, userId: ${data.id}, token: ${data.accessToken}")
                null
            } else {
                val errorBody = response.errorBody()?.string() ?: "Signup failed"
                Log.e("SkillBoost", "Signup failed: $errorBody")
                errorBody
            }
        } catch (e: Exception) {
            Log.e("SkillBoost", "Signup error: ${e.message}", e)
            "Signup error: ${e.message}"
        }
    }

    suspend fun login(email: String, password: String): String? {
        return try {
            val response = RetrofitInstance.api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val data = response.body()!!
                saveAuthData(data.id, data.accessToken)
                Log.d("SkillBoost", "Login successful, userId: ${data.id}, token: ${data.accessToken}")
                null
            } else {
                val errorBody = response.errorBody()?.string() ?: "Login failed"
                Log.e("SkillBoost", "Login failed: $errorBody")
                errorBody
            }
        } catch (e: Exception) {
            Log.e("SkillBoost", "Login error: ${e.message}", e)
            "Login error: ${e.message}"
        }
    }

    private fun saveAuthData(userId: String, token: String) {
        sharedPreferences.edit()
            .putString("user_id", userId)
            .putString("token", token)
            .apply()
        Log.d("SkillBoost", "Saved auth data - userId: $userId, token: $token")
    }

    suspend fun selectUserRole(role: String) {
        val token = getToken() ?: run {
            Log.e("SkillBoost", "No token available for role selection")
            throw Exception("No token available, please log in again")
        }
        Log.d("SkillBoost", "Selecting role: $role, token: $token")
        try {
            val response = RetrofitInstance.api.selectrole(
                token = "Bearer $token",
                roleRequest = RoleRequest(role)
            )
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.e("SkillBoost", "Role selection failed: HTTP ${response.code()}, $errorBody")
                if (response.code() == 403 && errorBody.contains("Invalid token")) {
                    clearSession()
                    throw Exception("Invalid token, please log in again")
                }
                throw Exception("Role selection failed (HTTP ${response.code()}): $errorBody")
            } else {
                saveUserRole(role)
                Log.d("SkillBoost", "Role $role selected successfully")
            }
        } catch (e: ConnectException) {
            Log.e("SkillBoost", "Failed to connect to server: ${e.message}", e)
            throw Exception("Cannot connect to server, please check your network")
        } catch (e: SocketTimeoutException) {
            Log.e("SkillBoost", "Connection timed out: ${e.message}", e)
            throw Exception("Server took too long to respond, please try again")
        } catch (e: Exception) {
            Log.e("SkillBoost", "Network error in role selection: ${e.message}", e)
            throw e // Re-throw to preserve original message
        }
    }

    private fun saveUserRole(role: String) {
        sharedPreferences.edit()
            .putString("user_role", role)
            .apply()
        Log.d("SkillBoost", "Saved user role: $role")
    }

    fun getUserRole(): String? {
        val role = sharedPreferences.getString("user_role", null)
        Log.d("SkillBoost", "Retrieved user role: $role")
        return role
    }

    fun isLoggedIn(): Boolean {
        val isLoggedIn = sharedPreferences.getString("token", null) != null
        Log.d("SkillBoost", "isLoggedIn: $isLoggedIn")
        return isLoggedIn
    }

    fun getToken(): String? {
        if (!::sharedPreferences.isInitialized) {
            throw IllegalStateException("UserRepository not initialized")
        }
        val token = sharedPreferences.getString("token", null)
        Log.d("SkillBoost", "Retrieved token: $token")
        return token
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
        Log.d("SkillBoost", "Session cleared")
    }
}