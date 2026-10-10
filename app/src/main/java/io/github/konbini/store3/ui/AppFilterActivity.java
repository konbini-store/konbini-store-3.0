package io.github.konbini.store3.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import io.github.konbini.store3.R;
import io.github.konbini.store3.api.Category;

public class AppFilterActivity extends FragmentActivity implements CategoryFilterFragment.OnCategorySelectedListener {
    private ViewPager pager;
    private TabsAdapter adapter;
    private String selectedCategoryId = "";
    private boolean isGame = false;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.app_filter_activity);

        if (getIntent() != null) {
            isGame = getIntent().getBooleanExtra("is_game", false);
            selectedCategoryId = getIntent().getStringExtra("category_code");
            if (selectedCategoryId == null) {
                selectedCategoryId = "";
            }
        }

        pager = findViewById(R.id.view_pager);
        adapter = new TabsAdapter(getSupportFragmentManager(), isGame, selectedCategoryId);
        pager.setAdapter(adapter);

        PagerTabStrip strip = findViewById(R.id.pager_tab_strip);
        strip.setTabIndicatorColorResource(android.R.color.holo_blue_light);
        strip.setDrawFullUnderline(true);
    }

    public String getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public boolean isGame() {
        return isGame;
    }

    @Override
    public void onCategorySelected(Category category) {
        selectedCategoryId = category != null ? category.getId() : "";
        if (adapter != null && adapter.getAppsFragment() != null) {
            adapter.getAppsFragment().filterByCategory(selectedCategoryId);
        }
        if (pager != null) {
            pager.setCurrentItem(1, true);
        }
    }

    static class TabsAdapter extends FragmentPagerAdapter {
        private final String[] tabs = new String[] {"Categories", "Apps"};
        private final boolean isGame;
        private final String initialCategoryId;
        private CategoryFilterFragment categoryFragment;
        private AppFilterFragment appsFragment;

        TabsAdapter(FragmentManager fm, boolean isGame, String initialCategoryId) {
            super(fm);
            this.isGame = isGame;
            this.initialCategoryId = initialCategoryId;
        }

        @Override
        public int getCount() { return tabs.length; }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) {
                CategoryFilterFragment cf = new CategoryFilterFragment();
                Bundle b = new Bundle();
                b.putBoolean("is_game", isGame);
                b.putString("active_category_id", initialCategoryId);
                cf.setArguments(b);
                return cf;
            } else {
                AppFilterFragment ff = new AppFilterFragment();
                Bundle b = new Bundle();
                b.putString("category_code", initialCategoryId);
                b.putBoolean("is_game", isGame);
                ff.setArguments(b);
                return ff;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            if (position == 0) {
                categoryFragment = (CategoryFilterFragment) fragment;
            } else if (position == 1) {
                appsFragment = (AppFilterFragment) fragment;
            }
            return fragment;
        }

        public CategoryFilterFragment getCategoryFragment() {
            return categoryFragment;
        }

        public AppFilterFragment getAppsFragment() {
            return appsFragment;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            return tabs[i];
        }
    }
}
