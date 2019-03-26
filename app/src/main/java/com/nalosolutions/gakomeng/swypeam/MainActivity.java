package com.nalosolutions.gakomeng.swypeam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!new Save2db(this.getApplicationContext()).isuseractive()){
            Intent intent = new Intent(this, RegPhone.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finishAffinity();
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_main);
            startService(new Intent(getBaseContext(), MyService.class));

            ImageView preferencesbtn = (ImageView) findViewById(R.id.preferences_btn);
            preferencesbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getBaseContext(),General_Pref.class));
                }
            });

        }
    }
}
