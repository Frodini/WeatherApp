package com.example.weatherapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.beans.ChatMessage
import com.example.weatherapplication.utils.DrawableUtil

class ChatAdapter(private val context: Context, private val currentUserEmail: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages: MutableList<ChatMessage> = mutableListOf()

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_OTHER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].user == currentUserEmail) {
            VIEW_TYPE_USER
        } else {
            VIEW_TYPE_OTHER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val view = LayoutInflater.from(context).inflate(R.layout.activity_chat_adapter, parent, false)
            UserMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat_message_other, parent, false)
            OtherMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is UserMessageViewHolder) {
            holder.bind(message)
        } else if (holder is OtherMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    inner class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.chatContentTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.chatTimestampTextView)
        private val profileImageView: ImageView = itemView.findViewById(R.id.chatProfileImageView)

        fun bind(message: ChatMessage) {
            contentTextView.text = message.content
            timestampTextView.text = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", message.timestamp)
            val initial = message.user.first().toString().uppercase()
            profileImageView.setImageDrawable(DrawableUtil.getTextDrawableWithColor(initial, 40))
        }
    }

    inner class OtherMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userTextView: TextView = itemView.findViewById(R.id.chatUserTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.chatContentTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.chatTimestampTextView)
        private val profileImageView: ImageView = itemView.findViewById(R.id.chatProfileImageView)

        fun bind(message: ChatMessage) {
            userTextView.text = message.user
            contentTextView.text = message.content
            timestampTextView.text = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", message.timestamp)
            val initial = message.user.first().toString().uppercase()
            profileImageView.setImageDrawable(DrawableUtil.getTextDrawableWithColor(initial, 40))
        }
    }
}
