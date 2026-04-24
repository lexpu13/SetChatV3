package com.example.setchat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.setchat.data.local.GalleryImage
import com.example.setchat.ui.components.AvatarCircle
import com.example.setchat.ui.components.FakeStatusBar
import com.example.setchat.ui.screens.AdminScreen
import com.example.setchat.ui.screens.AppearanceEditorScreen
import com.example.setchat.ui.screens.AudioCallScreen
import com.example.setchat.ui.screens.ChatScreen
import com.example.setchat.ui.screens.ContactsEditorScreen
import com.example.setchat.ui.screens.ContactsScreen
import com.example.setchat.ui.screens.DeviceHomeScreen
import com.example.setchat.ui.screens.DeviceLockScreen
import com.example.setchat.ui.screens.DeviceOffScreen
import com.example.setchat.ui.screens.DiscussionsEditorScreen
import com.example.setchat.ui.screens.EditorHubScreen
import com.example.setchat.ui.screens.GalleryEditorScreen
import com.example.setchat.ui.screens.GroupsEditorScreen
import com.example.setchat.ui.screens.HomeScreen
import com.example.setchat.ui.screens.NotificationsEditorScreen
import com.example.setchat.ui.screens.VideoCallScreen
import com.example.setchat.ui.theme.SetChatTheme
import com.example.setchat.viewmodel.DeviceShellSettings
import com.example.setchat.viewmodel.ExportSettings
import com.example.setchat.viewmodel.FakeStatusBarSettings
import com.example.setchat.viewmodel.NotificationConfig
import com.example.setchat.viewmodel.SetChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

private enum class AppPhase {
    OFF,
    LOCK,
    HOME,
    APP,
    EDITOR
}

private enum class EditorPage {
    HUB,
    CONTACTS,
    GROUPS,
    DISCUSSIONS,
    APPEARANCE,
    GALLERY,
    NOTIFICATIONS
}

private data class RuntimeNotification(
    val id: Long,
    val conversationId: Long,
    val senderName: String,
    val senderAvatarText: String,
    val senderAvatarUri: String,
    val shownText: String,
    val durationSeconds: Int,
    val stamp: Long = System.currentTimeMillis()
)

@Composable
fun SetChatApp() {
    val vm: SetChatViewModel = viewModel()
    val navController = rememberNavController()
    val contacts by vm.contacts.collectAsState()
    val conversations by vm.conversations.collectAsState()

    var phase by rememberSaveable { mutableStateOf(AppPhase.OFF.name) }
    var editorPage by rememberSaveable { mutableStateOf(EditorPage.HUB.name) }

    var isDarkTheme by rememberSaveable { mutableStateOf(true) }
    var fakeStatusBarSettings by remember { mutableStateOf(FakeStatusBarSettings()) }
    var deviceShellSettings by remember { mutableStateOf(DeviceShellSettings()) }
    var exportSettings by remember { mutableStateOf(ExportSettings()) }
    var galleryImages by remember { mutableStateOf<List<GalleryImage>>(emptyList()) }
    var nextGalleryId by rememberSaveable { mutableStateOf(1L) }
    var notificationConfigs by remember { mutableStateOf<List<NotificationConfig>>(emptyList()) }
    var nextNotificationId by rememberSaveable { mutableStateOf(1L) }
    var activeNotification by remember { mutableStateOf<RuntimeNotification?>(null) }
    var pendingOpenConversationId by rememberSaveable { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) { vm.seed() }

    val appPhase = remember(phase) { runCatching { AppPhase.valueOf(phase) }.getOrDefault(AppPhase.OFF) }
    val currentEditorPage = remember(editorPage) { runCatching { EditorPage.valueOf(editorPage) }.getOrDefault(EditorPage.HUB) }

    val openEditorHome = { editorPage = EditorPage.HUB.name }
    val openContactsEditor = { editorPage = EditorPage.CONTACTS.name }
    val openGroupsEditor = { editorPage = EditorPage.GROUPS.name }
    val openDiscussionsEditor = { editorPage = EditorPage.DISCUSSIONS.name }
    val openAppearanceEditor = { editorPage = EditorPage.APPEARANCE.name }
    val openGalleryEditor = { editorPage = EditorPage.GALLERY.name }
    val openNotificationsEditor = { editorPage = EditorPage.NOTIFICATIONS.name }
    val goToMessagingApp = {
        if (phase == AppPhase.APP.name) {
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
                launchSingleTop = true
            }
        } else {
            phase = AppPhase.APP.name
        }
    }

    fun triggerNotificationById(notificationId: Long): Boolean {
        val config = notificationConfigs.firstOrNull { it.id == notificationId } ?: return false
        val conversation = conversations.firstOrNull { it.id == config.conversationId } ?: return false
        val sender = contacts.firstOrNull { it.id == config.senderContactId }
        val senderName = sender?.name?.ifBlank { "Personnage" } ?: "Personnage"
        val senderAvatarText = sender?.avatarText?.ifBlank { senderName.take(1).uppercase() }
            ?: senderName.take(1).uppercase()
        val senderAvatarUri = if (config.useProfilePhoto && sender?.avatarType == "photo") {
            sender.avatarUri
        } else {
            ""
        }
        val shownText = if (config.previewMode == "partial") {
            val max = config.previewLength.coerceIn(8, 200)
            if (config.messageText.length <= max) config.messageText else config.messageText.take(max).trimEnd() + "..."
        } else {
            config.messageText
        }

        activeNotification = RuntimeNotification(
            id = config.id,
            conversationId = conversation.id,
            senderName = senderName,
            senderAvatarText = senderAvatarText,
            senderAvatarUri = senderAvatarUri,
            shownText = shownText,
            durationSeconds = config.durationSeconds.coerceIn(1, 30)
        )

        vm.sendOtherMessage(
            conversationId = conversation.id,
            text = config.messageText,
            senderName = senderName
        )
        if (phase == AppPhase.OFF.name) {
            phase = AppPhase.LOCK.name
        }
        return true
    }

    fun triggerNotificationByKey(rawKey: String): Boolean {
        val key = rawKey.trim().take(1).lowercase()
        if (key.isBlank()) return false
        val config = notificationConfigs.firstOrNull { it.triggerKey == key } ?: return false
        return triggerNotificationById(config.id)
    }

    LaunchedEffect(activeNotification?.id, activeNotification?.stamp) {
        val current = activeNotification ?: return@LaunchedEffect
        delay(current.durationSeconds * 1000L)
        if (activeNotification?.stamp == current.stamp) {
            activeNotification = null
        }
    }

    LaunchedEffect(Unit) {
        NotificationKeyBus.keys.collectLatest { key ->
            if (runCatching { AppPhase.valueOf(phase) }.getOrDefault(AppPhase.OFF) != AppPhase.EDITOR) {
                triggerNotificationByKey(key)
            }
        }
    }

    LaunchedEffect(appPhase, pendingOpenConversationId) {
        val targetConversationId = pendingOpenConversationId ?: return@LaunchedEffect
        if (appPhase == AppPhase.APP) {
            navController.navigate("chat/$targetConversationId") {
                launchSingleTop = true
            }
            pendingOpenConversationId = null
            activeNotification = null
        }
    }

    SetChatTheme(isDarkTheme = isDarkTheme) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (appPhase) {
            AppPhase.OFF -> {
                DeviceOffScreen(onTap = { phase = AppPhase.LOCK.name })
            }

            AppPhase.LOCK -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    DeviceLockScreen(
                        settings = deviceShellSettings,
                        onTap = { phase = AppPhase.HOME.name }
                    )
                    FakeStatusBar(
                        isDarkTheme = isDarkTheme,
                        settings = fakeStatusBarSettings,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .background(Color.Transparent)
                    )
                }
            }

            AppPhase.HOME -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    DeviceHomeScreen(
                        settings = deviceShellSettings,
                        onOpenSetChat = goToMessagingApp,
                        onOpenPlans = { phase = AppPhase.OFF.name },
                        onOpenEditor = {
                            editorPage = EditorPage.HUB.name
                            phase = AppPhase.EDITOR.name
                        }
                    )
                    FakeStatusBar(
                        isDarkTheme = isDarkTheme,
                        settings = fakeStatusBarSettings,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .background(Color.Transparent)
                    )
                }
            }

            AppPhase.APP -> {
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        Box(modifier = Modifier.fillMaxSize()) {
                            HomeScreen(
                                conversations = conversations,
                                onOpenChat = { conversationId -> navController.navigate("chat/$conversationId") },
                                onOpenContacts = { navController.navigate("contacts") },
                                onOpenAdmin = { navController.navigate("admin") },
                                isDarkTheme = isDarkTheme
                            )
                            IconButton(
                                onClick = { phase = AppPhase.HOME.name },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(top = 40.dp, start = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Retour accueil telephone",
                                    tint = if (isDarkTheme) Color.White else Color(0xFF111B21)
                                )
                            }
                        }
                    }
                    composable("contacts") {
                        ContactsScreen(
                            contacts = contacts,
                            onBack = { navController.popBackStack() },
                            onAddContact = vm::addContact
                        )
                    }
                    composable("admin") {
                        AdminScreen(
                            onBack = { navController.popBackStack() },
                            onCreateConversation = vm::addConversation
                        )
                    }
                    composable(
                        route = "chat/{conversationId}",
                        arguments = listOf(navArgument("conversationId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: 0L
                        val messagesFlow = remember(conversationId) { vm.messages(conversationId) }
                        val messages by messagesFlow.collectAsState()
                        val conversation = conversations.firstOrNull { it.id == conversationId }

                        LaunchedEffect(conversationId) { vm.openConversation(conversationId) }
                        DisposableEffect(conversationId) {
                            onDispose { vm.closeConversation() }
                        }

                        ChatScreen(
                            conversationTitle = conversation?.title ?: "Discussion",
                            conversationSubtitle = conversation?.subtitle ?: "Connecte",
                            isGroup = conversation?.type == "group",
                            messages = messages,
                            onBack = { navController.popBackStack() },
                            onSendMine = { text -> vm.sendMineMessage(conversationId, text) },
                            onSendOther = { text, senderName ->
                                vm.sendOtherMessage(conversationId, text, senderName)
                            },
                            onOpenAudioCall = { navController.navigate("call/$conversationId") },
                            onOpenVideoCall = { navController.navigate("video/$conversationId") },
                            isDarkTheme = isDarkTheme
                        )
                    }
                    composable(
                        route = "call/{conversationId}",
                        arguments = listOf(navArgument("conversationId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: 0L
                        val conversation = conversations.firstOrNull { it.id == conversationId }
                        val matchingContact = contacts.firstOrNull { it.name == conversation?.title }
                        val avatarUri = if (matchingContact?.avatarType == "photo") matchingContact.avatarUri else ""
                        val avatarLabel = matchingContact?.avatarText?.ifBlank {
                            conversation?.title?.take(1)?.uppercase().orEmpty()
                        } ?: conversation?.title?.take(1)?.uppercase().orEmpty()

                        AudioCallScreen(
                            contactName = conversation?.title ?: "Contact",
                            avatarLabel = avatarLabel.ifBlank { "C" },
                            avatarUri = avatarUri,
                            onBack = { navController.popBackStack() },
                            onStartVideoCall = { navController.navigate("video/$conversationId") }
                        )
                    }
                    composable(
                        route = "video/{conversationId}",
                        arguments = listOf(navArgument("conversationId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: 0L
                        val conversation = conversations.firstOrNull { it.id == conversationId }
                        val matchingContact = contacts.firstOrNull { it.name == conversation?.title }
                        val avatarUri = if (matchingContact?.avatarType == "photo") matchingContact.avatarUri else ""
                        val avatarLabel = matchingContact?.avatarText?.ifBlank {
                            conversation?.title?.take(1)?.uppercase().orEmpty()
                        } ?: conversation?.title?.take(1)?.uppercase().orEmpty()

                        VideoCallScreen(
                            contactName = conversation?.title ?: "Contact",
                            avatarLabel = avatarLabel.ifBlank { "C" },
                            avatarUri = avatarUri,
                            galleryVideos = galleryImages.filter { it.mediaType == "video" },
                            initialBackgroundVideoUri = conversation?.videoCallBackgroundUri.orEmpty(),
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }

            AppPhase.EDITOR -> {
                when (currentEditorPage) {
                    EditorPage.HUB -> EditorHubScreen(
                        onBack = { phase = AppPhase.HOME.name },
                        onGoToApp = goToMessagingApp,
                        onOpenEditorHome = openEditorHome,
                        onOpenContacts = openContactsEditor,
                        onOpenGroups = openGroupsEditor,
                        onOpenDiscussions = openDiscussionsEditor,
                        onOpenAppearance = openAppearanceEditor,
                        onOpenGallery = openGalleryEditor,
                        onOpenNotifications = openNotificationsEditor,
                        onResetAll = {
                            isDarkTheme = true
                            fakeStatusBarSettings = FakeStatusBarSettings()
                            deviceShellSettings = DeviceShellSettings()
                            exportSettings = ExportSettings()
                            galleryImages = emptyList()
                            nextGalleryId = 1L
                            notificationConfigs = emptyList()
                            nextNotificationId = 1L
                        }
                    )

                    EditorPage.CONTACTS -> ContactsEditorScreen(
                        contacts = contacts,
                        onBack = { phase = AppPhase.HOME.name },
                        onGoToApp = goToMessagingApp,
                        onOpenEditorHome = openEditorHome,
                        onOpenContacts = openContactsEditor,
                        onOpenGroups = openGroupsEditor,
                        onOpenDiscussions = openDiscussionsEditor,
                        onOpenAppearance = openAppearanceEditor,
                        onOpenGallery = openGalleryEditor,
                        onOpenNotifications = openNotificationsEditor,
                        onResetContacts = {},
                        onCreateContact = { name, role, status, _, _, _ ->
                            vm.addContact(name, role, status)
                        },
                        onUpdateContact = { _, _, _, _, _, _, _ -> },
                        onDeleteContact = {}
                    )

                    EditorPage.GROUPS -> GroupsEditorScreen(
                        groups = conversations.filter { it.type == "group" },
                        onBack = { phase = AppPhase.HOME.name },
                        onGoToApp = goToMessagingApp,
                        onOpenEditorHome = openEditorHome,
                        onOpenContacts = openContactsEditor,
                        onOpenGroups = openGroupsEditor,
                        onOpenDiscussions = openDiscussionsEditor,
                        onOpenAppearance = openAppearanceEditor,
                        onOpenGallery = openGalleryEditor,
                        onOpenNotifications = openNotificationsEditor,
                        onResetGroups = {},
                        onOpenGroup = {},
                        onCreateGroup = { title -> vm.addConversation(title, true) },
                        onDeleteGroup = {}
                    )

                    EditorPage.DISCUSSIONS -> DiscussionsEditorScreen(
                        discussions = conversations.filter { it.type != "group" },
                        onBack = { phase = AppPhase.HOME.name },
                        onGoToApp = goToMessagingApp,
                        onOpenEditorHome = openEditorHome,
                        onOpenContacts = openContactsEditor,
                        onOpenGroups = openGroupsEditor,
                        onOpenDiscussions = openDiscussionsEditor,
                        onOpenAppearance = openAppearanceEditor,
                        onOpenGallery = openGalleryEditor,
                        onOpenNotifications = openNotificationsEditor,
                        onResetDiscussions = {},
                        onOpenDiscussion = {},
                        onCreateDiscussion = { title -> vm.addConversation(title, false) },
                        onDeleteDiscussion = {}
                    )

                    EditorPage.APPEARANCE -> AppearanceEditorScreen(
                        isDarkTheme = isDarkTheme,
                        statusBarSettings = fakeStatusBarSettings,
                        deviceSettings = deviceShellSettings,
                        exportSettings = exportSettings,
                        onBack = { phase = AppPhase.HOME.name },
                        onGoToApp = goToMessagingApp,
                        onOpenEditorHome = openEditorHome,
                        onOpenContacts = openContactsEditor,
                        onOpenGroups = openGroupsEditor,
                        onOpenDiscussions = openDiscussionsEditor,
                        onOpenAppearance = openAppearanceEditor,
                        onOpenGallery = openGalleryEditor,
                        onOpenNotifications = openNotificationsEditor,
                        onResetAppearance = {
                            isDarkTheme = true
                            fakeStatusBarSettings = FakeStatusBarSettings()
                            deviceShellSettings = DeviceShellSettings()
                            exportSettings = ExportSettings()
                        },
                        onSetDarkTheme = { isDarkTheme = it },
                        onSetStatusBarTime = { fakeStatusBarSettings = fakeStatusBarSettings.copy(time = it.take(16)) },
                        onSetStatusBarOperator = { fakeStatusBarSettings = fakeStatusBarSettings.copy(operator = it.take(24)) },
                        onSetStatusBarNetwork = { fakeStatusBarSettings = fakeStatusBarSettings.copy(network = it.take(10)) },
                        onSetStatusBarBattery = { fakeStatusBarSettings = fakeStatusBarSettings.copy(batteryLevel = it.coerceIn(0, 100)) },
                        onSetLockWallpaper = { deviceShellSettings = deviceShellSettings.copy(lockWallpaperUri = it.trim()) },
                        onClearLockWallpaper = { deviceShellSettings = deviceShellSettings.copy(lockWallpaperUri = "") },
                        onSetHomeWallpaper = { deviceShellSettings = deviceShellSettings.copy(homeWallpaperUri = it.trim()) },
                        onClearHomeWallpaper = { deviceShellSettings = deviceShellSettings.copy(homeWallpaperUri = "") },
                        onSetLockTime = { deviceShellSettings = deviceShellSettings.copy(lockTime = it.take(16)) },
                        onSetLockDate = { deviceShellSettings = deviceShellSettings.copy(lockDate = it.take(24)) },
                        onSetShowLockTime = { deviceShellSettings = deviceShellSettings.copy(showLockTime = it) },
                        onSetShowLockDate = { deviceShellSettings = deviceShellSettings.copy(showLockDate = it) },
                        onSetExportEnabled = { exportSettings = exportSettings.copy(enabled = it) },
                        onSetExportFormat = {
                            val f = it.lowercase()
                            if (f == "pdf" || f == "svg") exportSettings = exportSettings.copy(format = f)
                        }
                    )

                    EditorPage.GALLERY -> GalleryEditorScreen(
                        galleryImages = galleryImages,
                        onBack = { phase = AppPhase.HOME.name },
                        onGoToApp = goToMessagingApp,
                        onOpenEditorHome = openEditorHome,
                        onOpenContacts = openContactsEditor,
                        onOpenGroups = openGroupsEditor,
                        onOpenDiscussions = openDiscussionsEditor,
                        onOpenAppearance = openAppearanceEditor,
                        onOpenGallery = openGalleryEditor,
                        onOpenNotifications = openNotificationsEditor,
                        onResetGallery = { galleryImages = emptyList() },
                        onImportImage = { uri ->
                            galleryImages = galleryImages + GalleryImage(
                                id = nextGalleryId++,
                                uri = uri,
                                mediaType = "image"
                            )
                        },
                        onImportVideo = { uri ->
                            galleryImages = galleryImages + GalleryImage(
                                id = nextGalleryId++,
                                uri = uri,
                                mediaType = "video"
                            )
                        },
                        onDeleteImage = { id ->
                            galleryImages = galleryImages.filterNot { it.id == id }
                        }
                    )

                    EditorPage.NOTIFICATIONS -> NotificationsEditorScreen(
                        notifications = notificationConfigs,
                        contacts = contacts,
                        conversations = conversations,
                        onBack = { phase = AppPhase.HOME.name },
                        onGoToApp = goToMessagingApp,
                        onOpenEditorHome = openEditorHome,
                        onOpenContacts = openContactsEditor,
                        onOpenGroups = openGroupsEditor,
                        onOpenDiscussions = openDiscussionsEditor,
                        onOpenAppearance = openAppearanceEditor,
                        onOpenGallery = openGalleryEditor,
                        onOpenNotifications = openNotificationsEditor,
                        onResetNotifications = {
                            notificationConfigs = emptyList()
                            nextNotificationId = 1L
                        },
                        onSendNotificationNow = { id ->
                            triggerNotificationById(id)
                        },
                        onCreateNotification = { label, conversationId, senderContactId, messageText, previewMode, previewLength, useProfilePhoto, durationSeconds, triggerKey ->
                            val key = triggerKey.trim().take(1).lowercase()
                            if (conversationId <= 0L || senderContactId <= 0L || messageText.isBlank() || key.isBlank()) {
                                null
                            } else if (notificationConfigs.any { it.triggerKey == key }) {
                                null
                            } else {
                                val id = nextNotificationId++
                                notificationConfigs = notificationConfigs + NotificationConfig(
                                    id = id,
                                    label = label.trim(),
                                    conversationId = conversationId,
                                    senderContactId = senderContactId,
                                    messageText = messageText.trim(),
                                    previewMode = if (previewMode == "partial") "partial" else "full",
                                    previewLength = previewLength.coerceIn(8, 200),
                                    useProfilePhoto = useProfilePhoto,
                                    durationSeconds = durationSeconds.coerceIn(1, 30),
                                    triggerKey = key
                                )
                                id
                            }
                        },
                        onUpdateNotification = { id, label, conversationId, senderContactId, messageText, previewMode, previewLength, useProfilePhoto, durationSeconds, triggerKey ->
                            val key = triggerKey.trim().take(1).lowercase()
                            if (id <= 0L || conversationId <= 0L || senderContactId <= 0L || messageText.isBlank() || key.isBlank()) {
                                false
                            } else if (notificationConfigs.any { it.id != id && it.triggerKey == key }) {
                                false
                            } else {
                                notificationConfigs = notificationConfigs.map { existing ->
                                    if (existing.id != id) existing else existing.copy(
                                        label = label.trim(),
                                        conversationId = conversationId,
                                        senderContactId = senderContactId,
                                        messageText = messageText.trim(),
                                        previewMode = if (previewMode == "partial") "partial" else "full",
                                        previewLength = previewLength.coerceIn(8, 200),
                                        useProfilePhoto = useProfilePhoto,
                                        durationSeconds = durationSeconds.coerceIn(1, 30),
                                        triggerKey = key
                                    )
                                }
                                true
                            }
                        },
                        onDeleteNotification = { id ->
                            notificationConfigs = notificationConfigs.filterNot { it.id == id }
                        }
                    )
                }
            }
            }

            val banner = activeNotification
            if (banner != null) {
                NotificationOverlayBubble(
                    senderName = banner.senderName,
                    senderAvatarText = banner.senderAvatarText,
                    senderAvatarUri = banner.senderAvatarUri,
                    textPreview = banner.shownText,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 36.dp, start = 12.dp, end = 12.dp)
                        .clickable {
                            pendingOpenConversationId = banner.conversationId
                            phase = AppPhase.APP.name
                        }
                )
            }
        }
    }
}

@Composable
private fun NotificationOverlayBubble(
    senderName: String,
    senderAvatarText: String,
    senderAvatarUri: String,
    textPreview: String,
    modifier: Modifier = Modifier
) {
    val bubbleColor = Color(0xFFDBECFF)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        AvatarCircle(
            label = senderAvatarText,
            imageUri = senderAvatarUri,
            size = 38.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Surface(
                color = bubbleColor,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                shadowElevation = 9.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = senderName,
                        color = Color(0xFF0E355F),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Text(
                        text = textPreview,
                        color = Color(0xFF0A1F36),
                        fontSize = 14.sp,
                        maxLines = 3
                    )
                }
            }
            Box(
                modifier = Modifier
                    .offset(x = (-4).dp, y = 16.dp)
                    .size(11.dp)
                    .rotate(45f)
                    .background(
                        color = bubbleColor,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}
