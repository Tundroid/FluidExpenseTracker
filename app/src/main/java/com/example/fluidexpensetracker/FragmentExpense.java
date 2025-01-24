package com.example.fluidexpensetracker;

import static com.android.volley.VolleyLog.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fluidexpensetracker.databinding.FragmentExpenseBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentExpense extends Fragment implements NewExpenseDialogFragment.NewExpenseDialogListener {
    private FragmentExpenseBinding binding;
    private ExpenseAdapter adapter;
    private List<Expense> expenses;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentExpenseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        swipeRefreshLayout = getActivity().findViewById(R.id.swipeRefreshLayout); // Initialize SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchExpenses(); // Call fetchExpenses() on refresh
        });

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NewExpenseDialogFragment dialog = new NewExpenseDialogFragment();
//                dialog.show(getSupportFragmentManager(), "NewExpenseDialog");
//            }
//        });


        // Find the RecyclerView
        RecyclerView recyclerView = getActivity().findViewById(R.id.recycler_view);

        // Set layout manager (usually LinearLayoutManager for a vertical list)
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create an ExpenseAdapter with sample data (consider replacing with your data source)
        adapter = new ExpenseAdapter();

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, getContext()));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        fetchExpenses(); // Fetch expenses from the API

//        binding.fabSecond.setOnClickListener(v -> {
//            Toast.makeText(getContext(), "Add button clicked in Second Fragment", Toast.LENGTH_SHORT).show();
//            // Handle adding new items here
//        });
        return root;
    }

    private void fetchExpenses() {
        String url = getString(R.string.base_url) + "/get/expense"; // Replace with your API endpoint

        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        adapter.getExpenseList().clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String date = jsonObject.getString("ExpenseDate");
                            int amount = jsonObject.getInt("Amount");
                            String category = jsonObject.getString("CategoryID");
                            String description = jsonObject.getString("ExpenseDescription");

                            Expense expense = new Expense(date, amount, category, description);
                            onExpenseAdded(expense);
                        }
                        adapter.notifyDataSetChanged(); // Notify adapter after fetching data

                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                    } finally {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, error -> {
            Log.e(TAG, "Volley Error: " + error.getMessage());
            swipeRefreshLayout.setRefreshing(false);
        });

        queue.add(jsonArrayRequest);
    }

    @Override
    public void onExpenseAdded(Expense expense) {
        adapter.getExpenseList().add(expense);
        adapter.notifyItemInserted(adapter.getExpenseList().size() - 1); // Notify adapter of new item
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}