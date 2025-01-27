package com.example.fluidexpensetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fluidexpensetracker.databinding.FragmentCategoryBinding;
import com.example.fluidexpensetracker.model.Category;
import com.example.fluidexpensetracker.util.FetchCallback;

public class FragmentCategory extends Fragment implements NewCategoryDialogFragment.NewCategoryDialogListener {
    private FragmentCategoryBinding binding;
    private CategoryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CategorySharedViewModel viewModel;
    private String categoryType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewCategoryDialogFragment dialog = new NewCategoryDialogFragment();
                dialog.setArguments(getArguments());
                dialog.show(getChildFragmentManager(), "NewCategoryDialog");
            }
        });

        System.out.println("Here in Category");
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryAdapter();
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, getContext(), "category"));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        viewModel = new ViewModelProvider(requireActivity()).get(CategorySharedViewModel.class);

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.fetchCategories(requireContext(), new FetchCallback() {
                @Override
                public void onFetched() {
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFetchFailed() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        });

        categoryType = getArguments().getString("CategoryType");
        System.out.println(categoryType);
        viewModel.setCategoryType(categoryType);

        // Observe the LiveData
        viewModel.getFilteredCategoryList().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                adapter.setList(categories);
                adapter.notifyDataSetChanged();
            }
        });

        return root;
    }

    @Override
    public void onCategoryAdded(Category category) {
        viewModel.fetchCategories(requireContext(), new FetchCallback() {
            @Override
            public void onFetched() {
            }

            @Override
            public void onFetchFailed() {
            }
        });
//        adapter.getList().add(category);
//        adapter.notifyItemInserted(adapter.getList().size() - 1); // Notify adapter of new item
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}