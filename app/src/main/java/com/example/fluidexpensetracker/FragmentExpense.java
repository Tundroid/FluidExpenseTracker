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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fluidexpensetracker.databinding.FragmentExpenseBinding;
import com.example.fluidexpensetracker.model.Expense;
import com.example.fluidexpensetracker.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class FragmentExpense extends Fragment implements NewExpenseDialogFragment.NewExpenseDialogListener {
    private FragmentExpenseBinding binding;
    private ExpenseAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentExpenseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchExpenses();
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewExpenseDialogFragment dialog = new NewExpenseDialogFragment();
                dialog.setArguments(getArguments());
                dialog.show(getChildFragmentManager(), "NewExpenseDialog");
            }
        });
        System.out.println("Here in Expense");

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter();
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, getContext(),"expense"));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        fetchExpenses();

        return root;
    }

    private void fetchExpenses() {
        String url = getString(R.string.base_url) + "/get_expenses";

        Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
        uriBuilder.appendQueryParameter("UserID", String.valueOf(Util.getAppUser().getId()));

        url = uriBuilder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        adapter.getList().clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("ExpenseID");
                            String date = jsonObject.getString("ExpenseDate");
                            int amount = jsonObject.getInt("Amount");
                            String category = jsonObject.getString("CategoryName");
                            String description = jsonObject.getString("ExpenseDescription");

                            Expense expense = new Expense(id, date, amount, category, description);
                            onExpenseAdded(expense);
                        }
                        // Sort expenses by date (desc) and description (asc)
                        adapter.getList().sort((expense1, expense2) -> {
                            // Compare by date in descending order
                            Date date1 = expense1.getDateObject();
                            Date date2 = expense2.getDateObject();
                            int dateComparison = date1 != null && date2 != null ? date2.compareTo(date1) : 0;

                            // If dates are the same, compare by description in ascending order
                            if (dateComparison == 0) {
                                return expense1.getDescription().compareToIgnoreCase(expense2.getDescription());
                            }

                            return dateComparison;
                        });
                        adapter.notifyDataSetChanged();

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
        adapter.getList().add(expense);
        adapter.notifyItemInserted(adapter.getList().size() - 1); // Notify adapter of new item
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}