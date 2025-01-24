package com.example.fluidexpensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private List<Expense> expenseList; // Your data list

    public ExpenseAdapter() {
        this.expenseList = new ArrayList<>();
        this.expenseList.add(new Expense("2024-10-26", 15.75, "Food", "Grocery shopping"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false); // Inflate your item layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.dateTextView.setText(expense.getDate()); // Set data to views
        holder.amountTextView.setText(String.valueOf(expense.getAmount()));
        holder.categoryTextView.setText(expense.getCategory());
        // ... set other views
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public List<Expense> getExpenseList() {
        return expenseList;
    }

    public void deleteItem(int position) {
        expenseList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Expense item, int position) {
        expenseList.add(position, item);
        notifyItemInserted(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView amountTextView;
        TextView categoryTextView;
        // ... other views

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.tvDate);
            amountTextView = itemView.findViewById(R.id.tvAmount);
            categoryTextView = itemView.findViewById(R.id.tvCategory);
            // ... initialize other views
        }
    }
}