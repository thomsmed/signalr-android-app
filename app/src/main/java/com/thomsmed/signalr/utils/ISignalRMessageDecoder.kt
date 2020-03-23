package com.thomsmed.signalr.utils

import com.thomsmed.signalr.model.SignalRMessage

interface ISignalRMessageDecoder {
    fun decode(message: String): SignalRMessage
    fun extractArgument(message: String): String?
}