package com.anahit.pawmatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.anahit.pawmatch.R;

import java.util.ArrayList;
import java.util.List;

public class HealthTipsAdapter extends RecyclerView.Adapter<HealthTipsAdapter.TipViewHolder> {

    private Context context;
    private List<String> tipsList;

    public HealthTipsAdapter(Context context, List<String> tipsList) {
        this.context = context;
        this.tipsList = tipsList != null ? tipsList : new ArrayList<>();
    }

    @NonNull
    @Override
    public TipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_health_tip, parent, false);
        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipViewHolder holder, int position) {
        String tip = tipsList.get(position);
        holder.tipTextView.setText(tip);
    }

    @Override
    public int getItemCount() {
        return tipsList.size();
    }

    public static class TipViewHolder extends RecyclerView.ViewHolder {
        TextView tipTextView;

        public TipViewHolder(@NonNull View itemView) {
            super(itemView);
            tipTextView = itemView.findViewById(R.id.health_tip_text);
        }
    }
}