package com.example.fluidexpensetracker.ui.fragment;

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

import com.example.fluidexpensetracker.ui.util.SwipeToDeleteCallback;
import com.example.fluidexpensetracker.databinding.FragmentListBinding;
import com.example.fluidexpensetracker.data.model.Category;
import com.example.fluidexpensetracker.data.model.Expense;
import com.example.fluidexpensetracker.data.adapter.CategoryAdapter;
import com.example.fluidexpensetracker.data.adapter.ExpenseAdapter;
import com.example.fluidexpensetracker.data.viewmodel.SharedViewModel;
import com.example.fluidexpensetracker.util.FetchCallback;
import com.example.fluidexpensetracker.util.GenericAdapter;
import com.example.fluidexpensetracker.util.Util;

import java.util.List;

public class FragmentList extends Fragment implements NewDialogFragment.NewDialogListener {
    private FragmentListBinding binding;
    private GenericAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        switch (Util.ACTIVE_MENU) {
            case CATEGORY:
                viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                break;
            default:
                viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
                break;
        }

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.fetchItems(getContext(), Util.ACTIVE_MODEL, Util.ACTIVE_MENU, new FetchCallback() {
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

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewDialogFragment dialog = new NewDialogFragment();
                dialog.show(getChildFragmentManager(), "NewDialog");
            }
        });
        System.out.println("Here in Expense");

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        switch (Util.ACTIVE_MENU) {
            case CATEGORY:
                adapter = new CategoryAdapter();
                recyclerView.setAdapter((CategoryAdapter) adapter);

                viewModel.setCategoryType(Util.ACTIVE_CATEGORY.getValue());
                viewModel.getFilteredItemList().observe(getViewLifecycleOwner(), items -> {
                    if (items != null) {
                        adapter.setList((List<Category>) items);
                        adapter.notifyAdapterDataSetChanged();
                    }
                });
                break;
            default:
                adapter = new ExpenseAdapter();
                recyclerView.setAdapter((ExpenseAdapter) adapter);
                viewModel.getItemList().observe(getViewLifecycleOwner(), items -> {
                    if (items != null) {
                        adapter.setList((List<Expense>) items);
                        adapter.notifyAdapterDataSetChanged();
                    }
                });

                viewModel.fetchItems(getContext(), Util.ACTIVE_MODEL, Util.ACTIVE_MENU, new FetchCallback() {
                    @Override
                    public void onFetched() {
                    }

                    @Override
                    public void onFetchFailed() {
                    }
                });
                break;
        }
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, getContext()));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return root;
    }

    @Override
    public void onItemAdded() {
        viewModel.fetchItems(requireContext(), Util.ACTIVE_MODEL, Util.ACTIVE_MENU, new FetchCallback() {
            @Override
            public void onFetched() {
            }

            @Override
            public void onFetchFailed() {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}