package com.vasilitate.entertainmentstore;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Displays a virtual Store where users can browse items, and purchase content then interact with it.
 */
public class StoreActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.store_viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.store_tabs);

        viewPager.setAdapter(new StoreFragmentPagerAdapter(getSupportFragmentManager()));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Adapter which handles how the Books & Music categories are displayed for the store
     */
    private static class StoreFragmentPagerAdapter extends FragmentPagerAdapter {

        private static final String[] titles = {"Books", "Music"};

        public StoreFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override public Fragment getItem(int position) {
            return StoreListFragment.newInstance(position);
        }

        @Override public int getCount() {
            return 2;
        }

        @Override public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

}
