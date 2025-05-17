package com.anahit.pawmatch.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.anahit.pawmatch.ChatActivity;
import com.anahit.pawmatch.R;
import com.anahit.pawmatch.adapters.ChatRoomAdapter;
import com.anahit.pawmatch.models.ChatRoom;
import com.anahit.pawmatch.models.Pet;
import com.anahit.pawmatch.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatRoomAdapter adapter;
    private List<ChatRoom> chatRoomList = new ArrayList<>();
    private DatabaseReference matchesRef;
    private DatabaseReference chatRoomsRef;
    private DatabaseReference petsRef;
    private DatabaseReference usersRef;
    private ValueEventListener chatRoomsListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        recyclerView = view.findViewById(R.id.chat_room_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatRoomAdapter(chatRoomList, chatRoom -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("chatRoomId", chatRoom.getId());
            intent.putExtra("otherUserId", chatRoom.getUser1Id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ?
                    chatRoom.getUser2Id() : chatRoom.getUser1Id());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        matchesRef = FirebaseDatabase.getInstance().getReference("matches");
        chatRoomsRef = FirebaseDatabase.getInstance().getReference("chatRooms");
        petsRef = FirebaseDatabase.getInstance().getReference("pets");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadChatRooms();

        return view;
    }

    private void loadChatRooms() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // First, find mutual matches
        matchesRef.orderByChild("userId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot matchSnapshot : snapshot.getChildren()) {
                            String matchId = matchSnapshot.getKey();
                            String petId = matchSnapshot.child("petId").getValue(String.class);
                            String petOwnerId = matchSnapshot.child("petOwnerId").getValue(String.class);

                            // Check for mutual match
                            matchesRef.orderByChild("userId").equalTo(petOwnerId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot mutualSnapshot) {
                                            for (DataSnapshot mutualMatch : mutualSnapshot.getChildren()) {
                                                String matchedPetOwnerId = mutualMatch.child("petOwnerId").getValue(String.class);
                                                if (matchedPetOwnerId != null && matchedPetOwnerId.equals(currentUserId)) {
                                                    // Mutual match found, create or fetch chat room
                                                    createOrFetchChatRoom(currentUserId, petOwnerId, petId);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            if (getContext() != null) {
                                                Toast.makeText(getContext(), "Error checking matches: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Error loading matches: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Listen for chat rooms
        chatRoomsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatRoomList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    ChatRoom chatRoom = data.getValue(ChatRoom.class);
                    if (chatRoom != null && (chatRoom.getUser1Id().equals(currentUserId) || chatRoom.getUser2Id().equals(currentUserId))) {
                        chatRoom.setId(data.getKey());
                        // Fetch pet and user data for display
                        fetchChatRoomData(chatRoom);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load chat rooms: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        chatRoomsRef.addValueEventListener(chatRoomsListener);
    }

    private void createOrFetchChatRoom(String user1Id, String user2Id, String petId) {
        // Generate a unique chat room ID based on user IDs (sorted to ensure consistency)
        String chatRoomId = user1Id.compareTo(user2Id) < 0 ? user1Id + "_" + user2Id : user2Id + "_" + user1Id;

        chatRoomsRef.child(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Create a new chat room
                    ChatRoom chatRoom = new ChatRoom(user1Id, user2Id);
                    chatRoom.setPetId(petId);
                    chatRoomsRef.child(chatRoomId).setValue(chatRoom);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error creating chat room: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchChatRoomData(ChatRoom chatRoom) {
        String otherUserId = chatRoom.getUser1Id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ?
                chatRoom.getUser2Id() : chatRoom.getUser1Id();
        String petId = chatRoom.getPetId();

        // Fetch pet data
        petsRef.child(petId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot petSnapshot) {
                Pet pet = petSnapshot.getValue(Pet.class);
                if (pet != null) {
                    chatRoom.setPetName(pet.getName());
                    chatRoom.setPetImageUrl(pet.getImageUrl());
                    // Fetch user data
                    usersRef.child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                chatRoom.setOtherUserName(user.getName());
                                chatRoomList.add(chatRoom); // Add to list after fetching all data
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (chatRoomsListener != null) {
            chatRoomsRef.removeEventListener(chatRoomsListener);
        }
    }
}