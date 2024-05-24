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

// Adapter for managing the list of comments in the RecyclerView
class ComentAdapter(
    private val context: Context, // The context of the calling activity
    private val currentUserEmail: String, // Email of the current user to identify their comments
    private val onDeleteClickListener: (String) -> Unit, // Lambda function to handle comment deletion
    private val onEditClickListener: (Comment, String) -> Unit // Lambda function to handle comment editing
) : RecyclerView.Adapter<ComentAdapter.CommentViewHolder>() {

    // List to hold the comments
    private val comments: MutableList<Comment> = mutableListOf()
    // List to hold the keys of the comments
    private val commentKeys: MutableList<String> = mutableListOf()

    // Inflates the item layout and creates the ViewHolder object
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_coment_adapter, parent, false)
        return CommentViewHolder(view)
    }

    // Binds the data to the ViewHolder
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment, commentKeys[position], currentUserEmail, onDeleteClickListener, onEditClickListener)
    }

    // Returns the total number of comments
    override fun getItemCount(): Int {
        return comments.size
    }

    // Updates the list of comments and notifies the adapter to refresh the view
    fun updateComments(newComments: List<Comment>, newCommentKeys: List<String>) {
        comments.clear()
        comments.addAll(newComments)
        commentKeys.clear()
        commentKeys.addAll(newCommentKeys)
        notifyDataSetChanged()
    }

    // ViewHolder class that holds the view for each comment item
    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userTextView: TextView = itemView.findViewById(R.id.commentUserTextView) // TextView to display the user email
        private val contentTextView: TextView = itemView.findViewById(R.id.commentContentTextView) // TextView to display the comment content
        private val timestampTextView: TextView = itemView.findViewById(R.id.commentTimestampTextView) // TextView to display the timestamp
        private val deleteButton: Button = itemView.findViewById(R.id.commentDeleteButton) // Button to delete the comment
        private val editButton: Button = itemView.findViewById(R.id.commentEditButton) // Button to edit the comment
        private val profileImageView: ImageView = itemView.findViewById(R.id.commentProfileImageView) // ImageView to display the profile picture

        // Binds the data to the views
        fun bind(
            comment: Comment, // The comment object
            commentKey: String, // The key of the comment in the database
            currentUserEmail: String, // Email of the current user
            onDeleteClickListener: (String) -> Unit, // Lambda function to handle comment deletion
            onEditClickListener: (Comment, String) -> Unit // Lambda function to handle comment editing
        ) {
            userTextView.text = comment.user // Set the user email
            contentTextView.text = comment.content // Set the comment content
            timestampTextView.text = android.text.format.DateFormat.format("dd-MM-yyyy hh:mm:ss", comment.timestamp) // Format and set the timestamp

            // Generate and set the profile image with the user's initial
            val initial = comment.user.first().toString().uppercase()
            profileImageView.setImageDrawable(DrawableUtil.getTextDrawableWithColor(initial, 40))

            // Show edit and delete buttons only for comments made by the current user
            if (comment.user == currentUserEmail) {
                deleteButton.visibility = View.VISIBLE
                editButton.visibility = View.VISIBLE
                deleteButton.setOnClickListener {
                    onDeleteClickListener(commentKey) // Call the delete function with the comment key
                }
                editButton.setOnClickListener {
                    onEditClickListener(comment, commentKey) // Call the edit function with the comment and its key
                }
            } else {
                // Hide the edit and delete buttons for comments made by other users
                deleteButton.visibility = View.GONE
                editButton.visibility = View.GONE
            }
        }
    }
}
