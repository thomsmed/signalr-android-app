package com.thomsmed.signalr.model

import android.util.JsonReader

private fun tryGetNextString(jsonReader: JsonReader): String {
    return try {
        jsonReader.nextString()
    } catch (exception: Exception) {
        return try {
            jsonReader.nextNull()
            ""
        } catch (exception: Exception) {
            return  try {
                jsonReader.skipValue()
                ""
            } catch (exception: Exception) {
                ""
            }
        }
    }
}

data class ChatMessage(var id: String, var sender: String, var receiver: String, var group: String, var header: String, var body: String): ISignalRMessageArgument<ChatMessage> {
    override fun encode(): String {
        return "{\"id\":\"$id\",\"sender\":\"$sender\",\"receiver\":\"$receiver\",\"group\":\"$group\",\"header\":\"$header\",\"body\":\"$body\"}"
    }

    override fun decodeAndMutate(jsonReader: JsonReader) {
        try {
            jsonReader.beginObject()
            while (jsonReader.hasNext()) {
                when (jsonReader.nextName()) {
                    "id" -> id = tryGetNextString(jsonReader)
                    "sender" -> sender = tryGetNextString(jsonReader)
                    "receiver" -> receiver = tryGetNextString(jsonReader)
                    "group" -> group = tryGetNextString(jsonReader)
                    "header" -> header = tryGetNextString(jsonReader)
                    "body" -> body = tryGetNextString(jsonReader)
                    else -> jsonReader.skipValue()
                }
            }
            jsonReader.endObject()
        } catch (exception: Exception) {
            // Ignore
        }
    }

    override fun toString(): String {
        return body
    }
}

data class ChatGroup(var id: String, var name: String, var participant: String): ISignalRMessageArgument<ChatGroup> {
    override fun encode(): String {
        return "{\"id\":\"$id\",\"name\":\"$name\",\"participant\":\"$participant\"}"
    }

    override fun decodeAndMutate(jsonReader: JsonReader) {
        try {
            jsonReader.beginObject()
            while (jsonReader.hasNext()) {
                when (jsonReader.nextName()) {
                    "id" -> id = tryGetNextString(jsonReader)
                    "name" -> name = tryGetNextString(jsonReader)
                    "participant" -> participant = tryGetNextString(jsonReader)
                    else -> jsonReader.skipValue()
                }
            }
            jsonReader.endObject()
        } catch (exception: Exception) {
            // Ignore
        }
    }

    override fun toString(): String {
        return id
    }
}

data class ChatUser(var id: String, var name: String): ISignalRMessageArgument<ChatUser> {
    override fun encode(): String {
        return "{\"id\":\"$id\",\"name\":\"$name\"}"
    }

    override fun decodeAndMutate(jsonReader: JsonReader) {
        try {
            jsonReader.beginObject()
            while (jsonReader.hasNext()) {
                when (jsonReader.nextName()) {
                    "id" -> id = tryGetNextString(jsonReader)
                    "name" -> name = tryGetNextString(jsonReader)
                    else -> jsonReader.skipValue()
                }
            }
            jsonReader.endObject()
        } catch (exception: Exception) {
            // Ignore
        }
    }

    override fun toString(): String {
        return id
    }
}