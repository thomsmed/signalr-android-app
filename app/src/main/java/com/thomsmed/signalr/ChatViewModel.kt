package com.thomsmed.signalr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thomsmed.signalr.model.ChatGroup
import com.thomsmed.signalr.model.ChatMessage
import com.thomsmed.signalr.services.ISignalRChatService
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.min

class ChatViewModel : ViewModel(), KoinComponent {

    private val chatClient: ISignalRChatService by inject()

    private val connectionState: MutableLiveData<Boolean> = MutableLiveData()
    private val chatGroups: MutableLiveData<List<ChatGroup>> = MutableLiveData()
    private val incomingChatMessages: MutableLiveData<ChatMessage> = MutableLiveData()

    private val chatGroupList: MutableList<ChatGroup> = mutableListOf()

    init {
        chatClient.connect()

        chatClient.onConnectionChange = { connected ->
            connectionState.postValue(connected)

            chatGroupList.add(ChatGroup("myself", "myself", ""))
            chatGroups.postValue(chatGroupList)

            chatClient.invokeJoinGroup(ChatGroup("global", "global", "")) {
                chatGroupList.add(ChatGroup("global", "global", ""))
                chatGroups.postValue(chatGroupList)
                incomingChatMessages.postValue(ChatMessage("", "", "", "", "", "You joined global group!"))
            }
        }

        chatClient.onUserConnected = { chatUser ->
            val userId = chatUser.id.substring(0, min(5, chatUser.id.length))
            chatGroupList.add(ChatGroup(chatUser.id, chatUser.id, ""))
            chatGroups.postValue(chatGroupList)
            incomingChatMessages.postValue(ChatMessage("", "", "", "", "", "User ($userId) connected!"))
        }

        chatClient.onUserDisconnected = { chatUser ->
            chatGroupList.removeIf { group -> group.id == chatUser.id }
            chatGroups.postValue(chatGroupList)
            val userId = chatUser.id.substring(0, min(5, chatUser.id.length))
            incomingChatMessages.postValue(ChatMessage("", "", "", "", "", "User ($userId) disconnected..."))
        }

        chatClient.onUserJoinedGroup = { chatGroup ->
            val userId = chatGroup.participant.substring(0, min(5, chatGroup.participant.length))
            val groupId = chatGroup.id.substring(0, min(10, chatGroup.id.length))
            incomingChatMessages.postValue(ChatMessage("", "", "", "", "", "User ($userId) joined group: $groupId"))
        }

        chatClient.onUserLeftGroup = { chatGroup ->
            val userId = chatGroup.participant.substring(0, min(5, chatGroup.participant.length))
            val groupId = chatGroup.id.substring(0, min(10, chatGroup.id.length))
            incomingChatMessages.postValue(ChatMessage("", "", "", "","", "User ($userId) left group: $groupId"))
        }

        chatClient.onMessageReceived = { chatMessage ->
            incomingChatMessages.postValue(chatMessage)
        }
    }

    fun postMessage(message: ChatMessage) {
        chatClient.invokeSendMessage(message) {
            // Ignore
        }
    }

    fun joinPrivateGroup() {
        chatClient.invokeJoinGroup(ChatGroup("private", "private", "")) {
            chatGroupList.add(ChatGroup("private", "private", ""))
            chatGroups.postValue(chatGroupList)
            incomingChatMessages.postValue(ChatMessage("", "", "", "", "", "You joined private group!"))
        }
    }

    fun leavePrivateGroup() {
        chatClient.invokeLeaveGroup(ChatGroup("private", "private", "")) {
            chatGroupList.removeIf { group -> group.id == "private" }
            chatGroups.postValue(chatGroupList)
            incomingChatMessages.postValue(ChatMessage("", "", "", "", "", "You left private group..."))
        }
    }

    fun connectionState(): LiveData<Boolean> {
        return connectionState
    }

    fun availableChatGroups(): LiveData<List<ChatGroup>> {
        return chatGroups
    }

    fun incomingChatMessages(): LiveData<ChatMessage> {
        return incomingChatMessages
    }

    override fun onCleared() {
        super.onCleared()
        chatClient.disconnect()
    }

}
