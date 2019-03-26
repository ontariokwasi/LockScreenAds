package com.nalosolutions.gakomeng.swypeam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ScreenReceiver extends BroadcastReceiver {
    public static boolean wasScreenOn = true;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // do whatever you need to do here
            wasScreenOn = false;

            Intent i = new Intent(context, Ads.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
            //start updating the server//
            Intent serviceintent = new Intent(context,Sendviews.class);
            context.startService(serviceintent);

            System.out.println("SCREEN TURNED OFFR");
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // and do whatever you need to do here
            wasScreenOn = true;
            System.out.println("SCREEN TURNED ONR");

            final Save2db save2db = new Save2db(context);
            Makepost makepost = new Makepost(new Makepost.AsyncResponse() {
                @Override
                public void processFinish(Object output) {
                   // do whatever here if post is successful //
                    String ad_id = null,imageurl = null,expdate = null;
                    int maxview = 0;
                    try {
                        JSONObject incoming = new JSONObject(output.toString());
                        if(incoming.getString("status").equals("success")) {
                            ad_id = incoming.getString("ad_id");
                            expdate = incoming.getString("expiry_date");
                            imageurl = incoming.getString("ad_image");
                            maxview = incoming.getInt("no_views");

                            save2db.updatequeue(save2db.queuesize()-1);
                        }
                        else if(incoming.getString("status").equals("no_ad")){
                            //set queue to 0 from here//
                            save2db.updatequeue(0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String finalAd_id = ad_id;
                    final String finalImageurl = imageurl;
                    final String finalExpdate = expdate;
                    final int finalMaxview = maxview;
                    Downloader downloader = new Downloader(new Downloader.AsyncResponse() {
                        @Override
                        public void processFinish(Bitmap output) {
                            //
                            save2db.saveImage(context,output, finalAd_id +".png");
                            //save add to db//
                            save2db.saveAd(finalAd_id, finalImageurl, finalMaxview, finalExpdate);
                        }
                    });
                    if(imageurl != null)
                        downloader.execute(new Makepost(new Makepost.AsyncResponse() {
                            @Override
                            public void processFinish(Object output) {

                            }
                        }).imageUrl+imageurl);

                }
            });

            int currentqueue = save2db.queuesize();
            if(currentqueue > 0){
                //go fetch new image*******//
                String userid = save2db.getuserid();
                JSONObject postdata = new JSONObject();
                try {
                    postdata.put("user_id", userid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(makepost.isNetworkAvailable(context))
                    makepost.execute(makepost.pullAd,postdata.toString());
                    //makepost.execute("https://secure.nalosolutions.com/swypeam/api/sendqueue",postdata.toString());
            }
            Log.d("TQ", currentqueue+"");

        }
        else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_REBOOT)) {
            //system has boot up is completed, start service
            Intent serviceintent = new Intent(context,MyService.class);
            context.startService(serviceintent);
            System.out.println("SYSTEM BOOT COMPLETE");

        }
    }
}

