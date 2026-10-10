package io.github.konbini.store3.util;

// Source - https://stackoverflow.com/a/16979546
// Posted by Morteza Mousavi
// Retrieved 2026-10-09, License - CC BY-SA 3.0

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@SuppressWarnings("deprecation")
public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imgView;

    public DownloadImagesTask(ImageView imgView) {
        this.imgView = new WeakReference<>(imgView);
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        return download_Image(urls[0]);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        this.imgView.get().setImageBitmap(result);
    }

    private Bitmap download_Image(String url) {
        //---------------------------------------------------
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("DownloadImagesTask", "Something went wrong: ", e);
        }
        return bm;
        //---------------------------------------------------
    }


}
