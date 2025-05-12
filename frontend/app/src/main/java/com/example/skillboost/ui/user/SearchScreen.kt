package com.example.skillboost.ui.user

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.skillboost.R
import com.example.skillboost.models.Course
import com.example.skillboost.ui.common.CourseItemGrid
import com.example.skillboost.viewmodels.user.SearchViewModel
import com.example.skillboost.viewmodels.user.SearchViewModelFactory
import androidx.compose.foundation.lazy.grid.items


@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel = viewModel(factory = SearchViewModelFactory())) {
    LaunchedEffect(Unit) {
        Log.d("SkillBoost", "SearchScreen rendered")
    }

    Scaffold(
        bottomBar = { BottomNavigation(navController) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            SearchScreenContent(viewModel)
        }
    }
}

@Composable
fun SearchScreenContent(viewModel: SearchViewModel) {
    val courses by viewModel.courses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val scope = currentRecomposeScope

    LaunchedEffect(Unit) {
        Log.d("SkillBoost", "SearchScreenContent recomposed")
    }

    LaunchedEffect(searchQuery) {
        Log.d("SkillBoost", "LaunchedEffect: Syncing query $searchQuery")
        viewModel.updateSearchQuery(searchQuery)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .shadow(elevation = 8.dp)
                .zIndex(1f),
            color = Color(0xFFFFFFFF)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SB",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            color = Color(0xFF884FD0),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(8.dp)
                )

                TextField(
                    value = searchQuery,
                    onValueChange = {
                        Log.d("SkillBoost", "Search query updated: $it")
                        searchQuery = it
                        scope.invalidate()
                    },
                    placeholder = { Text("Search...", color = Color(0xFFBBBBBB)) },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .height(55.dp)
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            Log.d("SkillBoost", "TextField focus: ${focusState.isFocused}")
                        },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFB39DDB),
                        unfocusedContainerColor = Color(0xFFB39DDB),
                        disabledContainerColor = Color(0xFFB39DDB),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedPlaceholderColor = Color(0xFFBBBBBB),
                        unfocusedPlaceholderColor = Color(0xFFBBBBBB)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            Log.d("SkillBoost", "Search action triggered")
                            viewModel.updateSearchQuery(searchQuery)
                        }
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Icon",
                            tint = Color(0xFF884FD0)
                        )
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Discover",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                courses.isEmpty() && searchQuery.isNotBlank() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Course not found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                courses.size == 1 && searchQuery.isNotBlank() -> {
                    CourseItem(course = courses[0])
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(courses) { course ->
                            CourseItemGrid(course = course)
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun CourseItem(course: Course) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = course.image,
                contentDescription = course.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder_image),
                error = painterResource(R.drawable.placeholder_image),
                onError = { Log.e("SkillBoost", "Failed to load image: ${course.image}") }
            )
            Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = course.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}