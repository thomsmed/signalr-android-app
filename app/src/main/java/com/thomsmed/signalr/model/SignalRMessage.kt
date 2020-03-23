package com.thomsmed.signalr.model

data class SignalRMessage(var type: SignalRMessageType?, var target: String?, var invocationId: String?, var result: String?, var error: String?) {
    enum class SignalRMessageType(val value: Int) {
        INVOCATION(1),
        STREAM_ITEM(2),
        COMPLETION(3),
        STREAM_INVOCATION(4),
        CANCEL_INVOCATION(5),
        PING(6),
        CLOSE(7);

        companion object {
            fun byValue(value: Int?): SignalRMessageType? {
                return when (value) {
                    1 -> INVOCATION
                    2 -> STREAM_ITEM
                    3 -> COMPLETION
                    4 -> STREAM_INVOCATION
                    5 -> CANCEL_INVOCATION
                    6 -> PING
                    7 -> CLOSE
                    else -> null
                }
            }
        }
    }

}