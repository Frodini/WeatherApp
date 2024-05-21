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

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var cityName: String
    private lateinit var currentUserEmail: String
    private lateinit var presenceReference: DatabaseReference
    private var userPresenceKey: String? = null
    private var usersPresent: MutableList<String> = mutableListOf()
    private lateinit var chatListener: ChildEventListener
    private lateinit var presenceListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        cityName = intent.getStringExtra("city_name") ?: "Unknown City"

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)

        auth = FirebaseAuth.getInstance()
        currentUserEmail = auth.currentUser?.email ?: "Anonymous"
        database = FirebaseDatabase.getInstance("https://weatherappfirebasedata-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("chats")
            .child(cityName)
        presenceReference = FirebaseDatabase.getInstance("https://weatherappfirebasedata-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("presence")
            .child(cityName)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(this)
        chatRecyclerView.adapter = chatAdapter

        sendButton.setOnClickListener {
            sendMessage()
        }

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

    private fun sendMessage() {
        val messageText = messageEditText.text.toString().trim()
        if (messageText.isNotEmpty()) {
            val message = ChatMessage(
                user = currentUserEmail,
                content = messageText,
                timestamp = System.currentTimeMillis(),
                usersPresent = usersPresent
            )
            Log.d("ChatActivity", "Sending message: $message")
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

    private fun addUserPresence() {
        userPresenceKey = presenceReference.push().key
        if (userPresenceKey != null) {
            presenceReference.child(userPresenceKey!!).setValue(currentUserEmail)
            presenceReference.child(userPresenceKey!!).onDisconnect().removeValue()
        }
    }

    private fun setupRealtimeChat() {
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

    override fun onDestroy() {
        super.onDestroy()
        presenceReference.removeEventListener(presenceListener)
        database.removeEventListener(chatListener)
        if (userPresenceKey != null) {
            presenceReference.child(userPresenceKey!!).removeValue()
        }
    }
}
