package com.moleculesoft.fluidfinanceassistant.ui;

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
import androidx.lifecycle.ViewModelProvider;

import com.moleculesoft.fluidfinanceassistant.ui.fragment.FragmentList;
import com.moleculesoft.fluidfinanceassistant.R;
import com.moleculesoft.fluidfinanceassistant.data.viewmodel.SharedViewModel;
import com.moleculesoft.fluidfinanceassistant.databinding.ActivityMainBinding;
import com.moleculesoft.fluidfinanceassistant.data.model.User;
import com.moleculesoft.fluidfinanceassistant.util.Category;
import com.moleculesoft.fluidfinanceassistant.util.FetchCallback;
import com.moleculesoft.fluidfinanceassistant.util.Menu;
import com.moleculesoft.fluidfinanceassistant.util.Model;
import com.moleculesoft.fluidfinanceassistant.util.Util;
import com.google.android.material.navigation.NavigationView;

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
                Util.ACTIVE_MENU = Menu.CATEGORY;
                switch (id){
                    case R.id.nav_category_budget:
                        Util.ACTIVE_CATEGORY = Category.BUDGET;
                        break;
                    case R.id.nav_category_income:
                        Util.ACTIVE_CATEGORY = Category.INCOME;
                        break;
                    case R.id.nav_category_saving:
                        Util.ACTIVE_CATEGORY = Category.SAVING;
                        break;
                    default:
                        Util.ACTIVE_CATEGORY = Category.EXPENSE;
                        break;
                }
                Util.ACTIVE_MODEL = Model.CATEGORY;

                if (id == R.id.nav_expense) {
                    Util.ACTIVE_MENU = Menu.EXPENSE;
                    Util.ACTIVE_CATEGORY = Category.EXPENSE;
                    Util.ACTIVE_MODEL = Model.EXPENSE;
                } else if (id == R.id.nav_income){
                    Util.ACTIVE_MENU = Menu.INCOME;
                    Util.ACTIVE_CATEGORY = Category.INCOME;
                    Util.ACTIVE_MODEL = Model.INCOME;
                }
                FragmentList fragmentList = new FragmentList();
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

        SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        viewModel.fetchItems(this, Model.CATEGORY, Menu.CATEGORY, new FetchCallback() {
            @Override
            public void onFetched() {
            }

            @Override
            public void onFetchFailed() {
            }
        });

        if (savedInstanceState == null) { // Check if it's the initial creation
            Util.ACTIVE_MENU = Menu.EXPENSE;
            Util.ACTIVE_CATEGORY = Category.EXPENSE;
            Util.ACTIVE_MODEL = Model.EXPENSE;
            FragmentList fragmentList = new FragmentList();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, fragmentList); // Use add() instead of replace() for initial fragment
            transaction.commit();
        }
    }
}