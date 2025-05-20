package com.anahit.pawmatch.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.anahit.pawmatch.fragments.ExploreFragment;
import com.anahit.pawmatch.fragments.MatchesFragment;
import com.anahit.pawmatch.fragments.ProfileFragment;
import com.anahit.pawmatch.fragments.SwipeFragment;
import com.anahit.pawmatch.fragments.HealthFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {

    public HomePagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new SwipeFragment();
            case 1: return new ExploreFragment();
            case 2: return new MatchesFragment();
            case 3: return new HealthFragment();
            case 4: return new ProfileFragment();
            default: return new SwipeFragment();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}