package com.anahit.pawmatch.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.anahit.pawmatch.R;
import com.anahit.pawmatch.models.Pet;
import com.anahit.pawmatch.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private EditText userNameEdit, petNameEdit, petAgeEdit, petGenderEdit;
    private TextView userEmailText;
    private Button saveButton;
    private DatabaseReference database;
    private User user;
    private Pet pet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userNameEdit = view.findViewById(R.id.user_name_edit);
        userEmailText = view.findViewById(R.id.user_email_text);
        petNameEdit = view.findViewById(R.id.pet_name_edit);
        petAgeEdit = view.findViewById(R.id.pet_age_edit);
        petGenderEdit = view.findViewById(R.id.pet_gender_edit);
        saveButton = view.findViewById(R.id.save_button);

        database = FirebaseDatabase.getInstance().getReference();
        loadUserProfile();
        loadPetProfile();

        saveButton.setOnClickListener(v -> saveProfile());

        return view;
    }

    private void loadUserProfile() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user != null) {
                    userNameEdit.setText(user.name);
                    userEmailText.setText(user.email);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void loadPetProfile() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.child("pets").orderByChild("ownerId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            pet = data.getValue(Pet.class);
                            petNameEdit.setText(pet.name);
                            petAgeEdit.setText(String.valueOf(pet.age));
                            petGenderEdit.setText(pet.gender);
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    private void saveProfile() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user.name = userNameEdit.getText().toString();
        pet.name = petNameEdit.getText().toString();
        pet.age = Integer.parseInt(petAgeEdit.getText().toString());
        pet.gender = petGenderEdit.getText().toString();

        database.child("users").child(userId).setValue(user);
        database.child("pets").child(pet.id).setValue(pet)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                });
    }
}