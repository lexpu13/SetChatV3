package com.example.setchat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.setchat.data.local.AppDatabase
import com.example.setchat.data.local.Contact
import com.example.setchat.data.local.Conversation
import com.example.setchat.data.local.Message
import com.example.setchat.data.repository.SetChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SetChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SetChatRepository(AppDatabase.getInstance(application).dao())

    val contacts: StateFlow<List<Contact>> =
        repository.contacts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversations: StateFlow<List<Conversation>> =
        repository.conversations().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val openedConversationId = MutableStateFlow<Long?>(null)

    fun messages(conversationId: Long): StateFlow<List<Message>> =
        repository.messages(conversationId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun seed() { viewModelScope.launch { repository.seedIfNeeded() } }
    fun addContact(name: String, role: String, status: String) { viewModelScope.launch { repository.addContact(name, role, status) } }
    fun addConversation(title: String, isGroup: Boolean) { viewModelScope.launch { repository.addConversation(title, isGroup) } }

    fun openConversation(conversationId: Long) {
        openedConversationId.value = conversationId
        viewModelScope.launch { repository.markConversationRead(conversationId) }
    }

    fun closeConversation() {
        openedConversationId.value = null
    }

    fun sendMineMessage(conversationId: Long, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(conversationId, text, true, "Moi")
        }
    }

    fun sendOtherMessage(conversationId: Long, text: String, senderName: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(
                conversationId = conversationId,
                text = text,
                isMine = false,
                senderName = senderName.ifBlank { "Personnage" },
                incrementUnread = openedConversationId.value != conversationId
            )
        }
    }
}
