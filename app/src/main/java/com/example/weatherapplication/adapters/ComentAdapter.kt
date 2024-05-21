package com.example.weatherapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.beans.Comment

class CommentAdapter(private val context: Context) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private var comments: List<Comment> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_coment_adapter, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    fun updateComments(comments: List<Comment>) {
        this.comments = comments
        notifyDataSetChanged()
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userTextView: TextView = itemView.findViewById(R.id.commentUserTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.commentContentTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.commentTimestampTextView)

        fun bind(comment: Comment) {
            userTextView.text = comment.user
            contentTextView.text = comment.content
            timestampTextView.text = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", comment.timestamp)
        }
    }
}
