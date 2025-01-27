package com.example.fluidexpensetracker;

import static com.android.volley.VolleyLog.TAG;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fluidexpensetracker.databinding.FragmentListBinding;
import com.example.fluidexpensetracker.model.Category;
import com.example.fluidexpensetracker.model.Expense;
import com.example.fluidexpensetracker.util.FetchCallback;
import com.example.fluidexpensetracker.util.GenericAdapter;
import com.example.fluidexpensetracker.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
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

        switch (getArguments().getString("Menu")) {
            case "category":
                viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                break;
            default:
                viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
                break;
        }

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.fetchItems(getContext(), getArguments().getString("Menu"), new FetchCallback() {
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
                dialog.setArguments(getArguments());
                dialog.show(getChildFragmentManager(), "NewDialog");
            }
        });
        System.out.println("Here in Expense");

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, getContext(), "expense"));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        switch (getArguments().getString("Menu")) {
            case "category":
                adapter = new CategoryAdapter();
                recyclerView.setAdapter((CategoryAdapter) adapter);

                String categoryType = getArguments().getString("CategoryType");
                System.out.println("My Menu: " + categoryType);
                viewModel.setCategoryType(categoryType);
                System.out.println("All data: " + viewModel.getDataList().size());
                System.out.println("Filtered data: " + ((List<Category>)viewModel.getFilteredItemList().getValue()).size());
                viewModel.getFilteredItemList().observe(getViewLifecycleOwner(), items -> {
                    if (items != null) {
                        adapter.setList((List<Category>)items);
                        adapter.notifyAdapterDataSetChanged();
                    }
                });
                break;
            default:
                adapter = new ExpenseAdapter();
                recyclerView.setAdapter((ExpenseAdapter) adapter);
                viewModel.getItemList().observe(getViewLifecycleOwner(), items -> {
                    if (items != null) {
                        adapter.setList((List<Expense>)items);
                        adapter.notifyAdapterDataSetChanged();
                    }
                });

                System.out.println("Before expense fetched: " + viewModel.getDataList().size());
                viewModel.fetchItems(getContext(), getArguments().getString("Menu"), new FetchCallback() {
                    @Override
                    public void onFetched() {
                        System.out.println("After expense fetched: " + viewModel.getDataList().size());
                    }

                    @Override
                    public void onFetchFailed() {
                        System.out.println("After expense not fetched: " + viewModel.getDataList().size());
                    }
                });
                break;
        }

        return root;
    }

//    public static void fetchItems(String endpoint) {
//        String url = getString(R.string.base_url) + "/" + endpoint;
//
//        Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
//        uriBuilder.appendQueryParameter("UserID", String.valueOf(Util.getAppUser().getId()));
//
//        url = uriBuilder.build().toString();
//
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
//                response -> {
//                    switch (getArguments().getString("CategoryType")) {
//                        case "Category":
//                            processCategoryResponse(response);
//                            break;
//                        default:
//                            processExpenseResponse(response);
//                            break;
//                    }
//                }, error -> {
//            Log.e(TAG, "Volley Error: " + error.getMessage());
//            swipeRefreshLayout.setRefreshing(false);
//        });
//
//        queue.add(jsonArrayRequest);
//    }

    @Override
    public void onItemAdded() {
        viewModel.fetchItems(requireContext(), getArguments().getString("Menu"), new FetchCallback() {
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
//
//    public void processExpenseResponse(JSONArray response) {
//        try {
//            adapter.getList().clear();
//            for (int i = 0; i < response.length(); i++) {
//                JSONObject jsonObject = response.getJSONObject(i);
//                int id = jsonObject.getInt("ExpenseID");
//                String date = jsonObject.getString("ExpenseDate");
//                int amount = jsonObject.getInt("Amount");
//                String category = jsonObject.getString("CategoryName");
//                String description = jsonObject.getString("ExpenseDescription");
//
//                onItemAdded(new Expense(id, date, amount, category, description));
//            }
//            // Sort expenses by date (desc) and description (asc)
//            adapter.getList().sort((expense1, expense2) -> {
//                // Compare by date in descending order
//                Date date1 = ((Expense) expense1).getDateObject();
//                Date date2 = ((Expense) expense2).getDateObject();
//                int dateComparison = date1 != null && date2 != null ? date2.compareTo(date1) : 0;
//
//                // If dates are the same, compare by description in ascending order
//                if (dateComparison == 0) {
//                    return ((Expense) expense1).getDescription().compareToIgnoreCase(((Expense) expense2).getDescription());
//                }
//
//                return dateComparison;
//            });
//        } catch (JSONException e) {
//            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
//            Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
//        } finally {
//            swipeRefreshLayout.setRefreshing(false);
//        }
//    }
//
//    public void processCategoryResponse(JSONArray response) {
//        try {
//            List<Category> fetchedCategories = new ArrayList<>();
//            for (int i = 0; i < response.length(); i++) {
//                JSONObject jsonObject = response.getJSONObject(i);
//                int id = jsonObject.getInt("CategoryID");
//                String name = jsonObject.getString("CategoryName");
//                String type = jsonObject.getString("CategoryType");
//
//                fetchedCategories.add(new Category(id, name, type));
//            }
//            viewModel.setCategories(fetchedCategories);
//        } catch (JSONException e) {
//            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
//            Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
//        } finally {
//            swipeRefreshLayout.setRefreshing(false);
//        }
//    }
//}
}