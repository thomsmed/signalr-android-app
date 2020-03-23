package com.thomsmed.signalr.services

interface ISignalRHubConnectionListener {
    fun onConnected()
    fun onClosed(error: String?)
}