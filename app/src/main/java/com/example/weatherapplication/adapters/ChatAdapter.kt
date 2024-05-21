package com.example.weatherapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.beans.ChatMessage

class ChatAdapter(private val context: Context) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val messages: MutableList<ChatMessage> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_chat_adapter, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun clearMessages() {
        messages.clear()
        notifyDataSetChanged()
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userTextView: TextView = itemView.findViewById(R.id.chatUserTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.chatContentTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.chatTimestampTextView)

        fun bind(message: ChatMessage) {
            userTextView.text = message.user
            contentTextView.text = message.content
            timestampTextView.text = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", message.timestamp)
        }
    }
}
