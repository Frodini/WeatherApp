package com.example.weatherapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.beans.Comment
import com.example.weatherapplication.utils.DrawableUtil

class ComentAdapter(private val context: Context, private val currentUserEmail: String, private val onDeleteClickListener: (String) -> Unit) : RecyclerView.Adapter<ComentAdapter.CommentViewHolder>() {

    private val comments: MutableList<Comment> = mutableListOf()
    private val commentKeys: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_coment_adapter, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment, commentKeys[position], currentUserEmail, onDeleteClickListener)
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
        private val profileImageView: ImageView = itemView.findViewById(R.id.commentProfileImageView)

        fun bind(comment: Comment, commentKey: String, currentUserEmail: String, onDeleteClickListener: (String) -> Unit) {
            userTextView.text = comment.user
            contentTextView.text = comment.content
            timestampTextView.text = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", comment.timestamp)

            // Set profile image with user initial
            val initial = comment.user.first().toString().uppercase()
            profileImageView.setImageDrawable(DrawableUtil.getTextDrawableWithColor(initial, 40))

            // Show delete button only for comments by the current user
            if (comment.user == currentUserEmail) {
                deleteButton.visibility = View.VISIBLE
                deleteButton.setOnClickListener {
                    onDeleteClickListener(commentKey)
                }
            } else {
                deleteButton.visibility = View.GONE
            }
        }
    }
}
