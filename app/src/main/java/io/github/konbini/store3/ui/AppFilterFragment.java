package io.github.konbini.store3.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import io.github.konbini.store3.R;
import io.github.konbini.store3.api.Api;
import io.github.konbini.store3.api.AppShort;
import io.github.konbini.store3.db.Database;

public class AppFilterFragment extends Fragment {
    private String currentCategoryCode = null;
    private AppCellAdapter adapter;
    private GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_filter_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView = view.findViewById(R.id.grid);
        adapter = new AppCellAdapter(getActivity(), new ArrayList<AppShort>());
        gridView.setAdapter(adapter);

        Bundle args = getArguments();
        if (args != null && args.containsKey("selection")) {
            String selection = args.getString("selection");
            String[] selectionArgs = args.getStringArray("selection_args");
            ArrayList<AppShort> as = Database.getAppsBySelection(getActivity(), selection, selectionArgs);
            if (as != null) {
                adapter.setItems(as);
            }
            return;
        }

        if (args != null && args.containsKey("category_code")) {
            currentCategoryCode = args.getString("category_code");
        } else if (getActivity() instanceof AppFilterActivity) {
            currentCategoryCode = ((AppFilterActivity) getActivity()).getSelectedCategoryId();
        }

        loadApps();
    }

    public void filterByCategory(String categoryCode) {
        this.currentCategoryCode = categoryCode;
        loadApps();
    }

    public String getCurrentCategoryCode() {
        return currentCategoryCode;
    }

    private void loadApps() {
        if (getActivity() == null) return;

        // Immediate load from database if available
        ArrayList<AppShort> cachedApps;
        if (currentCategoryCode == null || currentCategoryCode.isEmpty() || "all".equalsIgnoreCase(currentCategoryCode)) {
            cachedApps = Database.getAllApps(getActivity());
        } else {
            cachedApps = Database.getAppsByCategory(getActivity(), currentCategoryCode);
        }

        if (cachedApps != null && adapter != null) {
            adapter.setItems(cachedApps);
        }

        // Background query to ensure DB is populated from API if empty, and update UI
        new AsyncTask<Void, Void, ArrayList<AppShort>>() {
            @Override
            protected ArrayList<AppShort> doInBackground(Void... voids) {
                Context context = getActivity();
                if (context == null) return null;

                if (!Database.hasApps(context)) {
                    Api.getInstance(context).getAllApps(context);
                }

                if (currentCategoryCode == null || currentCategoryCode.isEmpty() || "all".equalsIgnoreCase(currentCategoryCode)) {
                    return Database.getAllApps(context);
                } else {
                    return Database.getAppsByCategory(context, currentCategoryCode);
                }
            }

            @Override
            protected void onPostExecute(ArrayList<AppShort> result) {
                if (getActivity() == null || isCancelled() || !isAdded()) return;
                if (adapter != null && result != null) {
                    adapter.setItems(result);
                }
            }
        }.execute();
    }
}
