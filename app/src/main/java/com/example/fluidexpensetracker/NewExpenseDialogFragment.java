package com.example.fluidexpensetracker;

import static com.android.volley.VolleyLog.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fluidexpensetracker.model.Category;
import com.example.fluidexpensetracker.model.Expense;
import com.example.fluidexpensetracker.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewExpenseDialogFragment extends DialogFragment {

    public interface NewExpenseDialogListener {
        void onExpenseAdded(Expense expense);
    }

    private NewExpenseDialogListener listener;
    private CategorySharedViewModel viewModel;
    private Category selectedCategory;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NewExpenseDialogListener) getParentFragment(); // Correct: Get parent fragment
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString() + " must implement NewExpenseDialogListener");
        } catch(NullPointerException e){
            Log.e(TAG, "Parent Fragment is null: " + e.getMessage());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_expense, null);

        viewModel = new ViewModelProvider(requireActivity()).get(CategorySharedViewModel.class);

        EditText etDate = view.findViewById(R.id.etDate);
        EditText etAmount = view.findViewById(R.id.etAmount);
        Spinner categorySpinner = view.findViewById(R.id.etCategory); // Replace EditText with Spinner
        EditText etDescription = view.findViewById(R.id.etDescription);

        builder.setView(view)
                .setTitle("Add New Expense")
                .setPositiveButton("Add", (dialog, id) -> {
                    String date = etDate.getText().toString();
                    String amountStr = etAmount.getText().toString();
                    String category = categorySpinner.getSelectedItem().toString(); // Get selected category name
                    String description = etDescription.getText().toString();

                    if (date.isEmpty() || amountStr.isEmpty() || category.isEmpty() || description.isEmpty()) {
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double amount = Double.parseDouble(amountStr);
                        Expense newExpense = new Expense(0, date, amount, category, description);
                        sendPostRequest(newExpense);

                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Spinner categorySpinner = requireDialog().findViewById(R.id.etCategory);

        viewModel.setCategoryType(getArguments().getString("CategoryType"));
        // Observe the LiveData for categories
        viewModel.getFilteredCategoryList().observe(this, categories -> {
            if (categories != null) {
                // Create a list to hold the category names
                List<String> categoryNames = new ArrayList<>();

                // Keep a list of the categories to fetch the ID later
                List<Category> categoryList = new ArrayList<>(categories);

                for (Category category : categories) {
                    categoryNames.add(category.getName());
                }

                // Create an adapter for the spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        categoryNames
                );
                categorySpinner.setAdapter(adapter);

                // Set a listener to track the selected category
                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Retrieve the selected category's ID
                        selectedCategory = categoryList.get(position);
                        Log.d(TAG, "Selected Category ID: " + selectedCategory.getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Handle no selection
                    }
                });
            }
        });
    }

    private void sendPostRequest(Expense expense) {
        String url = getString(R.string.base_url) + "/create/expense"; // Replace with your POST API endpoint

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ExpenseDate", expense.getDate());
            jsonObject.put("Amount", expense.getAmount());
            jsonObject.put("CategoryID", selectedCategory.getId());
            jsonObject.put("ExpenseDescription", expense.getDescription());
            jsonObject.put("UserID", Util.getAppUser().getId());
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception: " + e.getMessage());
            Toast.makeText(getActivity(), "Error creating JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    // Handle successful response
                    Log.d(TAG, "POST request successful: " + response.toString());
//                    Toast.makeText(getActivity(), "Expense added successfully", Toast.LENGTH_SHORT).show();
                    listener.onExpenseAdded(expense); // Notify MainActivity after successful post
                }, error -> {
            // Handle error
            Log.e(TAG, "Volley Error: " + error.getMessage());
            Toast.makeText(getActivity(), "Error adding expense", Toast.LENGTH_SHORT).show();
        });

        System.out.println(jsonObject);
        queue.add(jsonObjectRequest);
    }
}