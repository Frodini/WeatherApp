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
import com.example.weatherapplication.adapters.ChatAdapter
import com.example.weatherapplication.beans.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    // Declare UI elements
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatAdapter: ChatAdapter

    // Declare Firebase references
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var presenceReference: DatabaseReference

    // Variables to store city name, current user email, and user presence key
    private lateinit var cityName: String
    private lateinit var currentUserEmail: String
    private var userPresenceKey: String? = null

    // List to keep track of users currently present in the chat
    private var usersPresent: MutableList<String> = mutableListOf()

    // Declare listeners for chat and presence
    private lateinit var chatListener: ChildEventListener
    private lateinit var presenceListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Retrieve city name from intent extras
        cityName = intent.getStringExtra("city_name") ?: "Unknown City"

        // Initialize UI elements
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        currentUserEmail = auth.currentUser?.email ?: "Anonymous"

        // Initialize Firebase Database references for chat and presence
        database = FirebaseDatabase.getInstance("https://weatherappfirebasedata-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("chats")
            .child(cityName)
        presenceReference = FirebaseDatabase.getInstance("https://weatherappfirebasedata-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("presence")
            .child(cityName)

        // Set up RecyclerView with a LinearLayoutManager and ChatAdapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(this, currentUserEmail)
        chatRecyclerView.adapter = chatAdapter

        // Set up send button click listener to send a new message
        sendButton.setOnClickListener {
            sendMessage()
        }

        // Authenticate the user if not already authenticated
        if (auth.currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addUserPresence()
                    setupRealtimeChat()
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            addUserPresence()
            setupRealtimeChat()
        }
    }

    // Method to send a new message
    private fun sendMessage() {
        val messageText = messageEditText.text.toString().trim()
        if (messageText.isNotEmpty()) {
            // Create a new ChatMessage object
            val message = ChatMessage(
                user = currentUserEmail,
                content = messageText,
                timestamp = System.currentTimeMillis(),
                usersPresent = usersPresent
            )
            Log.d("ChatActivity", "Sending message: $message")
            // Push the message to the database
            database.push().setValue(message).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
                    messageEditText.text.clear()
                } else {
                    Log.e("ChatActivity", "Failed to send message", task.exception)
                    Toast.makeText(this, "Failed to send message: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to add the current user's presence to the presence reference
    private fun addUserPresence() {
        userPresenceKey = presenceReference.push().key
        if (userPresenceKey != null) {
            presenceReference.child(userPresenceKey!!).setValue(currentUserEmail)
            presenceReference.child(userPresenceKey!!).onDisconnect().removeValue()
        }
    }

    // Method to set up real-time chat listeners for presence and messages
    private fun setupRealtimeChat() {
        // Listener to track presence of users in the chat
        presenceListener = presenceReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val email = snapshot.getValue(String::class.java)
                if (email != null) {
                    usersPresent.add(email)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val email = snapshot.getValue(String::class.java)
                if (email != null) {
                    usersPresent.remove(email)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatActivity", "Failed to load presence: ${error.message}")
            }
        })

        // Listener to load chat messages in real-time
        chatListener = database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                if (message != null && message.usersPresent.contains(currentUserEmail)) {
                    chatAdapter.addMessage(message)
                    chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                    Log.d("ChatActivity", "Loaded message: $message")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Failed to load messages: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Clean up listeners and remove presence on activity destroy
    override fun onDestroy() {
        super.onDestroy()
        presenceReference.removeEventListener(presenceListener)
        database.removeEventListener(chatListener)
        if (userPresenceKey != null) {
            presenceReference.child(userPresenceKey!!).removeValue()
        }
    }
}
