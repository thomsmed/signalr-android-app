package com.thomsmed.signalr.model

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thomsmed.signalr.R

class ChatMessageAdapter(private val chatMessages: List<ChatMessage>) : RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder>() {

    class ChatMessageViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val textView = parent.context.getSystemService(LayoutInflater::class.java)?.inflate(R.layout.chat_item, parent, false) as TextView
        return ChatMessageViewHolder(textView = textView)
    }

    override fun getItemCount(): Int {
        return chatMessages.count()
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        holder.textView.text = chatMessages[position].body
    }
}