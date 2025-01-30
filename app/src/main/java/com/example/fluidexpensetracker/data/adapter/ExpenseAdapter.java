package com.example.fluidexpensetracker.data.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fluidexpensetracker.databinding.ExpenseItemBinding;
import com.example.fluidexpensetracker.data.model.Expense;
import com.example.fluidexpensetracker.util.GenericAdapter;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> implements GenericAdapter<Expense> {

    private List<Expense> expenseList;
    private ExpenseItemBinding binding;

    public ExpenseAdapter() {
        this.expenseList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ExpenseItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.bind(expense);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ExpenseItemBinding binding;

        public ViewHolder(@NonNull ExpenseItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Expense expense) {
            binding.tvDate.setText(expense.getExpenseDate());
            binding.tvAmount.setText(String.valueOf(expense.getAmount()));
            binding.tvCategory.setText(expense.getCategory());
            binding.tvDescription.setText(expense.getExpenseDescription());
        }
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
        return expenseList.get(position).getExpenseID();
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

    @Override
    public void notifyAdapterItemInserted(int position) {
        super.notifyItemInserted(position);
    }

    @Override
    public void notifyAdapterDataSetChanged() {
        super.notifyDataSetChanged();
    }

}