package com.example.skillboost.viewmodels.admin

import androidx.lifecycle.ViewModel
import com.example.skillboost.ui.admin.CourseUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CoursesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CourseUiState())
    val uiState: StateFlow<CourseUiState> = _uiState.asStateFlow()

    fun toggleVideos() {
        _uiState.update {
            it.copy(videosExpanded = !it.videosExpanded)
        }
    }

    fun toggleNotes() {
        _uiState.update {
            it.copy(notesExpanded = !it.notesExpanded)
        }
    }

    fun toggleAssignments() {
        _uiState.update {
            it.copy(assignmentsExpanded = !it.assignmentsExpanded)
        }
    }
}