package com.example.fluidexpensetracker;

import static com.android.volley.VolleyLog.TAG;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.fluidexpensetracker.ui.main.SectionsPagerAdapter;
import com.example.fluidexpensetracker.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NewExpenseDialogFragment.NewExpenseDialogListener {

    private ActivityMainBinding binding;
    private ExpenseAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_category) {
                FragmentCategory categoryFragment = new FragmentCategory();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, categoryFragment); // Replace fragment_container with the id of a FrameLayout in your main activity layout
                transaction.addToBackStack(null); // Optional: Add to back stack
                transaction.commit();
            } else if (id == R.id.nav_gallery) {
                // Handle the gallery action
            } else if (id == R.id.nav_slideshow) {
                // Handle the slideshow action
            }

            drawer.closeDrawer(GravityCompat.START); // Close the drawer after item selection
            return true;
        });


        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Initialize SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchExpenses(); // Call fetchExpenses() on refresh
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewExpenseDialogFragment dialog = new NewExpenseDialogFragment();
                dialog.show(getSupportFragmentManager(), "NewExpenseDialog");
            }
        });


        // Find the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        // Set layout manager (usually LinearLayoutManager for a vertical list)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create an ExpenseAdapter with sample data (consider replacing with your data source)
        adapter = new ExpenseAdapter();

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, this));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        fetchExpenses(); // Fetch expenses from the API
    }

    private void fetchExpenses() {
        String url = getString(R.string.base_url) + "/get/expense"; // Replace with your API endpoint

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        adapter.getExpenseList().clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String date = jsonObject.getString("ExpenseDate");
                            int amount = jsonObject.getInt("Amount");
                            String category = jsonObject.getString("CategoryID");
                            String description = jsonObject.getString("ExpenseDescription");

                            Expense expense = new Expense(date, amount, category, description);
                            onExpenseAdded(expense);
                        }
                        adapter.notifyDataSetChanged(); // Notify adapter after fetching data

                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    } finally {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, error -> {
            Log.e(TAG, "Volley Error: " + error.getMessage());
            swipeRefreshLayout.setRefreshing(false);
        });

        queue.add(jsonArrayRequest);
    }

    @Override
    public void onExpenseAdded(Expense expense) {
        adapter.getExpenseList().add(expense);
        adapter.notifyItemInserted(adapter.getExpenseList().size() - 1); // Notify adapter of new item
    }
}