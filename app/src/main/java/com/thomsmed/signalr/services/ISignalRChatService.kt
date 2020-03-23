package com.thomsmed.signalr.services

import com.thomsmed.signalr.model.ChatGroup
import com.thomsmed.signalr.model.ChatMessage
import com.thomsmed.signalr.model.ChatUser

interface ISignalRChatService {
    fun connect()
    fun disconnect()
    var onConnectionChange: ((connected: Boolean) -> Unit)?
    var onUserConnected: ((user: ChatUser) -> Unit)?
    var onUserDisconnected: ((user: ChatUser) -> Unit)?
    var onUserJoinedGroup: ((group: ChatGroup) -> Unit)?
    var onUserLeftGroup: ((group: ChatGroup) -> Unit)?
    var onMessageReceived: ((message: ChatMessage) -> Unit)?
    fun invokeSendMessage(message: ChatMessage, handler: (() -> Unit)?)
    fun invokeJoinGroup(group: ChatGroup, handler: (() -> Unit)?)
    fun invokeLeaveGroup(group: ChatGroup, handler: (() -> Unit)?)
}