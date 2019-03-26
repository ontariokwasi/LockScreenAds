package com.nalosolutions.gakomeng.swypeam;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Sendviews extends Service {
    public Sendviews() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*
        * 1. send all completed views to the server ie check if total views is up to the max views
        * Also check for expired ads and delete them after updating the server.
        * 2. delete record from database and remove file too on successful update to the server
        * */
        final Save2db save2db = new Save2db(getApplicationContext());
        final String impressions = save2db.Impressions();
        Makepost makepost = new Makepost(new Makepost.AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                //do whatever here after successful call to server
                try {
                    JSONObject updateresponse = new JSONObject(output.toString());
                    String status = updateresponse.getString("status");
                    if(status.equals("success")){
                        int adsqueue = updateresponse.getInt("ads_queue");
                        //update user's queue with new adsqueue
                        save2db.updatequeue(adsqueue);
                        //delete updated ads
                        JSONObject impressionsobject = new JSONObject(impressions);
                        JSONArray ads = impressionsobject.getJSONArray("ads");
                        for(int i=0; i < ads.length(); i++){
                            JSONObject individualad = ads.getJSONObject(i);
                            if(save2db.deleteadimage(getApplicationContext(),individualad.getString("ad_id")))
                                Log.d("ad_id"+i, individualad.getString("ad_id")+" was deleted");
                            else
                                Log.d("ad_id"+i, individualad.getString("ad_id")+" Failed to delete");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        if(makepost.isNetworkAvailable(getApplicationContext())){
            //makepost.execute("https://secure.nalosolutions.com/swypeam/api/update/",impressions);
            makepost.execute(makepost.updateviews,impressions);
        }
        return Service.START_STICKY;
    }
}
