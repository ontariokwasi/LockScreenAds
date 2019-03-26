package com.nalosolutions.gakomeng.swypeam;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class MyService extends Service {
    public MyService() {
    }
    private static final String TAG = MyService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(Intent.ACTION_REBOOT);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
        /*Intent intents = new Intent(getApplicationContext(),Ads.class);
        intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intents);*/

        //return super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        super.onDestroy();
    }

}
