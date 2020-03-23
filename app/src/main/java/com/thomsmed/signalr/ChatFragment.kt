package com.thomsmed.signalr

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thomsmed.signalr.model.ChatGroup
import com.thomsmed.signalr.model.ChatMessage
import com.thomsmed.signalr.model.ChatMessageAdapter
import kotlinx.android.synthetic.main.chat_fragment.*


class ChatFragment : Fragment() {

    companion object {
        fun newInstance() = ChatFragment()
    }

    private val viewModel: ChatViewModel by activityViewModels()

    private var chatGroups =  mutableListOf<ChatGroup>()
    private lateinit var spinnerAdapter: ArrayAdapter<ChatGroup>

    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var recyclerViewAdapter: RecyclerView.Adapter<*>
    private lateinit var recyclerViewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, chatGroups)
        groupSpinner.adapter = spinnerAdapter

        recyclerViewAdapter = ChatMessageAdapter(chatMessages)
        recyclerViewManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = recyclerViewManager

        togglePrivateButton.setOnClickListener {
            if (togglePrivateButton.text == getString(R.string.join_private)) {
                viewModel.joinPrivateGroup()
                togglePrivateButton.text = getString(R.string.leave_private)
            } else {
                viewModel.leavePrivateGroup()
                togglePrivateButton.text = getString(R.string.join_private)
            }
        }

        sendButton.setOnClickListener {
            val currentGroup = chatGroups[groupSpinner.selectedItemPosition]
            val message = ChatMessage("", "", "", "", "", textInput.text.toString())
            when (currentGroup.id) {
                "global" -> {
                    message.group = currentGroup.id
                }
                "private" -> {
                    message.group = currentGroup.id
                }
                "myself" -> {
                    // Ignore
                }
                else -> {
                    message.receiver = currentGroup.id
                }
            }
            viewModel.postMessage(message)
            textInput.text = null
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.connectionState().observe(viewLifecycleOwner, Observer { connected ->
            connectedBanner.visibility = View.VISIBLE
            if (connected) {
                connectedBanner.setBackgroundColor(Color.GREEN)
                connectedBanner.text = getString(R.string.connected)
            } else {
                connectedBanner.setBackgroundColor(Color.RED)
                connectedBanner.text = getString(R.string.connection_failed)
            }
        })

        viewModel.availableChatGroups().observe(viewLifecycleOwner, Observer { chatGroups ->
            this.chatGroups.clear()
            this.chatGroups.addAll(chatGroups)
            spinnerAdapter.notifyDataSetChanged()

        })

        viewModel.incomingChatMessages().observe(viewLifecycleOwner, Observer { newChatMessage ->
            this.chatMessages.add(newChatMessage)
            recyclerViewAdapter.notifyDataSetChanged()
        })
    }

}
