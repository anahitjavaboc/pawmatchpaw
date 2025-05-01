package com.anahit.pawmatch;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.anahit.pawmatch.databinding.CardBinding;
import java.util.List;

public class card_adapter extends RecyclerView.Adapter<card_adapter.myViewHolder> {
    List<card> cardList;
    public card_adapter(List<card> cardsList) {
        this.cardList = cardsList;
    }

    @NonNull
    @Override
    public card_adapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li=(LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CardBinding binding= CardBinding.inflate(li);
        return new myViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull card_adapter.myViewHolder holder, int position) {
        card cardItem=cardList.get(position);
        holder.binding.content.setText(cardItem.getContent());
        holder.binding.image.setImageDrawable(cardItem.getImage());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        CardBinding binding;
        public myViewHolder(@NonNull CardBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
