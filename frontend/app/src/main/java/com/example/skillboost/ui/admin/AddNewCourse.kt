package com.example.skillboost.ui.admin

import BottomNavigationAdmin
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.skillboost.data.CourseRepository
import com.example.skillboost.ui.common.Purple40
import com.example.skillboost.viewmodels.admin.AddNewCourseViewModel
import com.example.skillboost.viewmodels.admin.AddNewCourseViewModelFactory
import com.example.skillboost.viewmodels.admin.AdminCoursesViewModel
import com.example.skillboost.viewmodels.admin.AdminCoursesViewModelFactory
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewCourse(
    navController: NavController, // Moved navController to the first parameter
    modifier: Modifier = Modifier,
    viewModel: AddNewCourseViewModel = viewModel(
        factory = AddNewCourseViewModelFactory(
            courseRepository = CourseRepository.get(),
            context = LocalContext.current
        )
    ),
    adminCoursesViewModel: AdminCoursesViewModel = viewModel(
        factory = AdminCoursesViewModelFactory(
            context = LocalContext.current,
            courseRepository = CourseRepository.get()
        ),
        viewModelStoreOwner = navController.getBackStackEntry(Screen.AdminCourse.route)
    )
) {
    // Observe states
    val errorMessage by viewModel.errorMessage
    val courseSuccess by viewModel.courseSuccess
    val thumbnailSuccess by viewModel.thumbnailSuccess
    val videosSuccess by viewModel.videosSuccess
    val pdfsSuccess by viewModel.pdfsSuccess
    val courseId by viewModel.courseId

    // Navigate on final success (after all uploads) and refresh courses
    LaunchedEffect(pdfsSuccess) {
        if (pdfsSuccess && !courseId.isNullOrEmpty()) {
            adminCoursesViewModel.fetchUserCourses() // Refresh course list
            navController.navigate(Screen.AdminCourse.route)
            viewModel.resetSuccessStates()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Course", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple40
                )
            )
        },
        bottomBar = {
            BottomNavigationAdmin(navController = navController)
        }
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .padding(32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Display error message
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Course Title
            TextFieldArea(
                text = "Course Title",
                value = viewModel.title.value,
                onValueChange = { viewModel.updateTitle(it) }
            )

            // Course Description
            TextFieldArea(
                text = "Course Description",
                value = viewModel.description.value,
                onValueChange = { viewModel.updateDescription(it) },
                singleLine = false,
                modifier = Modifier.size(width = 355.dp, height = 100.dp)
            )

            // Add Course Button
            SaveBtn(
                text = "Add Course",
                onClick = { viewModel.createCourse() },
                enabled = viewModel.title.value.isNotBlank() && !courseSuccess
            )
            if (courseSuccess) {
                Text(
                    text = "Course created successfully!",
                    color = Color.Green,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Thumbnail Section
            DropDownComponents(
                text = "Add Thumbnail for your course",
                isExpanded = viewModel.isThumbnailExpanded.value,
                onToggle = { viewModel.toggleThumbnailExpanded() }
            ) {
                FileUploadArea(
                    onFilesSelected = { files ->
                        viewModel.onThumbnailFilesSelected(files)
                    },
                    selectedFiles = viewModel.thumbnailFile.value?.let { listOf(it) } ?: emptyList(),
                    mimeTypes = arrayOf("*/*")
                )
            }
            SaveBtn(
                text = "Add Thumbnail",
                onClick = { viewModel.uploadThumbnail() },
                enabled = courseSuccess && viewModel.thumbnailFile.value != null
            )
            if (thumbnailSuccess) {
                Text(
                    text = "Thumbnail uploaded successfully!",
                    color = Color.Green,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Videos Section
            DropDownComponents(
                text = "Add Videos",
                isExpanded = viewModel.isVideosExpanded.value,
                onToggle = { viewModel.toggleVideosExpanded() }
            ) {
                FileUploadArea(
                    onFilesSelected = { files -> viewModel.onVideosFilesSelected(files) },
                    selectedFiles = viewModel.videoFiles,
                    mimeTypes = arrayOf("*/*")
                )
            }
            SaveBtn(
                text = "Add Videos",
                onClick = { viewModel.uploadVideos() },
                enabled = courseSuccess && viewModel.videoFiles.isNotEmpty()
            )
            if (videosSuccess) {
                Text(
                    text = "Videos uploaded successfully!",
                    color = Color.Green,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Notes & Assignments Section
            DropDownComponents(
                text = "Add Notes & Assignments",
                isExpanded = viewModel.isNotesExpanded.value,
                onToggle = { viewModel.toggleNotesExpanded() }
            ) {
                FileUploadArea(
                    onFilesSelected = { files ->
                        viewModel.onPdfsFilesSelected(files)
                    },
                    selectedFiles = viewModel.pdfFiles,
                    mimeTypes = arrayOf("*/*")
                )
            }
            SaveBtn(
                text = "Add Notes & Assignments",
                onClick = { viewModel.uploadPdfs() },
                enabled = courseSuccess && viewModel.pdfFiles.isNotEmpty()
            )
            if (pdfsSuccess) {
                Text(
                    text = "Notes & Assignments uploaded successfully!",
                    color = Color.Green,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun TextFieldArea(
    text: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier.size(width = 355.dp, height = 58.dp)
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text) },
        singleLine = singleLine,
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF9B6ED8),
            unfocusedBorderColor = Color(0xFF884FD0),
            focusedLabelColor = Color(0xFF9B6ED8),
            cursorColor = Color(0xFF884FD0)
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun DropDownComponents(
    text: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.padding(top = 32.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {},
            enabled = false,
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.clickable { onToggle() }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF9B6ED8),
                unfocusedBorderColor = Color(0xFF884FD0),
                disabledBorderColor = Color(0xFF884FD0),
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Gray
            )
        )

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(modifier = Modifier.padding(top = 16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun FileUploadArea(
    onFilesSelected: (List<File>) -> Unit,
    selectedFiles: List<File>,
    modifier: Modifier = Modifier,
    mimeTypes: Array<String> = arrayOf("*/*")
) {
    val context = LocalContext.current
    var isDragging by remember { mutableStateOf(false) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        val files = uris.mapNotNull { uri ->
            val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}")
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file
        }
        onFilesSelected(files)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                if (isDragging) Color.LightGray.copy(alpha = 0.3f)
                else Color.LightGray.copy(alpha = 0.1f),
                RoundedCornerShape(24.dp)
            )
            .border(
                BorderStroke(
                    if (isDragging) 0.5.dp else 1.dp,
                    if (isDragging) Color(0xFF9B6ED8) else Color(0xFF884FD0)
                ),
                RoundedCornerShape(12.dp)
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onDrag = { change, _ -> change.consume() }
                )
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                filePicker.launch(mimeTypes)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Upload here.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "or click to browse",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (selectedFiles.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    "Selected Files:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                selectedFiles.forEach { file ->
                    Text(
                        file.name,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SaveBtn(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                .wrapContentWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple40,
                contentColor = Color.White
            ),
            enabled = enabled
        ) {
            Text(
                text,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}