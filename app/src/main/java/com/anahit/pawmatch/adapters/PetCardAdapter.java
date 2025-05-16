package com.anahit.pawmatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.anahit.pawmatch.databinding.ItemPetCardBinding;
import com.anahit.pawmatch.models.Pet;
import com.bumptech.glide.Glide;
import java.util.List;

public class PetCardAdapter extends RecyclerView.Adapter<PetCardAdapter.MyViewHolder> {
    private final List<Pet> petList; // Marked final to address warning
    private final Context context;   // Marked final to address warning

    public PetCardAdapter(Context context, List<Pet> petList) {
        this.context = context;
        this.petList = petList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemPetCardBinding binding = ItemPetCardBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.binding.petName.setText(pet.getName());
        Glide.with(context).load(pet.getImageUrl()).into(holder.binding.petImage);
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ItemPetCardBinding binding;

        public MyViewHolder(@NonNull ItemPetCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}