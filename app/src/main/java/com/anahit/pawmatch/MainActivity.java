package com.anahit.pawmatch;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.anahit.pawmatch.fragments.MessagesFragment;
import com.anahit.pawmatch.fragments.ProfileFragment;
import com.anahit.pawmatch.fragments.SwipeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private SwipeFragment swipeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // Set up BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(this::onNavigationItemSelected);
        }

        // Initialize fragments
        swipeFragment = new SwipeFragment();

        // Restore or set the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, swipeFragment)
                    .commit();
        } else {
            // Restore the fragment state if it exists
            Fragment restoredFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (restoredFragment instanceof SwipeFragment) {
                swipeFragment = (SwipeFragment) restoredFragment;
            } else if (restoredFragment == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, swipeFragment)
                        .commit();
            }
        }
    }

    // Handle navigation item selection
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.nav_swipe) {
            selectedFragment = swipeFragment;
        } else if (itemId == R.id.nav_messages) {
            selectedFragment = new MessagesFragment();
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .addToBackStack(null) // Adds to back stack for back navigation
                    .commit();
            return true;
        }
        return false;
    }

    // Inflate the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handle options menu item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof SwipeFragment) {
            SwipeFragment swipeFragment = (SwipeFragment) currentFragment;
            int itemId = item.getItemId();
            if (itemId == R.id.reload) {
                swipeFragment.reloadPets();
                return true;
            } else if (itemId == R.id.add_spot_to_first) {
                swipeFragment.addPetToFirst();
                return true;
            } else if (itemId == R.id.add_spot_to_last) {
                swipeFragment.addPetToLast();
                return true;
            } else if (itemId == R.id.remove_spot_from_first) {
                swipeFragment.removePetFromFirst();
                return true;
            } else if (itemId == R.id.remove_spot_from_last) {
                swipeFragment.removePetFromLast();
                return true;
            } else if (itemId == R.id.replace_first_spot) {
                swipeFragment.replaceFirstPet();
                return true;
            } else if (itemId == R.id.swap_first_for_last) {
                swipeFragment.swapFirstForLast();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}