package com.anahit.pawmatch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.anahit.pawmatch.R;
import com.anahit.pawmatch.models.Match;
import com.bumptech.glide.Glide;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<Match> matchList;
    private OnMatchClickListener listener;

    public interface OnMatchClickListener {
        void onMatchClick(Match match);
    }

    public MatchAdapter(List<Match> matchList, OnMatchClickListener listener) {
        this.matchList = matchList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matchList.get(position);
        holder.petNameTextView.setText(match.getPetName());
        holder.ownerNameTextView.setText(match.getOwnerName());
        // Load pet image (you'll need to fetch this in MessagesFragment)
        if (match.getPetImageUrl() != null && !match.getPetImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(match.getPetImageUrl())
                    .placeholder(R.drawable.ic_pet_placeholder)
                    .into(holder.petImageView);
        } else {
            holder.petImageView.setImageResource(R.drawable.ic_pet_placeholder);
        }
        holder.itemView.setOnClickListener(v -> listener.onMatchClick(match));
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        ImageView petImageView;
        TextView petNameTextView;
        TextView ownerNameTextView;

        MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            petImageView = itemView.findViewById(R.id.match_pet_image);
            petNameTextView = itemView.findViewById(R.id.match_pet_name);
            ownerNameTextView = itemView.findViewById(R.id.match_owner_name);
        }
    }
}