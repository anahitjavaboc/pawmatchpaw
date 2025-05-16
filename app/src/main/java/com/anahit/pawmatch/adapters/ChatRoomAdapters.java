package com.anahit.pawmatch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.anahit.pawmatch.R;
import com.anahit.pawmatch.models.ChatRoom;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {
    private List<ChatRoom> chatRooms;
    private OnChatRoomClickListener listener;

    public interface OnChatRoomClickListener {
        void onChatRoomClick(ChatRoom chatRoom);
    }

    public ChatRoomAdapter(List<ChatRoom> chatRooms, OnChatRoomClickListener listener) {
        this.chatRooms = chatRooms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatRoom chatRoom = chatRooms.get(position);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";
        String displayName = chatRoom.getUser1Id().equals(currentUserId)
                ? chatRoom.getUser2Id()
                : chatRoom.getUser1Id();
        holder.chatRoomName.setText("Chat with " + displayName);
        holder.lastMessage.setText(chatRoom.getLastMessage() != null ? chatRoom.getLastMessage() : "");
        holder.itemView.setOnClickListener(v -> listener.onChatRoomClick(chatRoom));
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView chatRoomName, lastMessage;

        ViewHolder(View itemView) {
            super(itemView);
            chatRoomName = itemView.findViewById(R.id.chat_room_name);
            lastMessage = itemView.findViewById(R.id.last_message);
        }
    }
}