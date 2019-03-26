package com.nalosolutions.gakomeng.swypeam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class RegPhone extends AppCompatActivity {

    private CountryCodePicker countryCodePicker;
    private EditText phonenumber;
    private Button verify;
    private TextView testview, errorText;
    String []networks = new String[]{"Select network","MTN","AIRTELTIGO","VODAFONE"};
    private String countryName,device_id,msgData,matchbody;
    private View mProgressView;
    private static final int REQUEST_READ_CONTACTS=0,REQUEST_READ_SMS = 0, REQUEST_READ_PHONE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reg_phone);

        //show permission request notice dialogue;
        //mayRequestPhone();

        //get phone's display//
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        //

        final Spinner ntwks = (Spinner) findViewById(R.id.network2);
        ArrayAdapter<String> netwkadapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,networks);
        ntwks.setAdapter(netwkadapter);
        countryCodePicker = (CountryCodePicker) findViewById(R.id.ccp);
        phonenumber = (EditText) findViewById(R.id.phonenumber);
        testview = (TextView) findViewById(R.id.testview);
        verify = (Button) findViewById(R.id.verify);
        mProgressView = findViewById(R.id.login_progress2);
        countryCodePicker.setDefaultCountryUsingNameCode("GH");
        countryCodePicker.resetToDefaultCountry();
        countryCodePicker.registerCarrierNumberEditText(phonenumber);

        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                //testview.setText(countryCodePicker.getSelectedCountryEnglishName());
                //do something here, probably change the network adapter items;
                countryName = countryCodePicker.getSelectedCountryEnglishName();
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {

                if (!countryCodePicker.isValidFullNumber()) {
                    testview.setText(countryCodePicker.getFullNumber() + " is not a valid number for "+countryCodePicker.getSelectedCountryEnglishName());
                    phonenumber.setError("Invalid mobile number");
                }
                else if(ntwks.getSelectedItem().toString().equals("Select network")){
                    //setspinner errorText
                    errorText = (TextView)ntwks.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Select network");
                }
                else {
                    //
                    final MakepostIN senddata =  new MakepostIN();
                    final Makepost makepost = new Makepost(new Makepost.AsyncResponse() {
                        @Override
                        public void processFinish(Object output) {

                        }
                    });

                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if(mayRequestPhone()){
                        //function to read phoneid;
                        device_id = tm.getDeviceId();
                    }
                    else{
                        return;
                    }

                    //Alert dialogue to inform the user the number will be verified//
                    final AlertDialog.Builder builder = new AlertDialog.Builder(RegPhone.this);
                    builder.setCancelable(false);
                    builder.setMessage("Swypeam will verify your phone number: "+countryCodePicker.getFullNumberWithPlus()+
                            "("+ntwks.getSelectedItem()+")\n Press OK to continue or EDIT to make changes");
                    builder.setTitle("Confirmation");
                    builder.setIcon(R.drawable.ic_logo);
                    builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //start loader...
                            mProgressView.setVisibility(View.VISIBLE);

                            //call async task to verify//
                            if(mayRequestSMS()){
                                //readsms
                            }
                            else{
                                //don't read sms
                            }
                            //startActivity(new Intent(getApplication(),ConfirmOtp.class));
                            //String smsbody = readsms();
                            String jsondata = jsondata(countryCodePicker.getFullNumber(), height+"x"+width, device_id, ntwks.getSelectedItem().toString());

                            if(senddata.isNetworkAvailable(getApplicationContext())){
                                try {
                                    senddata.execute(makepost.login, jsondata);
                                }
                                catch (Exception e){
                                    Log.e("Error",e.toString());
                                }

                                //senddata.execute("https://secure.nalosolutions.com/swypeam/api/login", jsondata);
                            }
                            else {
                                mProgressView.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    //testview.setText("Screen size(HxW): "+height+"x"+width+"DeviceID:"+device_id+"ntwk:"+ntwks.getSelectedItem().toString()
                    //      +"operator: "+tm.getNetworkOperatorName()+" number: "+tm.getLine1Number());
                    //
                    //showProgress(true);
                    //mProgressView.setVisibility(View.VISIBLE);
//                    try {
//                        verify.setVisibility(View.INVISIBLE);
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    //showProgress(false);
                    //startActivity(new Intent(getApplication(),ConfirmOtp.class));
                }
            }
        });

    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(phonenumber, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS,CAMERA}, REQUEST_READ_CONTACTS);
                        }
                    }).show();
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    private boolean mayRequestPhone() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        else if (checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
            Snackbar.make(phonenumber, R.string.permission_rationale_phone, Snackbar.LENGTH_LONG)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            //requestPermissions(new String[]{READ_PHONE_STATE}, REQUEST_READ_PHONE);
                            ActivityCompat.requestPermissions(RegPhone.this,new String[]{READ_PHONE_STATE},REQUEST_READ_PHONE);
                        }
                    }).show();
        }
        else {
            AlertDialog.Builder permRequestNotice = new AlertDialog.Builder(this);
            permRequestNotice.setIcon(R.drawable.ic_logo);
            permRequestNotice.setTitle("Permission Request");
            permRequestNotice.setMessage(R.string.noticephonemessage);
            permRequestNotice.setCancelable(false);
            permRequestNotice.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    requestPermissions(new String[]{READ_PHONE_STATE}, REQUEST_READ_PHONE);
                }
            });
            permRequestNotice.show();
        }
        return false;
    }

    private boolean mayRequestSMS() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        else if (checkSelfPermission(READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else if (shouldShowRequestPermissionRationale(READ_SMS)) {
            Snackbar.make(phonenumber, R.string.permission_rationale_message, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{Manifest.permission.READ_SMS}, REQUEST_READ_SMS);
                        }
                    }).show();
        }
        else {
            AlertDialog.Builder permRequestNotice = new AlertDialog.Builder(this);
            permRequestNotice.setIcon(R.drawable.ic_logo);
            permRequestNotice.setTitle("Permission Request");
            permRequestNotice.setMessage(R.string.noticeSMSmessage);
            permRequestNotice.setCancelable(false);
            permRequestNotice.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    requestPermissions(new String[]{Manifest.permission.READ_SMS}, REQUEST_READ_SMS);
                }
            });
            permRequestNotice.show();
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Toast.makeText(this,"helloWorld",Toast.LENGTH_LONG);
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //populateAutoComplete();
                Toast.makeText(this,permissions[0],Toast.LENGTH_LONG);
            }
        }

        if (requestCode == REQUEST_READ_SMS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mayRequestSMS();
            }
        }
    }

    //read sms//
    private String readsms(){
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            msgData = "";
            String matchsender = "";
            matchbody = "";
            do {
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx)+"\n";
                    if(cursor.getColumnName(idx).equals("address") && cursor.getString(idx).equals("Swypeam")){
                        matchsender = cursor.getString(idx);
                    }
                    if(cursor.getColumnName(idx).equals("body") && matchsender.equals("Swypeam")){
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

    //form jsondata to post to server
    private String jsondata(String msisdn, String screen_size, String phone_id, String network) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("msisdn", msisdn);
            postData.put("screen_size", screen_size);
            postData.put("phone_id", phone_id);
            postData.put("network", network);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData.toString();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }

    }

    public class MakepostIN extends AsyncTask<String, Void, String> {

        public String response;
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
            Log.e("TAG", result);
            response = result;
            //test  view.setText("Request: "+testview.getText()+"\n Response: "+result);
            mProgressView.setVisibility(View.INVISIBLE);

            //decode incoming json;
            try {
                JSONObject responsedata = new JSONObject(result);
                String status = responsedata.getString("status");
                String userID = responsedata.getString("user_id");
                String phone = countryCodePicker.getFullNumberWithPlus();
                if(status.equals("pending_password" )|| status.equals("complete_registration")){
                    String pin = responsedata.getString("pin");
                    Intent intent = new Intent(getApplicationContext(),ConfirmOtp.class);
                    intent.putExtra("phone",phone);
                    intent.putExtra("userID",userID);
                    intent.putExtra("otp",pin);
                    intent.putExtra("deviceid",device_id);
                    startActivityForResult(intent,1);
                }
                else if(status.equals("login_success")){
                    Save2db save2db = new Save2db(getApplicationContext());
                    save2db.insertUserData(device_id,countryCodePicker.getFullNumberWithPlus(),userID,0);
                    save2db.updatequeue(responsedata.getInt("ads_queue"));
                    finishAffinity();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
                else {
                    Toast.makeText(getApplicationContext(),"Error: "+status,Toast.LENGTH_LONG);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected() ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
                return true;
            }
            else
                return false;
        }
    }
}

