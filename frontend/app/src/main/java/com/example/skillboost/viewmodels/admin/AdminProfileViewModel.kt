// AdminProfileViewModel.kt
package com.example.skillboost.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.skillboost.data.AdminRepository
import com.example.skillboost.models.AdminData
import com.example.skillboost.models.AdminUpdateRequest
import kotlinx.coroutines.launch

class AdminProfileViewModel(private val repository: AdminRepository) : ViewModel() {
    private val _coursesCreated = mutableStateOf(5) // Default value set to 5 for testing
    val coursesCreated: State<Int> = _coursesCreated

    var profile by mutableStateOf<AdminData?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Load Admin Profile
    fun loadProfile(token: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val response = repository.fetchAdminProfile(token)
            if (response?.success == true) {
                profile = response.data
            } else {
                errorMessage = response?.message ?: "Failed to load profile"
            }
            isLoading = false
        }
    }

    // Update Admin Profile
    fun updateProfile(token: String, newUsername: String, newEmail: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            val response = repository.updateAdminProfile(token, AdminUpdateRequest(newUsername, newEmail))
            if (response?.success == true) {
                profile = response.data
                onSuccess()
            } else {
                errorMessage = response?.message ?: "Update failed"
            }
            isLoading = false
        }
    }

    // Update Courses Created Count
    fun setCoursesCount(count: Int) {
        _coursesCreated.value = count
    }
}

