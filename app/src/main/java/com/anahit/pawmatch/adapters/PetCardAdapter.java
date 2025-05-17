package com.anahit.pawmatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.anahit.pawmatch.R;
import com.anahit.pawmatch.models.Pet;
import com.bumptech.glide.Glide;

import java.util.List;

public class PetCardAdapter extends ArrayAdapter<Pet> {

    public PetCardAdapter(@NonNull Context context, @NonNull List<Pet> pets) {
        super(context, 0, pets);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_pet_card, parent, false);
        }

        Pet pet = getItem(position);
        if (pet != null) {
            ImageView petImageView = convertView.findViewById(R.id.pet_image_view);
            TextView nameTextView = convertView.findViewById(R.id.pet_name_text_view);
            TextView infoTextView = convertView.findViewById(R.id.pet_info_text_view);
            TextView descriptionTextView = convertView.findViewById(R.id.pet_description_text_view);

            if (pet.getImageUrl() != null && !pet.getImageUrl().isEmpty()) {
                Glide.with(getContext())
                        .load(pet.getImageUrl())
                        .placeholder(R.drawable.ic_pet_placeholder)
                        .into(petImageView);
            } else {
                petImageView.setImageResource(R.drawable.ic_pet_placeholder);
            }

            nameTextView.setText(pet.getName());
            infoTextView.setText(pet.getAge() + " years, " + pet.getBreed());
            descriptionTextView.setText(pet.getBio() != null ? pet.getBio() : "No bio available");
        }

        return convertView;
    }
}