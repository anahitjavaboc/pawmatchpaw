package com.anahit.pawmatch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.anahit.pawmatch.R;
import com.anahit.pawmatch.models.ChatRoom;
import com.bumptech.glide.Glide;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {

    private List<ChatRoom> chatRoomList;
    private OnChatRoomClickListener listener;

    public interface OnChatRoomClickListener {
        void onChatRoomClick(ChatRoom chatRoom);
    }

    public ChatRoomAdapter(List<ChatRoom> chatRoomList, OnChatRoomClickListener listener) {
        this.chatRoomList = chatRoomList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false);
        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        ChatRoom chatRoom = chatRoomList.get(position);
        holder.petNameTextView.setText(chatRoom.getPetName());
        holder.ownerNameTextView.setText(chatRoom.getOtherUserName());
        if (chatRoom.getPetImageUrl() != null && !chatRoom.getPetImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(chatRoom.getPetImageUrl())
                    .placeholder(R.drawable.ic_pet_placeholder)
                    .into(holder.petImageView);
        } else {
            holder.petImageView.setImageResource(R.drawable.ic_pet_placeholder);
        }
        holder.itemView.setOnClickListener(v -> listener.onChatRoomClick(chatRoom));
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    static class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        ImageView petImageView;
        TextView petNameTextView;
        TextView ownerNameTextView;

        ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            petImageView = itemView.findViewById(R.id.chat_room_pet_image);
            petNameTextView = itemView.findViewById(R.id.chat_room_pet_name);
            ownerNameTextView = itemView.findViewById(R.id.chat_room_owner_name);
        }
    }
}