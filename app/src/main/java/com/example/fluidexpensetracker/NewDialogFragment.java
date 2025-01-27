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

public class NewDialogFragment extends DialogFragment {

    public interface NewDialogListener<T> {
        void onItemAdded(T item);
    }

    private NewDialogListener listener;
    private CategorySharedViewModel viewModel;
    private Category selectedCategory;
    JSONObject requestBody;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NewDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString() + " must implement NewDialogListener");
        } catch (NullPointerException e) {
            Log.e(TAG, "Parent Fragment is null: " + e.getMessage());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog;
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view;
        switch (getArguments().getString("CategoryType")) {
            case "Category":
                view = inflater.inflate(R.layout.dialog_new_category, null);
                dialog = buildCategoryDialog(view);
                break;
            default:
                view = inflater.inflate(R.layout.dialog_new_expense, null);
                viewModel = new ViewModelProvider(requireActivity()).get(CategorySharedViewModel.class);
                dialog = buildExpenseDialog(view);
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        switch (getArguments().getString("CategoryType")) {
            case "Expense":
                Spinner categorySpinner = requireDialog().findViewById(R.id.etCategory);

                viewModel.setCategoryType(getArguments().getString("CategoryType"));
                viewModel.getFilteredCategoryList().observe(this, categories -> {
                    if (categories != null) {
                        List<String> categoryNames = new ArrayList<>();
                        List<Category> categoryList = new ArrayList<>(categories);

                        for (Category category : categories) {
                            categoryNames.add(category.getName());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                categoryNames
                        );
                        categorySpinner.setAdapter(adapter);

                        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
                break;
            default:
                break;
        }
    }

    private void uploadToCloud(String urlModel, Object item) {
        String url = getString(R.string.base_url) + "/create/" + urlModel;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    // Handle successful response
                    Log.d(TAG, "POST request successful: " + response.toString());
//                    Toast.makeText(getActivity(), "Expense added successfully", Toast.LENGTH_SHORT).show();
                    listener.onItemAdded(item); // Notify MainActivity after successful post
                }, error -> {
            // Handle error
            Log.e(TAG, "Volley Error: " + error.getMessage());
            Toast.makeText(getActivity(), "Error adding expense", Toast.LENGTH_SHORT).show();
        });

        System.out.println(requestBody);
        queue.add(jsonObjectRequest);
    }

    public Dialog buildExpenseDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        Expense expense = new Expense(0, date, amount, category, description);
                        try {
                            requestBody.put("ExpenseDate", expense.getDate());
                            requestBody.put("Amount", expense.getAmount());
                            requestBody.put("CategoryID", selectedCategory.getId());
                            requestBody.put("ExpenseDescription", expense.getDescription());
                            requestBody.put("UserID", Util.getAppUser().getId());
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Exception: " + e.getMessage());
                            Toast.makeText(getActivity(), "Error creating JSON", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        uploadToCloud("expense", expense);

                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog
                });
        return builder.create();
    }

    public Dialog buildCategoryDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        EditText etCategory = view.findViewById(R.id.etCategory);

        builder.setView(view)
                .setTitle("Add New Category")
                .setPositiveButton("Add", (dialog, id) -> {
                    String category = etCategory.getText().toString();

                    if(category.isEmpty()){
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        Category newCategory = new Category(0, category, getArguments().getString("CategoryType"));
                        try {
                            requestBody.put("CategoryName", newCategory.getName());
                            requestBody.put("CategoryType", newCategory.getType());
                            requestBody.put("UserID", Util.getAppUser().getId());
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Exception: " + e.getMessage());
                            Toast.makeText(getActivity(), "Error creating JSON", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        uploadToCloud("category", newCategory);

                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog
                });

        return builder.create();
    }

}