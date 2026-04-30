@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.example.setchat.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.example.setchat.data.local.Contact
import com.example.setchat.data.local.Conversation
import com.example.setchat.data.local.GalleryImage
import com.example.setchat.data.local.Message
import com.example.setchat.ui.components.AvatarCircle
import com.example.setchat.ui.components.ChatBubble
import com.example.setchat.ui.components.FakeStatusBar
import com.example.setchat.viewmodel.DeviceShellSettings
import com.example.setchat.viewmodel.ExportSettings
import com.example.setchat.viewmodel.FakeStatusBarSettings
import com.example.setchat.viewmodel.NotificationConfig
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private enum class EditorTopSection {
    HUB,
    CONTACTS,
    GROUPS,
    DISCUSSIONS,
    APPEARANCE,
    GALLERY,
    NOTIFICATIONS
}

private data class NotificationVisualPreview(
    val sender: Contact?,
    val conversation: Conversation?,
    val previewText: String,
    val useProfilePhoto: Boolean,
    val triggerKey: String,
    val durationSeconds: Int,
    val stamp: Long = System.currentTimeMillis()
)

@Composable
fun EditorHubScreen(
    onBack: () -> Unit,
    onGoToApp: () -> Unit,
    onOpenEditorHome: () -> Unit,
    onOpenContacts: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenDiscussions: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenNotifications: () -> Unit,
    onResetAll: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 88.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Mode accessoiriste", fontWeight = FontWeight.Bold)
                    Button(onClick = onResetAll) {
                        Text("Reinitialiser toute l'app")
                    }
                }
            }
            item {
                EditorMenuCard(
                    icon = Icons.Default.Person,
                    title = "Contacts",
                    onClick = onOpenContacts
                )
            }
            item {
                EditorMenuCard(
                    icon = Icons.Default.Groups,
                    title = "Groupes",
                    onClick = onOpenGroups
                )
            }
            item {
                EditorMenuCard(
                    icon = Icons.Default.Chat,
                    title = "Discussions",
                    onClick = onOpenDiscussions
                )
            }
            item {
                EditorMenuCard(
                    icon = Icons.Default.Palette,
                    title = "Apparence",
                    onClick = onOpenAppearance
                )
            }
            item {
                EditorMenuCard(
                    icon = Icons.Default.Image,
                    title = "Galerie",
                    onClick = onOpenGallery
                )
            }
            item {
                EditorMenuCard(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    onClick = onOpenNotifications
                )
            }
        }
        FloatingNavButtons(
            onBack = onBack,
            onGoToApp = onGoToApp,
            currentSection = EditorTopSection.HUB,
            onOpenEditorHome = onOpenEditorHome,
            onOpenContacts = onOpenContacts,
            onOpenGroups = onOpenGroups,
            onOpenDiscussions = onOpenDiscussions,
            onOpenAppearance = onOpenAppearance,
            onOpenGallery = onOpenGallery,
            onOpenNotifications = onOpenNotifications
        )
    }
}

@Composable
fun GalleryEditorScreen(
    galleryImages: List<GalleryImage>,
    onBack: () -> Unit,
    onGoToApp: () -> Unit,
    onOpenEditorHome: () -> Unit,
    onOpenContacts: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenDiscussions: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenNotifications: () -> Unit,
    onResetGallery: () -> Unit,
    onImportImage: (String) -> Unit,
    onImportVideo: (String) -> Unit,
    onDeleteImage: (Long) -> Unit
) {
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            onImportImage(uri.toString())
        }
    }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            onImportVideo(uri.toString())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 88.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionCard(
                    title = "Galerie",
                    subtitle = "Reinitialise seulement les medias de la galerie."
                ) {
                    Button(onClick = onResetGallery) {
                        Text("Reinitialiser la section")
                    }
                }
            }
            item {
                SectionCard(
                    title = "Galerie interne",
                    subtitle = "Importe des images et vidéos depuis le téléphone."
                ) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { imagePicker.launch(arrayOf("image/*")) }) {
                            Text("Importer image")
                        }
                        Button(onClick = { videoPicker.launch(arrayOf("video/*")) }) {
                            Text("Importer vidéo")
                        }
                    }
                    if (galleryImages.isEmpty()) {
                        Text("Aucun média dans la galerie.", color = Color(0xFF667781))
                    } else {
                        galleryImages.forEach { image ->
                            Surface(color = Color(0xFFF7F9FA), shape = RoundedCornerShape(14.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (image.mediaType == "video") {
                                        Surface(
                                            color = Color(0xFF0F1720),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .width(64.dp)
                                                .height(64.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Videocam,
                                                    contentDescription = null,
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                    } else {
                                        AsyncImage(
                                            model = image.uri,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .width(64.dp)
                                                .height(64.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            "${if (image.mediaType == "video") "Vidéo" else "Image"} #${image.id}",
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Button(onClick = { onDeleteImage(image.id) }) {
                                        Text("Supprimer")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        FloatingNavButtons(
            onBack = onBack,
            onGoToApp = onGoToApp,
            currentSection = EditorTopSection.GALLERY,
            onOpenEditorHome = onOpenEditorHome,
            onOpenContacts = onOpenContacts,
            onOpenGroups = onOpenGroups,
            onOpenDiscussions = onOpenDiscussions,
            onOpenAppearance = onOpenAppearance,
            onOpenGallery = onOpenGallery,
            onOpenNotifications = onOpenNotifications
        )
    }
}

@Composable
fun AppearanceEditorScreen(
    isDarkTheme: Boolean,
    statusBarSettings: FakeStatusBarSettings,
    deviceSettings: DeviceShellSettings,
    exportSettings: ExportSettings,
    onBack: () -> Unit,
    onGoToApp: () -> Unit,
    onOpenEditorHome: () -> Unit,
    onOpenContacts: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenDiscussions: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenNotifications: () -> Unit,
    onResetAppearance: () -> Unit,
    onSetDarkTheme: (Boolean) -> Unit,
    onSetStatusBarTime: (String) -> Unit,
    onSetStatusBarOperator: (String) -> Unit,
    onSetStatusBarNetwork: (String) -> Unit,
    onSetStatusBarBattery: (Int) -> Unit,
    onSetLockWallpaper: (String) -> Unit,
    onClearLockWallpaper: () -> Unit,
    onSetHomeWallpaper: (String) -> Unit,
    onClearHomeWallpaper: () -> Unit,
    onSetLockTime: (String) -> Unit,
    onSetLockDate: (String) -> Unit,
    onSetShowLockTime: (Boolean) -> Unit,
    onSetShowLockDate: (Boolean) -> Unit,
    onSetExportEnabled: (Boolean) -> Unit,
    onSetExportFormat: (String) -> Unit
) {
    val context = LocalContext.current
    val lockWallpaperPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            onSetLockWallpaper(uri.toString())
        }
    }
    val homeWallpaperPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            onSetHomeWallpaper(uri.toString())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 88.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionCard(
                    title = "Reinitialiser apparence",
                    subtitle = "Remet le mode de base: noir, heure/date visibles."
                ) {
                    Button(onClick = onResetAppearance) {
                        Text("Reinitialiser la section")
                    }
                }
            }
            item {
                SectionCard(
                    title = "Apparence",
                    subtitle = "Reglage hors jeu du rendu visible par le comedien."
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = !isDarkTheme,
                            onClick = { onSetDarkTheme(false) },
                            label = { Text("Clair") }
                        )
                        FilterChip(
                            selected = isDarkTheme,
                            onClick = { onSetDarkTheme(true) },
                            label = { Text("Sombre") }
                        )
                    }
                }
            }
            item {
                SectionCard(
                    title = "Barre de statut",
                    subtitle = "Configure l'heure, l'operateur, le reseau et la batterie."
                ) {
                    EditorTextField(
                        value = statusBarSettings.time,
                        onValueChange = onSetStatusBarTime,
                        label = "Heure"
                    )
                    EditorTextField(
                        value = statusBarSettings.operator,
                        onValueChange = onSetStatusBarOperator,
                        label = "Operateur"
                    )
                    EditorTextField(
                        value = statusBarSettings.network,
                        onValueChange = onSetStatusBarNetwork,
                        label = "Reseau"
                    )
                    Text("Batterie ${statusBarSettings.batteryLevel}%")
                    Slider(
                        value = statusBarSettings.batteryLevel.toFloat(),
                        onValueChange = { onSetStatusBarBattery(it.roundToInt()) },
                        valueRange = 0f..100f
                    )
                    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFEBEEF3)) {
                        FakeStatusBar(
                            isDarkTheme = isDarkTheme,
                            settings = statusBarSettings,
                            applySystemInsets = false
                        )
                    }
                }
            }
            item {
                SectionCard(
                    title = "Ecran de verrouillage",
                    subtitle = "Fond d'ecran + heure/date affichables."
                ) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { lockWallpaperPicker.launch(arrayOf("image/*")) }) {
                            Text("Fond verrouillage")
                        }
                        Button(onClick = onClearLockWallpaper) {
                            Text("Effacer")
                        }
                    }
                    EditorTextField(
                        value = deviceSettings.lockTime,
                        onValueChange = onSetLockTime,
                        label = "Heure verrouillage"
                    )
                    EditorTextField(
                        value = deviceSettings.lockDate,
                        onValueChange = onSetLockDate,
                        label = "Date verrouillage (jour + mois)"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Afficher l'heure")
                        Switch(
                            checked = deviceSettings.showLockTime,
                            onCheckedChange = onSetShowLockTime
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Afficher la date")
                        Switch(
                            checked = deviceSettings.showLockDate,
                            onCheckedChange = onSetShowLockDate
                        )
                    }
                }
            }
            item {
                SectionCard(
                    title = "Accueil smartphone",
                    subtitle = "Fond d'ecran de la page avec les icones."
                ) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { homeWallpaperPicker.launch(arrayOf("image/*")) }) {
                            Text("Fond accueil")
                        }
                        Button(onClick = onClearHomeWallpaper) {
                            Text("Effacer")
                        }
                    }
                }
            }
            item {
                SectionCard(
                    title = "Export discussion",
                    subtitle = "Reglage hors jeu des exports PDF/SVG."
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Activer export")
                        Switch(
                            checked = exportSettings.enabled,
                            onCheckedChange = onSetExportEnabled
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = exportSettings.format == "pdf",
                            onClick = { onSetExportFormat("pdf") },
                            label = { Text("PDF") }
                        )
                        FilterChip(
                            selected = exportSettings.format == "svg",
                            onClick = { onSetExportFormat("svg") },
                            label = { Text("SVG") }
                        )
                    }
                }
            }
        }
        FloatingNavButtons(
            onBack = onBack,
            onGoToApp = onGoToApp,
            currentSection = EditorTopSection.APPEARANCE,
            onOpenEditorHome = onOpenEditorHome,
            onOpenContacts = onOpenContacts,
            onOpenGroups = onOpenGroups,
            onOpenDiscussions = onOpenDiscussions,
            onOpenAppearance = onOpenAppearance,
            onOpenGallery = onOpenGallery,
            onOpenNotifications = onOpenNotifications
        )
    }
}

@Composable
fun ContactsEditorScreen(
    contacts: List<Contact>,
    onBack: () -> Unit,
    onGoToApp: () -> Unit,
    onOpenEditorHome: () -> Unit,
    onOpenContacts: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenDiscussions: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenNotifications: () -> Unit,
    onResetContacts: () -> Unit,
    onCreateContact: (String, String, String, String, String, String) -> Unit,
    onUpdateContact: (Long, String, String, String, String, String, String) -> Unit,
    onDeleteContact: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 88.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionCard(
                    title = "Contacts",
                    subtitle = "Efface seulement les contacts et participants."
                ) {
                    Button(onClick = onResetContacts) {
                        Text("Reinitialiser la section")
                    }
                }
            }
            item {
                ContactSection(
                    contacts = contacts,
                    onCreateContact = onCreateContact,
                    onUpdateContact = onUpdateContact,
                    onDeleteContact = onDeleteContact
                )
            }
        }
        FloatingNavButtons(
            onBack = onBack,
            onGoToApp = onGoToApp,
            currentSection = EditorTopSection.CONTACTS,
            onOpenEditorHome = onOpenEditorHome,
            onOpenContacts = onOpenContacts,
            onOpenGroups = onOpenGroups,
            onOpenDiscussions = onOpenDiscussions,
            onOpenAppearance = onOpenAppearance,
            onOpenGallery = onOpenGallery,
            onOpenNotifications = onOpenNotifications
        )
    }
}

@Composable
fun GroupsEditorScreen(
    groups: List<Conversation>,
    onBack: () -> Unit,
    onGoToApp: () -> Unit,
    onOpenEditorHome: () -> Unit,
    onOpenContacts: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenDiscussions: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenNotifications: () -> Unit,
    onResetGroups: () -> Unit,
    onOpenGroup: (Long) -> Unit,
    onCreateGroup: (String) -> Unit,
    onDeleteGroup: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 88.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionCard(
                    title = "Groupes",
                    subtitle = "Efface seulement les groupes et leurs messages."
                ) {
                    Button(onClick = onResetGroups) {
                        Text("Reinitialiser la section")
                    }
                }
            }
            item {
                GroupListSection(
                    groups = groups,
                    onOpenGroup = onOpenGroup,
                    onCreateGroup = onCreateGroup,
                    onDeleteGroup = onDeleteGroup
                )
            }
        }
        FloatingNavButtons(
            onBack = onBack,
            onGoToApp = onGoToApp,
            currentSection = EditorTopSection.GROUPS,
            onOpenEditorHome = onOpenEditorHome,
            onOpenContacts = onOpenContacts,
            onOpenGroups = onOpenGroups,
            onOpenDiscussions = onOpenDiscussions,
            onOpenAppearance = onOpenAppearance,
            onOpenGallery = onOpenGallery,
            onOpenNotifications = onOpenNotifications
        )
    }
}

@Composable
fun DiscussionsEditorScreen(
    discussions: List<Conversation>,
    onBack: () -> Unit,
    onGoToApp: () -> Unit,
    onOpenEditorHome: () -> Unit,
    onOpenContacts: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenDiscussions: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenNotifications: () -> Unit,
    onResetDiscussions: () -> Unit,
    onOpenDiscussion: (Long) -> Unit,
    onCreateDiscussion: (String) -> Unit,
    onDeleteDiscussion: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 88.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionCard(
                    title = "Discussions privees",
                    subtitle = "Efface seulement les discussions privees et leurs messages."
                ) {
                    Button(onClick = onResetDiscussions) {
                        Text("Reinitialiser la section")
                    }
                }
            }
            item {
                DiscussionListSection(
                    discussions = discussions,
                    onOpenDiscussion = onOpenDiscussion,
                    onCreateDiscussion = onCreateDiscussion,
                    onDeleteDiscussion = onDeleteDiscussion
                )
            }
        }
        FloatingNavButtons(
            onBack = onBack,
            onGoToApp = onGoToApp,
            currentSection = EditorTopSection.DISCUSSIONS,
            onOpenEditorHome = onOpenEditorHome,
            onOpenContacts = onOpenContacts,
            onOpenGroups = onOpenGroups,
            onOpenDiscussions = onOpenDiscussions,
            onOpenAppearance = onOpenAppearance,
            onOpenGallery = onOpenGallery,
            onOpenNotifications = onOpenNotifications
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun NotificationsEditorScreen(
    notifications: List<NotificationConfig>,
    contacts: List<Contact>,
    conversations: List<Conversation>,
    onBack: () -> Unit,
    onGoToApp: () -> Unit,
    onOpenEditorHome: () -> Unit,
    onOpenContacts: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenDiscussions: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenNotifications: () -> Unit,
    onResetNotifications: () -> Unit,
    onSendNotificationNow: (Long) -> Boolean,
    onCreateNotification: (String, Long, Long, String, String, Int, Boolean, Int, String) -> Long?,
    onUpdateNotification: (Long, String, Long, Long, String, String, Int, Boolean, Int, String) -> Boolean,
    onDeleteNotification: (Long) -> Unit
) {
    var label by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }
    var selectedConversationId by remember { mutableStateOf(0L) }
    var selectedSenderId by remember { mutableStateOf(0L) }
    var previewMode by remember { mutableStateOf("full") }
    var previewLength by remember { mutableStateOf(42f) }
    var useProfilePhoto by remember { mutableStateOf(true) }
    var durationSeconds by remember { mutableStateOf(4f) }
    var triggerKey by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var editNotificationId by remember { mutableStateOf<Long?>(null) }
    var editLabel by remember { mutableStateOf("") }
    var editMessageText by remember { mutableStateOf("") }
    var editSelectedConversationId by remember { mutableStateOf(0L) }
    var editSelectedSenderId by remember { mutableStateOf(0L) }
    var editPreviewMode by remember { mutableStateOf("full") }
    var editPreviewLength by remember { mutableStateOf(42f) }
    var editUseProfilePhoto by remember { mutableStateOf(true) }
    var editDurationSeconds by remember { mutableStateOf(4f) }
    var editTriggerKey by remember { mutableStateOf("") }
    var editFeedback by remember { mutableStateOf("") }
    var visiblePreview by remember { mutableStateOf<NotificationVisualPreview?>(null) }
    val privateConversations = remember(conversations) { conversations.filter { it.type != "group" } }
    val groupConversations = remember(conversations) { conversations.filter { it.type == "group" } }

    LaunchedEffect(conversations) {
        if (selectedConversationId <= 0L && conversations.isNotEmpty()) {
            selectedConversationId = conversations.first().id
        }
    }
    LaunchedEffect(contacts) {
        if (selectedSenderId <= 0L && contacts.isNotEmpty()) {
            selectedSenderId = contacts.first().id
        }
    }

    val resetDraft: () -> Unit = {
        label = ""
        messageText = ""
        previewMode = "full"
        previewLength = 42f
        useProfilePhoto = true
        durationSeconds = 4f
        triggerKey = ""
        selectedConversationId = conversations.firstOrNull()?.id ?: 0L
        selectedSenderId = contacts.firstOrNull()?.id ?: 0L
    }
    val closeEditDialog: () -> Unit = {
        editNotificationId = null
        editFeedback = ""
    }
    val openEditDialog: (NotificationConfig) -> Unit = { notif ->
        editNotificationId = notif.id
        editLabel = notif.label
        editMessageText = notif.messageText
        editSelectedConversationId = notif.conversationId
        editSelectedSenderId = notif.senderContactId
        editPreviewMode = notif.previewMode
        editPreviewLength = notif.previewLength.toFloat()
        editUseProfilePhoto = notif.useProfilePhoto
        editDurationSeconds = notif.durationSeconds.toFloat()
        editTriggerKey = notif.triggerKey
        editFeedback = ""
    }

    val editSelectedConversation = conversations.firstOrNull { it.id == editSelectedConversationId }
    val editSelectedSender = contacts.firstOrNull { it.id == editSelectedSenderId }

    LaunchedEffect(visiblePreview?.stamp) {
        val currentPreview = visiblePreview ?: return@LaunchedEffect
        delay(currentPreview.durationSeconds.coerceIn(1, 30) * 1000L)
        if (visiblePreview?.stamp == currentPreview.stamp) {
            visiblePreview = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 88.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionCard(
                    title = "Notifications",
                    subtitle = "Configure l'expediteur, le texte, l'ouverture et la touche clavier."
                ) {
                    Button(onClick = onResetNotifications) {
                        Text("Reinitialiser la section")
                    }
                    NotificationEditorFields(
                        label = label,
                        onLabelChange = { label = it },
                        messageText = messageText,
                        onMessageTextChange = { messageText = it },
                        triggerKey = triggerKey,
                        onTriggerKeyChange = { triggerKey = it.take(1).lowercase() },
                        privateConversations = privateConversations,
                        groupConversations = groupConversations,
                        selectedConversationId = selectedConversationId,
                        onSelectedConversationIdChange = { selectedConversationId = it },
                        contacts = contacts,
                        selectedSenderId = selectedSenderId,
                        onSelectedSenderIdChange = { selectedSenderId = it },
                        previewMode = previewMode,
                        onPreviewModeChange = { previewMode = it },
                        previewLength = previewLength,
                        onPreviewLengthChange = { previewLength = it },
                        useProfilePhoto = useProfilePhoto,
                        onUseProfilePhotoChange = { useProfilePhoto = it },
                        durationSeconds = durationSeconds,
                        onDurationSecondsChange = { durationSeconds = it }
                    )

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val createdId = onCreateNotification(
                                    label,
                                    selectedConversationId,
                                    selectedSenderId,
                                    messageText,
                                    previewMode,
                                    previewLength.toInt(),
                                    useProfilePhoto,
                                    durationSeconds.toInt(),
                                    triggerKey
                                )
                                if (createdId != null) {
                                    feedback = "Notification creee."
                                    resetDraft()
                                } else {
                                    feedback = "Impossible de creer: verifie discussion, expediteur, message et touche unique."
                                }
                            }
                        ) {
                            Text("Creer")
                        }
                        Button(onClick = {
                            resetDraft()
                            feedback = ""
                        }) {
                            Text("Effacer")
                        }
                    }
                    if (feedback.isNotBlank()) {
                        Text(feedback, color = Color(0xFF5E6B78), fontSize = 12.sp)
                    }
                }
            }
            item {
                SectionCard(
                    title = "Previsualisations",
                    subtitle = "Chaque notification peut etre modifiee ou supprimee directement."
                ) {
                    if (notifications.isEmpty()) {
                        Text("Aucune notification creee.", color = Color(0xFF667781))
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            notifications.forEach { notif ->
                                val notifConversation = conversations.firstOrNull { it.id == notif.conversationId }
                                val notifSender = contacts.firstOrNull { it.id == notif.senderContactId }
                                NotificationPreviewCard(
                                    sender = notifSender,
                                    conversation = notifConversation,
                                    previewText = notificationPreviewText(
                                        messageText = notif.messageText,
                                        previewMode = notif.previewMode,
                                        previewLength = notif.previewLength
                                    ),
                                    useProfilePhoto = notif.useProfilePhoto,
                                    triggerKey = notif.triggerKey,
                                    durationSeconds = notif.durationSeconds,
                                    modifierButtons = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Button(
                                                onClick = {
                                                    visiblePreview = NotificationVisualPreview(
                                                        sender = notifSender,
                                                        conversation = notifConversation,
                                                        previewText = notificationPreviewText(
                                                            messageText = notif.messageText,
                                                            previewMode = notif.previewMode,
                                                            previewLength = notif.previewLength
                                                        ),
                                                        useProfilePhoto = notif.useProfilePhoto,
                                                        triggerKey = notif.triggerKey,
                                                        durationSeconds = notif.durationSeconds
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAF2FF), contentColor = Color(0xFF12324A))
                                            ) {
                                                Text("Afficher la notification")
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(onClick = { openEditDialog(notif) }) {
                                                Text("Modifier")
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = {
                                                    onDeleteNotification(notif.id)
                                                    feedback = "Notification supprimee."
                                                    if (editNotificationId == notif.id) closeEditDialog()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEDEFF2), contentColor = Color(0xFF23313F))
                                            ) {
                                                Text("Supprimer")
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        visiblePreview?.let { preview ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 92.dp, start = 16.dp, end = 16.dp)
                    .zIndex(3f)
            ) {
                NotificationToastPreview(preview = preview)
            }
        }
        if (editNotificationId != null) {
            Dialog(onDismissRequest = closeEditDialog) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .heightIn(max = 680.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Modifier la notification",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = Color(0xFF102338)
                        )
                        NotificationEditorFields(
                            label = editLabel,
                            onLabelChange = { editLabel = it },
                            messageText = editMessageText,
                            onMessageTextChange = { editMessageText = it },
                            triggerKey = editTriggerKey,
                            onTriggerKeyChange = { editTriggerKey = it.take(1).lowercase() },
                            privateConversations = privateConversations,
                            groupConversations = groupConversations,
                            selectedConversationId = editSelectedConversationId,
                            onSelectedConversationIdChange = { editSelectedConversationId = it },
                            contacts = contacts,
                            selectedSenderId = editSelectedSenderId,
                            onSelectedSenderIdChange = { editSelectedSenderId = it },
                            previewMode = editPreviewMode,
                            onPreviewModeChange = { editPreviewMode = it },
                            previewLength = editPreviewLength,
                            onPreviewLengthChange = { editPreviewLength = it },
                            useProfilePhoto = editUseProfilePhoto,
                            onUseProfilePhotoChange = { editUseProfilePhoto = it },
                            durationSeconds = editDurationSeconds,
                            onDurationSecondsChange = { editDurationSeconds = it }
                        )
                        NotificationPreviewCard(
                            sender = editSelectedSender,
                            conversation = editSelectedConversation,
                            previewText = notificationPreviewText(
                                messageText = editMessageText,
                                previewMode = editPreviewMode,
                                previewLength = editPreviewLength.toInt()
                            ),
                            useProfilePhoto = editUseProfilePhoto,
                            triggerKey = editTriggerKey,
                            durationSeconds = editDurationSeconds.toInt(),
                            modifierButtons = null
                        )
                        if (editFeedback.isNotBlank()) {
                            Text(editFeedback, color = Color(0xFF5E6B78), fontSize = 12.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = closeEditDialog,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEDEFF2), contentColor = Color(0xFF23313F))
                            ) {
                                Text("Fermer")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val updated = onUpdateNotification(
                                        editNotificationId ?: 0L,
                                        editLabel,
                                        editSelectedConversationId,
                                        editSelectedSenderId,
                                        editMessageText,
                                        editPreviewMode,
                                        editPreviewLength.toInt(),
                                        editUseProfilePhoto,
                                        editDurationSeconds.toInt(),
                                        editTriggerKey
                                    )
                                    if (updated) {
                                        feedback = "Notification modifiee."
                                        closeEditDialog()
                                    } else {
                                        editFeedback = "Impossible de modifier: touche deja utilisee ou champs invalides."
                                    }
                                }
                            ) {
                                Text("Valider les modifications")
                            }
                        }
                    }
                }
            }
        }
        FloatingNavButtons(
            onBack = onBack,
            onGoToApp = onGoToApp,
            currentSection = EditorTopSection.NOTIFICATIONS,
            onOpenEditorHome = onOpenEditorHome,
            onOpenContacts = onOpenContacts,
            onOpenGroups = onOpenGroups,
            onOpenDiscussions = onOpenDiscussions,
            onOpenAppearance = onOpenAppearance,
            onOpenGallery = onOpenGallery,
            onOpenNotifications = onOpenNotifications
        )
    }
}

private fun notificationPreviewText(
    messageText: String,
    previewMode: String,
    previewLength: Int
): String {
    if (previewMode == "full") return messageText
    val max = previewLength.coerceIn(8, 200)
    return if (messageText.length <= max) messageText else messageText.take(max).trimEnd() + "..."
}

@Composable
private fun NotificationEditorFields(
    label: String,
    onLabelChange: (String) -> Unit,
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    triggerKey: String,
    onTriggerKeyChange: (String) -> Unit,
    privateConversations: List<Conversation>,
    groupConversations: List<Conversation>,
    selectedConversationId: Long,
    onSelectedConversationIdChange: (Long) -> Unit,
    contacts: List<Contact>,
    selectedSenderId: Long,
    onSelectedSenderIdChange: (Long) -> Unit,
    previewMode: String,
    onPreviewModeChange: (String) -> Unit,
    previewLength: Float,
    onPreviewLengthChange: (Float) -> Unit,
    useProfilePhoto: Boolean,
    onUseProfilePhotoChange: (Boolean) -> Unit,
    durationSeconds: Float,
    onDurationSecondsChange: (Float) -> Unit
) {
    EditorTextField(
        value = label,
        onValueChange = onLabelChange,
        label = "Nom (optionnel)"
    )
    EditorTextField(
        value = messageText,
        onValueChange = onMessageTextChange,
        label = "Message notification"
    )
    EditorTextField(
        value = triggerKey,
        onValueChange = onTriggerKeyChange,
        label = "Touche clavier (1 caractere)"
    )

    Text("Discussion ouverte au clic")
    Text("Discussions privées", color = Color(0xFF667781), fontSize = 12.sp)
    if (privateConversations.isEmpty()) {
        Text("Aucune discussion privée.", color = Color(0xFF667781))
    } else {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            privateConversations.forEach { conversation ->
                FilterChip(
                    selected = selectedConversationId == conversation.id,
                    onClick = { onSelectedConversationIdChange(conversation.id) },
                    label = { Text(conversation.title.ifBlank { "Privée ${conversation.id}" }) }
                )
            }
        }
    }
    Text("Groupes", color = Color(0xFF667781), fontSize = 12.sp)
    if (groupConversations.isEmpty()) {
        Text("Aucun groupe.", color = Color(0xFF667781))
    } else {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            groupConversations.forEach { conversation ->
                FilterChip(
                    selected = selectedConversationId == conversation.id,
                    onClick = { onSelectedConversationIdChange(conversation.id) },
                    label = { Text(conversation.title.ifBlank { "Groupe ${conversation.id}" }) }
                )
            }
        }
    }

    Text("Expediteur")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        contacts.forEach { contact ->
            FilterChip(
                selected = selectedSenderId == contact.id,
                onClick = { onSelectedSenderIdChange(contact.id) },
                label = { Text(contact.name) }
            )
        }
    }

    Text("Affichage du texte")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = previewMode == "full",
            onClick = { onPreviewModeChange("full") },
            label = { Text("Message entier") }
        )
        FilterChip(
            selected = previewMode == "partial",
            onClick = { onPreviewModeChange("partial") },
            label = { Text("Partiel + ...") }
        )
    }
    if (previewMode == "partial") {
        Text("Longueur bulle ${previewLength.toInt()} caractères")
        Slider(
            value = previewLength,
            onValueChange = onPreviewLengthChange,
            valueRange = 8f..200f
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Photo profil")
        Switch(
            checked = useProfilePhoto,
            onCheckedChange = onUseProfilePhotoChange
        )
    }

    Text("Durée affichage ${durationSeconds.toInt()}s")
    Slider(
        value = durationSeconds,
        onValueChange = onDurationSecondsChange,
        valueRange = 1f..30f
    )
}

@Composable
private fun NotificationPreviewCard(
    sender: Contact?,
    conversation: Conversation?,
    previewText: String,
    useProfilePhoto: Boolean,
    triggerKey: String,
    durationSeconds: Int,
    modifierButtons: (@Composable () -> Unit)?
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            color = Color(0xFF1B2430),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (useProfilePhoto && sender?.avatarUri?.isNotBlank() == true) {
                    AvatarCircle(
                        label = sender.avatarText.ifBlank { sender.name.take(1).uppercase() },
                        imageUri = sender.avatarUri,
                        size = 32.dp
                    )
                } else {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0x332D8CFF),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF2D8CFF))
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = sender?.name ?: "Expediteur",
                        color = Color(0xFFE8F0FF),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = previewText.ifBlank { "Message de notification" },
                        color = Color(0xFFD5DFEE),
                        fontSize = 13.sp,
                        maxLines = 3
                    )
                    Text(
                        text = "Touche ${triggerKey.ifBlank { "?" }.uppercase()} · ${durationSeconds}s · ${(if (conversation?.type == "group") "Groupe" else "Privée")} · ${conversation?.title ?: "Discussion"}",
                        color = Color(0xFF9CB2CC),
                        fontSize = 11.sp
                    )
                }
            }
        }
        modifierButtons?.invoke()
    }
}

@Composable
private fun NotificationToastPreview(
    preview: NotificationVisualPreview
) {
    Surface(
        color = Color(0xFF1B2430),
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (preview.useProfilePhoto && preview.sender?.avatarUri?.isNotBlank() == true) {
                AvatarCircle(
                    label = preview.sender.avatarText.ifBlank { preview.sender.name.take(1).uppercase() },
                    imageUri = preview.sender.avatarUri,
                    size = 38.dp
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x332D8CFF),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF2D8CFF))
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = preview.sender?.name ?: "Expediteur",
                    color = Color(0xFFE8F0FF),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = preview.previewText.ifBlank { "Message de notification" },
                    color = Color(0xFFD5DFEE),
                    fontSize = 13.sp,
                    maxLines = 2
                )
                Text(
                    text = "${preview.conversation?.title ?: "Discussion"} · ${preview.durationSeconds}s · ${preview.triggerKey.ifBlank { "?" }.uppercase()}",
                    color = Color(0xFF9CB2CC),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ConversationEditorScreen(
    title: String,
    contacts: List<Contact>,
    galleryVideos: List<GalleryImage>,
    items: List<Conversation>,
    defaultIsGroup: Boolean,
    showCreateSection: Boolean = true,
    onBack: () -> Unit,
    onGoToApp: () -> Unit,
    onOpenEditorHome: () -> Unit,
    onOpenContacts: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenDiscussions: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenNotifications: () -> Unit,
    participantIdsForConversation: (Long) -> kotlinx.coroutines.flow.StateFlow<List<Long>>,
    onCreateConversation: (String, String, Boolean, List<Long>, String, Boolean, String, String) -> Unit,
    onUpdateConversation: (Long, String, String, Boolean, List<Long>, String, Boolean, String, String) -> Unit,
    onDeleteConversation: (Long) -> Unit,
    messagesForConversation: (Long) -> kotlinx.coroutines.flow.StateFlow<List<Message>>,
    onAddMessage: (Long, String, String, Boolean, String, Boolean, String) -> Unit,
    onUpdateMessage: (Long, String, String, Boolean, String, Boolean, String) -> Unit,
    onDeleteMessage: (Long) -> Unit,
    onAddDaySeparator: (Long, String, Long?) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 88.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ConversationSection(
                    sectionTitle = title,
                    contacts = contacts,
                    galleryVideos = galleryVideos,
                    items = items,
                    defaultIsGroup = defaultIsGroup,
                    showCreateSection = showCreateSection,
                    participantIdsForConversation = participantIdsForConversation,
                    onCreateConversation = onCreateConversation,
                    onUpdateConversation = onUpdateConversation,
                    onDeleteConversation = onDeleteConversation,
                    messagesForConversation = messagesForConversation,
                    onAddMessage = onAddMessage,
                    onUpdateMessage = onUpdateMessage,
                    onDeleteMessage = onDeleteMessage,
                    onAddDaySeparator = onAddDaySeparator
                )
            }
        }
        FloatingNavButtons(
            onBack = onBack,
            onGoToApp = onGoToApp,
            currentSection = if (defaultIsGroup) EditorTopSection.GROUPS else EditorTopSection.DISCUSSIONS,
            onOpenEditorHome = onOpenEditorHome,
            onOpenContacts = onOpenContacts,
            onOpenGroups = onOpenGroups,
            onOpenDiscussions = onOpenDiscussions,
            onOpenAppearance = onOpenAppearance,
            onOpenGallery = onOpenGallery,
            onOpenNotifications = onOpenNotifications
        )
    }
}

@Composable
private fun EditorMenuCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(color = Color(0xFFEAF2FF), shape = RoundedCornerShape(16.dp)) {
                Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color(0xFF1F7AE0))
                }
            }
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF152C44))
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.FloatingNavButtons(
    onBack: () -> Unit,
    onGoToApp: () -> Unit,
    currentSection: EditorTopSection,
    onOpenEditorHome: () -> Unit,
    onOpenContacts: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenDiscussions: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    var menuOpen by rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = onBack,
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(start = 8.dp, top = 42.dp)
            .size(30.dp)
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Retour",
            tint = Color(0xFF1F7AE0)
        )
    }

    val openApp: () -> Unit = {
        menuOpen = false
        onGoToApp()
    }
    val openEditorHome: () -> Unit = {
        menuOpen = false
        onOpenEditorHome()
    }
    val openContacts: () -> Unit = {
        menuOpen = false
        onOpenContacts()
    }
    val openGroups: () -> Unit = {
        menuOpen = false
        onOpenGroups()
    }
    val openDiscussions: () -> Unit = {
        menuOpen = false
        onOpenDiscussions()
    }
    val openAppearance: () -> Unit = {
        menuOpen = false
        onOpenAppearance()
    }
    val openGallery: () -> Unit = {
        menuOpen = false
        onOpenGallery()
    }
    val openNotifications: () -> Unit = {
        menuOpen = false
        onOpenNotifications()
    }

    Row(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 108.dp, end = 0.dp)
            .zIndex(50f),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (menuOpen) {
            Surface(
                color = Color(0xFFF7FAFF),
                shape = RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp),
                modifier = Modifier
                    .width(64.dp)
                    .heightIn(max = 600.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 14.dp, horizontal = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    TopNavIconButton(
                        icon = Icons.Default.Home,
                        contentDescription = "Accueil edition",
                        selected = currentSection == EditorTopSection.HUB,
                        onClick = openEditorHome
                    )
                    TopNavIconButton(
                        icon = Icons.Default.Forum,
                        contentDescription = "Aller aux discussions",
                        selected = false,
                        onClick = openApp
                    )
                    TopNavIconButton(
                        icon = Icons.Default.Person,
                        contentDescription = "Contacts",
                        selected = currentSection == EditorTopSection.CONTACTS,
                        onClick = openContacts
                    )
                    TopNavIconButton(
                        icon = Icons.Default.Groups,
                        contentDescription = "Groupes",
                        selected = currentSection == EditorTopSection.GROUPS,
                        onClick = openGroups
                    )
                    TopNavIconButton(
                        icon = Icons.Default.Chat,
                        contentDescription = "Discussions",
                        selected = currentSection == EditorTopSection.DISCUSSIONS,
                        onClick = openDiscussions
                    )
                    TopNavIconButton(
                        icon = Icons.Default.Palette,
                        contentDescription = "Apparence",
                        selected = currentSection == EditorTopSection.APPEARANCE,
                        onClick = openAppearance
                    )
                    TopNavIconButton(
                        icon = Icons.Default.Image,
                        contentDescription = "Galerie",
                        selected = currentSection == EditorTopSection.GALLERY,
                        onClick = openGallery
                    )
                    TopNavIconButton(
                        icon = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        selected = currentSection == EditorTopSection.NOTIFICATIONS,
                        onClick = openNotifications
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        Surface(
            onClick = { menuOpen = !menuOpen },
            color = Color(0xFF1F7AE0),
            shape = RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp)
        ) {
            Icon(
                imageVector = if (menuOpen) Icons.Default.KeyboardArrowRight else Icons.Default.KeyboardArrowLeft,
                contentDescription = if (menuOpen) "Fermer menu" else "Ouvrir menu",
                tint = Color.White,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun TopNavIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = if (selected) Color(0xFF1F7AE0) else Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.size(46.dp)
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = if (selected) Color.White else Color(0xFF1F7AE0)
        )
    }
}

@Composable
private fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit
) {
    androidx.compose.material3.FilterChip(
        selected = selected,
        onClick = onClick,
        label = label,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF1F7AE0),
            selectedLabelColor = Color.White,
            containerColor = Color.White,
            labelColor = Color(0xFF17406C)
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = Color(0xFF9DB7D3),
            selectedBorderColor = Color(0xFF1F7AE0)
        )
    )
}

@Composable
private fun SectionCard(title: String, subtitle: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF152C44))
                if (subtitle.isNotBlank()) {
                    Text(subtitle, color = Color(0xFF3F556B))
                }
            }
            content()
        }
    }
}

@Composable
private fun ContactSection(
    contacts: List<Contact>,
    onCreateContact: (String, String, String, String, String, String) -> Unit,
    onUpdateContact: (Long, String, String, String, String, String, String) -> Unit,
    onDeleteContact: (Long) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var avatarType by remember { mutableStateOf("initials") }
    var avatarText by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf("") }
    val createPhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            avatarType = "photo"
            avatarUri = uri.toString()
        }
    }

    SectionCard(
        title = "Contacts",
        subtitle = "Initiales personnalisees ou photo de profil."
    ) {
        AvatarEditor(
            avatarType = avatarType,
            avatarText = avatarText,
            avatarUri = avatarUri,
            onAvatarTypeChange = { avatarType = it },
            onAvatarTextChange = { avatarText = it },
            onPickPhoto = { createPhotoPicker.launch(arrayOf("image/*")) }
        )
        EditorTextField(value = name, onValueChange = { name = it }, label = "Nom")
        EditorTextField(value = role, onValueChange = { role = it }, label = "Role")
        EditorTextField(value = status, onValueChange = { status = it }, label = "Statut")
        Button(
            onClick = {
                onCreateContact(name, role, status, avatarType, avatarText, avatarUri)
                name = ""
                role = ""
                status = ""
                avatarType = "initials"
                avatarText = ""
                avatarUri = ""
            },
            enabled = name.isNotBlank()
        ) {
            Text("Creer le contact")
        }

        contacts.forEach { contact ->
            val rowContext = LocalContext.current
            var editName by remember(contact.id) { mutableStateOf(contact.name) }
            var editRole by remember(contact.id) { mutableStateOf(contact.role) }
            var editStatus by remember(contact.id) { mutableStateOf(contact.status) }
            var editAvatarType by remember(contact.id) { mutableStateOf(contact.avatarType) }
            var editAvatarText by remember(contact.id) { mutableStateOf(contact.avatarText) }
            var editAvatarUri by remember(contact.id) { mutableStateOf(contact.avatarUri) }
            val editPhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                if (uri != null) {
                    runCatching {
                        rowContext.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    editAvatarType = "photo"
                    editAvatarUri = uri.toString()
                }
            }

            Surface(color = Color(0xFFF7F9FA), shape = RoundedCornerShape(14.dp)) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Contact #${contact.id}", fontWeight = FontWeight.SemiBold)
                    AvatarEditor(
                        avatarType = editAvatarType,
                        avatarText = editAvatarText,
                        avatarUri = editAvatarUri,
                        onAvatarTypeChange = { editAvatarType = it },
                        onAvatarTextChange = { editAvatarText = it },
                        onPickPhoto = { editPhotoPicker.launch(arrayOf("image/*")) }
                    )
                    EditorTextField(value = editName, onValueChange = { editName = it }, label = "Nom")
                    EditorTextField(value = editRole, onValueChange = { editRole = it }, label = "Role")
                    EditorTextField(value = editStatus, onValueChange = { editStatus = it }, label = "Statut")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                onUpdateContact(
                                    contact.id,
                                    editName,
                                    editRole,
                                    editStatus,
                                    editAvatarType,
                                    editAvatarText,
                                    editAvatarUri
                                )
                            },
                            enabled = editName.isNotBlank()
                        ) { Text("Modifier") }
                        Button(onClick = { onDeleteContact(contact.id) }) { Text("Supprimer") }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationSection(
    sectionTitle: String,
    contacts: List<Contact>,
    galleryVideos: List<GalleryImage>,
    items: List<Conversation>,
    defaultIsGroup: Boolean,
    showCreateSection: Boolean,
    participantIdsForConversation: (Long) -> kotlinx.coroutines.flow.StateFlow<List<Long>>,
    onCreateConversation: (String, String, Boolean, List<Long>, String, Boolean, String, String) -> Unit,
    onUpdateConversation: (Long, String, String, Boolean, List<Long>, String, Boolean, String, String) -> Unit,
    onDeleteConversation: (Long) -> Unit,
    messagesForConversation: (Long) -> kotlinx.coroutines.flow.StateFlow<List<Message>>,
    onAddMessage: (Long, String, String, Boolean, String, Boolean, String) -> Unit,
    onUpdateMessage: (Long, String, String, Boolean, String, Boolean, String) -> Unit,
    onDeleteMessage: (Long) -> Unit,
    onAddDaySeparator: (Long, String, Long?) -> Unit
) {
    val context = LocalContext.current
    var createTitle by remember(sectionTitle) { mutableStateOf("") }
    var createSubtitle by remember(sectionTitle) { mutableStateOf("") }
    var createParticipantIds by remember(sectionTitle) { mutableStateOf(setOf<Long>()) }
    var createAvatarUri by remember(sectionTitle) { mutableStateOf("") }
    var createShowMessageTimes by remember(sectionTitle) { mutableStateOf(true) }
    var createSequenceNumber by remember(sectionTitle) { mutableStateOf("") }
    var createVideoCallBackgroundUri by remember(sectionTitle) { mutableStateOf("") }
    val createPhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            createAvatarUri = uri.toString()
        }
    }

    SectionCard(
        title = sectionTitle,
        subtitle = ""
    ) {
        if (showCreateSection) {
            EditorTextField(
                value = createTitle,
                onValueChange = { createTitle = it },
                label = if (defaultIsGroup) "Nom du groupe" else "Titre de la discussion"
            )
            if (!defaultIsGroup) {
                EditorTextField(
                    value = createSubtitle,
                    onValueChange = { createSubtitle = it },
                    label = "Sous-titre visible"
                )
            }
            if (defaultIsGroup) {
                GroupAvatarEditor(
                    avatarUri = createAvatarUri,
                    onPickPhoto = { createPhotoPicker.launch(arrayOf("image/*")) },
                    onRemovePhoto = { createAvatarUri = "" }
                )
                ParticipantSelector(
                    contacts = contacts,
                    selectedIds = createParticipantIds,
                    label = "Participants du groupe"
                ) { contactId ->
                    createParticipantIds =
                        if (contactId in createParticipantIds) createParticipantIds - contactId else createParticipantIds + contactId
                }
                GroupTimesSwitch(
                    checked = createShowMessageTimes,
                    onCheckedChange = { createShowMessageTimes = it }
                )
                EditorTextField(
                    value = createSequenceNumber,
                    onValueChange = { createSequenceNumber = it },
                    label = "Numero de sequence"
                )
            } else {
                VideoCallBackgroundSelector(
                    videos = galleryVideos,
                    selectedUri = createVideoCallBackgroundUri,
                    onSelect = { createVideoCallBackgroundUri = it }
                )
            }
            Button(
                onClick = {
                    onCreateConversation(
                        createTitle,
                        if (defaultIsGroup) "" else createSubtitle,
                        defaultIsGroup,
                        createParticipantIds.toList(),
                        createAvatarUri,
                        createShowMessageTimes,
                        createSequenceNumber,
                        createVideoCallBackgroundUri
                    )
                    createTitle = ""
                    createSubtitle = ""
                    createParticipantIds = emptySet()
                    createAvatarUri = ""
                    createShowMessageTimes = true
                    createSequenceNumber = ""
                    createVideoCallBackgroundUri = ""
                },
                enabled = createTitle.isNotBlank() && (!defaultIsGroup || createParticipantIds.isNotEmpty())
            ) {
                Text(if (defaultIsGroup) "Creer le groupe" else "Creer la discussion")
            }
        }

        items.forEach { conversation ->
            val participantIds by participantIdsForConversation(conversation.id).collectAsState()
            val groupContacts = contacts.filter { it.id in participantIds }
            var title by remember(conversation.id) { mutableStateOf(conversation.title) }
            var avatarUri by remember(conversation.id) { mutableStateOf(conversation.avatarUri) }
            var videoCallBackgroundUri by remember(conversation.id) { mutableStateOf(conversation.videoCallBackgroundUri) }
            var showMessageTimes by remember(conversation.id) { mutableStateOf(conversation.showMessageTimes) }
            var sequenceNumber by remember(conversation.id) { mutableStateOf(conversation.sequenceNumber) }
            var selectedParticipantIds by remember(conversation.id, participantIds) {
                mutableStateOf(participantIds.toSet())
            }
            var selectedPrivateContactId by remember(conversation.id) { mutableStateOf<Long?>(null) }
            LaunchedEffect(conversation.id, conversation.title, conversation.subtitle, contacts) {
                val selectedContactStillExists = selectedPrivateContactId?.let { id ->
                    contacts.any { it.id == id }
                } ?: false
                if (!selectedContactStillExists) {
                    selectedPrivateContactId = contacts.firstOrNull { contact ->
                        contact.name.equals(conversation.title, ignoreCase = true) ||
                            contact.name.equals(conversation.subtitle, ignoreCase = true)
                    }?.id
                }
            }
            val selectedPrivateContact = contacts.firstOrNull { it.id == selectedPrivateContactId }
            val privateSenderName = selectedPrivateContact?.name?.ifBlank { title }
                ?: title.ifBlank { conversation.subtitle }
            var newSender by remember(conversation.id, groupContacts) {
                mutableStateOf(groupContacts.firstOrNull()?.name ?: "")
            }
            var newText by remember(conversation.id) { mutableStateOf("") }
            var newIsMine by remember(conversation.id) { mutableStateOf(false) }
            var newImageUri by remember(conversation.id) { mutableStateOf("") }
            var newSeen by remember(conversation.id) { mutableStateOf(true) }
            var newTimestamp by remember(conversation.id) { mutableStateOf(conversation.lastTime.ifBlank { "09:41" }) }
            var newDayLabel by remember(conversation.id) { mutableStateOf("") }
            var selectedMessageId by remember(conversation.id) { mutableStateOf<Long?>(null) }
            val messages by messagesForConversation(conversation.id).collectAsState()
            val selectedMessage = messages.firstOrNull { it.id == selectedMessageId }
            val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                if (uri != null) {
                    runCatching {
                        context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    newImageUri = uri.toString()
                }
            }
            val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                if (uri != null) {
                    runCatching {
                        context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    avatarUri = uri.toString()
                }
            }

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(color = Color(0xFFF7F9FA), shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Discussion #${conversation.id}", fontWeight = FontWeight.SemiBold)
                        if (defaultIsGroup) {
                            EditorTextField(value = title, onValueChange = { title = it }, label = "Titre")
                        } else {
                            PrivateDiscussionContactSelector(
                                contacts = contacts,
                                selectedId = selectedPrivateContactId
                            ) { contactId ->
                                selectedPrivateContactId = contactId
                                contacts.firstOrNull { it.id == contactId }?.let { contact ->
                                    title = contact.name
                                }
                            }
                            Text(
                                text = selectedPrivateContact?.status?.ifBlank { "Aucun statut" }
                                    ?: "Aucun statut",
                                color = Color(0xFF667781)
                            )
                        }
                        GroupTimesSwitch(
                            checked = showMessageTimes,
                            onCheckedChange = { showMessageTimes = it }
                        )
                        EditorTextField(
                            value = sequenceNumber,
                            onValueChange = { sequenceNumber = it },
                            label = "Numero de sequence"
                        )
                        if (defaultIsGroup) {
                            GroupAvatarEditor(
                                avatarUri = avatarUri,
                                onPickPhoto = { avatarPicker.launch(arrayOf("image/*")) },
                                onRemovePhoto = { avatarUri = "" }
                            )
                            Text(
                                text = groupContacts.joinToString(", ") { it.name }.ifBlank { "Aucun participant" },
                                color = Color(0xFF667781)
                            )
                            ParticipantSelector(
                                contacts = contacts,
                                selectedIds = selectedParticipantIds,
                                label = "Participants du groupe"
                            ) { contactId ->
                                selectedParticipantIds =
                                    if (contactId in selectedParticipantIds) selectedParticipantIds - contactId else selectedParticipantIds + contactId
                            }
                        } else {
                            VideoCallBackgroundSelector(
                                videos = galleryVideos,
                                selectedUri = videoCallBackgroundUri,
                                onSelect = { videoCallBackgroundUri = it }
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val selectedContact = contacts.firstOrNull { it.id == selectedPrivateContactId }
                                    onUpdateConversation(
                                        conversation.id,
                                        if (defaultIsGroup) title else selectedContact?.name.orEmpty(),
                                        if (defaultIsGroup) "" else selectedContact?.status.orEmpty(),
                                        defaultIsGroup,
                                        selectedParticipantIds.toList(),
                                        avatarUri,
                                        showMessageTimes,
                                        sequenceNumber,
                                        videoCallBackgroundUri
                                    )
                                },
                                enabled = if (defaultIsGroup) {
                                    title.isNotBlank() && selectedParticipantIds.isNotEmpty()
                                } else {
                                    selectedPrivateContact != null
                                }
                            ) {
                                Text("Modifier")
                            }
                            Button(onClick = { onDeleteConversation(conversation.id) }) {
                                Text("Supprimer")
                            }
                        }
                    }
                }

                Surface(color = Color.White, shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (defaultIsGroup) {
                                EditorTextField(
                                    value = newDayLabel,
                                    onValueChange = { newDayLabel = it },
                                    label = "Jour a ajouter sous la bulle"
                                )
                                Button(
                                    onClick = {
                                    onAddDaySeparator(conversation.id, newDayLabel, null)
                                    newDayLabel = ""
                                },
                                enabled = newDayLabel.isNotBlank()
                            ) {
                                Text("Ajouter un jour")
                            }
                        }
                        if (defaultIsGroup && !newIsMine) {
                            SenderSelector(
                                contacts = groupContacts,
                                selectedName = newSender
                            ) { newSender = it }
                        }
                        EditorTextField(
                            value = newText,
                            onValueChange = { newText = it },
                            label = "Texte de la bulle"
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = !newIsMine,
                                onClick = { newIsMine = false },
                                label = { Text("Personnage") }
                            )
                            FilterChip(
                                selected = newIsMine,
                                onClick = {
                                    newIsMine = true
                                    newSeen = true
                                },
                                label = { Text("Moi") }
                            )
                        }
                        if (newIsMine) {
                            ReadReceiptSwitch(
                                checked = newSeen,
                                onCheckedChange = { newSeen = it }
                            )
                        }
                        if (showMessageTimes) {
                            EditorTextField(
                                value = newTimestamp,
                                onValueChange = { newTimestamp = it },
                                label = "Heure de la bulle"
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { photoPicker.launch(arrayOf("image/*")) }) {
                                Text(if (newImageUri.isBlank()) "Ajouter une photo" else "Changer la photo")
                            }
                            if (newImageUri.isNotBlank()) {
                                Button(onClick = { newImageUri = "" }) {
                                    Text("Retirer la photo")
                                }
                            }
                        }
                        Button(
                            onClick = {
                                onAddMessage(
                                    conversation.id,
                                    if (newIsMine) "Moi" else if (defaultIsGroup) newSender else privateSenderName,
                                    newText,
                                    newIsMine,
                                    newImageUri,
                                    newSeen,
                                    newTimestamp
                                )
                                newText = ""
                                newImageUri = ""
                                newSeen = true
                                newTimestamp = conversation.lastTime.ifBlank { "09:41" }
                            },
                            enabled = (newText.isNotBlank() || newImageUri.isNotBlank()) && when {
                                newIsMine -> true
                                defaultIsGroup -> newSender.isNotBlank()
                                else -> true
                            }
                        ) {
                            Text("Ajouter la bulle")
                        }

                        messages.forEach { message ->
                            val draftMessage = if (!defaultIsGroup && !message.isMine && message.kind == "message") {
                                message.copy(senderName = privateSenderName)
                            } else {
                                message
                            }
                            val senderContact = if (defaultIsGroup && !draftMessage.isMine && draftMessage.kind == "message") {
                                groupContacts.firstOrNull { it.name.equals(draftMessage.senderName, ignoreCase = true) }
                            } else {
                                null
                            }
                            val showGroupIncomingAvatar =
                                defaultIsGroup && !draftMessage.isMine && draftMessage.kind == "message"
                            Surface(
                                onClick = { selectedMessageId = message.id },
                                color = Color(0xFFF7F9FA),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.padding(10.dp)) {
                                    if (showGroupIncomingAvatar) {
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            AvatarCircle(
                                                label = senderContact?.avatarText?.ifBlank {
                                                    draftMessage.senderName.take(1).uppercase().ifBlank { "?" }
                                                } ?: draftMessage.senderName.take(1).uppercase().ifBlank { "?" },
                                                imageUri = if (senderContact?.avatarType == "photo") {
                                                    senderContact.avatarUri
                                                } else {
                                                    ""
                                                },
                                                size = 34.dp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            ChatBubble(
                                                message = draftMessage,
                                                isDarkTheme = false,
                                                showTimestamp = showMessageTimes
                                            )
                                        }
                                    } else {
                                        Column(horizontalAlignment = if (draftMessage.isMine) Alignment.End else Alignment.Start) {
                                            ChatBubble(
                                                message = draftMessage,
                                                isDarkTheme = false,
                                                showTimestamp = showMessageTimes
                                            )
                                            if (draftMessage.isMine) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = if (draftMessage.seen) "lu" else "distribué",
                                                    color = if (draftMessage.seen) Color(0xFF1FAF57) else Color(0xFF667781),
                                                    fontSize = 11.sp
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

            if (selectedMessage != null) {
                var saveSignal by remember(selectedMessage.id) { mutableStateOf(0) }
                var isValidated by remember(selectedMessage.id) { mutableStateOf(false) }
                Dialog(onDismissRequest = { selectedMessageId = null }) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            EditableMessageCard(
                                message = selectedMessage,
                                availableContacts = if (defaultIsGroup) groupContacts else emptyList(),
                                defaultSender = if (defaultIsGroup) conversation.title else privateSenderName,
                                showSenderSelector = defaultIsGroup,
                                showConversationTimes = showMessageTimes,
                                showModifyButton = false,
                                externalSaveSignal = saveSignal,
                                onExternalSaveResult = { success ->
                                    isValidated = success
                                },
                                onUpdateMessage = onUpdateMessage,
                                onDeleteMessage = { messageId ->
                                    onDeleteMessage(messageId)
                                    selectedMessageId = null
                                },
                                onAddDaySeparator = { label, afterMessageId ->
                                    onAddDaySeparator(conversation.id, label, afterMessageId)
                                }
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = { saveSignal += 1 },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isValidated) Color(0xFF1F7AE0) else Color(0xFFC93939),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Valider")
                                }
                                Button(onClick = { selectedMessageId = null }) {
                                    Text("Fermer")
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
private fun DiscussionListSection(
    discussions: List<Conversation>,
    onOpenDiscussion: (Long) -> Unit,
    onCreateDiscussion: (String) -> Unit,
    onDeleteDiscussion: (Long) -> Unit
) {
    var createTitle by remember { mutableStateOf("") }

    SectionCard(
        title = "Discussions",
        subtitle = ""
    ) {
        EditorTextField(
            value = createTitle,
            onValueChange = { createTitle = it },
            label = "Titre de la discussion"
        )
        Button(
            onClick = {
                onCreateDiscussion(createTitle)
                createTitle = ""
            },
            enabled = createTitle.isNotBlank()
        ) {
            Text("Creer la discussion")
        }

        discussions.forEachIndexed { index, discussion ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color(0xFFF7F9FA),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenDiscussion(discussion.id) }
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        AvatarCircle(label = discussion.title.take(1).uppercase())
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            GroupInfoLine(
                                text = discussion.title.ifBlank { "Nom de la discussion" },
                                isPrimary = true,
                                topRounded = true
                            )
                            GroupInfoLine(
                                text = discussion.subtitle.ifBlank { "Statut/participants" },
                                isPrimary = false
                            )
                            GroupInfoLine(
                                text = discussion.lastMessage.ifBlank { "Aucun message" },
                                isPrimary = false,
                                bottomRounded = true
                            )
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { onOpenDiscussion(discussion.id) }) { Text("Ouvrir") }
                        Button(onClick = { onDeleteDiscussion(discussion.id) }) { Text("Supprimer") }
                    }
                }
                if (index < discussions.lastIndex) {
                    HorizontalDivider(
                        color = Color(0xFFCFD8DC),
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupListSection(
    groups: List<Conversation>,
    onOpenGroup: (Long) -> Unit,
    onCreateGroup: (String) -> Unit,
    onDeleteGroup: (Long) -> Unit
) {
    var createTitle by remember { mutableStateOf("") }

    SectionCard(
        title = "Groupes",
        subtitle = ""
    ) {
        EditorTextField(
            value = createTitle,
            onValueChange = { createTitle = it },
            label = "Nom du groupe"
        )
        Button(
            onClick = {
                onCreateGroup(createTitle)
                createTitle = ""
            },
            enabled = createTitle.isNotBlank()
        ) {
            Text("Creer le groupe")
        }

        groups.forEachIndexed { index, group ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color(0xFFF7F9FA),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenGroup(group.id) }
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        AvatarCircle(label = "👥", imageUri = group.avatarUri)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            GroupInfoLine(
                                text = group.title.ifBlank { "Nom du groupe" },
                                isPrimary = true,
                                topRounded = true
                            )
                            GroupInfoLine(
                                text = group.subtitle.ifBlank { "Nom des participants" },
                                isPrimary = false
                            )
                            GroupInfoLine(
                                text = group.sequenceNumber.ifBlank { "Numero de sequence" },
                                isPrimary = false,
                                bottomRounded = true
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { onOpenGroup(group.id) }) {
                            Text("Ouvrir")
                        }
                        Button(onClick = { onDeleteGroup(group.id) }) {
                            Text("Supprimer")
                        }
                    }
                }
                if (index < groups.lastIndex) {
                    HorizontalDivider(
                        color = Color(0xFFCFD8DC),
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupInfoLine(
    text: String,
    isPrimary: Boolean,
    topRounded: Boolean = false,
    bottomRounded: Boolean = false
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(
            topStart = if (topRounded) 12.dp else 0.dp,
            topEnd = if (topRounded) 12.dp else 0.dp,
            bottomStart = if (bottomRounded) 12.dp else 0.dp,
            bottomEnd = if (bottomRounded) 12.dp else 0.dp
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            fontWeight = if (isPrimary) FontWeight.SemiBold else FontWeight.Normal,
            color = Color(0xFF111B21)
        )
    }
}

@Composable
private fun EditableMessageCard(
    message: Message,
    availableContacts: List<Contact>,
    defaultSender: String,
    showSenderSelector: Boolean,
    showConversationTimes: Boolean,
    showModifyButton: Boolean = true,
    externalSaveSignal: Int = 0,
    onExternalSaveResult: ((Boolean) -> Unit)? = null,
    onUpdateMessage: (Long, String, String, Boolean, String, Boolean, String) -> Unit,
    onDeleteMessage: (Long) -> Unit,
    onAddDaySeparator: (String, Long?) -> Unit
) {
    val context = LocalContext.current
    var sender by remember(message.id, message.senderName, defaultSender) {
        mutableStateOf(
            if (message.isMine) {
                "Moi"
            } else if (showSenderSelector) {
                message.senderName.ifBlank { defaultSender }
            } else {
                defaultSender
            }
        )
    }
    var text by remember(message.id) { mutableStateOf(message.text) }
    var isMine by remember(message.id) { mutableStateOf(message.isMine) }
    var imageUri by remember(message.id) { mutableStateOf(message.imageUri) }
    var seen by remember(message.id) { mutableStateOf(message.seen) }
    var timestamp by remember(message.id) { mutableStateOf(message.timestamp) }
    var dayLabel by remember(message.id) { mutableStateOf("") }

    fun canSave(): Boolean {
        if (message.kind == "day") return text.isNotBlank()
        val senderIsValid = isMine || !showSenderSelector || sender.isNotBlank()
        return (text.isNotBlank() || imageUri.isNotBlank()) && senderIsValid
    }

    fun saveNow(): Boolean {
        if (!canSave()) return false
        onUpdateMessage(
            message.id,
            if (message.kind == "day") "" else if (isMine) "Moi" else if (showSenderSelector) sender else defaultSender,
            text,
            if (message.kind == "day") false else isMine,
            if (message.kind == "day") "" else imageUri,
            if (message.kind == "day") false else seen,
            if (message.kind == "day") "" else timestamp
        )
        return true
    }

    LaunchedEffect(externalSaveSignal) {
        if (externalSaveSignal <= 0) return@LaunchedEffect
        onExternalSaveResult?.invoke(saveNow())
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            imageUri = uri.toString()
        }
    }

    Surface(color = Color(0xFFF7F9FA), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(if (message.kind == "day") "Jour #${message.id}" else "Bulle #${message.id}", fontWeight = FontWeight.Medium)
            val previewMessage = message.copy(
                senderName = if (isMine) "Moi" else sender,
                text = text,
                imageUri = imageUri,
                isMine = isMine,
                seen = seen,
                timestamp = timestamp
            )
            val senderContact = if (showSenderSelector && !previewMessage.isMine && previewMessage.kind == "message") {
                availableContacts.firstOrNull { it.name.equals(previewMessage.senderName, ignoreCase = true) }
            } else {
                null
            }
            val showIncomingAvatar = showSenderSelector && !previewMessage.isMine && previewMessage.kind == "message"
            if (showIncomingAvatar) {
                Row(verticalAlignment = Alignment.Bottom) {
                    AvatarCircle(
                        label = senderContact?.avatarText?.ifBlank {
                            previewMessage.senderName.take(1).uppercase().ifBlank { "?" }
                        } ?: previewMessage.senderName.take(1).uppercase().ifBlank { "?" },
                        imageUri = if (senderContact?.avatarType == "photo") {
                            senderContact.avatarUri
                        } else {
                            ""
                        },
                        size = 34.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ChatBubble(
                        message = previewMessage,
                        isDarkTheme = false,
                        showTimestamp = showConversationTimes
                    )
                }
            } else {
                Column(horizontalAlignment = if (previewMessage.isMine) Alignment.End else Alignment.Start) {
                    ChatBubble(
                        message = previewMessage,
                        isDarkTheme = false,
                        showTimestamp = showConversationTimes
                    )
                    if (previewMessage.isMine) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (previewMessage.seen) "lu" else "distribué",
                            color = if (previewMessage.seen) Color(0xFF1FAF57) else Color(0xFF667781),
                            fontSize = 11.sp
                        )
                    }
                }
            }
            if (message.kind == "day") {
                EditorTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = "Jour"
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (showModifyButton) {
                        Button(
                            onClick = {
                                saveNow()
                            },
                            enabled = canSave()
                        ) {
                            Text("Modifier")
                        }
                    }
                    Button(onClick = { onDeleteMessage(message.id) }) {
                        Text("Supprimer")
                    }
                }
                return@Column
            }
            if (showSenderSelector && !isMine) {
                SenderSelector(
                    contacts = availableContacts,
                    selectedName = sender
                ) { sender = it }
            }
            EditorTextField(
                value = text,
                onValueChange = { text = it },
                label = "Texte"
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !isMine,
                    onClick = {
                        isMine = false
                        seen = false
                        if (sender.isBlank()) sender = availableContacts.firstOrNull()?.name ?: defaultSender
                    },
                    label = { Text("Personnage") }
                )
                FilterChip(
                    selected = isMine,
                    onClick = {
                        isMine = true
                        seen = true
                    },
                    label = { Text("Moi") }
                )
            }
            if (isMine) {
                ReadReceiptSwitch(
                    checked = seen,
                    onCheckedChange = { seen = it }
                )
            }
            if (showConversationTimes) {
                EditorTextField(
                    value = timestamp,
                    onValueChange = { timestamp = it },
                    label = "Heure"
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { photoPicker.launch(arrayOf("image/*")) }) {
                    Text(if (imageUri.isBlank()) "Ajouter une photo" else "Changer la photo")
                }
                if (imageUri.isNotBlank()) {
                    Button(onClick = { imageUri = "" }) {
                        Text("Retirer la photo")
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (showModifyButton) {
                    Button(
                        onClick = {
                            saveNow()
                        },
                        enabled = canSave()
                    ) {
                        Text("Modifier")
                    }
                }
                Button(onClick = { onDeleteMessage(message.id) }) {
                    Text("Supprimer")
                }
            }
            EditorTextField(
                value = dayLabel,
                onValueChange = { dayLabel = it },
                label = "Ajouter un jour apres cette bulle"
            )
            Button(
                onClick = {
                    onAddDaySeparator(dayLabel, message.id)
                    dayLabel = ""
                },
                enabled = dayLabel.isNotBlank()
            ) {
                Text("Inserer le jour")
            }
        }
    }
}

@Composable
private fun GroupAvatarEditor(
    avatarUri: String,
    onPickPhoto: () -> Unit,
    onRemovePhoto: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        AvatarCircle(label = "👥", imageUri = avatarUri)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onPickPhoto) {
                Text(if (avatarUri.isBlank()) "Photo du groupe" else "Changer la photo du groupe")
            }
            if (avatarUri.isNotBlank()) {
                Button(onClick = onRemovePhoto) {
                    Text("Retirer la photo")
                }
            }
        }
    }
}

@Composable
private fun GroupTimesSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Heure des bulles", fontWeight = FontWeight.Medium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun ReadReceiptSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Etat de lecture", fontWeight = FontWeight.Medium)
            Text(
                if (checked) "Affiche lu" else "Affiche distribué",
                color = Color(0xFF667781)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ParticipantSelector(
    contacts: List<Contact>,
    selectedIds: Set<Long>,
    label: String,
    onToggle: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, fontWeight = FontWeight.Medium)
        if (contacts.isEmpty()) {
            Text("Aucun contact disponible.", color = Color(0xFF667781))
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                contacts.forEach { contact ->
                    FilterChip(
                        selected = contact.id in selectedIds,
                        onClick = { onToggle(contact.id) },
                        label = { Text(contact.name) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PrivateDiscussionContactSelector(
    contacts: List<Contact>,
    selectedId: Long?,
    onSelect: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Contact de la discussion", fontWeight = FontWeight.Medium)
        if (contacts.isEmpty()) {
            Text("Aucun contact cree. Cree d'abord un contact.", color = Color(0xFF667781))
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                contacts.forEach { contact ->
                    FilterChip(
                        selected = contact.id == selectedId,
                        onClick = { onSelect(contact.id) },
                        label = { Text(contact.name) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VideoCallBackgroundSelector(
    videos: List<GalleryImage>,
    selectedUri: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Vidéo FaceTime (discussion privée)", fontWeight = FontWeight.Medium)
        if (videos.isEmpty()) {
            Text("Aucune vidéo dans la galerie.", color = Color(0xFF667781))
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedUri.isBlank(),
                    onClick = { onSelect("") },
                    label = { Text("Aucune") }
                )
                videos.forEach { video ->
                    FilterChip(
                        selected = video.uri == selectedUri,
                        onClick = { onSelect(video.uri) },
                        label = { Text("Vidéo #${video.id}") }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SenderSelector(
    contacts: List<Contact>,
    selectedName: String,
    onSelect: (String) -> Unit
) {
    if (contacts.isNotEmpty()) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            contacts.forEach { contact ->
                FilterChip(
                    selected = contact.name == selectedName,
                    onClick = { onSelect(contact.name) },
                    label = { Text(contact.name) }
                )
            }
        }
    }
}

@Composable
private fun EditorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label, color = Color(0xFF667781)) },
        textStyle = TextStyle(color = Color.Black),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Black,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF1F7AE0),
            unfocusedBorderColor = Color(0xFFB8C4CC),
            focusedLabelColor = Color(0xFF1F7AE0),
            unfocusedLabelColor = Color(0xFF667781),
            cursorColor = Color.Black
        )
    )
}

@Composable
private fun AvatarEditor(
    avatarType: String,
    avatarText: String,
    avatarUri: String,
    onAvatarTypeChange: (String) -> Unit,
    onAvatarTextChange: (String) -> Unit,
    onPickPhoto: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        AvatarCircle(
            label = avatarText.ifBlank { "SC" },
            imageUri = if (avatarType == "photo") avatarUri else ""
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = avatarType == "initials",
                    onClick = { onAvatarTypeChange("initials") },
                    label = { Text("Initiales") }
                )
                FilterChip(
                    selected = avatarType == "photo",
                    onClick = { onAvatarTypeChange("photo") },
                    label = { Text("Photo") }
                )
            }
            if (avatarType == "initials") {
                EditorTextField(
                    value = avatarText,
                    onValueChange = { onAvatarTextChange(it.take(3).uppercase()) },
                    label = "Initiales"
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onPickPhoto) { Text("Choisir une photo") }
                    if (avatarUri.isNotBlank()) {
                        Button(onClick = {
                            onAvatarTypeChange("initials")
                            onAvatarTextChange("")
                        }) { Text("Retirer") }
                    }
                }
            }
        }
    }
}
