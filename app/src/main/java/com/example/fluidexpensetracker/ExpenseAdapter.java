package com.example.fluidexpensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fluidexpensetracker.databinding.ExpenseItemBinding;
import com.example.fluidexpensetracker.model.Expense;
import com.example.fluidexpensetracker.util.GenericAdapter;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> implements GenericAdapter<Expense> {

    private List<Expense> expenseList;
    private static ExpenseItemBinding binding;

    public ExpenseAdapter() {
        this.expenseList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false); // Inflate your item layout
        binding = ExpenseItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.dateTextView.setText(expense.getDate()); // Set data to views
        holder.amountTextView.setText(String.valueOf(expense.getAmount()));
        holder.categoryTextView.setText(expense.getCategory());
        holder.descTV.setText(expense.getDescription());
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    @Override
    public List<Expense> getList() {
        return expenseList;
    }

    @Override
    public void setList(List<Expense> list) {
        expenseList = list;
    }

    @Override
    public Expense getItem(int position) {
        return expenseList.get(position);
    }

    @Override
    public int getItemID(int position) {
        return expenseList.get(position).getId();
    }

    @Override
    public void deleteItem(int position) {
        expenseList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void restoreItem(Expense item, int position) {
        expenseList.add(position, item);
        notifyItemInserted(position);
    }

    @Override
    public void notifyAdapterItemChanged(int position) {
        super.notifyItemChanged(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView amountTextView;
        TextView categoryTextView;
        TextView descTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.tvDate);
            amountTextView = itemView.findViewById(R.id.tvAmount);
            categoryTextView = itemView.findViewById(R.id.tvCategory);
            descTV = itemView.findViewById(R.id.tvDescription);
        }
    }
}