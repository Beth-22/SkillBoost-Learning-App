package com.example.skillboost.viewmodels.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillboost.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RoleSelectionState {
    object Idle : RoleSelectionState()
    object Loading : RoleSelectionState()
    object Success : RoleSelectionState()
    object InvalidToken : RoleSelectionState()
    data class NetworkError(val message: String) : RoleSelectionState()
    data class Error(val message: String) : RoleSelectionState()
}

class RoleSelectionViewModel(private val repository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow<RoleSelectionState>(RoleSelectionState.Idle)
    val state: StateFlow<RoleSelectionState> = _state

    fun selectRole(role: String) {
        viewModelScope.launch {
            _state.value = RoleSelectionState.Loading
            try {
                repository.selectUserRole(role)
                _state.value = RoleSelectionState.Success
            } catch (e: Exception) {
                when {
                    e.message == "Invalid token, please log in again" -> {
                        _state.value = RoleSelectionState.InvalidToken
                    }
                    e.message?.contains("Cannot connect") == true || e.message?.contains("timed out") == true -> {
                        _state.value = RoleSelectionState.NetworkError(e.message ?: "Failed to connect to server")
                    }
                    else -> {
                        _state.value = RoleSelectionState.Error(e.message ?: "Failed to select role")
                    }
                }
            }
        }
    }

    fun resetState() {
        _state.value = RoleSelectionState.Idle
    }
}