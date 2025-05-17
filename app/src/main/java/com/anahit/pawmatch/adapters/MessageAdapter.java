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
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages != null ? messages : new ArrayList<>();
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
        if (messages == null || position < 0 || position >= messages.size()) return;
        Message message = messages.get(position);
        if (message == null) {
            holder.messageText.setText("Invalid message");
            return;
        }

        holder.messageText.setText(message.getContent() != null ? message.getContent() : "No content");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        boolean isSentByCurrentUser = currentUserId != null && currentUserId.equals(message.getSenderId());
        holder.messageText.setBackgroundResource(isSentByCurrentUser ?
                R.drawable.bg_message_sent : R.drawable.bg_message_received);
        holder.messageText.setGravity(isSentByCurrentUser ? android.view.Gravity.END : android.view.Gravity.START);

        String description = isSentByCurrentUser
                ? "Sent message: " + (message.getContent() != null ? message.getContent() : "No content")
                : "Received message: " + (message.getContent() != null ? message.getContent() : "No content");
        holder.messageText.setContentDescription(description);
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            if (messageText == null) {
                throw new IllegalStateException("TextView with ID messageText not found in item_message layout");
            }
        }
    }

    public void updateMessages(List<Message> newMessages) {
        if (newMessages != null) {
            this.messages.clear();
            this.messages.addAll(newMessages);
            notifyDataSetChanged();
        }
    }
}