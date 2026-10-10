package io.github.konbini.store3.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.konbini.store3.R;
import io.github.konbini.store3.api.AppShort;
import io.github.konbini.store3.util.DownloadImagesTask;

public class AppListAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final List<AppShort> items = new ArrayList<>();

    public AppListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    // Call this when your backend response arrives
    public void setItems(List<AppShort> newItems) {
        items.clear();
        if (newItems != null)
            items.addAll(newItems);
        notifyDataSetChanged(); // must be called on the UI thread
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public AppShort getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    @SuppressWarnings("deprecation")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.main_app_row, parent, false);
            h = new ViewHolder();
            h.hitbox = convertView.findViewById(R.id.clickable_area);
            h.icon = convertView.findViewById(R.id.app_icon);
            h.name = convertView.findViewById(R.id.app_name);
            h.developer = convertView.findViewById(R.id.app_developer);
            h.rating = convertView.findViewById(R.id.app_rating);
            h.tagline = convertView.findViewById(R.id.app_tagline);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        AppShort app = getItem(position);
        h.name.setText(app.name);
        h.developer.setText(app.author);
        h.rating.setText(String.valueOf(app.rating));
        h.tagline.setText(app.description);
        h.icon.setImageResource(R.mipmap.ic_launcher);
        new DownloadImagesTask(h.icon).execute(app.icon);

        return convertView;
    }

    private static class ViewHolder {
        public TextView tagline;
        public FrameLayout hitbox;
        ImageView icon;
        TextView name;
        TextView developer;
        TextView rating;
    }
}