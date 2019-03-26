package com.nalosolutions.gakomeng.swypeam;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextClock;

import java.util.Random;

public class Ads extends AppCompatActivity {
    public ImageButton padlock;
    public ImageButton unlockscreen ;
    public ImageButton settings ;
    public TextClock textClock ;
    int previousposition = 0;
    String ad_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES);
        setContentView(R.layout.activity_ads);

        //change();
        padlock = (ImageButton) findViewById(R.id.unlockbtn);
        unlockscreen = (ImageButton) findViewById(R.id.unlockimage);
        settings = (ImageButton) findViewById(R.id.settings);
        textClock = (TextClock) findViewById(R.id.textClock);
        textClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(textClock.INVISIBLE);
            }
        });
        padlock.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View.DragShadowBuilder padlockshadow = new View.DragShadowBuilder(padlock);
                view.startDrag(null,padlockshadow,null,0);
                //unlockscreen.setVisibility(unlockscreen.VISIBLE);
                //settings.setVisibility(settings.VISIBLE);
                finish();
                return false;
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        //viewPager.removeView(viewPager.getRootView());
        final Save2db save2db = new Save2db(getApplicationContext());

        final ImageAdapter adapter = new ImageAdapter(getApplicationContext()); //Here we are defining the Imageadapter object

        //adapter.GalImages[adapter.GalImages.length-1] = R.drawable.fimg;
        //adapter.notifyDataSetChanged();
        viewPager.setHapticFeedbackEnabled(true);
        viewPager.setAdapter(adapter); // Here we are passing and setting the adapter for the images
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.d("current_Item1: ", position+"");
            }

            @Override
            public void onPageSelected(int position) {
                //Log.d("current_Item: ", adapter.files[position].getName().replace(".png",""));
                ad_id = adapter.files[position].getName().replace(".png","");
                //Log.d("adviews: ", save2db.getviews(ad_id)+"");
                if(position > previousposition){
                    previousposition = position;
                    int totalviews = save2db.getviews(ad_id)+1;
                    save2db.updateviews(ad_id,totalviews);
                    Log.d("adviews1: ", save2db.getviews(ad_id)+"");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("current_state: ", state+"");
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        //this.finish();
        Log.d("TAG", "onStop: stating api call");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //return super.onKeyDown(keyCode, event);
        return false;
    }

    int imgid = new Random().nextInt(5);
    public void close(View view){
        this.finish();
    }
    public void change(){
        ImageView ads = (ImageView) findViewById(R.id.adsview);
        if(imgid == 1){
            ads.setBackgroundResource(R.drawable.flag_afghanistan);
            //imgid++;
        }
        else if(imgid == 2){
            ads.setBackgroundResource(R.drawable.flag_aland);
            //imgid++;
        }
        else if(imgid == 3){
            ads.setBackgroundResource(R.drawable.flag_american_samoa);
            //imgid++;
        }
        else if(imgid == 4){
            ads.setBackgroundResource(R.drawable.flag_algeria);
            //imgid++;
        }
        else if(imgid == 5){
            ads.setBackgroundResource(R.drawable.flag_andorra);
            //imgid++;
        }
        else{
            ads.setBackgroundResource(R.drawable.flag_argentina);
            imgid = 0;
        }
    }
    public void next(View view){
        imgid++;
        change();
    }
    public void prev(View view){
        imgid--;
        change();
    }
   /* public void toggle(View view){

        if(padlock.getVisibility() == View.VISIBLE){
            padlock.setVisibility(padlock.INVISIBLE);
            unlockscreen.setVisibility(unlockscreen.INVISIBLE);
            settings.setVisibility(settings.INVISIBLE);
            textClock.setVisibility(textClock.INVISIBLE);
        }
        else {
            padlock.setVisibility(padlock.VISIBLE);
            unlockscreen.setVisibility(unlockscreen.VISIBLE);
            settings.setVisibility(settings.VISIBLE);
            textClock.setVisibility(textClock.VISIBLE);
        }
    }
    */
}