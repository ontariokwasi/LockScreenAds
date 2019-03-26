package com.nalosolutions.gakomeng.swypeam;

/**
 * Created by gakomeng on 29/10/2018.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;


public class Save2db {
    myDbHelper myhelper;

    public Save2db(Context context) {
        //myhelper = new myDbHelper(context);
        myhelper = myDbHelper.getInstance(context);
    }

    public long insertUserData(String deviceid, String msisdn, String userid, int totalscore) {
        truncateusertable();
        //continue to insert new row
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.deviceID, deviceid);
        contentValues.put(myDbHelper.msisdn, msisdn);
        contentValues.put(myDbHelper.userID, userid);
        contentValues.put(myDbHelper.score, totalscore);
        long id = dbb.insert(myDbHelper.USER_TABLE, null, contentValues);
        return id;
    }

    public long newpreference(String p_id, String p_name, String p_status) {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.p_id, p_id);
        contentValues.put(myDbHelper.p_name, p_name);
        contentValues.put(myDbHelper.p_status, p_status);
        long id = dbb.insert(myDbHelper.PREFERENCES_TABLE, null, contentValues);
        return id;
    }

    public long saveAd(String ad_id, String ad_image, int maxview, String expiry) {
        //truncateusertable();
        //continue to insert new row
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ad_id, ad_id);
        contentValues.put(myDbHelper.ad_image, ad_image);
        contentValues.put(myDbHelper.maxview, maxview);
        contentValues.put(myDbHelper.expiry, expiry);
        long id = dbb.insert(myDbHelper.ADS_TABLE, null, contentValues);
        return id;
    }

    public String getuserData() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID, myDbHelper.deviceID, myDbHelper.userID, myDbHelper.msisdn, myDbHelper.score,myDbHelper.queue};
        Cursor cursor = db.query(myDbHelper.USER_TABLE, columns, null, null, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int cid = cursor.getInt(cursor.getColumnIndex(myDbHelper.UID));
            String deviceid = cursor.getString(cursor.getColumnIndex(myDbHelper.deviceID));
            String userid = cursor.getString(cursor.getColumnIndex(myDbHelper.userID));
            String msisdn = cursor.getString(cursor.getColumnIndex(myDbHelper.msisdn));
            String totalscore = cursor.getString(cursor.getColumnIndex(myDbHelper.score));
            int queue = cursor.getInt(cursor.getColumnIndex(myDbHelper.queue));
            buffer.append(cid + "   deviceid:" + deviceid + "   userid:" + userid + "   msisdn:" +
                    msisdn + "   totalscore:" + totalscore + "   queuesize:" + queue + " \n");
        }
        return buffer.toString();
    }

    public int queuesize() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID, myDbHelper.queue};
        int queuesize = 0;
        Cursor cursor = db.query(myDbHelper.USER_TABLE, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
             queuesize = cursor.getInt(cursor.getColumnIndex(myDbHelper.queue));
        }
        return queuesize;
    }

    public String getuserid() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID, myDbHelper.userID};
        String userid = null;
        Cursor cursor = db.query(myDbHelper.USER_TABLE, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            userid = cursor.getString(cursor.getColumnIndex(myDbHelper.userID));
        }
        return userid;
    }

    public String getdeviceid() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID, myDbHelper.deviceID};
        String deviceid = null;
        Cursor cursor = db.query(myDbHelper.USER_TABLE, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            deviceid = cursor.getString(cursor.getColumnIndex(myDbHelper.deviceID));
        }
        return deviceid;
    }
    public boolean isuseractive() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID, myDbHelper.userID};
        Cursor cursor = db.query(myDbHelper.USER_TABLE, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int cid = cursor.getInt(cursor.getColumnIndex(myDbHelper.UID));
            String userid = cursor.getString(cursor.getColumnIndex(myDbHelper.userID));

            if(cid > 0 && !userid.isEmpty()){
                //Log.e("UD",cid+"->"+userid);
                return true;
            }
            else{
               // Log.e("UD","first false");
                return false;
            }
        }
        //Log.e("UD","Final anytime false");
        return false;
    }
    public int getviews(String ad_id) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID, myDbHelper.views};
        String[] whereArgs = {ad_id};
        int views = 0;
        //Cursor cursor = db.query(myDbHelper.ADS_TABLE, columns, null, null, null, null, null);
        Cursor cursor = db.rawQuery("Select " + myDbHelper.views + " from " + myDbHelper.ADS_TABLE +
                " where " + myDbHelper.ad_id + " = '" + ad_id +"'", null);
        try {
            while (cursor.moveToNext()) {
                views = cursor.getInt(cursor.getColumnIndex(myDbHelper.views));
            }
        }catch (Exception e){
            Log.e("Qerror",e.toString());
        }
        finally {
            if(!cursor.isClosed())
                cursor.close();
            db.close();
        }

        return views;
    }

    public String Impressions() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        //get all ads due for update;
        String ad_id = null, userid = null, deviceid = null;
        int ttviews = 0;
        userid = getuserid();
        deviceid = getdeviceid();

        JSONObject sendviewsdata = new JSONObject();
        JSONArray ads = new JSONArray();
        try {
            sendviewsdata.put("user_id",userid);
            sendviewsdata.put("checkbit","0");
            sendviewsdata.put("device_id",deviceid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Cursor cursor = db.rawQuery("Select " + myDbHelper.ad_id +","+myDbHelper.views+ " from " + myDbHelper.ADS_TABLE +
                " where " + myDbHelper.views + " >= "+ myDbHelper.maxview +" OR " + myDbHelper.expiry+" < DATE()", null);
        try {
            while (cursor.moveToNext()) {
                ad_id = cursor.getString(cursor.getColumnIndex(myDbHelper.ad_id));
                ttviews = cursor.getInt(cursor.getColumnIndex(myDbHelper.views));
                JSONObject curr_ad = new JSONObject();
                curr_ad.put("ad_id",ad_id);
                curr_ad.put("no_views",ttviews);
                ads.put(curr_ad);
            }
            sendviewsdata.put("ads",ads);
            Log.d("VJ",sendviewsdata.toString());
            //Log.d("VJ", new Date().toString());
        }catch (Exception e){
            Log.e("Qerror",e.toString());
        }
        return sendviewsdata.toString();
    }

    public int deletead(String addid) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs = {addid};

        int count = db.delete(myDbHelper.ADS_TABLE, myDbHelper.ad_id + " = ?", whereArgs);
        return count;
    }

    public int truncateusertable() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try {
            db.execSQL(myDbHelper.DROP_TABLE_USER);
            db.execSQL(myDbHelper.CREATE_USER_TABLE);
            return 1;
        } catch (Exception e) {
            // Message.message(context, "" + e);
            Log.e("TAG",e.toString());
            return 0;
        }
    }

    public int updatescore(String newscore) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.score, newscore);
        //String[] whereArgs = {oldscore};
        int count = db.update(myDbHelper.USER_TABLE, contentValues,null,null);
        return count;
    }

    public int updateviews(String ad_id, int views) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.views, views);
        String[] whereArgs = {ad_id};
        int count = db.update(myDbHelper.ADS_TABLE, contentValues,myDbHelper.ad_id+" = ?",whereArgs);
        if(db.isOpen())
            db.close();
        return count;
    }

    public long updatequeue(int queue) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.queue, queue);
        String[] whereArgs = {"1"};
        int count = db.update(myDbHelper.USER_TABLE, contentValues,null,null);
        //write yours
        /*db.execSQL("INSERT INTO userdetails (queue,score,msisdn) values('2','100','233540105699')");*/
        return count;
    }
    public long updatequeue(int queue,boolean addoldqueue) {
        int oldqueue = queuesize();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.queue, queue+oldqueue);
        String[] whereArgs = {"1"};
        int count = db.update(myDbHelper.USER_TABLE, contentValues,null,null);
        //write yours
        /*db.execSQL("INSERT INTO userdetails (queue,score,msisdn) values('2','100','233540105699')");*/
        return count;
    }

    public int updatepreference(String p_id, String p_status) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.p_status, p_status);
        String[] whereArgs = {p_id};
        int count = db.update(myDbHelper.PREFERENCES_TABLE, contentValues,myDbHelper.p_id+" = ?",whereArgs);
        if(db.isOpen())
            db.close();
        return count;
    }

    public void saveImage(Context context, Bitmap b, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
        } catch (Exception e) {
            Log.d("saveImage", "Exception:"+e.toString());
            e.printStackTrace();
        }
    }

    public boolean deleteadimage(Context context, String imageName){
        File imagefile = new File(context.getFilesDir().getAbsoluteFile() , imageName + ".png");
        return imagefile.delete();
    }

    public Bitmap loadImageBitmap(Context context, String imageName) {
        Bitmap bitmap = null;
        FileInputStream fiStream;
        try {
            fiStream = context.openFileInput(imageName);
            bitmap = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
        } catch (Exception e) {
            Log.d("LIMG", "Exception "+e.toString());
            e.printStackTrace();
        }
        return bitmap;
    }

    static class myDbHelper extends SQLiteOpenHelper {
        //DB NAME
        private static final String DATABASE_NAME = "swypeam";
        //tables
        private static final String USER_TABLE = "userdetails";
        private static final String ADS_TABLE = "ads";
        private static final String PREFERENCES_TABLE = "preferences";
        //db version
        private static final int DATABASE_Version = 2;
        //user table columns
        private static final String UID = "_id";
        private static final String userID = "userID";
        private static final String deviceID = "deviceID";
        private static final String msisdn = "msisdn";
        private static final String score = "score";
        private static final String queue = "queue";
        //ads table columns
        private static final String ad_id = "adID";
        private static final String ad_image = "ad_image";
        private static final String maxview = "maxview";
        private static final String views = "views";
        private static final String expiry = "expiry";
        private static final String priority = "priority";
        private static final String p_id = "pref_id";
        private static final String p_name = "pref_name";
        private static final String p_status = "status";
        //private static final String priority = "priority";
        //create user table;
        private static final String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE +
                " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + deviceID + " VARCHAR(255) ," + userID + " VARCHAR(20) ," +
                msisdn + " VARCHAR(12),"+score+" VARCHAR(11),"+queue+" VARCHAR(11));";
        //create ads table;
        private static final String CREATE_ADS_TABLE = "CREATE TABLE " + ADS_TABLE +
                " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ad_id + " VARCHAR(255) ," +
                ad_image + " LONGTEXT,"+maxview+" VARCHAR(11),"+views+" INTEGER,"+expiry+" DATETIME,"+priority+" VARCHAR(2));";
        //create preferences table
        private static final String CREATE_PREFERENCES_TABLE = "CREATE TABLE " + PREFERENCES_TABLE +
                " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + p_id + " VARCHAR(20) ," +
                p_name + " VARCHAR(255),"+p_status+" VARCHAR(11));";

        private static final String DROP_TABLE_USER = "DROP TABLE IF EXISTS " + USER_TABLE;
        private static final String DROP_TABLE_ADS = "DROP TABLE IF EXISTS " + ADS_TABLE;
        private static final String DROP_TABLE_PREFERENCES = "DROP TABLE IF EXISTS " + PREFERENCES_TABLE;
        private Context context;
        private static myDbHelper mInstance = null;

        public static myDbHelper getInstance(Context ctx) {

            if (mInstance == null) {
                mInstance = new myDbHelper(ctx.getApplicationContext());
            }
            return mInstance;
        }

        private myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_USER_TABLE);
                db.execSQL(CREATE_ADS_TABLE);
                db.execSQL(CREATE_PREFERENCES_TABLE);
            } catch (Exception e) {
               // Message.message(context, "" + e);
                //Log.e("TAG",e.toString());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_TABLE_USER);
                db.execSQL(DROP_TABLE_ADS);
                db.execSQL(DROP_TABLE_PREFERENCES);
                onCreate(db);
            } catch (Exception e) {
                //Message.message(context, "" + e);
                Log.e("TAG",e.toString());
            }
        }
    }
}