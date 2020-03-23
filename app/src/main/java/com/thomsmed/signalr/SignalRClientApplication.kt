package com.thomsmed.signalr

import android.app.Application
import com.thomsmed.signalr.services.ISignalRChatService
import com.thomsmed.signalr.services.ISignalRHubConnectionBuilder
import com.thomsmed.signalr.services.SignalRChatService
import com.thomsmed.signalr.services.SignalRHubConnectionBuilder
import com.thomsmed.signalr.utils.ISignalRMessageDecoder
import com.thomsmed.signalr.utils.SignalRMessageDecoder
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class SignalRClientApplication : Application() {

    // Application wide Koin module
    private var appModule = module {
        single { OkHttpClient.Builder().build() }
        factory<ISignalRMessageDecoder> { SignalRMessageDecoder() }
        factory<ISignalRHubConnectionBuilder> { SignalRHubConnectionBuilder() }
        factory<ISignalRChatService> { SignalRChatService() }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SignalRClientApplication)
            modules(appModule)
        }
    }
}