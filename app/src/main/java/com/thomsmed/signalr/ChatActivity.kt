package com.thomsmed.signalr

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity

class ChatActivity : FragmentActivity() {

    val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)

    }
}
