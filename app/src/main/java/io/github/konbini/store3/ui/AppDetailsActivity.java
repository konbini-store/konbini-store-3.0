package io.github.konbini.store3.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.github.konbini.store3.R;
import io.github.konbini.store3.api.Api;
import io.github.konbini.store3.api.App;
import io.github.konbini.store3.ui.tasks.LoadAppDetailsAsyncTask;
import io.github.konbini.store3.util.DownloadImagesTask;

public class AppDetailsActivity extends Activity {
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.app_details_activity);

        String packageName = getIntent().getStringExtra("package_name");
        if (packageName == null) {
            Toast.makeText(this, "No app to show!", Toast.LENGTH_SHORT).show();
            finish();
        }

        new LoadAppDetailsAsyncTask(this).execute(packageName);
    }
}