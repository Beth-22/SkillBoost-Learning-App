package com.example.skillboost.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillboost.R
import com.example.skillboost.models.CourseStatus


@Composable
fun CourseCard(course: CourseStatus, onDelete: () -> Unit) {
    val buttonColor = when (course.status) {
        "Completed" -> Color.Gray
        "Active" -> Color(0xFF22C55E)
        "Enroll" -> Color(0xFF884FD0)
        else -> Color(0xFF9B6ED8)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEE7F8))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Course Image
            Image(
                painter = painterResource(id = course.imageRes),
                contentDescription = "${course.title} Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))

            // Course Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = course.description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { /* Enroll or action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Text(text = course.status, color = Color.White, fontSize = 12.sp)
                }
            }

            // Delete Button
            IconButton(
                onClick = onDelete,
                //modifier = Modifier.align(BottomEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete Course",
                    tint = Color(0xFFEE204D),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
