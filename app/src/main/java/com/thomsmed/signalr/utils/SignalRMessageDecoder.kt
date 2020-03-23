package com.thomsmed.signalr.utils

import android.util.JsonReader
import java.io.InputStreamReader
import com.thomsmed.signalr.model.SignalRMessage

class SignalRMessageDecoder : ISignalRMessageDecoder {
    override fun decode(message: String): SignalRMessage {
        val signalRMessage = SignalRMessage(null, null, null, null, null)

        try {
            val jsonReader = JsonReader(InputStreamReader(message.byteInputStream(Charsets.UTF_8)))

            jsonReader.beginObject()
            while (jsonReader.hasNext()) {
                when (jsonReader.nextName()) {
                    "type" -> signalRMessage.type =
                        SignalRMessage.SignalRMessageType.byValue(tryGetNextInt(jsonReader))
                    "target" -> signalRMessage.target = tryGetNextString(jsonReader)
                    "invocationId" -> signalRMessage.invocationId = tryGetNextString(jsonReader)
                    "result" -> signalRMessage.result = tryGetNextString(jsonReader)
                    "error" -> signalRMessage.error = tryGetNextString(jsonReader)
                    else -> jsonReader.skipValue()
                }
            }
            jsonReader.endObject()
        } catch (exception: Exception) {
            // Ignore
        }

        return signalRMessage
    }

    private fun tryGetNextString(jsonReader: JsonReader): String? {
        return try {
            jsonReader.nextString()
        } catch (exception: Exception) {
            return try {
                jsonReader.nextNull()
                null
            } catch (exception: Exception) {
                return  try {
                    jsonReader.skipValue()
                    null
                } catch (exception: Exception) {
                    null
                }
            }
        }
    }

    private fun tryGetNextInt(jsonReader: JsonReader): Int? {
        return try {
            jsonReader.nextInt()
        } catch (exception: Exception) {
            return try {
                jsonReader.nextNull()
                null
            } catch (exception: Exception) {
                return  try {
                    jsonReader.skipValue()
                    null
                } catch (exception: Exception) {
                    null
                }
            }
        }
    }

    override fun extractArgument(message: String): String? {
        val firstIndex = message.indexOfFirst { char -> char == '[' }
        val lastIndex = message.indexOfLast { char -> char == ']' }

        if (lastIndex > firstIndex) {
            return message.subSequence(firstIndex + 1, lastIndex).toString()
        }

        return null
    }
}