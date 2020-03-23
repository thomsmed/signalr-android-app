package com.thomsmed.signalr.services

import com.thomsmed.signalr.model.SignalRMessage
import com.thomsmed.signalr.utils.ISignalRMessageDecoder
import okhttp3.*
import okio.ByteString
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.net.URL
import java.util.*
import kotlin.collections.HashMap

class SignalRHubConnection(private val url: URL, private val listener: ISignalRHubConnectionListener?)
    : WebSocketListener(), KoinComponent, ISignalRHubConnection {

    private enum class WebSocketStatusCode(val code: Int) {
        NORMAL_CLOSE(1000),
        GOING_AWAY(1001),
        PROTOCOL_ERROR(1002),
        WRONG_DATA(1003),
        // etc...
    }

    private enum class HubConnectionState {
        CONNECTING,
        OPEN,
        CLOSING,
        CLOSED,
        CANCELED
    }

    companion object {
        const val delimiter: Char = '\u001e'
    }

    private val okHttpClient: OkHttpClient by inject()
    private val signalRMessageDecoder: ISignalRMessageDecoder by inject()
    private var webSocket: WebSocket? = null

    private var state: HubConnectionState = HubConnectionState.CLOSED
    private var listeners: MutableMap<String, (message: String?) -> Unit> = HashMap()
    private var callbacks: MutableMap<String, ((error: String?, result: String?) -> Unit)?> = HashMap()

    override fun start() {
        if (state != HubConnectionState.CLOSED) {
            return
        }

        state = HubConnectionState.CONNECTING

        val request = Request.Builder()
            .url(url)
            .build()
        webSocket = okHttpClient.newWebSocket(request, this)
    }

    override fun stop() {
        if (state == HubConnectionState.CLOSED) {
            return
        }

        state = HubConnectionState.CLOSED

        webSocket?.close(WebSocketStatusCode.NORMAL_CLOSE.code, null)
    }

    override fun on(event: String, callback: (message: String?) -> Unit) {
        if (state != HubConnectionState.OPEN) {
            return
        }

        listeners[event] = callback
    }

    override fun off(event: String) {
        if (state != HubConnectionState.OPEN) {
            return
        }

        listeners.remove(event)
    }

    override fun invoke(method: String, argument: String, callback: ((error: String?, result: String?) -> Unit)?) {
        if (state != HubConnectionState.OPEN) {
            return
        }

        val invocationId = UUID.randomUUID().toString()
        callbacks[invocationId] = callback
        val message = "{ \"type\": ${SignalRMessage.SignalRMessageType.INVOCATION.value}, \"target\":\"$method\", \"invocationId\": \"$invocationId\", \"arguments\": [$argument] }" + delimiter
        webSocket?.send(message)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        val handshakeRequest = "{\"protocol\":\"json\",\"version\":1}" + delimiter
        webSocket.send(handshakeRequest)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        // Failure - connection closed
        state = HubConnectionState.CLOSED
        listener?.onClosed(t.localizedMessage)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        // Remote wish to close the connection
        state = HubConnectionState.CLOSING
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        // Peers agreed on closing connection
        state = HubConnectionState.CLOSED
        listener?.onClosed(reason)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        // Not used...
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val messages = text.split(delimiter).dropLast(1)
        for (message in messages) {
            val signalRMessage = signalRMessageDecoder.decode(message)
            when (signalRMessage.type) {
                SignalRMessage.SignalRMessageType.INVOCATION -> handleInvocationMessage(signalRMessage.target, signalRMessage.invocationId, message)
                SignalRMessage.SignalRMessageType.STREAM_ITEM -> { }
                SignalRMessage.SignalRMessageType.COMPLETION -> handleCompletionMessage(signalRMessage.invocationId, signalRMessage.result, signalRMessage.error)
                SignalRMessage.SignalRMessageType.STREAM_INVOCATION -> { }
                SignalRMessage.SignalRMessageType.CANCEL_INVOCATION -> { }
                SignalRMessage.SignalRMessageType.PING -> handlePingMessage(message)
                SignalRMessage.SignalRMessageType.CLOSE -> handleCloseMessage()
                else -> {
                    if (signalRMessage.error != null) {
                        handlePureErrorMessage(signalRMessage.error!!)
                    } else {
                        handleHandshakeSuccess()
                    }
                }
            }
        }
    }

    private fun handlePureErrorMessage(error: String) {
        webSocket?.close(WebSocketStatusCode.NORMAL_CLOSE.code, error)
    }

    private fun handleHandshakeSuccess() {
        state = HubConnectionState.OPEN
        listener?.onConnected()
    }

    private fun handleInvocationMessage(target: String?, invocationId: String?, rawMessage: String) {
        target?.let { key ->
            listeners[key]?.let { listener ->
                listener(signalRMessageDecoder.extractArgument(rawMessage))
            }
        }
    }

    private fun handleCompletionMessage(invocationId: String?, result: String?, error: String?) {
        invocationId?.let { key ->
            callbacks.remove(key)?.let { callback ->
                callback(error, result)
            }
        }
    }

    private fun handlePingMessage(rawMessage: String) {
        webSocket?.send(rawMessage + delimiter)
    }

    private fun handleCloseMessage() {
        webSocket?.close(WebSocketStatusCode.NORMAL_CLOSE.code, null)
    }
}