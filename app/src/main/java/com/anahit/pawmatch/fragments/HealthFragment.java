package com.anahit.pawmatch.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.anahit.pawmatch.R;
import com.anahit.pawmatch.adapters.HealthTipsAdapter;
import java.util.ArrayList;
import java.util.List;

public class HealthFragment extends Fragment {

    private RecyclerView recyclerView;
    private HealthTipsAdapter adapter;
    private List<String> healthTipsList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health, container, false);

        recyclerView = view.findViewById(R.id.health_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HealthTipsAdapter(requireContext(), healthTipsList);
        recyclerView.setAdapter(adapter);

        healthTipsList.add("Ensure your pet gets regular vet checkups.");
        healthTipsList.add("Provide a balanced diet suitable for your pet’s age and breed.");
        healthTipsList.add("Keep your pet hydrated with fresh water daily.");
        healthTipsList.add("Regular exercise helps keep your pet healthy and active.");
        adapter.notifyDataSetChanged();

        return view;
    }
}