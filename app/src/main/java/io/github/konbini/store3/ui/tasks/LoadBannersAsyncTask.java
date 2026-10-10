package io.github.konbini.store3.ui.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Objects;

import io.github.konbini.store3.api.Api;
import io.github.konbini.store3.api.Banner;
import io.github.konbini.store3.util.DownloadImagesTask;

@SuppressWarnings("deprecation")
public class LoadBannersAsyncTask extends AsyncTask<Void, Void, Banner> {
    private final WeakReference<ImageView> banner;
    private final WeakReference<Context> context;

    public LoadBannersAsyncTask(Context context, ImageView banner) {
        this.banner = new WeakReference<>(banner);
        this.context = new WeakReference<>(context);
    }

    private void launchIntent(Activity activity, Uri uri, boolean isInternal) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (isInternal) {
                intent.setPackage(activity.getPackageName());
            }

            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e("LoadBannersAsyncTask", "No app available to handle URL: " + uri, e);
            Toast.makeText(activity, "Failed to open :(", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("LoadBannersAsyncTask", "Failed to open target URL: ", e);
        }
    }


    @Override
    protected void onPostExecute(Banner result) {
        ImageView banner = this.banner.get();
        String imageUrl = result.getImageUrl();
        String targetUrl = result.getTargetUrl();
        new DownloadImagesTask(banner).execute(imageUrl);
        banner.setOnClickListener(v -> {
            final Uri uri = Uri.parse(targetUrl);
            if (!Objects.equals(uri.getScheme(), "konbini")) {
                new AlertDialog.Builder(context.get())
                        .setTitle("Warning")
                        .setMessage("This banner leads to a third-party website. Want to continue?")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            launchIntent((Activity) context.get(), uri, false);
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            } else {
                launchIntent((Activity) context.get(), uri, true);
            }
        });

    }
    @Override
    protected Banner doInBackground(Void... voids) {
        return Api.getInstance(context.get()).getRandomBanner(context.get());
    }
}
