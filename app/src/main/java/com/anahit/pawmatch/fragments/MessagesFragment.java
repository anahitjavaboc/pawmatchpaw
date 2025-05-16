package com.anahit.pawmatch.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.anahit.pawmatch.ChatActivity;
import com.anahit.pawmatch.R;
import com.anahit.pawmatch.adapters.ChatRoomAdapter;
import com.anahit.pawmatch.models.ChatRoom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatRoomAdapter adapter;
    private List<ChatRoom> chatRoomList = new ArrayList<>();
    private DatabaseReference chatRoomsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        recyclerView = view.findViewById(R.id.chat_room_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatRoomAdapter(chatRoomList, chatRoom -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("chatRoomId", chatRoom.getId()); // Use getId() after adding it to ChatRoom
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        chatRoomsRef = FirebaseDatabase.getInstance().getReference("chatRooms");
        loadChatRooms();

        return view;
    }

    private void loadChatRooms() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatRoomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                chatRoomList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    ChatRoom chatRoom = data.getValue(ChatRoom.class);
                    if (chatRoom != null && (chatRoom.getUser1Id().equals(currentUserId) || chatRoom.getUser2Id().equals(currentUserId))) {
                        chatRoomList.add(chatRoom);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error (e.g., log or show a toast)
            }
        });
    }
}