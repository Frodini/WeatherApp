package com.example.weatherapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.beans.Comment

class ComentAdapter(private val context: Context, private val onDeleteClickListener: (String) -> Unit) : RecyclerView.Adapter<ComentAdapter.CommentViewHolder>() {

    private val comments: MutableList<Comment> = mutableListOf()
    private val commentKeys: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_coment_adapter, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment, commentKeys[position], onDeleteClickListener)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    fun updateComments(newComments: List<Comment>, newCommentKeys: List<String>) {
        comments.clear()
        comments.addAll(newComments)
        commentKeys.clear()
        commentKeys.addAll(newCommentKeys)
        notifyDataSetChanged()
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userTextView: TextView = itemView.findViewById(R.id.commentUserTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.commentContentTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.commentTimestampTextView)
        private val deleteButton: Button = itemView.findViewById(R.id.commentDeleteButton)

        fun bind(comment: Comment, commentKey: String, onDeleteClickListener: (String) -> Unit) {
            userTextView.text = comment.user
            contentTextView.text = comment.content
            timestampTextView.text = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", comment.timestamp)
            deleteButton.setOnClickListener {
                onDeleteClickListener(commentKey)
            }
        }
    }
}
