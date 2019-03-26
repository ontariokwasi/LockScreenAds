package com.nalosolutions.gakomeng.swypeam;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.READ_PHONE_STATE;

public class ConfirmOtp extends AppCompatActivity {
    private EditText pinbox;
    private TextView resendpin;
    private String msgData,matchbody,phone,userid,otp,deviceid;
    private Button confirm;
    private Boolean isactivated = false;
    private Timer timer = new Timer();
    @Override
    public void onBackPressed() {

        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_otp);

        phone = getIntent().getExtras().getString("phone");
        userid = getIntent().getExtras().getString("userID");
        otp = getIntent().getExtras().getString("otp");
        deviceid = getIntent().getExtras().getString("deviceid");
        pinbox = (EditText) findViewById(R.id.pinbox);
        resendpin = (TextView) findViewById(R.id.resendpin);
        confirm = (Button) findViewById(R.id.confirmpin);
        //pinbox.setText(phone);
        if (checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            String smsbody = readsms();
            String pin;
            try {
                if(!smsbody.isEmpty()) {
                    pin = smsbody.substring(smsbody.indexOf("is") + 3);
                    //call for validation with pin//
                    if(isvalidpin(pin)){
                        pinbox.setText(pin);
                    }
                    else{
                        timer.schedule(doTask, 10, 3000);
                    }
                }
                else{
                    timer.schedule(doTask, 10, 3000);
                }
            }
            catch (Exception e){

            }

        }
        else{
            //do something here
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin = pinbox.getText().toString();
                if(pin.isEmpty() || pin.length() < 6){
                    pinbox.setError("Invalid pin");
                    return;
                }
                else{
                    //make validation call..
                    if(isvalidpin(pin)){
                        //save details in db
                        Save2db save2db = new Save2db(getApplicationContext());
                        save2db.insertUserData(deviceid,phone,userid,0);
                        Log.e("Tag", save2db.getuserData());
                        //activate user
                        Makepost makepost = new Makepost(new Makepost.AsyncResponse() {
                            @Override
                            public void processFinish(Object output) {
                                try {
                                    JSONObject jsonObject = new JSONObject(output.toString());
                                    String status = jsonObject.getString("status");
                                    int queue = jsonObject.getInt("ads_queue");
                                    if(status.equals("account_activated")){
                                        Save2db save2db = new Save2db(getApplicationContext());
                                        save2db.updatequeue(queue);
                                        Log.e("Tag", save2db.getuserData());

                                        //
                                        startService(new Intent(getBaseContext(), MyService.class));
                                        finishAffinity();
                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    }
                                    else{
                                        Log.e("AE",output.toString());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        JSONObject sendjson = new  JSONObject();
                        try {
                            sendjson.put("user_id",userid);
                            sendjson.put("code", pin);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        makepost.execute(makepost.otpConfirm,sendjson.toString());

                        Downloader downloader = new Downloader(new Downloader.AsyncResponse() {
                            @Override
                            public void processFinish(Bitmap output) {
                                new Save2db(getApplicationContext()).saveImage(getApplicationContext(),output,"testimage1.png");
                                File file = getApplicationContext().getFileStreamPath("testimage.png");
                                String imageFullPath = file.getAbsolutePath();
                                Log.e("IML",imageFullPath+"-->"+getApplicationContext().getFilesDir().getAbsolutePath());
                                Toast.makeText(ConfirmOtp.this, imageFullPath +" -> "+ Environment.getDataDirectory().getAbsolutePath(), Toast.LENGTH_LONG).show();

//                                ImageView imageView = (ImageView) findViewById(R.id.testimv);
//                                imageView.setImageBitmap( new Save2db(getApplicationContext()).loadImageBitmap(getApplicationContext(), "testimage.png"));
                            }
                        });
                        downloader.execute("https://tpc.googlesyndication.com/daca_images/simgad/2019182879296879463");
                        //startservice
                        /*Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.nalosolutions.onscreenlock");
                        if (launchIntent != null) {
                            //startActivity(launchIntent);
                        }*/
                        //finish();
                    }
                    else{
                        pinbox.setError("Wrong pin, try again");
                    }

                }
            }
        });
    }
    //read sms//
    private String readsms(){
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        try {
            if (cursor.moveToFirst()) { // must check the result to prevent exception
                msgData = "";
                String matchsender = "";
                matchbody = "";
                do {
                    for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                        msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx) + "\n";
                        if (cursor.getColumnName(idx).equals("address") && cursor.getString(idx).equals("Swypeam")) {
                            matchsender = cursor.getString(idx);
                        }
                        if (cursor.getColumnName(idx).equals("body") && matchsender.equals("Swypeam")) {
                            matchbody = cursor.getString(idx);
                            return matchbody;
                        }
                    }
                    // use msgData
                } while (cursor.moveToNext());
            } else {
                // empty box, no SMS
            }
            return matchbody;
        }
        catch (Exception ex){
            return null;
        }
        finally {
            try {
                if( cursor != null && !cursor.isClosed())
                    cursor.close();
            } catch(Exception ex) {}
        }
    }

    private boolean isvalidpin(String pin){
        if(pin.equals(otp)){
            timer.cancel();
            return true;
        }
        else
            return false;
    }

    final Handler handler = new Handler();
    TimerTask doTask = new TimerTask() {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @SuppressWarnings("unchecked")
                public void run() {
                    try {
                        String smsbody = readsms();
                        String pin;
                        if(!smsbody.isEmpty()) {
                            pin = smsbody.substring(smsbody.indexOf("is") + 3);
                            if(isvalidpin(pin)){
                                pinbox.setText(pin);
                            }
                        }
                        Log.e("TimerLog","timer running..");
                    }
                    catch (Exception e) {
                        Log.e("TimerException",e.toString());
                    }
                }
            });
        }
    };

    /*timer.schedule(doTask, 0, "Your time 10 minute");*/

}
