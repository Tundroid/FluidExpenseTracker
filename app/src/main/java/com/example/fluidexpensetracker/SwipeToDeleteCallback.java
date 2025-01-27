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
import com.example.fluidexpensetracker.util.GenericAdapter;
import com.example.fluidexpensetracker.util.Util;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SwipeToDeleteCallback<T> extends ItemTouchHelper.SimpleCallback {

    private final GenericAdapter<T> adapter;
    private final Context context;
    private final Drawable icon;
    private final ColorDrawable background;
    private final String urlModel; // Generic URL for delete requests

    public SwipeToDeleteCallback(GenericAdapter<T> adapter, Context context, String urlModel) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.context = context;
        this.urlModel = urlModel;

        icon = ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground); // Replace with your delete icon
        background = new ColorDrawable(Color.RED);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        T deletedItem = adapter.getItem(position);
        int deletedID = adapter.getItemID(position);


        new AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    adapter.deleteItem(position);
                    Snackbar snackbar = Snackbar
                            .make(viewHolder.itemView, "Item deleted", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", view -> {
                        adapter.restoreItem(deletedItem, position);
                    });
                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            if (event != DISMISS_EVENT_ACTION) { // If not undone
                                sendDeleteRequest(deletedID); // Send DELETE request
                            }
                        }
                    });
                    snackbar.show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    adapter.notifyAdapterItemChanged(position);
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        if (dX > 0) { // Swiping to the right
            background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else {
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + iconMargin;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) {
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = iconLeft + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        } else if (dX < 0) {
            int iconRight = itemView.getRight() - iconMargin;
            int iconLeft = iconRight - icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        }

        icon.draw(c);
    }

    private void sendDeleteRequest(int deletedID) {
        String baseUrl = context.getString(R.string.base_url);
        String url = baseUrl + "/delete/" + urlModel; // DELETE URL (no longer includes ID directly)

        try {
            JSONObject requestBody = new JSONObject();
            JSONArray idsArray = new JSONArray();
            idsArray.put(deletedID);
            requestBody.put("ids", idsArray);

            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                    response -> Log.d(TAG, "DELETE request successful: " + response.toString()),
                    error -> Log.e(TAG, "Volley Error: " + error.getMessage()));

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            Log.e(TAG, "JSONException creating request body: " + e.getMessage());
        }
    }
}
