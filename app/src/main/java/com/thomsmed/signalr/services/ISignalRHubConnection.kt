package com.thomsmed.signalr.services

interface ISignalRHubConnection {
    fun start()
    fun stop()
    fun on(event: String, callback: (message: String?) -> Unit)
    fun off(event: String)
    fun invoke(method: String, argument: String, callback: ((error: String?, result: String?) -> Unit)?)
}