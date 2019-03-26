package com.nalosolutions.gakomeng.swypeam;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileFilter;

public class ImageAdapter extends PagerAdapter {

    Context context;
    //Ads ads = new Ads();
    ImageAdapter(Context context){

        this.context=context;

    }


    public int[] GalImages = new int[] {

            R.drawable.flag_afghanistan,    //Here first,second,third... are the name of the jpeg files placed in drawable folder
            R.drawable.flag_zimbabwe,
            R.drawable.flag_albania,
            R.drawable.flag_aland,
            R.drawable.flag_american_samoa,
            R.drawable.flag_andorra
    };

    public Drawable[] adsdrawablw;

    FileFilter myfilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return false;
            }else
                return true;
        }
    };

    //File parentDir = new File(Environment.getExternalStorageDirectory(),"/.swypeam");
//    File parentDir = new File("/data/user/0/com.nalosolutions.gakomeng.swypeam/files","");
    File parentDir = new File("/data/user/0/com.nalosolutions.gakomeng.swypeam/files","");
    //File parentDir = new File(fpath(),"");
    File[] files = parentDir.listFiles(myfilter);

    @Override

    public int getCount() {

        try {
            return files.length;
        }
        catch (Exception e) {
            return 0;
        }

    }


    public Object fpath(){
        File parentDir1 = new File(context.getApplicationContext().getFilesDir().getPath(),"");
        return parentDir1.listFiles(myfilter);
    }
    @Override

    public boolean isViewFromObject(View view, Object object) {

        return view == ((ImageView) object);

    }

    @Override

    public Object instantiateItem(ViewGroup container, int position) {

        ImageView imageView = new ImageView(context);
        
       // imageView.setBackgroundResource(GalImages[position]);
        imageView.setBackground(imageconverter(files[position].getName()));

        ((ViewPager) container).addView(imageView, 0);
        System.out.println("ImagePOS: "+position+" "+ files[position]);

        return imageView;

    }


    @Override

    public void destroyItem(ViewGroup container, int position, Object object) {

        ((ViewPager) container).removeView((ImageView) object);

    }

    public Drawable imageconverter(String fname){
        //Bitmap bitmap = BitmapFactory.decodeFile(RefActivity.getdataPath()+"/"+fname);
        //Log.e("FPath2:",context.getApplicationContext().getFilesDir()+"/"+fname);
        //Log.e("FPath3:",context.getApplicationInfo().dataDir);
        Bitmap bitmap = BitmapFactory.decodeFile(context.getApplicationContext().getFilesDir()+"/"+fname);
        BitmapDrawable imgdrawable = new BitmapDrawable(Resources.getSystem(), bitmap);
        return imgdrawable;
    }

    private void getListFiles(File parentDir) {

        File[] files = parentDir.listFiles();
        int index = 0;
        for (File file : files) {
               if(file.getName().endsWith(".jpg")){
                  adsdrawablw[index] = imageconverter(file.getName());
                }
                index++;
            }
    }

}
