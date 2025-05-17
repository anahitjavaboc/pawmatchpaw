package com.anahit.pawmatch.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.anahit.pawmatch.R;
import com.anahit.pawmatch.models.Pet;
import com.anahit.pawmatch.models.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private ImageView petImageView;
    private TextView petNameTextView, petInfoTextView, ownerNameTextView;
    private DatabaseReference petsRef;
    private DatabaseReference usersRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        petImageView = view.findViewById(R.id.profile_pet_image);
        petNameTextView = view.findViewById(R.id.profile_pet_name);
        petInfoTextView = view.findViewById(R.id.profile_pet_info);
        ownerNameTextView = view.findViewById(R.id.profile_owner_name);

        petsRef = FirebaseDatabase.getInstance().getReference("pets");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadProfileData();

        return view;
    }

    private void loadProfileData() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Load user data
        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    ownerNameTextView.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Load pet data
        petsRef.orderByChild("ownerId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                            Pet pet = petSnapshot.getValue(Pet.class);
                            if (pet != null) {
                                petNameTextView.setText(pet.getName());
                                petInfoTextView.setText(pet.getAge() + " years, " + pet.getBreed());
                                if (pet.getImageUrl() != null && !pet.getImageUrl().isEmpty()) {
                                    Glide.with(requireContext())
                                            .load(pet.getImageUrl())
                                            .placeholder(R.drawable.ic_pet_placeholder)
                                            .into(petImageView);
                                } else {
                                    petImageView.setImageResource(R.drawable.ic_pet_placeholder);
                                }
                                break; // Display the first pet
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}