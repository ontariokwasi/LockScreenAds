package com.nalosolutions.gakomeng.swypeam;

import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.FontRequest;
import android.provider.FontsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Random;

public class General_Pref extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general__pref);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void switched(View switchview){
        //String viewid = switchview.getTag(0).toString();
        Switch btnswitch = (Switch) findViewById(switchview.getId());
        if(btnswitch.isChecked()){
            if(btnswitch.getText().equals("Health")){
                LinearLayout preflayout = (LinearLayout) findViewById(R.id.pref_layout);
                final Switch switchbtn = new Switch(this);

                String boldText = "Preference Name\n";
                String normalText = "Description to tell user about the chosen preferences and the expected ads. Yeah just making up stuff to occupy space for now.";
                SpannableString str = new SpannableString(boldText + normalText);
                str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                str.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)),0,boldText.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                str.setSpan(new RelativeSizeSpan(0.6f),boldText.length(),str.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                switchbtn.setText(str);

                switchbtn.setId(new Random(100).nextInt());
                Log.d("switch",switchbtn.getId()+" added");
                switchbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switched(view);
                    }
                });
                switchbtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                preflayout.addView(switchbtn);
            }
            Log.d("switch",btnswitch.getText()+" added");
        }
        else{
            Toast.makeText(getApplicationContext(),btnswitch.getText()+" removed",Toast.LENGTH_LONG);
            Log.d("switch",btnswitch.getText()+" removed");
        }
    }

}
