package com.example.fluidexpensetracker.data.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fluidexpensetracker.databinding.CategoryItemBinding;
import com.example.fluidexpensetracker.data.model.Category;
import com.example.fluidexpensetracker.util.GenericAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> implements GenericAdapter<Category> {

    private List<Category> categoryList;
    private CategoryItemBinding binding;

    public CategoryAdapter() {
        this.categoryList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CategoryItemBinding binding;

        public ViewHolder(@NonNull CategoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Category category) {
            binding.catTV.setText(category.getCategoryName());
        }
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
        return categoryList.get(position).getCategoryID();
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
}