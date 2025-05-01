package com.anahit.pawmatch;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.anahit.pawmatch.databinding.ActivityMainBinding; // Import the generated binding class
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding; // Use private for encapsulation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ensure the `card` class exists
        List<card> cards = new ArrayList<>();
        cards.add(new card("Balik", ResourcesCompat.getDrawable(getResources(), R.drawable.shun1, null)));
        cards.add(new card("Chay", ResourcesCompat.getDrawable(getResources(), R.drawable.shun2, null)));
        cards.add(new card("Aghasik", ResourcesCompat.getDrawable(getResources(), R.drawable.shun3, null)));
        cards.add(new card("Naxshun", ResourcesCompat.getDrawable(getResources(), R.drawable.shun4, null)));
        cards.add(new card("Gjuk", ResourcesCompat.getDrawable(getResources(), R.drawable.shun5, null)));
        card_adapter adapter = new card_adapter(cards);
        binding.cardStack.setLayoutManager(new CardStackLayoutManager(getApplicationContext()));
        binding.cardStack.setAdapter(adapter);
    }
}
