package io.github.konbini.store3.ui;

import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import io.github.konbini.store3.R;
import io.github.konbini.store3.api.Api;
import io.github.konbini.store3.api.AppShort;
import io.github.konbini.store3.db.Database;
import io.github.konbini.store3.ui.tasks.LoadAppsAsyncTask;
import io.github.konbini.store3.ui.tasks.LoadBannersAsyncTask;
import io.github.konbini.store3.util.DownloadImagesTask;

public class MainActivity extends Activity {
    ImageView adBtn1, adBtn2;
    View header;
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Generating the cache database first
        Database.getDatabase(this);

        ListView listView = findViewById(R.id.app_list);

        header = getLayoutInflater().inflate(R.layout.list_header, listView, false);
        listView.addHeaderView(header, null, false);

        AppListAdapter adapter = new AppListAdapter(this);
        listView.setAdapter(adapter);   // fine with zero items, the header still shows

        listView.setOnItemClickListener((parent, view, position, id) -> {
            AppShort app = (AppShort) parent.getItemAtPosition(position);
            // open the app details activity for app.packageName
        });

        int[] pressIds = {
                R.id.all_apps_button,
                R.id.new_apps_button,
                R.id.popular_apps_button,
                R.id.top_rated_apps_button,
                R.id.random_app_button,
                R.id.games_button
        };
        for (int id : pressIds) {
            ImageView btn = header.findViewById(id);
            addPressEffect(btn);
        }

        adBtn1 = header.findViewById(R.id.ad1_button);
        adBtn2 = header.findViewById(R.id.ad2_button);
        addPressEffect(adBtn1);
        addPressEffect(adBtn2);

        LoadAds();
        DoCategoryButtons();
        new LoadBannersAsyncTask(this, header.findViewById(R.id.main_banner)).execute();
        new LoadAppsAsyncTask(this, listView).execute();
    }

    // TODO
    public void DoCategoryButtons() {
        if (header == null) return;
        ImageView appsBtn = header.findViewById(R.id.all_apps_button);
        ImageView newAppsBtn = header.findViewById(R.id.new_apps_button);
        ImageView popAppsBtn = header.findViewById(R.id.popular_apps_button);
        ImageView topRatedAppsBtn = header.findViewById(R.id.top_rated_apps_button);
        ImageView randomAppBtn = header.findViewById(R.id.random_app_button);
        ImageView gamesBtn = header.findViewById(R.id.games_button);

        appsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppFilterActivity.class);
            startActivity(intent);
        });
        if (newAppsBtn != null) {
            newAppsBtn.setOnClickListener(v -> {
                Intent intent = new Intent(this, AppFilterActivity.class);
                startActivity(intent);
            });
        }
        popAppsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppFilterActivity.class);
            startActivity(intent);
        });
        topRatedAppsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppFilterActivity.class);
            startActivity(intent);
        });
        randomAppBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Not implemented yet!", LENGTH_SHORT).show();
        });
        if (gamesBtn != null) {
            gamesBtn.setOnClickListener(v -> {
                Intent intent = new Intent(this, AppFilterActivity.class);
                intent.putExtra("is_game", true);
                startActivity(intent);
            });
        }
    }

    // TODO
    @SuppressWarnings("deprecation")
    public void LoadAds() {
        if (adBtn1 == null || adBtn2 == null) return;
        String adUrl1 = "http://konbini.lol/ads/ph_ad2.png";
        String adUrl2 = "http://konbini.lol/ads/ph_ad1.png";
        new DownloadImagesTask(adBtn1).execute(adUrl1);
        new DownloadImagesTask(adBtn2).execute(adUrl2);
        adBtn1.setOnClickListener((v) -> {
            Toast.makeText(MainActivity.this, "Minecraft ad clicked!!", LENGTH_SHORT).show();
        });
        adBtn2.setOnClickListener((v) -> {
            Toast.makeText(MainActivity.this, "notPipe ad clicked!!", LENGTH_SHORT).show();
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void addPressEffect(final ImageView v) {
        v.setClickable(true);
        v.setOnTouchListener((view, e) -> {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setColorFilter(0x66000000, PorterDuff.Mode.SRC_ATOP);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.clearColorFilter();
                    break;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        if (searchView != null) {
            String[] from = {"suggestion"};
            int[] to = {android.R.id.text1};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    null,
                    from,
                    to,
                    0
            );
            searchView.setSuggestionsAdapter(adapter);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Toast.makeText(MainActivity.this, "Submitted: " + query, LENGTH_SHORT).show();
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    ArrayList<String> suggestions = Api.getInstance(MainActivity.this)
                            .getAppSuggestions(MainActivity.this, newText);
                    MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._ID, "suggestion"});
                    if (suggestions==null) suggestions = new ArrayList<>();
                    for (int i = 0; i < suggestions.size(); i++) {
                        if (suggestions.get(i).toLowerCase().contains(newText.toLowerCase())) {
                            cursor.addRow(new Object[]{i, suggestions.get(i)});
                        }
                    }
                    adapter.changeCursor(cursor);
                    return true;
                }
            });

            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    return false;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    Cursor cursor = (Cursor) adapter.getItem(position);
                    int index = cursor.getColumnIndex("suggestion");
                    if (index != -1) {
                        String suggestion = cursor.getString(index);
                        searchView.setQuery(suggestion, true);
                    }
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Toast.makeText(this, "Search icon clicked!", LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
