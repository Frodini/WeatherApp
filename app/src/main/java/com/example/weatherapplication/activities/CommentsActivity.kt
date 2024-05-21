package com.example.weatherapplication.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.adapters.ComentAdapter
import com.example.weatherapplication.beans.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CommentsActivity : AppCompatActivity() {

    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentEditText: EditText
    private lateinit var postButton: Button
    private lateinit var commentAdapter: ComentAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var cityName: String
    private lateinit var currentUserEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        cityName = intent.getStringExtra("city_name") ?: "Unknown City"

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)
        commentEditText = findViewById(R.id.commentEditText)
        postButton = findViewById(R.id.postButton)

        auth = FirebaseAuth.getInstance()
        currentUserEmail = auth.currentUser?.email ?: "Anonymous"
        database = FirebaseDatabase.getInstance("https://weatherappfirebasedata-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("comments")
            .child(cityName)

        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = ComentAdapter(this, currentUserEmail) { commentKey -> deleteComment(commentKey) }
        commentsRecyclerView.adapter = commentAdapter

        postButton.setOnClickListener {
            postComment()
        }

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

    private fun postComment() {
        val commentText = commentEditText.text.toString().trim()
        if (commentText.isNotEmpty()) {
            val user = auth.currentUser
            if (user != null) {
                val comment = Comment(
                    user = user.email ?: "Anonymous",
                    content = commentText,
                    timestamp = System.currentTimeMillis()
                )
                Log.d("CommentsActivity", "Posting comment: $comment")
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
                commentAdapter.updateComments(comments, commentKeys)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CommentsActivity, "Failed to load comments: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteComment(commentKey: String) {
        database.child(commentKey).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Comment deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to delete comment: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
