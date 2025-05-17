package com.anahit.pawmatch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anahit.pawmatch.R;
import com.anahit.pawmatch.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageText.setText(message.getContent());

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        boolean isSentByCurrentUser = currentUserId != null && message.getSenderId().equals(currentUserId);
        holder.messageText.setBackgroundResource(isSentByCurrentUser ?
                R.drawable.bg_message_sent : R.drawable.bg_message_received);
        holder.messageText.setGravity(isSentByCurrentUser ? android.view.Gravity.END : android.view.Gravity.START);

        // Set contentDescription for accessibility
        String description = isSentByCurrentUser
                ? "Sent message: " + message.getContent()
                : "Received message: " + message.getContent();
        holder.messageText.setContentDescription(description);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }
}