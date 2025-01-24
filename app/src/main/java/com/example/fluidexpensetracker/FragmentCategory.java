package com.example.fluidexpensetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fluidexpensetracker.databinding.FragmentCategoryBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentCategory extends Fragment {
    private FragmentCategoryBinding binding;
    private ExpenseAdapter adapter;
    private List<Expense> expenses;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_second);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        expenses = new ArrayList<>();
        adapter = new ExpenseAdapter();
        recyclerView.setAdapter(adapter);

        binding.fabSecond.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Add button clicked in Second Fragment", Toast.LENGTH_SHORT).show();
            // Handle adding new items here
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}