package com.moleculesoft.fluidfinanceassistant.ui.fragment;

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
import com.moleculesoft.fluidfinanceassistant.R;
import com.moleculesoft.fluidfinanceassistant.data.model.Category;
import com.moleculesoft.fluidfinanceassistant.data.model.Expense;
import com.moleculesoft.fluidfinanceassistant.data.model.Income;
import com.moleculesoft.fluidfinanceassistant.data.viewmodel.SharedViewModel;
import com.moleculesoft.fluidfinanceassistant.databinding.DialogNewCategoryBinding;
import com.moleculesoft.fluidfinanceassistant.databinding.DialogNewExpenseBinding;
import com.moleculesoft.fluidfinanceassistant.util.Menu;
import com.moleculesoft.fluidfinanceassistant.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class NewDialogFragment extends DialogFragment {

    public interface NewDialogListener {
        void onItemAdded();
    }

    private NewDialogListener listener;
    private SharedViewModel viewModel;
    private Category selectedCategory;
    private JSONObject requestBody;
    private DialogNewCategoryBinding catBinding;
    private DialogNewExpenseBinding expBinding;

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
        switch (Util.ACTIVE_MENU) {
            case CATEGORY:
                catBinding = DialogNewCategoryBinding.inflate(getLayoutInflater());
                dialog = buildCategoryDialog();
                break;
            default:
                expBinding = DialogNewExpenseBinding.inflate(getLayoutInflater());
                viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                dialog = buildExpenseDialog();
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        switch (Util.ACTIVE_MENU) {
            case EXPENSE:
            case INCOME:
                Spinner categorySpinner = requireDialog().findViewById(R.id.etCategory);

                viewModel.setCategoryType(Util.ACTIVE_CATEGORY.getValue());
                viewModel.getFilteredItemList().observe(this, categories -> {
                    if (categories != null) {
                        List<String> categoryNames = new ArrayList<>();
                        List<Category> categoryList = (List<Category>) categories;

                        for (Category category : categoryList) {
                            categoryNames.add(category.getCategoryName());
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

    private void uploadToCloud() {
        String url = getString(R.string.base_url) + "/create/" + Util.ACTIVE_MODEL.getValue();

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    Log.d(TAG, "POST request successful: " + response.toString());
                    listener.onItemAdded();
                }, error -> {
            Log.e(TAG, "Volley Error: " + error.getMessage());
            if (error.networkResponse != null) {
                try {
                    // Get the response body as a String
                    String jsonResponse = new String(error.networkResponse.data, "UTF-8");

                    // Log the response for debugging
                    Log.e(TAG, "Volley Response: " + jsonResponse);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "Volley Error: No response from server");
            }
            Toast.makeText(getActivity(), "Error adding expense", Toast.LENGTH_SHORT).show();
        });

        queue.add(jsonObjectRequest);
    }

    public Dialog buildExpenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(expBinding.getRoot())
                .setTitle("Add New Expense")
                .setPositiveButton("Add", (dialog, id) -> {
                    String date = expBinding.etDate.getText().toString();
                    String amountStr = expBinding.etAmount.getText().toString();
                    String category = expBinding.etCategory.getSelectedItem().toString(); // Get selected category name
                    String description = expBinding.etDescription.getText().toString();

                    if (date.isEmpty() || amountStr.isEmpty() || category.isEmpty() || description.isEmpty()) {
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double amount = Double.parseDouble(amountStr);
                        Object newObj = new Object();
                        if (Util.ACTIVE_MENU == Menu.EXPENSE) {
                            Expense expense = new Expense(null, date, amount, category, description);
                            newObj = expense;
                        } else if (Util.ACTIVE_MENU == Menu.INCOME) {
                            Income income = new Income(null, date, amount, category, description);
                            newObj = income;
                        }
                        try {
                            requestBody = new JSONObject(newObj.toString());
                            requestBody.put("CategoryID", selectedCategory.getCategoryID());
                            requestBody.put("UserID", Util.getAppUser().getId());
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Exception: " + e.getMessage());
                            Toast.makeText(getActivity(), "Error creating JSON", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        uploadToCloud();

                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog
                });
        return builder.create();
    }

    public Dialog buildCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(catBinding.getRoot())
                .setTitle("Add New Category")
                .setPositiveButton("Add", (dialog, id) -> {
                    String category = catBinding.etCategory.getText().toString();

                    if (category.isEmpty()) {
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        Category newCategory = new Category(0, category, Util.ACTIVE_CATEGORY.getValue());
                        try {
                            requestBody = new JSONObject(newCategory.toString());
                            requestBody.put("UserID", Util.getAppUser().getId());
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Exception: " + e.getMessage());
                            Toast.makeText(getActivity(), "Error creating JSON", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        uploadToCloud();

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
    public void onDestroyView() {
        super.onDestroyView();
        catBinding = null;
        expBinding = null;
    }
}