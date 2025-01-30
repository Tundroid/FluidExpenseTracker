package com.example.fluidexpensetracker.ui.util;

import static com.android.volley.VolleyLog.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fluidexpensetracker.R;
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

    public SwipeToDeleteCallback(GenericAdapter<T> adapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        if (adapter == null)
            System.out.println("Generic adapter is null ya");
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

    private void sendDeleteRequest(int deletedID) {
        String baseUrl = context.getString(R.string.base_url);
        String url = baseUrl + "/delete/" + Util.ACTIVE_MODEL.getValue(); // DELETE URL (no longer includes ID directly)

        Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
        uriBuilder.appendQueryParameter("UserID", String.valueOf(Util.getAppUser().getId()));
        uriBuilder.appendQueryParameter(Util.ACTIVE_MENU.getValue() + "ID", String.valueOf(deletedID));

        url = uriBuilder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.DELETE, url, null,
                response -> Log.d(TAG, "DELETE request successful: " + response));

        queue.add(request);
    }
}
