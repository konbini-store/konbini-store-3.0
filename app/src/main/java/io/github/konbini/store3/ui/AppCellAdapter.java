package io.github.konbini.store3.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.konbini.store3.R;
import io.github.konbini.store3.api.AppShort;
import io.github.konbini.store3.util.DownloadImagesTask;

public class AppCellAdapter extends BaseAdapter {
    private final Context context;
    private final List<AppShort> items;

    public AppCellAdapter(Context context, List<AppShort> items) {
        this.context = context;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<AppShort>();
    }

    @Override public int getCount() { return items.size(); }
    @Override public Object getItem(int position) { return items.get(position); }
    @Override public long getItemId(int position) { return position; }

    public void setItems(List<AppShort> items) {
        this.items.clear();
        if (items != null)
            this.items.addAll(items);
        this.notifyDataSetChanged();
    }

    @Override
    @SuppressWarnings("deprecation")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.grid_app, parent, false);
            holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.app_icon);
            holder.label = convertView.findViewById(R.id.app_name);
            holder.author = convertView.findViewById(R.id.app_developer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AppShort app = items.get(position);
        holder.label.setText(app.name);
        holder.author.setText(app.author);
        new DownloadImagesTask(holder.icon).execute(app.icon);
        return convertView;
    }

    static class ViewHolder {
        public TextView author;
        ImageView icon;
        TextView label;
    }
}
