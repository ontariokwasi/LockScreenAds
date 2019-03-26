package com.nalosolutions.gakomeng.swypeam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Makepost extends AsyncTask<String, Void, String> {

    public String response;
    public interface AsyncResponse {
        void processFinish(Object output);
    }
    public AsyncResponse delegate = null;

    public Makepost(AsyncResponse asyncResponse) {
        delegate = asyncResponse;//Assigning call back interfacethrough constructor
    }
    @Override
    protected String doInBackground(String... params) {

        String data = "";

        HttpURLConnection httpURLConnection = null;
        try {

            httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("content-type","application/json");
            httpURLConnection.setRequestProperty("Accept","application/json");

            httpURLConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(params[1]);
            wr.flush();
            wr.close();

            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                data += current;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Log.e("TAG", result);
        delegate.processFinish(result);
    }

    /*public boolean isconnected (){
        ConnectivityManager connectivityManager =  getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else
            return false;
    }*/
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected() ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
            return true;
        }
        else
            return false;
        //NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    String login = "https://secure.nalosolutions.com/swypeam/api/login";
    String otpConfirm = "https://secure.nalosolutions.com/swypeam/api/otpConfirm";
    String confirmSwitch = "https://secure.nalosolutions.com/swypeam/api/confirmSwitch";
    String switchAccount = "https://secure.nalosolutions.com/swypeam/api/switchAccount";
    String updateviews = "https://secure.nalosolutions.com/swypeam/api/update";
    String pullAd = "https://secure.nalosolutions.com/swypeam/api/pullAd";
    String preferences = "https://secure.nalosolutions.com/swypeam/api/loadPrefOptions";
    String progress = "https://secure.nalosolutions.com/swypeam/api/fetchUserAccount";
    String apiUrl = "https://secure.nalosolutions.com/swypeam/api/";
   String imageUrl = "https://secure.nalosolutions.com";
}
