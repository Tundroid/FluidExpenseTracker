package com.example.fluidexpensetracker;

import static com.android.volley.VolleyLog.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import org.json.JSONException;
import org.json.JSONObject;

public class NewExpenseDialogFragment extends DialogFragment {

    public interface NewExpenseDialogListener {
        void onExpenseAdded(Expense expense);
    }

    private NewExpenseDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NewExpenseDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement NewExpenseDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_expense, null);

        EditText etDate = view.findViewById(R.id.etDate);
        EditText etAmount = view.findViewById(R.id.etAmount);
        EditText etCategory = view.findViewById(R.id.etCategory);
        EditText etDescription = view.findViewById(R.id.etDescription);

        builder.setView(view)
                .setTitle("Add New Expense")
                .setPositiveButton("Add", (dialog, id) -> {
                    String date = etDate.getText().toString();
                    String amountStr = etAmount.getText().toString();
                    String category = etCategory.getText().toString();
                    String description = etDescription.getText().toString();

                    if(date.isEmpty() || amountStr.isEmpty() || category.isEmpty() || description.isEmpty()){
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double amount = Double.parseDouble(amountStr);
                        Expense newExpense = new Expense(date, amount, category, description);
//                        listener.onExpenseAdded(newExpense);
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

    private void sendPostRequest(Expense expense) {
        String url = getString(R.string.base_url) + "/create/expense"; // Replace with your POST API endpoint

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ExpenseDate", expense.getDate());
            jsonObject.put("Amount", expense.getAmount());
            jsonObject.put("CategoryID", 1);
            jsonObject.put("ExpenseDescription", expense.getDescription());
            jsonObject.put("UserID", 4);
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

        queue.add(jsonObjectRequest);
    }
}