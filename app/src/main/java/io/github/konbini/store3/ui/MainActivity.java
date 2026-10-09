package io.github.konbini.store3.ui;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import io.github.konbini.store3.R;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView appBtn1, appBtn2, appBtn3, appBtn4, appBtn5;
        appBtn1 = findViewById(R.id.all_apps_button);
        appBtn2 = findViewById(R.id.new_apps_button);
        appBtn3 = findViewById(R.id.popular_apps_button);
        appBtn4 = findViewById(R.id.top_rated_apps_button);
        appBtn5 = findViewById(R.id.random_app_button);

        addPressEffect(appBtn1);
        addPressEffect(appBtn2);
        addPressEffect(appBtn3);
        addPressEffect(appBtn4);
        addPressEffect(appBtn5);

        ImageView adBtn1, adBtn2, gamesButton;
        adBtn1 = findViewById(R.id.ad1_button);
        adBtn2 = findViewById(R.id.ad2_button);
        gamesButton = findViewById(R.id.games_button);

        addPressEffect(adBtn1);
        addPressEffect(adBtn2);
        addPressEffect(gamesButton);

        String[] items = new String[30];
        for (int i = 0; i < items.length; i++) {
            items[i] = "Test app " + (i + 1);
        }

        ListView list = findViewById(R.id.app_list);
        list.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, items));
    }

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
            return false; // let the click listener still fire
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        if (searchView != null) {
            String[] suggestions = {"Coffee", "Bento Box", "Onigiri", "Green Tea", "Ramen", "Energy Drink", "Sandwich", "Ice Cream"};

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
                    MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._ID, "suggestion"});
                    for (int i = 0; i < suggestions.length; i++) {
                        if (suggestions[i].toLowerCase().contains(newText.toLowerCase())) {
                            cursor.addRow(new Object[]{i, suggestions[i]});
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
