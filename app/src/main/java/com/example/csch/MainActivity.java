package com.example.csch;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.csch.fragmentadmin.AccountAdminFragment;
import com.example.csch.fragmentsuser.AccountFragment;
import com.example.csch.fragmentsuser.FollowFragment;
import com.example.csch.fragmentsuser.HomeFragment;
import com.example.csch.fragmentsuser.NoUserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment (HomeFragment)
        loadFragment(new HomeFragment());

        // Handle fragment switching on bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_follow) {
                selectedFragment = new FollowFragment();
            } else if (itemId == R.id.nav_account) {
                SharedPreferences preferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                boolean isAdmin = preferences.getBoolean("isAdmin", false);
                if (isAdmin) {
                    selectedFragment = new AccountAdminFragment();
                } else if (preferences.contains("username")) {
                    selectedFragment = getAccountFragmentWithArgs();
                } else {
                    selectedFragment = new NoUserFragment();
                }
            }

            // Load the fragment
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    // Retrieve user data and pass to AccountFragment
    private AccountFragment getAccountFragmentWithArgs() {
        SharedPreferences preferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        String username = preferences.getString("username", "");
        String phonenumber = preferences.getString("phonenumber", "");
        String role = preferences.getString("role", "");
        String avatar = preferences.getString("avatar", "");

        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("phonenumber", phonenumber);
        bundle.putString("role", role);
        bundle.putString("avatar", avatar);

        AccountFragment accountFragment = new AccountFragment();
        accountFragment.setArguments(bundle);
        return accountFragment;
    }

    // Method to load a fragment
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }



}
