package com.thomsmed.signalr.services

import java.lang.Exception
import java.net.URL

class SignalRHubConnectionBuilder : ISignalRHubConnectionBuilder {
    private var url: URL? = null
    private var listener: ISignalRHubConnectionListener? = null
    override fun withURL(url: URL): ISignalRHubConnectionBuilder {
        this.url = url
        return this
    }

    override fun withListener(listener: ISignalRHubConnectionListener): ISignalRHubConnectionBuilder {
        this.listener = listener
        return this
    }

    override fun build(): ISignalRHubConnection {
        if (url == null) {
            throw Exception("Need URL ffs!")
        }
        return SignalRHubConnection(url!!, listener)
    }
}