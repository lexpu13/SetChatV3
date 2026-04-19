package com.example.setchat.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.view.ViewGroup
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.setchat.data.local.GalleryImage
import com.example.setchat.ui.components.AvatarCircle
import kotlinx.coroutines.delay

@Composable
fun AudioCallScreen(
    contactName: String,
    avatarLabel: String,
    avatarUri: String,
    onBack: () -> Unit,
    onStartVideoCall: () -> Unit
) {
    var muted by remember { mutableStateOf(false) }
    var speaker by remember { mutableStateOf(false) }
    var showKeypad by remember { mutableStateOf(false) }
    var digits by remember { mutableStateOf("") }
    var callConnected by remember { mutableStateOf(false) }
    var connectingDotsPhase by remember { mutableIntStateOf(0) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    val closeKeypad: () -> Unit = {
        showKeypad = false
        digits = ""
    }

    LaunchedEffect(Unit) {
        val connectDelayMs = 2000L
        val dotsIntervalMs = 300L
        var elapsed = 0L
        while (elapsed < connectDelayMs) {
            delay(dotsIntervalMs)
            elapsed += dotsIntervalMs
            connectingDotsPhase = (connectingDotsPhase + 1) % 3
        }
        callConnected = true
        elapsedSeconds = 0
        while (true) {
            delay(1000)
            elapsedSeconds += 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D1621), Color(0xFF101F34))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 44.dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!showKeypad) {
                    AvatarCircle(
                        label = avatarLabel,
                        imageUri = avatarUri,
                        size = 102.dp
                    )
                    Text(
                        text = contactName,
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 14.dp)
                    )
                }
                Text(
                    text = "Appel en cours",
                    color = Color(0xFFB7C6D8),
                    fontSize = 15.sp
                )
                if (!callConnected) {
                    AnimatedConnectingDots(phase = connectingDotsPhase)
                }
                if (callConnected) {
                    Text(
                        text = formatCallTimer(elapsedSeconds),
                        color = Color(0xFFD8E4F2),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (showKeypad) {
                    Text(
                        text = digits,
                        color = Color.White,
                        fontSize = 28.sp,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(if (showKeypad) 10.dp else 24.dp))

                if (showKeypad) {
                    DialPad(
                        onDigit = { digits += it },
                        onClose = closeKeypad
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val actionSpacing = 8.dp
                    val actionSlotWidth = (maxWidth - (actionSpacing * 3)) / 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(actionSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CallAction(
                            icon = Icons.Default.MicOff,
                            label = "Muet",
                            active = muted,
                            onClick = { muted = !muted },
                            buttonSize = if (showKeypad) 62.dp else 58.dp,
                            slotWidth = actionSlotWidth
                        )
                        if (showKeypad) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.size(width = actionSlotWidth, height = 92.dp)
                            ) {
                                Surface(
                                    color = Color(0xFFE53935),
                                    shape = CircleShape,
                                    modifier = Modifier.size(62.dp)
                                ) {
                                    IconButton(onClick = onBack) {
                                        Icon(Icons.Default.CallEnd, contentDescription = "Fin d'appel", tint = Color.White)
                                    }
                                }
                                Text("Fin appel", color = Color(0xFFD6E0EB), fontSize = 12.sp)
                            }
                        } else {
                            CallAction(
                                icon = Icons.Default.Dialpad,
                                label = "Clavier",
                                active = false,
                                onClick = { showKeypad = true },
                                buttonSize = 58.dp,
                                slotWidth = actionSlotWidth
                            )
                        }
                        CallAction(
                            icon = Icons.Default.VolumeUp,
                            label = "H-P",
                            active = speaker,
                            onClick = { speaker = !speaker },
                            buttonSize = if (showKeypad) 62.dp else 58.dp,
                            slotWidth = actionSlotWidth
                        )
                        CallAction(
                            icon = Icons.Default.Videocam,
                            label = "Caméra",
                            active = false,
                            onClick = onStartVideoCall,
                            buttonSize = if (showKeypad) 62.dp else 58.dp,
                            slotWidth = actionSlotWidth
                        )
                    }
                }
                if (!showKeypad) {
                    Surface(
                        color = Color(0xFFE53935),
                        shape = CircleShape,
                        modifier = Modifier.size(66.dp)
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.CallEnd, contentDescription = "Raccrocher", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoCallScreen(
    contactName: String,
    avatarLabel: String,
    avatarUri: String,
    galleryVideos: List<GalleryImage>,
    initialBackgroundVideoUri: String,
    onBack: () -> Unit
) {
    var rearCamera by remember { mutableStateOf(false) }
    var muted by remember { mutableStateOf(false) }
    var localCameraEnabled by remember { mutableStateOf(true) }
    var showOptions by remember { mutableStateOf(false) }
    var selectedBackgroundVideoUri by remember(initialBackgroundVideoUri, galleryVideos) {
        mutableStateOf(
            when {
                initialBackgroundVideoUri.isNotBlank() -> initialBackgroundVideoUri
                else -> galleryVideos.firstOrNull()?.uri.orEmpty()
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF101722), Color(0xFF1B2A3B))
                )
            )
    ) {
        if (selectedBackgroundVideoUri.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF202E3C), Color(0xFF0D1117))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AvatarCircle(label = avatarLabel, imageUri = avatarUri, size = 96.dp)
                    Text(
                        text = contactName,
                        color = Color.White,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Text(
                        text = "Ajoute une vidéo dans la galerie puis choisis-la via Options",
                        color = Color(0xFFB7C6D8),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        } else {
            LoopingVideoBackground(
                videoUri = selectedBackgroundVideoUri,
                modifier = Modifier.fillMaxSize()
            )
        }

        Surface(
            color = Color(0x8830363F),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 14.dp, top = 42.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarCircle(label = avatarLabel, imageUri = avatarUri, size = 24.dp)
                Text(
                    text = "  $contactName",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Surface(
            color = Color(0x990D1117),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0x33FFFFFF)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = 24.dp)
                .size(width = 118.dp, height = 176.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            if (localCameraEnabled) {
                LiveCameraPreview(
                    lensFacing = if (rearCamera) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VideocamOff, contentDescription = null, tint = Color.White)
                        Text("Caméra coupée", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }

        IconButton(
            onClick = { rearCamera = !rearCamera },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(start = 12.dp, end = 12.dp, top = 40.dp, bottom = 12.dp)
        ) {
            Icon(Icons.Default.Cameraswitch, contentDescription = "Basculer caméra", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 14.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                color = Color(0xFFE54B4B),
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.CallEnd, contentDescription = "Raccrocher", tint = Color.White)
                }
            }
            Surface(
                color = if (muted) Color(0x661F7AE0) else Color(0x66303A46),
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                IconButton(onClick = { muted = !muted }) {
                    Icon(
                        imageVector = Icons.Default.MicOff,
                        contentDescription = "Couper le micro",
                        tint = Color.White
                    )
                }
            }
            Surface(
                color = Color(0x66303A46),
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                IconButton(onClick = { showOptions = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "Options",
                        tint = Color.White
                    )
                }
            }
            Surface(
                color = if (!localCameraEnabled) Color(0x661F7AE0) else Color(0x66303A46),
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                IconButton(onClick = { localCameraEnabled = !localCameraEnabled }) {
                    Icon(
                        imageVector = if (localCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Couper la caméra",
                        tint = Color.White
                    )
                }
            }
        }

        if (showOptions) {
            VideoOptionsDialog(
                galleryVideos = galleryVideos,
                selectedUri = selectedBackgroundVideoUri,
                onSelect = { uri ->
                    selectedBackgroundVideoUri = uri
                    showOptions = false
                },
                onClose = { showOptions = false }
            )
        }
    }
}

@Composable
private fun LoopingVideoBackground(
    videoUri: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            VideoView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = { videoView ->
            if (videoView.tag != videoUri) {
                videoView.setVideoURI(Uri.parse(videoUri))
                videoView.setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.isLooping = true
                    mediaPlayer.setVolume(0f, 0f)
                    videoView.start()
                }
                videoView.tag = videoUri
            } else if (!videoView.isPlaying) {
                videoView.start()
            }
        }
    )
}

@Composable
private fun LiveCameraPreview(
    lensFacing: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!hasPermission) {
        Box(
            modifier = modifier.background(Color(0xFF0D1117)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.VideocamOff, contentDescription = null, tint = Color.White)
        }
        return
    }

    AndroidView(
        modifier = modifier,
        factory = { previewContext ->
            PreviewView(previewContext).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener(
                {
                    val cameraProvider = runCatching { cameraProviderFuture.get() }.getOrNull() ?: return@addListener
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build()
                    runCatching {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview)
                    }
                },
                ContextCompat.getMainExecutor(context)
            )
        }
    )

    DisposableEffect(lifecycleOwner, lensFacing, hasPermission) {
        onDispose {
            if (hasPermission) {
                runCatching { ProcessCameraProvider.getInstance(context).get().unbindAll() }
            }
        }
    }
}

@Composable
private fun VideoOptionsDialog(
    galleryVideos: List<GalleryImage>,
    selectedUri: String,
    onSelect: (String) -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF121C26)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Fond de l'appel visio",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedUri.isBlank()) Color(0x332B6CB0) else Color(0x33202C33),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect("") }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VideocamOff, contentDescription = null, tint = Color.White)
                        Text(
                            text = "Aucun fond vidéo",
                            color = Color.White,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
                if (galleryVideos.isEmpty()) {
                    Text("Aucune vidéo dans la galerie.", color = Color(0xFF9FB0BA))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(galleryVideos, key = { it.id }) { video ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedUri == video.uri) Color(0x332B6CB0) else Color(0x33202C33),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(video.uri) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = Color(0xFF0F1720),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White)
                                        }
                                    }
                                    Text(
                                        text = "Vidéo #${video.id}",
                                        color = Color.White,
                                        modifier = Modifier.padding(start = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedConnectingDots(phase: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        repeat(3) { index ->
            Surface(
                color = Color(0xFFB7C6D8).copy(alpha = if (index <= phase) 1f else 0.28f),
                shape = CircleShape,
                modifier = Modifier.size(6.dp)
            ) {}
        }
    }
}

@Composable
private fun CallAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    buttonSize: androidx.compose.ui.unit.Dp = 56.dp,
    slotWidth: androidx.compose.ui.unit.Dp = 98.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.size(width = slotWidth, height = 92.dp)
    ) {
        Surface(
            color = if (active) Color(0xFF2B6CB0) else Color(0x55202C33),
            shape = CircleShape,
            modifier = Modifier.size(buttonSize)
        ) {
            IconButton(onClick = onClick) {
                Icon(icon, contentDescription = label, tint = Color.White)
            }
        }
        Text(label, color = Color(0xFFD6E0EB), fontSize = 12.sp)
    }
}

@Composable
private fun DialPad(
    onDigit: (String) -> Unit,
    onClose: () -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("*", "0", "#")
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { digit ->
                    Surface(
                        color = Color(0x55202C33),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(72.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onDigit(digit) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(digit, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0x55202C33),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(40.dp)
                    .clickable(onClick = onClose)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Fermer clavier", color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}

private fun formatCallTimer(totalSeconds: Int): String {
    val minutes = (totalSeconds / 60).coerceAtLeast(0)
    val seconds = (totalSeconds % 60).coerceAtLeast(0)
    return "%02d:%02d".format(minutes, seconds)
}
