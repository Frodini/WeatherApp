package com.example.weatherapplication.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.adapters.ComentAdapter
import com.example.weatherapplication.beans.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CommentsActivity : AppCompatActivity() {

    // Declare UI elements
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentEditText: EditText
    private lateinit var postButton: Button
    private lateinit var commentAdapter: ComentAdapter

    // Declare Firebase references
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    // Variables to store city name and current user email
    private lateinit var cityName: String
    private lateinit var currentUserEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        // Retrieve city name from intent extras
        cityName = intent.getStringExtra("city_name") ?: "Unknown City"

        // Initialize UI elements
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)
        commentEditText = findViewById(R.id.commentEditText)
        postButton = findViewById(R.id.postButton)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        currentUserEmail = auth.currentUser?.email ?: "Anonymous"

        // Initialize Firebase Database reference for comments
        database = FirebaseDatabase.getInstance("https://weatherappfirebasedata-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("comments")
            .child(cityName)

        // Set up RecyclerView with a LinearLayoutManager and CommentAdapter
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = ComentAdapter(this, currentUserEmail, { commentKey -> deleteComment(commentKey) }, { comment, commentKey -> editComment(comment, commentKey) })
        commentsRecyclerView.adapter = commentAdapter

        // Set up post button click listener to post a new comment
        postButton.setOnClickListener {
            postComment()
        }

        // Authenticate the user if not already authenticated
        if (auth.currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loadComments()
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            loadComments()
        }
    }

    // Method to post a new comment
    private fun postComment() {
        val commentText = commentEditText.text.toString().trim()
        if (commentText.isNotEmpty()) {
            val user = auth.currentUser
            if (user != null) {
                // Create a new Comment object
                val comment = Comment(
                    user = user.email ?: "Anonymous",
                    content = commentText,
                    timestamp = System.currentTimeMillis()
                )
                Log.d("CommentsActivity", "Posting comment: $comment")
                // Push the comment to the database
                database.push().setValue(comment).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Comment posted", Toast.LENGTH_SHORT).show()
                        commentEditText.text.clear()
                    } else {
                        Log.e("CommentsActivity", "Failed to post comment", task.exception)
                        Toast.makeText(this, "Failed to post comment: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to load comments from the database
    private fun loadComments() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val comments = mutableListOf<Comment>()
                val commentKeys = mutableListOf<String>()
                for (commentSnapshot in snapshot.children) {
                    val comment = commentSnapshot.getValue(Comment::class.java)
                    if (comment != null) {
                        comments.add(comment)
                        commentKeys.add(commentSnapshot.key ?: "")
                        Log.d("CommentsActivity", "Loaded comment: $comment")
                    }
                }
                // Update the adapter with the loaded comments and keys
                commentAdapter.updateComments(comments, commentKeys)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CommentsActivity, "Failed to load comments: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Method to delete a comment
    private fun deleteComment(commentKey: String) {
        database.child(commentKey).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Comment deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to delete comment: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Method to edit a comment
    private fun editComment(comment: Comment, commentKey: String) {
        val editText = EditText(this)
        editText.setText(comment.content)
        // Create an AlertDialog to edit the comment
        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Comment")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newContent = editText.text.toString().trim()
                if (newContent.isNotEmpty()) {
                    // Create an updated Comment object
                    val updatedComment = comment.copy(content = newContent, timestamp = System.currentTimeMillis())
                    // Update the comment in the database
                    database.child(commentKey).setValue(updatedComment).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Comment updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to update comment: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }
}
