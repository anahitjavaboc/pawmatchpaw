package com.anahit.pawmatch;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private EditText messageInput;
    private Button sendButton;
    private DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.message_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        String chatRoomId = getIntent().getStringExtra("chatRoomId");
        chatRef = FirebaseDatabase.getInstance().getReference("messages").child(chatRoomId);

        sendButton.setOnClickListener(v -> sendMessage());
        loadMessages();
    }

    private void sendMessage() {
        String content = messageInput.getText().toString().trim();
        if (content.isEmpty()) return;

        String messageId = chatRef.push().getKey();
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Message message = new Message(messageId, senderId, content, System.currentTimeMillis());
        chatRef.child(messageId).setValue(message);
        messageInput.setText("");
    }

    private void loadMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    messageList.add(message);
                }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}