package com.moleculesoft.fluidfinanceassistant.ui.fragment;

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

import com.moleculesoft.fluidfinanceassistant.data.adapter.IncomeAdapter;
import com.moleculesoft.fluidfinanceassistant.data.model.Income;
import com.moleculesoft.fluidfinanceassistant.ui.util.SwipeToDeleteCallback;
import com.moleculesoft.fluidfinanceassistant.databinding.FragmentListBinding;
import com.moleculesoft.fluidfinanceassistant.data.model.Category;
import com.moleculesoft.fluidfinanceassistant.data.model.Expense;
import com.moleculesoft.fluidfinanceassistant.data.adapter.CategoryAdapter;
import com.moleculesoft.fluidfinanceassistant.data.adapter.ExpenseAdapter;
import com.moleculesoft.fluidfinanceassistant.data.viewmodel.SharedViewModel;
import com.moleculesoft.fluidfinanceassistant.util.FetchCallback;
import com.moleculesoft.fluidfinanceassistant.util.GenericAdapter;
import com.moleculesoft.fluidfinanceassistant.util.Util;

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
            case INCOME:
                adapter = new IncomeAdapter();
                recyclerView.setAdapter((IncomeAdapter) adapter);
                viewModel.getItemList().observe(getViewLifecycleOwner(), items -> {
                    if (items != null) {
                        adapter.setList((List<Income>) items);
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