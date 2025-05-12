package com.example.atry

import BottomNavigationAdmin
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.skillboost.R
import com.example.skillboost.viewmodels.admin.CourseDetailUiState
import com.example.skillboost.viewmodels.admin.CourseDetailViewModel
import com.example.skillboost.viewmodels.admin.Note
import com.example.skillboost.viewmodels.admin.Video
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import kotlin.time.Duration.Companion.seconds

@Preview(showBackground = true, widthDp = 400)
@Composable
fun CourseDetail_Preview() {
    // Mocked UI state with sample data
    val mockUiState = CourseDetailUiState(
        title = "Figma Master Class for Beginners",
        videosExpanded = true,
        notesExpanded = true,
        assignmentsExpanded = false,
        certificateClicked = false,
        videos = listOf(
            Video(
                id = "1",
                title = "1.1 Introduction to Figma",
                uri = Uri.parse("https://example.com/videos/intro.mp4")
            ),
            Video(
                id = "2",
                title = "1.2 Designing Your First Screen",
                uri = Uri.parse("https://example.com/videos/screen.mp4")
            )
        ),
        notes = listOf(
            Note(
                id = "1",
                title = "Figma Basics Notes",
                uri = Uri.parse("https://example.com/pdfs/notes.pdf")
            ),
            Note(
                id = "2",
                title = "Advanced Figma Techniques",
                uri = Uri.parse("https://example.com/pdfs/advanced.pdf")
            )
        ),
        selectedVideoUri = Uri.parse("https://example.com/videos/intro.mp4"),
        selectedNoteUri = Uri.parse("https://example.com/pdfs/notes.pdf"),
        error = null
    )

    // Mock NavController
    val navController = NavController(LocalContext.current)

    // Mock ViewModel with fixed UI state
    val mockViewModel: CourseDetailViewModel = FakeCourseDetailViewModel(mockUiState)

    // Render CourseDetail with mocked data
    CourseDetail(
        navController = navController,
        courseId = "preview_id",
        viewModel = mockViewModel
    )
}

class FakeCourseDetailViewModel(mockUiState: CourseDetailUiState) : CourseDetailViewModel() {
    override val uiState: StateFlow<CourseDetailUiState> = MutableStateFlow(mockUiState).asStateFlow()
}

@Composable
fun CourseDetail(
    navController: NavController,
    courseId: String,
    modifier: Modifier = Modifier,
    viewModel: CourseDetailViewModel = viewModel()
) {
    println("CourseDetail: Entered with courseId: $courseId")
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(courseId) {
        println("CourseDetail: Triggering fetchCourseDetails for courseId: $courseId")
        viewModel.fetchCourseDetails(courseId)
    }

    Scaffold(
        bottomBar = { BottomNavigationAdmin(navController) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            // Video Player or Error UI
            when {
                uiState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error ?: "Failed to load course details",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                uiState.selectedVideoUri != null -> {
                    val videoUri: Uri = uiState.selectedVideoUri as Uri
                    ProfessionalVideoPlayer(
                        videoUri = videoUri,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No video selected",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
            ImageUploaded(R.drawable.ic_trophy)
            Text(
                uiState.title,
                modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            // Note Viewer or Placeholder
            when {
                uiState.selectedNoteUri != null -> {
                    val noteUri: Uri = uiState.selectedNoteUri as Uri
                    NoteViewer(
                        noteUri = noteUri,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No note selected",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
            UserLessons(
                videosExpanded = uiState.videosExpanded,
                notesExpanded = uiState.notesExpanded,
                assignmentsExpanded = uiState.assignmentsExpanded,
                certificateClicked = uiState.certificateClicked,
                videos = uiState.videos,
                notes = uiState.notes,
                onVideosToggle = { viewModel.toggleVideos() },
                onNotesToggle = { viewModel.toggleNotes() },
                onAssignmentsToggle = { viewModel.toggleAssignments() },
                onCertificateClick = { viewModel.toggleCertificate() },
                onVideoSelect = { uri -> viewModel.selectVideo(uri) },
                onNoteSelect = { uri -> viewModel.selectNote(uri) }
            )
        }
    }
}

@Composable
fun ImageUploaded(image: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(image),
        contentDescription = "Course Image",
        modifier = modifier
            .padding(24.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

@Composable
fun NoteViewer(
    noteUri: Uri,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(noteUri) {
        try {
            // Convert resource URI to File using FileProvider
            val resourceId = noteUri.pathSegments.last().toIntOrNull()
            if (resourceId != null) {
                val tempFile = File(context.cacheDir, "temp_document.docx")
                context.resources.openRawResource(resourceId).use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                val contentUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    tempFile
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(contentUri, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    errorMessage = "No document viewer installed"
                }
            } else {
                errorMessage = "Invalid note URI"
            }
        } catch (e: Exception) {
            errorMessage = "Error opening document: ${e.message}"
        }
    }

    Box(
        modifier = modifier
            .background(Color.White)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "Opening document...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun UserLessons(
    videosExpanded: Boolean,
    notesExpanded: Boolean,
    assignmentsExpanded: Boolean,
    certificateClicked: Boolean,
    videos: List<Video>,
    notes: List<Note>,
    onVideosToggle: () -> Unit,
    onNotesToggle: () -> Unit,
    onAssignmentsToggle: () -> Unit,
    onCertificateClick: () -> Unit,
    onVideoSelect: (Uri) -> Unit,
    onNoteSelect: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(top = 16.dp)) {
        CourseComponent(
            text = "Videos",
            icon = R.drawable.ic_trophy,
            isExpanded = videosExpanded,
            onToggle = onVideosToggle
        ) { VideoHolder(videos = videos, notes = emptyList(), onVideoSelect = onVideoSelect, onNoteSelect = onNoteSelect) }

        CourseComponent(
            text = "Notes",
            icon = R.drawable.ic_trophy,
            isExpanded = notesExpanded,
            onToggle = onNotesToggle
        ) { VideoHolder(videos = emptyList(), notes = notes, onVideoSelect = onVideoSelect, onNoteSelect = onNoteSelect) }

        CourseComponent(
            text = "Assignments",
            icon = R.drawable.ic_trophy,
            isExpanded = assignmentsExpanded,
            onToggle = onAssignmentsToggle
        ) { VideoHolder(videos = emptyList(), notes = emptyList(), onVideoSelect = onVideoSelect, onNoteSelect = onNoteSelect) }

        GetCertificate(
            text = "Get your Certificate",
            icon = R.drawable.ic_trophy,
            isClicked = certificateClicked,
            onClick = onCertificateClick
        )
    }
}

@Composable
fun VideoHolder(
    videos: List<Video>,
    notes: List<Note>,
    onVideoSelect: (Uri) -> Unit,
    onNoteSelect: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        if (videos.isNotEmpty()) {
            videos.forEach { video ->
                CourseSubcomponent(
                    text = video.title,
                    icon = R.drawable.ic_trophy,
                    onClick = { onVideoSelect(video.uri) }
                )
            }
        } else if (notes.isNotEmpty()) {
            notes.forEach { note ->
                CourseSubcomponent(
                    text = note.title,
                    icon = R.drawable.ic_trophy,
                    onClick = { onNoteSelect(note.uri) }
                )
            }
        } else {
            repeat(4) {
                CourseSubcomponent(
                    text = "1.${it + 1} Part ${it + 1}.mp4",
                    icon = R.drawable.ic_trophy,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun CourseSubcomponent(
    text: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp)
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = "icon",
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(8.dp),
            thickness = 0.5.dp,
            color = Color(0xFF9A4DFF)
        )
    }
}

@Composable
fun CourseComponent(
    text: String,
    icon: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .then(
                if (isExpanded) Modifier
                    .border(1.dp, Color(0xFF9A4DFF), RoundedCornerShape(8.dp))
                else Modifier
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    if (!isExpanded) 1.dp else 0.5.dp,
                    color = if (!isExpanded) Color.Black else Color(0xFF9A4DFF),
                    shape = if (!isExpanded) RoundedCornerShape(8.dp) else RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp
                    )
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToggle
                )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = "icon",
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand"
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun GetCertificate(
    text: String,
    icon: Int,
    isClicked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = "certificate icon",
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun ProfessionalVideoPlayer(
    videoUri: Uri,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mockUri = Uri.parse("android.resource://${context.packageName}/raw/mockvideo")
    val sampleUri = Uri.parse("android.resource://${context.packageName}/raw/samplevideo")

    var currentUri by remember { mutableStateOf(videoUri) }
    var attemptCount by remember { mutableStateOf(0) }
    var player by remember { mutableStateOf<ExoPlayer?>(null) }

    // Handle lifecycle to pause/resume player
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> player?.pause()
                Lifecycle.Event.ON_RESUME -> player?.play()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player?.release()
        }
    }

    // Set up the player when URI changes
    LaunchedEffect(currentUri) {
        player?.release()
        val newPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(currentUri))
            prepare()
            playWhenReady = true
        }

        newPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                attemptCount += 1
                currentUri = when (attemptCount) {
                    1 -> mockUri
                    2 -> sampleUri
                    else -> sampleUri // final fallback
                }
            }
        })

        player = newPlayer
    }

    // Show video using AndroidView
    player?.let { exoPlayer ->
        AndroidView(
            modifier = modifier,
            factory = {
                PlayerView(it).apply {
                    this.player = exoPlayer
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            }
        )
    }
}


@OptIn(UnstableApi::class)
@Composable
private fun rememberExoPlayer(context: Context, uri: Uri, onError: (String) -> Unit): ExoPlayer {
    val trackSelector = remember {
        DefaultTrackSelector(context).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_OFF
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        println("ExoPlayer error: ${error.message}")
                        onError(error.message ?: "Unknown playback error")
                    }
                })
            }
    }

    LaunchedEffect(uri) {
        val dataSourceFactory = DefaultDataSource.Factory(context)
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.play()
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    return exoPlayer
}

@Composable
fun ControlsOverlay(
    isPlaying: Boolean,
    currentPosition: Long,
    totalDuration: Long,
    bufferedPercentage: Float,
    isFullScreen: Boolean,
    volume: Float,
    showSettings: Boolean,
    onPlayPauseToggle: () -> Unit,
    onSeekChanged: (Float) -> Unit,
    onFullScreenToggle: () -> Unit,
    onVolumeChanged: (Float) -> Unit,
    onSettingsToggle: () -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            IconButton(onClick = onSettingsToggle) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onPlayPauseToggle) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White
                )
            }
            IconButton(onClick = onFullScreenToggle) {
                Icon(
                    if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    contentDescription = if (isFullScreen) "Exit Fullscreen" else "Fullscreen",
                    tint = Color.White
                )
            }
        }

        Slider(
            value = currentPosition.toFloat(),
            onValueChange = onSeekChanged,
            valueRange = 0f..totalDuration.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Slider(
            value = volume,
            onValueChange = onVolumeChanged,
            valueRange = 0f..1f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}