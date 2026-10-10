package io.github.konbini.store3.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.github.konbini.store3.R;
import io.github.konbini.store3.api.Api;
import io.github.konbini.store3.api.Category;
import io.github.konbini.store3.db.Database;

public class CategoryFilterFragment extends Fragment {

    public interface OnCategorySelectedListener {
        void onCategorySelected(Category category);
    }

    public static final String DEFAULT_CATEGORY_ID = "";
    public static final String DEFAULT_CATEGORY_NAME = "All apps/games";

    private ListView listView;
    private CategoryAdapter adapter;
    private boolean isGame = false;
    private String activeCategoryId = DEFAULT_CATEGORY_ID;
    private OnCategorySelectedListener listener;

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnCategorySelectedListener) {
            listener = (OnCategorySelectedListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_filter_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            isGame = args.getBoolean("is_game", false);
            if (args.containsKey("active_category_id")) {
                activeCategoryId = args.getString("active_category_id");
                if (activeCategoryId == null) {
                    activeCategoryId = DEFAULT_CATEGORY_ID;
                }
            }
        }

        listView = view.findViewById(R.id.category_list);

        ArrayList<Category> initialList = new ArrayList<>();
        initialList.add(new Category(DEFAULT_CATEGORY_ID, DEFAULT_CATEGORY_NAME));

        adapter = new CategoryAdapter(getActivity(), initialList, activeCategoryId);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, v, position, id) -> {
            Category selected = adapter.getItem(position);
            if (selected != null) {
                activeCategoryId = selected.getId();
                adapter.setActiveCategoryId(activeCategoryId);
                if (listener != null) {
                    listener.onCategorySelected(selected);
                } else if (getActivity() instanceof OnCategorySelectedListener) {
                    ((OnCategorySelectedListener) getActivity()).onCategorySelected(selected);
                }
            }
        });

        loadCategories();
    }

    public void setActiveCategory(String categoryId) {
        this.activeCategoryId = categoryId != null ? categoryId : DEFAULT_CATEGORY_ID;
        if (adapter != null) {
            adapter.setActiveCategoryId(this.activeCategoryId);
        }
    }

    public String getActiveCategoryId() {
        return activeCategoryId;
    }

    private void loadCategories() {
        if (getActivity() == null) return;

        // TODO
        new AsyncTask<Void, Void, List<Category>>() {
            @Override
            protected List<Category> doInBackground(Void... voids) {
                Context context = getActivity();
                if (context == null) return null;

                List<Category> list = new ArrayList<>();
                list.add(new Category(DEFAULT_CATEGORY_ID, DEFAULT_CATEGORY_NAME));

                try {
                    JSONArray jsonArray = Api.getInstance(context).getCategories(context, isGame);
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Category cat = new Category(jsonArray.getJSONObject(i));
                            if (!containsCategory(list, cat.getId())) {
                                list.add(cat);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("CategoryFilterFragment", "Error loading categories from API: ", e);
                }

                try {
                    List<Category> dbCats = Database.getCategories(context);
                    if (dbCats != null) {
                        for (Category cat : dbCats) {
                            if (!containsCategory(list, cat.getId())) {
                                list.add(cat);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("CategoryFilterFragment", "Error loading categories from DB: ", e);
                }

                return list;
            }

            @Override
            protected void onPostExecute(List<Category> result) {
                if (result != null && isAdded() && adapter != null) {
                    adapter.setCategories(result);
                }
            }
        }.execute();
    }

    private static boolean containsCategory(List<Category> list, String id) {
        if (id == null) id = "";
        for (Category cat : list) {
            if (id.equalsIgnoreCase(cat.getId())) {
                return true;
            }
        }
        return false;
    }

    public static class CategoryAdapter extends BaseAdapter {
        private final Context context;
        private final List<Category> categories;
        private String activeCategoryId;

        public CategoryAdapter(Context context, List<Category> categories, String activeCategoryId) {
            this.context = context;
            this.categories = categories != null ? new ArrayList<>(categories) : new ArrayList<Category>();
            this.activeCategoryId = activeCategoryId != null ? activeCategoryId : DEFAULT_CATEGORY_ID;
        }

        public void setActiveCategoryId(String activeCategoryId) {
            this.activeCategoryId = activeCategoryId != null ? activeCategoryId : DEFAULT_CATEGORY_ID;
            notifyDataSetChanged();
        }

        public void setCategories(List<Category> newCategories) {
            this.categories.clear();
            if (newCategories != null) {
                this.categories.addAll(newCategories);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public Category getItem(int position) {
            return categories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.category_row, parent, false);
            }

            TextView textView = convertView.findViewById(R.id.category_name);
            if (textView == null && convertView instanceof TextView) {
                textView = (TextView) convertView;
            }

            Category category = getItem(position);
            if (textView != null && category != null) {
                textView.setText(category.getName());

                boolean isActive = (category.getId().equalsIgnoreCase(activeCategoryId))
                        || (category.getId().isEmpty() && (activeCategoryId == null || activeCategoryId.isEmpty()));

                if (isActive) {
                    textView.setTypeface(null, Typeface.BOLD);
                } else {
                    textView.setTypeface(null, Typeface.NORMAL);
                }
            }

            return convertView;
        }
    }
}
