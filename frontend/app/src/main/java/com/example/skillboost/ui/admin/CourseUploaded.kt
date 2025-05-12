package com.example.skillboost.ui.admin

import BottomNavigationAdmin
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.skillboost.R
import com.example.skillboost.viewmodels.admin.CoursesViewModel

data class CourseUiState(
    val title: String = "Figma Master Class for Beginners",
    val videosExpanded: Boolean = false,
    val notesExpanded: Boolean = false,
    val assignmentsExpanded: Boolean = false
)

@Preview(showBackground = true, widthDp = 400)
@Composable
fun CoursesUploaded_Preview() {
    CoursesUploaded(navController = NavController(LocalContext.current)) // Requires context
}

@Composable
fun CoursesUploaded(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CoursesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = { BottomNavigationAdmin(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            ImageUploaded(R.drawable.ic_trophy)
            Text(
                uiState.title,
                modifier = Modifier.padding(start = 24.dp, bottom = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            CoursePartHolder(
                videosExpanded = uiState.videosExpanded,
                notesExpanded = uiState.notesExpanded,
                assignmentsExpanded = uiState.assignmentsExpanded,
                onVideosToggle = { viewModel.toggleVideos() },
                onNotesToggle = { viewModel.toggleNotes() },
                onAssignmentsToggle = { viewModel.toggleAssignments() }
            )
        }
    }
}

@Composable
fun ImageUploaded(image: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(image),
        contentDescription = "Figma",
        modifier = Modifier
            .padding(24.dp)
            .clip(shape = RoundedCornerShape(16.dp))
    )
}

@Composable
fun CoursePartHolder(
    videosExpanded: Boolean,
    notesExpanded: Boolean,
    assignmentsExpanded: Boolean,
    onVideosToggle: () -> Unit,
    onNotesToggle: () -> Unit,
    onAssignmentsToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        CourseComponent(
            text = "Videos",
            icon = R.drawable.ic_trophy,
            isExpanded = videosExpanded,
            onToggle = onVideosToggle
        ) { VideoHolder() }
        CourseComponent(
            text = "Notes",
            icon = R.drawable.ic_trophy,
            isExpanded = notesExpanded,
            onToggle = onNotesToggle
        ) { VideoHolder() }
        CourseComponent(
            text = "Assignments",
            icon = R.drawable.ic_trophy,
            isExpanded = assignmentsExpanded,
            onToggle = onAssignmentsToggle
        ) { VideoHolder() }
    }
}

@Composable
fun CourseSubcomponent(text: String, icon: Int, modifier: Modifier = Modifier) {
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

    HorizontalDivider(modifier = Modifier.padding(8.dp), thickness = 0.5.dp, color = Color(0xFF9A4DFF))
}

@Composable
fun VideoHolder(modifier: Modifier = Modifier) {
    Column {
        CourseSubcomponent("1.1 Part 1.mp4", R.drawable.ic_trophy)
        CourseSubcomponent("1.1 Part 1.mp4", R.drawable.ic_trophy)
        CourseSubcomponent("1.1 Part 1.mp4", R.drawable.ic_trophy)
        CourseSubcomponent("1.1 Part 1.mp4", R.drawable.ic_trophy)
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
        modifier = Modifier
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
            Image(
                if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand"
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)) {
                content()
            }
        }
    }
}