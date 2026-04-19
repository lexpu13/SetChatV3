package com.example.setchat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage

@Composable
fun AvatarCircle(
    label: String,
    imageUri: String = "",
    size: Dp = 52.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFFDDEAE5), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri.isNotBlank()) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        } else {
            Text(
                text = label,
                color = Color(0xFF205844),
                fontWeight = FontWeight.Bold,
                fontSize = if (size < 40.dp) 12.sp else 20.sp
            )
        }
    }
}
