package com.moleculesoft.fluidfinanceassistant.util;

import java.util.List;

// Generic adapter interface
public interface GenericAdapter<T> {
    public List<T> getList();

    public void setList(List<T> list);

    public T getItem(int position);

    public int getItemID(int position);

    public void deleteItem(int position);

    public void restoreItem(T item, int position);

    public void notifyAdapterItemChanged(int position);

    public void notifyAdapterItemInserted(int position);

    public void notifyAdapterDataSetChanged();
}