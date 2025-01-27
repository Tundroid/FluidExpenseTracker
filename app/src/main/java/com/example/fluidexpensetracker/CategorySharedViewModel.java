package com.example.fluidexpensetracker;

import static com.android.volley.VolleyLog.TAG;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fluidexpensetracker.model.Category;
import com.example.fluidexpensetracker.util.FetchCallback;
import com.example.fluidexpensetracker.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategorySharedViewModel extends ViewModel {

    private MutableLiveData<List<Category>> categoryList = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Category>> filteredCategoryList = new MutableLiveData<>(new ArrayList<>());
    private String categoryType; // Store the current category type filter

    public MutableLiveData<List<Category>> getCategoryList() {
        return categoryList;
    }

    public MutableLiveData<List<Category>> getFilteredCategoryList() {
        return filteredCategoryList;
    }

    public void setCategories(List<Category> categories) {
        categoryList.setValue(categories);
        filterCategories(); // Apply filter if categoryType is set
    }

    public void addCategory(Category category) {
        List<Category> currentList = categoryList.getValue();
        if (currentList != null) {
            currentList.add(category);
            categoryList.setValue(currentList);
            filterCategories(); // Reapply filter after adding a new category
        }
    }

    public void deleteCategory(int position) {
        List<Category> currentList = categoryList.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.remove(position);
            categoryList.setValue(currentList);
            filterCategories(); // Reapply filter after deletion
        }
    }

    public void updateCategory(int position, Category updatedCategory) {
        List<Category> currentList = categoryList.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.set(position, updatedCategory);
            categoryList.setValue(currentList);
            filterCategories(); // Reapply filter after updating
        }
    }

    // Set the current category type filter
    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
        filterCategories(); // Apply the filter whenever the category type changes
    }

    // Fetch categories from the server and update the category list
    public void fetchCategories(Context context, FetchCallback callback) {
        String url = context.getString(R.string.base_url) + "/get_categories";

        Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
        uriBuilder.appendQueryParameter("UserID", String.valueOf(Util.getAppUser().getId()));

        url = uriBuilder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Category> fetchedCategories = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("CategoryID");
                            String name = jsonObject.getString("CategoryName");
                            String type = jsonObject.getString("CategoryType");

                            Category category = new Category(id, name, type);
                            fetchedCategories.add(category);
                        }
                        setCategories(fetchedCategories); // Set categories and apply filter
                        callback.onFetched();
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        Toast.makeText(context, "Error parsing data", Toast.LENGTH_SHORT).show();
                        callback.onFetchFailed();
                    }
                }, error -> {
            Log.e(TAG, "Volley Error: " + error.getMessage());
            Toast.makeText(context, "Error fetching categories", Toast.LENGTH_SHORT).show();
            callback.onFetchFailed();
        });

        queue.add(jsonArrayRequest);
    }

    // Filter categories based on the selected category type
    private void filterCategories() {
        List<Category> currentCategories = categoryList.getValue();
        if (currentCategories != null) {
            List<Category> filteredCategories = new ArrayList<>();
            if (categoryType != null) {
                for (Category category : currentCategories) {
                    if (category.getType().equals(categoryType)) {
                        filteredCategories.add(category);
                    }
                }
            } else {
                filteredCategories.addAll(currentCategories); // Show all if no filter
            }
            filteredCategoryList.setValue(filteredCategories);
        }
    }
}
