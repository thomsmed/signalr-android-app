package com.thomsmed.signalr.services

import java.net.URL

interface ISignalRHubConnectionBuilder {
    fun withURL(url: URL): ISignalRHubConnectionBuilder
    fun withListener(listener: ISignalRHubConnectionListener): ISignalRHubConnectionBuilder
    fun build(): ISignalRHubConnection
}