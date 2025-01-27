package com.example.fluidexpensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fluidexpensetracker.model.Category;
import com.example.fluidexpensetracker.model.Expense;
import com.example.fluidexpensetracker.util.GenericAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> implements GenericAdapter<Category> {

    private List<Category> categoryList; // Your data list

    public CategoryAdapter() {
        this.categoryList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false); // Inflate your item layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.catTextView.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public List<Category> getList() {
        return categoryList;
    }

    @Override
    public void setList(List<Category> list) {
        categoryList = list;
    }

    @Override
    public Category getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public int getItemID(int position) {
        return categoryList.get(position).getId();
    }

    @Override
    public void deleteItem(int position) {
        categoryList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void restoreItem(Category item, int position) {
        categoryList.add(position, item);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView catTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catTextView = itemView.findViewById(R.id.catDate);
        }
    }
}