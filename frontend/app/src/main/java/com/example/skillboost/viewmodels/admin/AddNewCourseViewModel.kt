package com.example.skillboost.viewmodels.admin

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillboost.data.CourseRepository
import com.example.skillboost.data.UserRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.util.*

class AddNewCourseViewModel(
    private val courseRepository: CourseRepository,
    private val context: Context
) : ViewModel() {
    var title = mutableStateOf("")
        private set

    var description = mutableStateOf("")
        private set

    var courseId = mutableStateOf<String?>(null)
        private set

    var isThumbnailExpanded = mutableStateOf(false)
        private set

    var isVideosExpanded = mutableStateOf(false)
        private set

    var isNotesExpanded = mutableStateOf(false)
        private set

    var thumbnailFile = mutableStateOf<File?>(null)
        private set

    val videoFiles = mutableStateListOf<File>()
    val pdfFiles = mutableStateListOf<File>()

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var courseSuccess = mutableStateOf(false)
        private set

    var thumbnailSuccess = mutableStateOf(false)
        private set

    var videosSuccess = mutableStateOf(false)
        private set

    var pdfsSuccess = mutableStateOf(false)
        private set

    var isSaving = mutableStateOf(false)
        private set

    fun updateTitle(newTitle: String) {
        title.value = newTitle
    }

    fun updateDescription(newDescription: String) {
        description.value = newDescription
    }

    fun toggleThumbnailExpanded() {
        isThumbnailExpanded.value = !isThumbnailExpanded.value
    }

    fun toggleVideosExpanded() {
        isVideosExpanded.value = !isVideosExpanded.value
    }

    fun toggleNotesExpanded() {
        isNotesExpanded.value = !isNotesExpanded.value
    }

    fun onThumbnailFilesSelected(files: List<File>) {
        thumbnailFile.value = files.firstOrNull()?.let { file ->
            if (file.exists() && file.length() > 0) {
                val renamedFile = renameFile(file)
                println("Thumbnail file selected: ${renamedFile.name}, size: ${renamedFile.length()} bytes")
                renamedFile
            } else {
                println("Skipping invalid thumbnail file: ${file.name}")
                null
            }
        }
        if (thumbnailFile.value == null && files.isNotEmpty()) {
            println("Thumbnail selection failed due to invalid file")
        }
    }

    fun onVideosFilesSelected(files: List<File>) {
        videoFiles.clear()
        files.forEach { file ->
            if (file.exists() && file.length() > 0) {
                val renamedFile = renameFile(file)
                println("Video file selected: ${renamedFile.name}, size: ${renamedFile.length()} bytes")
                videoFiles.add(renamedFile)
            } else {
                println("Skipping invalid video file: ${file.name}")
            }
        }
        println("Video files selected: ${videoFiles.size}")
    }

    fun onPdfsFilesSelected(files: List<File>) {
        pdfFiles.clear()
        files.forEach { file ->
            if (file.exists() && file.length() > 0) {
                val renamedFile = renameFile(file)
                println("PDF file selected: ${renamedFile.name}, size: ${renamedFile.length()} bytes")
                pdfFiles.add(renamedFile)
            } else {
                println("Skipping invalid PDF file: ${file.name}")
            }
        }
        println("PDF files selected: ${pdfFiles.size}")
    }

    private fun renameFile(file: File): File {
        val extension = file.extension.takeIf { it.isNotEmpty() }?.let { ".$it" } ?: ""
        val newName = "${file.nameWithoutExtension}_${System.currentTimeMillis()}$extension"
        val newFile = File(file.parent, newName)
        file.copyTo(newFile, overwrite = true)
        return newFile
    }

    fun createCourse() {
        println("createCourse called")
        viewModelScope.launch {
            isSaving.value = true
            try {
                if (title.value.isBlank()) {
                    errorMessage.value = "Title is required"
                    println("Validation failed: Title is blank")
                    return@launch
                }

                println("Calling createCourse with: title=${title.value}, description=${description.value}")
                val course = courseRepository.createCourse(
                    title = title.value,
                    description = description.value
                )

                if (course != null) {
                    courseId.value = course.id
                    courseSuccess.value = true
                    errorMessage.value = null
                    println("Course created successfully: $course, ID: ${course.id}")
                } else {
                    val token = UserRepository.getToken()
                    errorMessage.value = if (token == null) {
                        "Please log in to create a course"
                    } else {
                        "Failed to create course. Check your network or try again."
                    }
                    println("createCourse returned null")
                }
            } catch (e: Exception) {
                errorMessage.value = "Error creating course: ${e.message}"
                println("Exception in createCourse: ${e.message}, stacktrace: ${e.stackTraceToString()}")
            } finally {
                isSaving.value = false
            }
        }
    }

    fun uploadThumbnail() {
        viewModelScope.launch {
            isSaving.value = true
            try {
                val courseIdValue = courseId.value
                if (courseIdValue == null || courseIdValue.isBlank()) {
                    errorMessage.value = "Course must be created first"
                    println("Error: Course ID is null or blank")
                    return@launch
                }
                if (thumbnailFile.value == null) {
                    errorMessage.value = "No thumbnail selected"
                    println("Error: No thumbnail selected")
                    return@launch
                }

                println("Uploading thumbnail for courseId: $courseIdValue")
                val response = courseRepository.uploadThumbnail(courseIdValue, thumbnailFile.value!!)
                if (response.isSuccessful) {
                    thumbnailSuccess.value = true
                    errorMessage.value = null
                    println("Thumbnail uploaded successfully")
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage.value = errorBody?.let {
                        try {
                            val json = JSONObject(it)
                            json.getString("message") ?: "Failed to upload thumbnail: HTTP ${response.code()}"
                        } catch (e: Exception) {
                            "Failed to upload thumbnail: HTTP ${response.code()}"
                        }
                    } ?: "Failed to upload thumbnail"
                    println("Failed to upload thumbnail: HTTP ${response.code()}, $errorBody")
                }
            } catch (e: Exception) {
                errorMessage.value = "Error uploading thumbnail: ${e.message}"
                println("Exception in uploadThumbnail: ${e.message}")
            } finally {
                isSaving.value = false
            }
        }
    }

    fun uploadVideos() {
        println("uploadVideos called")
        viewModelScope.launch {
            isSaving.value = true
            try {
                val courseId = courseId.value ?: run {
                    errorMessage.value = "Course must be created first"
                    println("Error: Course ID not available")
                    return@launch
                }

                if (videoFiles.isEmpty()) {
                    errorMessage.value = "No videos selected"
                    println("Error: No videos selected")
                    return@launch
                }

                var allSuccess = true
                videoFiles.forEach { video ->
                    val success = courseRepository.uploadVideo(courseId, video)
                    if (!success) {
                        allSuccess = false
                        println("Failed to upload video: ${video.name}")
                    }
                }

                videosSuccess.value = allSuccess
                errorMessage.value = if (allSuccess) null else "One or more videos failed to upload"
                println("Video upload completed: allSuccess=$allSuccess")
            } catch (e: Exception) {
                errorMessage.value = "Error uploading videos: ${e.message}"
                println("Exception in uploadVideos: ${e.message}, stacktrace: ${e.stackTraceToString()}")
            } finally {
                isSaving.value = false
            }
        }
    }

    fun uploadPdfs() {
        println("uploadPdfs called")
        viewModelScope.launch {
            isSaving.value = true
            try {
                val courseId = courseId.value ?: run {
                    errorMessage.value = "Course must be created first"
                    println("Error: Course ID not available")
                    return@launch
                }
                if (pdfFiles.isEmpty()) {
                    errorMessage.value = "No PDFs selected"
                    println("Error: No PDFs selected")
                    return@launch
                }

                println("Uploading ${pdfFiles.size} PDFs to courseId=$courseId")
                var allSuccessful = true

                for (pdf in pdfFiles) {
                    val success = courseRepository.uploadPdf(courseId, pdf)
                    if (!success) {
                        allSuccessful = false
                        println("Failed to upload PDF: ${pdf.name}")
                        break
                    }
                }

                if (allSuccessful) {
                    pdfsSuccess.value = true
                    errorMessage.value = null
                    println("All PDFs uploaded successfully")
                } else {
                    errorMessage.value = "Failed to upload one or more PDFs"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error uploading PDFs: ${e.message}"
                println("Exception in uploadPdfs: ${e.message}, stacktrace: ${e.stackTraceToString()}")
            } finally {
                isSaving.value = false
            }
        }
    }

    fun resetSuccessStates() {
        courseSuccess.value = false
        thumbnailSuccess.value = false
        videosSuccess.value = false
        pdfsSuccess.value = false
        courseId.value = null
        title.value = ""
        description.value = ""
        thumbnailFile.value = null
        videoFiles.clear()
        pdfFiles.clear()
        errorMessage.value = null
    }
}

class AddNewCourseViewModelFactory(
    private val courseRepository: CourseRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddNewCourseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddNewCourseViewModel(courseRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}