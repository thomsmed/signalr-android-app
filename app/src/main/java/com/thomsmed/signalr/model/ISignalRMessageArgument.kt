package com.thomsmed.signalr.model

import android.util.JsonReader

interface ISignalRMessageArgument<T> {
    fun encode(): String
    fun decodeAndMutate(jsonReader: JsonReader)
}