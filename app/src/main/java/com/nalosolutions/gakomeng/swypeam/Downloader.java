package com.nalosolutions.gakomeng.swypeam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gakomeng on 29/11/2018.
 */

public class Downloader extends AsyncTask<String, Void, Bitmap> {
    private String TAG = "DownloadImage";
    public interface AsyncResponse {
        void processFinish(Bitmap output);
    }
    public Downloader.AsyncResponse delegate = null;

    public Downloader(Downloader.AsyncResponse asyncResponse) {
        delegate = asyncResponse;//Assigning call back interfacethrough constructor
    }
    private Bitmap downloadImageBitmap(String sUrl) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(sUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Accept","text/html");
            //InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
            InputStream inputStream = con.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
            inputStream.close();
        } catch (Exception e) {
            Log.d(TAG, "Exception: "+e.toString());
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return downloadImageBitmap(params[0]);
    }
    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }

}