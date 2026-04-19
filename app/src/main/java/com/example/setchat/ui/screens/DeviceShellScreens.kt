package com.example.setchat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.setchat.viewmodel.DeviceShellSettings

@Composable
fun DeviceOffScreen(onTap: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(onClick = onTap)
    )
}

@Composable
fun DeviceLockScreen(
    settings: DeviceShellSettings,
    onTap: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onTap)
    ) {
        DeviceWallpaper(
            wallpaperUri = settings.lockWallpaperUri,
            fallbackBrush = Brush.verticalGradient(
                colors = listOf(Color(0xFF2F343C), Color(0xFF1A1E24), Color(0xFF0D1014))
            )
        )
        if (settings.showLockTime) {
            Text(
                text = settings.lockTime.ifBlank { "19:42" },
                color = Color.White,
                fontSize = 56.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 112.dp)
            )
        }
        if (settings.showLockDate) {
            Text(
                text = settings.lockDate.ifBlank { "vendredi 12 avril" },
                color = Color(0xFFE9EEF5),
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 176.dp)
            )
        }
        Surface(
            color = Color(0x33000000),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 82.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = "Touchez pour deverrouiller",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun DeviceHomeScreen(
    settings: DeviceShellSettings,
    onOpenSetChat: () -> Unit,
    onOpenPlans: () -> Unit,
    onOpenEditor: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        DeviceWallpaper(
            wallpaperUri = settings.homeWallpaperUri,
            fallbackBrush = Brush.verticalGradient(
                colors = listOf(Color(0xFF30353D), Color(0xFF1B1F26), Color(0xFF0C0F14))
            )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Surface(
                color = Color(0x55000000),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Rechercher",
                    color = Color(0xCCFFFFFF),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AppIconTile(icon = Icons.Default.Call, label = "Tel")
                AppIconTile(icon = Icons.Default.CameraAlt, label = "Camera")
                AppIconTile(icon = Icons.Default.Map, label = "Plans", onClick = onOpenPlans)
                AppIconTile(icon = Icons.Default.Email, label = "Mail")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AppIconTile(icon = Icons.Default.Image, label = "Photos")
                AppIconTile(icon = Icons.Default.MusicNote, label = "Musique")
                AppIconTile(icon = Icons.Default.Settings, label = "Edition", onClick = onOpenEditor)
                AppIconTile(
                    icon = Icons.Default.Chat,
                    label = "SetChat",
                    highlighted = true,
                    onClick = onOpenSetChat
                )
            }
        }
    }
}

@Composable
private fun DeviceWallpaper(
    wallpaperUri: String,
    fallbackBrush: Brush
) {
    val uri = wallpaperUri.trim()
    if (uri.isNotBlank()) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(fallbackBrush)
        )
    }
}

@Composable
private fun AppIconTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    highlighted: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .size(width = 70.dp, height = 86.dp)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (highlighted) Color(0xFF1F7AE0) else Color(0x7F000000),
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(18.dp))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = Color.White
                )
            }
        }
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}
