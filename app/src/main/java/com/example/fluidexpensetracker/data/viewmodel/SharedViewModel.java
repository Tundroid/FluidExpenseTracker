package com.example.fluidexpensetracker.data.viewmodel;

import static com.android.volley.VolleyLog.TAG;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fluidexpensetracker.R;
import com.example.fluidexpensetracker.data.model.Category;
import com.example.fluidexpensetracker.data.model.Expense;
import com.example.fluidexpensetracker.util.FetchCallback;
import com.example.fluidexpensetracker.util.Menu;
import com.example.fluidexpensetracker.util.Model;
import com.example.fluidexpensetracker.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SharedViewModel<T> extends ViewModel {

    private MutableLiveData<List<T>> itemList = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<T>> filteredItemList = new MutableLiveData<>(new ArrayList<>());
    private String categoryType;

    public MutableLiveData<List<T>> getItemList() {
        return itemList;
    }

    public MutableLiveData<List<T>> getFilteredItemList() {
        return filteredItemList;
    }

    public void setItems(List<T> items) {
        itemList.setValue(items);
        filterItems();
    }

    public List<T> getDataList()
    {
        return itemList.getValue();
    }

    public void addCategory(T item) {
        List<T> currentList = itemList.getValue();
        if (currentList != null) {
            currentList.add(item);
            itemList.setValue(currentList);
            filterItems();
        }
    }

    public void deleteCategory(int position) {
        List<T> currentList = itemList.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.remove(position);
            itemList.setValue(currentList);
            filterItems();
        }
    }

    public void updateCategory(int position, T updatedItem) {
        List<T> currentList = itemList.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.set(position, updatedItem);
            itemList.setValue(currentList);
            filterItems();
        }
    }

    // Set the current category type filter
    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
        System.out.println("Setting filter");
        filterItems(); // Apply the filter whenever the category type changes
    }

    public void fetchItems(Context context, Model model, Menu menu, FetchCallback callback) {
        String url = context.getString(R.string.base_url) + "/get_list_" + model.getValue();

        Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
        uriBuilder.appendQueryParameter("UserID", String.valueOf(Util.getAppUser().getId()));

        url = uriBuilder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    switch (menu) {
                        case CATEGORY:
                            System.out.println("Receiving categories...");
                            processCategoryResponse(response, callback);
                            break;
                        default:
                            processExpenseResponse(response, callback);
                            break;
                    }
                }, error -> {
            Log.e(TAG, "Volley Error: " + error.getMessage());
            callback.onFetchFailed();
        });

        queue.add(jsonArrayRequest);
    }

    // Filter categories based on the selected category type
    private void filterItems() {
        System.out.println("Filtering...");
        List<T> currentItems = itemList.getValue();
        if (currentItems != null) {
            System.out.println("Really Filtering...: " + categoryType);
            List<T> filteredItems = new ArrayList<>();
            if (categoryType != null) {
                for (T item : currentItems) {
                    if (((Category)item).getCategoryType().equals(categoryType)) {
                        filteredItems.add(item);
                    }
                }
            } else {
                filteredItems.addAll(currentItems); // Show all if no filter
            }
            filteredItemList.setValue(filteredItems);
            System.out.println("Filtering... DONE: " + filteredItems.size());
        }
    }


    public void processExpenseResponse(JSONArray response, FetchCallback callback) {
        try {
            List<Expense> fetchedItems = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonObject = response.getJSONObject(i);
                int id = jsonObject.getInt("ExpenseID");
                String date = jsonObject.getString("ExpenseDate");
                int amount = jsonObject.getInt("Amount");
                String category = jsonObject.getString("CategoryName");
                String description = jsonObject.getString("ExpenseDescription");

                fetchedItems.add(new Expense(id, date, amount, category, description));
            }
            // Sort expenses by date (desc) and description (asc)
            fetchedItems.sort((expense1, expense2) -> {
                // Compare by date in descending order
                Date date1 = Util.getDateObject(expense1.getExpenseDate());
                Date date2 = Util.getDateObject(expense2.getExpenseDate());
                int dateComparison = date1 != null && date2 != null ? date2.compareTo(date1) : 0;

                // If dates are the same, compare by description in ascending order
                if (dateComparison == 0) {
                    return expense1.getExpenseDescription().compareToIgnoreCase(expense2.getExpenseDescription());
                }
                return dateComparison;
            });
            System.out.println("Fetched Expense items size: " + fetchedItems.size());
            setItems((List<T>)fetchedItems);
            callback.onFetched();
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
            callback.onFetchFailed();
        }
    }

    public void processCategoryResponse(JSONArray response, FetchCallback callback) {
        try {
            List<Category> fetchedItems = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonObject = response.getJSONObject(i);
                int id = jsonObject.getInt("CategoryID");
                String name = jsonObject.getString("CategoryName");
                String type = jsonObject.getString("CategoryType");

                fetchedItems.add(new Category(id, name, type));
            }
            System.out.println("Fetched Category items size: " + fetchedItems.size());
            setItems((List<T>)fetchedItems);
            callback.onFetched();
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
            callback.onFetchFailed();
        }
    }
}
