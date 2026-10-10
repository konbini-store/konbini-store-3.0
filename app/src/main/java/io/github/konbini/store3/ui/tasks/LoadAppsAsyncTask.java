package io.github.konbini.store3.ui.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.github.konbini.store3.api.Api;
import io.github.konbini.store3.api.AppShort;
import io.github.konbini.store3.ui.AppListAdapter;

@SuppressWarnings("deprecation")
public class LoadAppsAsyncTask extends AsyncTask<Void, Void, ArrayList<AppShort>> {
    private final WeakReference<ListView> listView;
    private final WeakReference<Context> context;

    public LoadAppsAsyncTask(Context context, ListView listView) {
        this.listView = new WeakReference<>(listView);
        this.context = new WeakReference<>(context);
    }

    @Override
    protected void onPostExecute(ArrayList<AppShort> result) {
        ListView lv = listView.get();
        if (lv == null || isCancelled() || result == null) return;

        ListAdapter adapter = lv.getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
        }
        if (adapter instanceof AppListAdapter) {
            ((AppListAdapter) adapter).setItems(result);
        }
    }
    @Override
    protected ArrayList<AppShort> doInBackground(Void... params) {
        return Api.getInstance(context.get()).getFeaturedApps(context.get());
    }
}
