package com.example.setchat.viewmodel

data class FakeStatusBarSettings(
    val time: String = "21:18",
    val operator: String = "Operateur",
    val network: String = "5G",
    val batteryLevel: Int = 58
)

data class DeviceShellSettings(
    val lockWallpaperUri: String = "",
    val homeWallpaperUri: String = "",
    val lockTime: String = "19:42",
    val showLockTime: Boolean = true,
    val lockDate: String = "vendredi 12 avril",
    val showLockDate: Boolean = true
)

data class ExportSettings(
    val enabled: Boolean = false,
    val format: String = "pdf"
)

data class NotificationConfig(
    val id: Long,
    val label: String = "",
    val conversationId: Long = 0L,
    val senderContactId: Long = 0L,
    val messageText: String = "",
    val previewMode: String = "full",
    val previewLength: Int = 42,
    val useProfilePhoto: Boolean = false,
    val durationSeconds: Int = 4,
    val triggerKey: String = ""
)
