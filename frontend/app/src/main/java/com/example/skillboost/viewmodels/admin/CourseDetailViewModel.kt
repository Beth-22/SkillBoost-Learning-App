package com.example.skillboost.viewmodels.admin

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillboost.data.CourseRepository
import com.example.skillboost.models.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CourseDetailUiState(
    val title: String = "",
    val videosExpanded: Boolean = false,
    val notesExpanded: Boolean = false,
    val assignmentsExpanded: Boolean = false,
    val certificateClicked: Boolean = false,
    val videos: List<Video> = emptyList(),
    val notes: List<Note> = emptyList(),
    val selectedVideoUri: Uri? = null,
    val selectedNoteUri: Uri? = null,
    val error: String? = null
)

data class Video(
    val id: String,
    val title: String,
    val uri: Uri
)

data class Note(
    val id: String,
    val title: String,
    val uri: Uri
)

open class CourseDetailViewModel : ViewModel() {
    private val courseRepository = CourseRepository.get()
    private val _uiState = MutableStateFlow(CourseDetailUiState())
    open val uiState: StateFlow<CourseDetailUiState> = _uiState.asStateFlow()

    fun fetchCourseDetails(courseId: String) {
        viewModelScope.launch {
            try {
                println("CourseDetailViewModel: Fetching course details for ID: $courseId")
                val course = courseRepository.getCourseById(courseId)
                if (course != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            title = course.title,
                            videos = course.content?.filter { it.type == "video" }?.map {
                                Video(
                                    id = it.id,
                                    title = it.title,
                                    uri = Uri.parse("http://192.168.145.103:5000/api/${it.url}")
                                )
                            } ?: emptyList(),
                            notes = course.content?.filter { it.type == "pdf" }?.map {
                                Note(
                                    id = it.id,
                                    title = it.title,
                                    uri = Uri.parse("http://192.168.145.103:5000/api/${it.url}")
                                )
                            } ?: emptyList(),
                            error = null
                        )
                    }
                    println("CourseDetailViewModel: Fetched course: ${course.title}")
                } else {
                    _uiState.update { it.copy(error = "Course not found") }
                    println("CourseDetailViewModel: Course not found for ID: $courseId")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to fetch course: ${e.message}") }
                println("CourseDetailViewModel: Error fetching course: ${e.message}")
            }
        }
    }

    fun toggleVideos() {
        _uiState.update { it.copy(videosExpanded = !it.videosExpanded) }
    }

    fun toggleNotes() {
        _uiState.update { it.copy(notesExpanded = !it.notesExpanded) }
    }

    fun toggleAssignments() {
        _uiState.update { it.copy(assignmentsExpanded = !it.assignmentsExpanded) }
    }

    fun toggleCertificate() {
        _uiState.update { it.copy(certificateClicked = !it.certificateClicked) }
    }

    fun selectVideo(uri: Uri) {
        _uiState.update { it.copy(selectedVideoUri = uri, error = null) }
    }

    fun selectNote(uri: Uri) {
        _uiState.update { it.copy(selectedNoteUri = uri, error = null) }
    }
}