package com.example.fluidexpensetracker;

import static com.android.volley.VolleyLog.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fluidexpensetracker.model.Expense;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExpenseSwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private ExpenseAdapter adapter;
    private Drawable icon;
    private final ColorDrawable background;
    private Context context;

    public ExpenseSwipeToDeleteCallback(ExpenseAdapter adapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.context = context;
        icon = ContextCompat.getDrawable(context,
                R.drawable.ic_launcher_foreground); // Replace with your delete icon
        background = new ColorDrawable(Color.RED);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        Expense deletedItem = adapter.getList().get(position); // Store the deleted item


        new AlertDialog.Builder(context)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    adapter.deleteItem(position);
                    Snackbar snackbar = Snackbar
                            .make(viewHolder.itemView, "Expense deleted", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", view -> {
                        adapter.restoreItem(deletedItem, position);
                    });
                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            if (event != DISMISS_EVENT_ACTION) { // If not undone
                                sendDeleteRequest(deletedItem); // Send DELETE request only if not undone
                            }
                        }
                    });
                    snackbar.show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    adapter.notifyItemChanged(position); // Important: Restore the item
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int backgroundHeight = itemView.getBottom() - itemView.getTop();
        int backgroundWidth = backgroundHeight / 3;

        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);

        int iconMargin = (backgroundHeight - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (backgroundHeight - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        }

        background.draw(c);
        icon.draw(c);
    }

    private void sendDeleteRequest(Expense deletedItem) {
        String baseUrl = context.getString(R.string.base_url);
        String url = baseUrl + "/delete/expense"; // DELETE URL (no longer includes ID directly)

        try {
            JSONObject requestBody = new JSONObject();
            JSONArray idsArray = new JSONArray();
            idsArray.put(deletedItem.getId()); // Add the ID to the array
            requestBody.put("ids", idsArray);


            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                    response -> {
                        Log.d(TAG, "DELETE request successful: " + response.toString());

                    }, error -> {
                Log.e(TAG, "Volley Error: " + error.getMessage());
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    try {
                        String errorString = new String(error.networkResponse.data, "UTF-8");
                        Log.e(TAG, "Error Response: " + errorString);
                    } catch (Exception e) {
                        Log.e(TAG, "Error decoding error response: " + e.getMessage());
                    }
                }
                //Handle error appropriately, maybe re-add the item to the list
            });

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            Log.e(TAG, "JSONException creating request body: " + e.getMessage());
        }
    }
}