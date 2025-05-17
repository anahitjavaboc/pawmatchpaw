package com.anahit.pawmatch;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.anahit.pawmatch.adapters.MessageAdapter;
import com.anahit.pawmatch.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText messageInput;
    private Button sendButton;
    private ProgressBar loadingProgress;
    private MessageAdapter messageAdapter;
    private List<Message> messages = new ArrayList<>();
    private DatabaseReference chatRef;
    private String chatId;
    private String currentUserId;
    private String otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize views with null check
        recyclerView = findViewById(R.id.recyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        loadingProgress = findViewById(R.id.loadingProgress);

        if (recyclerView == null || messageInput == null || sendButton == null || loadingProgress == null) {
            Toast.makeText(this, "Error: Layout components not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(messages);
        recyclerView.setAdapter(messageAdapter);

        // Authentication and user setup
        currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (currentUserId == null) {
            Toast.makeText(this, "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        otherUserId = getIntent().getStringExtra("otherUserId");
        if (otherUserId == null) {
            Toast.makeText(this, "Error: Other user ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Generate unique chat ID
        chatId = currentUserId.compareTo(otherUserId) < 0
                ? currentUserId + "_" + otherUserId
                : otherUserId + "_" + currentUserId;

        // Initialize Firebase Database reference
        chatRef = FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(chatId)
                .child("messages");

        // Load messages
        loadMessages();

        // Set up send button
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        if (loadingProgress == null) return; // Safeguard
        showLoading(true);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (message != null) {
                        messages.add(message);
                    } else {
                        Toast.makeText(ChatActivity.this, "Invalid message data detected", Toast.LENGTH_SHORT).show();
                    }
                }
                messageAdapter.updateMessages(messages); // Use updateMessages method
                recyclerView.scrollToPosition(messages.size() - 1);
                showLoading(false);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Failed to load messages: " + error.getMessage() +
                        ". Tap to retry.", Toast.LENGTH_LONG).show();
                showLoading(false);
                // Retry logic
                chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        loadMessages(); // Retry loading
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ChatActivity.this, "Retry failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void sendMessage() {
        String content = messageInput.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == null || loadingProgress == null) {
            Toast.makeText(this, "Error: Invalid state", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        String messageId = chatRef.push().getKey();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date()); // Current time: 2025-05-17 16:28:00
        Message message = new Message(messageId, currentUserId, otherUserId, content, timestamp);

        chatRef.child(messageId).setValue(message)
                .addOnSuccessListener(aVoid -> {
                    messageInput.setText("");
                    messageAdapter.updateMessages(messages); // Update adapter
                    recyclerView.scrollToPosition(messages.size() - 1);
                    showLoading(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send message: " + e.getMessage() +
                            ". Tap to retry.", Toast.LENGTH_LONG).show();
                    showLoading(false);
                    // Retry logic
                    chatRef.child(messageId).setValue(message)
                            .addOnSuccessListener(aVoid -> {
                                messageInput.setText("");
                                messageAdapter.updateMessages(messages);
                                recyclerView.scrollToPosition(messages.size() - 1);
                            })
                            .addOnFailureListener(retryError -> {
                                Toast.makeText(this, "Retry failed: " + retryError.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });
    }

    private void showLoading(boolean isLoading) {
        if (loadingProgress != null) {
            loadingProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            messageInput.setEnabled(!isLoading);
            sendButton.setEnabled(!isLoading);
        } else {
            Toast.makeText(this, "Error: Loading indicator not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove Firebase listener to prevent memory leaks
        chatRef.removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {}
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}