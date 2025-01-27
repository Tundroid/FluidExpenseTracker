package com.example.fluidexpensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.fluidexpensetracker.databinding.ActivityMainBinding;
import com.example.fluidexpensetracker.model.Category;
import com.example.fluidexpensetracker.model.User;
import com.example.fluidexpensetracker.util.FetchCallback;
import com.example.fluidexpensetracker.util.Util;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.initialize(this);

        user = Util.getAppUser();

        if (user.getId() < 1 || user.getName() == null || user.getEmail() == null) {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);

        TextView fullNameView = headerView.findViewById(R.id.userFullName);
        fullNameView.setText(user.getName());
        TextView emailView = headerView.findViewById(R.id.userEmail);
        emailView.setText(user.getEmail());


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (true) {
                String catType = item.getTitle().toString();
                String menu = "category";
                if (id == R.id.nav_expense) {
                    catType = "Expense";
                    menu = "expense";
                }
                FragmentList fragmentList = new FragmentList();
                Bundle bundle = new Bundle();
                bundle.putString("CategoryType", catType);
                bundle.putString("Menu", menu);
                fragmentList.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragmentList); // Replace fragment_container with the id of a FrameLayout in your main activity layout
                transaction.addToBackStack(null); // Optional: Add to back stack
                transaction.commit();
            } else if (id == R.id.nav_slideshow) {
                // Handle the slideshow action
            }

            drawer.closeDrawer(GravityCompat.START); // Close the drawer after item selection
            return true;
        });

        if (savedInstanceState == null) { // Check if it's the initial creation
            FragmentList fragmentList = new FragmentList();
            Bundle bundle = new Bundle();
            bundle.putString("CategoryType", "Expense");
            bundle.putString("Menu", "expense");
            fragmentList.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, fragmentList); // Use add() instead of replace() for initial fragment
            transaction.commit();
        }

        SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        System.out.println("Before: " + viewModel.getDataList().size());
        viewModel.fetchItems(this, "category", new FetchCallback() {
            @Override
            public void onFetched() {
                System.out.println("After fetched: " + viewModel.getDataList().size());
            }

            @Override
            public void onFetchFailed() {
                System.out.println("After not fetched: " + viewModel.getDataList().size());
            }
        });

    }
}