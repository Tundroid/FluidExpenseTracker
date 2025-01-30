package com.moleculesoft.fluidfinanceassistant.data.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moleculesoft.fluidfinanceassistant.data.model.Income;
import com.moleculesoft.fluidfinanceassistant.databinding.IncomeItemBinding;
import com.moleculesoft.fluidfinanceassistant.util.GenericAdapter;

import java.util.ArrayList;
import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder> implements GenericAdapter<Income> {

    private List<Income> incomeList;
    private IncomeItemBinding binding;

    public IncomeAdapter() {
        this.incomeList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = IncomeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Income income = incomeList.get(position);
        holder.bind(income);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final IncomeItemBinding binding;

        public ViewHolder(@NonNull IncomeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Income income) {
            binding.tvDate.setText(income.getIncomeDate());
            binding.tvAmount.setText(String.valueOf(income.getAmount()));
            binding.tvCategory.setText(income.getCategory());
            binding.tvDescription.setText(income.getIncomeDescription());
        }
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    @Override
    public List<Income> getList() {
        return incomeList;
    }

    @Override
    public void setList(List<Income> list) {
        incomeList = list;
    }

    @Override
    public Income getItem(int position) {
        return incomeList.get(position);
    }

    @Override
    public int getItemID(int position) {
        return incomeList.get(position).getIncomeID();
    }

    @Override
    public void deleteItem(int position) {
        incomeList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void restoreItem(Income item, int position) {
        incomeList.add(position, item);
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