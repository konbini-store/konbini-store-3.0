package io.github.konbini.store3.ui.tasks;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import io.github.konbini.store3.R;
import io.github.konbini.store3.api.Api;
import io.github.konbini.store3.api.App;
import io.github.konbini.store3.ui.AppDetailsActivity;
import io.github.konbini.store3.util.DownloadImagesTask;

@SuppressWarnings("deprecation")
public class LoadAppDetailsAsyncTask extends AsyncTask<String, Void, App> {
    private final WeakReference<AppDetailsActivity> context;
    private final Boolean[] descriptionCollapsed = new Boolean[] { true };

    public LoadAppDetailsAsyncTask(AppDetailsActivity context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    protected void onPostExecute(App app) {
        AppDetailsActivity activity = context.get();
        if (app == null) {
            Toast.makeText(activity, "App not found.", Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }

        TextView appName = activity.findViewById(R.id.app_name);
        TextView appDeveloper = activity.findViewById(R.id.app_developer);
        ImageView appIcon = activity.findViewById(R.id.app_icon);

        TextView tagline = activity.findViewById(R.id.tagline);
        TextView description = activity.findViewById(R.id.description);
        TextView toggleDescription = activity.findViewById(R.id.toggle_description);

        appName.setText(app.name);
        appDeveloper.setText(app.author);
        new DownloadImagesTask(appIcon).execute(app.icon);
        float density = activity.getResources().getDisplayMetrics().density;
        if (app.tagline == null || app.tagline.isEmpty()) {
            tagline.setVisibility(GONE);
            if (description.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)
                        description.getLayoutParams();

                int marginInPx = (int) (12 * density);

                params.setMargins(params.leftMargin, marginInPx, params.rightMargin, params.bottomMargin);

                description.setLayoutParams(params);
                description.requestLayout();
            }
            setTopMargin(description, density, 12);
        } else {
            tagline.setText(app.tagline);
            tagline.setVisibility(VISIBLE);
            setTopMargin(tagline, density, 12);
            setTopMargin(description, density, 4);
        }
        if (app.description.length() > 250) {
            final String descriptionString = app.description.substring(0, 250) + "...";
            toggleDescription.setVisibility(VISIBLE);
            toggleDescription.setOnClickListener(v -> {
                if (LoadAppDetailsAsyncTask.this.descriptionCollapsed[0]) {
                    toggleDescription.setText("collapse");
                    description.setText(app.description);
                    LoadAppDetailsAsyncTask.this.descriptionCollapsed[0] = false;
                    return;
                }
                toggleDescription.setText("expand");
                description.setText(descriptionString);
                LoadAppDetailsAsyncTask.this.descriptionCollapsed[0] = true;
            });
            description.setText(descriptionString);
        } else {
            description.setText(app.description);
            toggleDescription.setVisibility(GONE);
        }
    }

    private void setTopMargin(View view, float density, int top_margin) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)
                    view.getLayoutParams();

            int marginInPx = (int) (top_margin * density);

            params.setMargins(params.leftMargin, marginInPx, params.rightMargin, params.bottomMargin);

            view.setLayoutParams(params);
            view.requestLayout();
        }
    }

    @Override
    protected App doInBackground(String... args) {
        return Api.getInstance(context.get()).getAppByPackage(context.get(), args[0]);
    }
}
