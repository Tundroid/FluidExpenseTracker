package com.example.fluidexpensetracker;

import static com.android.volley.VolleyLog.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fluidexpensetracker.model.Category;
import com.example.fluidexpensetracker.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class NewCategoryDialogFragment extends DialogFragment {

    public interface NewCategoryDialogListener {
        void onCategoryAdded(Category category);
    }

    private NewCategoryDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NewCategoryDialogListener) getParentFragment(); // Correct: Get parent fragment
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString() + " must implement NewCategoryDialogListener");
        } catch(NullPointerException e){
            Log.e(TAG, "Parent Fragment is null: " + e.getMessage());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_category, null);

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
                        Category newCategory = new Category(0, category, "Expense");
//                        listener.onCategoryAdded(newCategory);
                        sendPostRequest(newCategory);

                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog
                });

        return builder.create();
    }

    private void sendPostRequest(Category category) {
        String url = getString(R.string.base_url) + "/create/category"; // Replace with your POST API endpoint

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("CategoryName", category.getName());
            jsonObject.put("CategoryType", getArguments().getString("CategoryType"));
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
//                    Toast.makeText(getActivity(), "Category added successfully", Toast.LENGTH_SHORT).show();
                    listener.onCategoryAdded(category); // Notify MainActivity after successful post

                }, error -> {
            // Handle error
            Log.e(TAG, "Volley Error: " + error.getMessage());
            Toast.makeText(getActivity(), "Error adding category", Toast.LENGTH_SHORT).show();
        });

        queue.add(jsonObjectRequest);
    }
}