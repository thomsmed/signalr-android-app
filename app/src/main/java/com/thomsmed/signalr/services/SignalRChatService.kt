package com.thomsmed.signalr.services

import android.util.JsonReader
import com.thomsmed.signalr.model.ChatGroup
import com.thomsmed.signalr.model.ChatMessage
import com.thomsmed.signalr.model.ChatUser
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.InputStreamReader
import java.net.URL

class SignalRChatService: ISignalRChatService, ISignalRHubConnectionListener, KoinComponent {

    companion object {
        val nicknameUrl = URL("http://10.0.0.62:5000/nickname")
        val chatWsUrl = URL("http://10.0.0.62:5000/chat") // converts to ws when passed to a webSocket
    }

    private val signalRClientConnectionBuilder: ISignalRHubConnectionBuilder by inject()
    private val connection = signalRClientConnectionBuilder
        .withURL(chatWsUrl)
        .withListener(this)
        .build()

    override var onConnectionChange: ((Boolean) -> Unit)? = null
    override var onUserConnected: ((ChatUser) -> Unit)? = null
    override var onUserDisconnected: ((ChatUser) -> Unit)? = null
    override var onUserJoinedGroup: ((ChatGroup) -> Unit)? = null
    override var onUserLeftGroup: ((ChatGroup) -> Unit)? = null
    override var onMessageReceived: ((ChatMessage) -> Unit)? = null

    override fun connect() {
        connection.start()
    }

    override fun disconnect() {
        connection.stop()
    }

    override fun invokeSendMessage(message: ChatMessage, handler: (() -> Unit)?) {
        val method = when {
            message.group.isNotEmpty() -> {
                "SendMessageToGroup"
            }
            message.receiver.isNotEmpty() -> {
                "SendMessageToUser"
            }
            else -> {
                "SendMessageToCaller"
            }
        }

        connection.invoke(method, message.encode()) { error, result ->
            handler?.invoke()
        }
    }

    override fun invokeJoinGroup(group: ChatGroup, handler: (() -> Unit)?) {
        connection.invoke("JoinGroup", group.encode()) { error, result ->
            handler?.invoke()
        }
    }

    override fun invokeLeaveGroup(group: ChatGroup, handler: (() -> Unit)?) {
        connection.invoke("LeaveGroup", group.encode()) { error, resutl ->
            handler?.invoke()
        }
    }

    override fun onConnected() {
        onConnectionChange?.apply {
            invoke(true)
        }

        setupListeners()
    }

    override fun onClosed(error: String?) {
        onConnectionChange?.apply {
            invoke(false)
        }
    }

    private fun setupListeners() {
        connection.on("UserConnected") { message ->
            message?.let {
                onUserConnected?.apply {
                    val chatUser = ChatUser("", "")
                    val jsonReader = JsonReader(InputStreamReader(it.byteInputStream(Charsets.UTF_8)))
                    chatUser.decodeAndMutate(jsonReader)
                    jsonReader.close()
                    invoke(chatUser)
                }
            }
        }

        connection.on("UserDisconnected") { message ->
            message?.let {
                onUserDisconnected?.apply {
                    val chatUser = ChatUser("", "")
                    val jsonReader = JsonReader(InputStreamReader(it.byteInputStream(Charsets.UTF_8)))
                    chatUser.decodeAndMutate(jsonReader)
                    invoke(chatUser)
                }
            }
        }

        connection.on("UserJoinedGroup") { message ->
            message?.let {
                onUserJoinedGroup?.apply {
                    val chatGroup = ChatGroup("", "", "")
                    val jsonReader = JsonReader(InputStreamReader(it.byteInputStream(Charsets.UTF_8)))
                    chatGroup.decodeAndMutate(jsonReader)
                    invoke(chatGroup)
                }
            }
        }

        connection.on("UserLeftGroup") { message ->
            message?.let {
                onUserLeftGroup?.apply {
                    val chatGroup = ChatGroup("", "", "")
                    val jsonReader = JsonReader(InputStreamReader(it.byteInputStream(Charsets.UTF_8)))
                    chatGroup.decodeAndMutate(jsonReader)
                    jsonReader.close()
                    invoke(chatGroup)
                }
            }
        }

        connection.on("ReceiveMessageFromSelf"){ message ->
            message?.let {
                onMessageReceived?.apply {
                    val chatMessage = ChatMessage("", "", "", "", "", "")
                    val jsonReader = JsonReader(InputStreamReader(it.byteInputStream(Charsets.UTF_8)))
                    chatMessage.decodeAndMutate(jsonReader)
                    invoke(chatMessage)
                }
            }
        }

        connection.on("ReceiveMessageFromUser"){ message ->
            message?.let {
                onMessageReceived?.apply {
                    val chatMessage = ChatMessage("", "", "", "", "", "")
                    val jsonReader = JsonReader(InputStreamReader(it.byteInputStream(Charsets.UTF_8)))
                    chatMessage.decodeAndMutate(jsonReader)
                    invoke(chatMessage)
                }
            }
        }

        connection.on("ReceiveMessageFromGroup"){ message ->
            message?.let {
                onMessageReceived?.apply {
                    val chatMessage = ChatMessage("", "", "", "", "", "")
                    val jsonReader = JsonReader(InputStreamReader(it.byteInputStream(Charsets.UTF_8)))
                    chatMessage.decodeAndMutate(jsonReader)
                    invoke(chatMessage)
                }
            }
        }
    }
}
