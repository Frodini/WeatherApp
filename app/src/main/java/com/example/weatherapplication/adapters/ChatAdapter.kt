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

// Adapter for managing the list of chat messages in the RecyclerView
class ChatAdapter(private val context: Context, private val currentUserEmail: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // List to hold the chat messages
    private val messages: MutableList<ChatMessage> = mutableListOf()

    // Constants to define the view types for user and other messages
    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_OTHER = 2
    }

    // Determine the view type based on the email of the message sender
    override fun getItemViewType(position: Int): Int {
        return if (messages[position].user == currentUserEmail) {
            VIEW_TYPE_USER
        } else {
            VIEW_TYPE_OTHER
        }
    }

    // Inflate the appropriate layout based on the view type and create the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val view = LayoutInflater.from(context).inflate(R.layout.activity_chat_adapter, parent, false)
            UserMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat_message_other, parent, false)
            OtherMessageViewHolder(view)
        }
    }

    // Bind the data to the ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is UserMessageViewHolder) {
            holder.bind(message)
        } else if (holder is OtherMessageViewHolder) {
            holder.bind(message)
        }
    }

    // Return the total number of chat messages
    override fun getItemCount(): Int {
        return messages.size
    }

    // Add a new message to the list and notify the adapter
    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    // ViewHolder class for user messages
    inner class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.chatContentTextView) // TextView to display the message content
        private val timestampTextView: TextView = itemView.findViewById(R.id.chatTimestampTextView) // TextView to display the timestamp
        private val profileImageView: ImageView = itemView.findViewById(R.id.chatProfileImageView) // ImageView to display the profile picture

        // Bind the data to the views
        fun bind(message: ChatMessage) {
            contentTextView.text = message.content // Set the message content
            timestampTextView.text = android.text.format.DateFormat.format("dd-MM-yyyy hh:mm:ss", message.timestamp) // Format and set the timestamp

            // Generate and set the profile image with the user's initial
            val initial = message.user.first().toString().uppercase()
            profileImageView.setImageDrawable(DrawableUtil.getTextDrawableWithColor(initial, 40))
        }
    }

    // ViewHolder class for messages from other users
    inner class OtherMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userTextView: TextView = itemView.findViewById(R.id.chatUserTextView) // TextView to display the sender's email
        private val contentTextView: TextView = itemView.findViewById(R.id.chatContentTextView) // TextView to display the message content
        private val timestampTextView: TextView = itemView.findViewById(R.id.chatTimestampTextView) // TextView to display the timestamp
        private val profileImageView: ImageView = itemView.findViewById(R.id.chatProfileImageView) // ImageView to display the profile picture

        // Bind the data to the views
        fun bind(message: ChatMessage) {
            userTextView.text = message.user // Set the sender's email
            contentTextView.text = message.content // Set the message content
            timestampTextView.text = android.text.format.DateFormat.format("dd-MM-yyyy hh:mm:ss", message.timestamp) // Format and set the timestamp

            // Generate and set the profile image with the user's initial
            val initial = message.user.first().toString().uppercase()
            profileImageView.setImageDrawable(DrawableUtil.getTextDrawableWithColor(initial, 40))
        }
    }
}
