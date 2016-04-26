package com.example.dingfeng.icms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;

import com.googlecode.tesseract.android.TessBaseAPI;


import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ResultActivity extends AppCompatActivity{


    ViewPager mViewPager;
    PagerAdapter pagerAdapter;
    SlidingTabLayout mSlidingTabLayout;

    Uri imageURI;
    Bitmap image;

    ImageButton textBtn;
    String recognizedText;
    String boxText;
    TessBaseAPI baseApi;
    String TARGET_BASE_PATH;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String uriMessage = intent.getStringExtra("uri");
        imageURI = Uri.parse(uriMessage);


        //image = intent.getParcelableExtra("image");
//        image = intent.getParcelableExtra("image");


        try{
            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
        }catch (IOException e){

        }

        int REQUEST_CODE = 1;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(pagerAdapter);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_layout);
        mSlidingTabLayout.setDistributeEvenly(true);
//        mSlidingTabLayout.setSelectedIndicatorColors(R.color.bittersweet);
        mSlidingTabLayout.setViewPager(mViewPager);


        TARGET_BASE_PATH = Environment.getExternalStorageDirectory() + "/tesseract/";


         baseApi = new TessBaseAPI();
        // DATA_PATH = Path to the storage
        // lang = for which the language data exists, usually "eng"
        String datapath = Environment.getExternalStorageDirectory() + "/tesseract/";
        String language = "eng";
        File dir = new File(datapath + "tessdata/");
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        File file = new File(datapath + "tessdata/eng.traineddata");
        if(!file.exists())
            copyFileOrDir("");

        baseApi.init(datapath, language);
        // Eg. baseApi.init("/mnt/sdcard/tesseract/tessdata/eng.traineddata", "eng");
        baseApi.setImage(image);

        recognizedText = baseApi.getUTF8Text();

        boxText = baseApi.getBoxText(0);

        try{

            File testUTF8 = new File(datapath+ "testUTF.txt");
            if(testUTF8.exists())
            {
                testUTF8.delete();
            }
            testUTF8 = new File(datapath, "testUTF.txt");
            FileWriter fwUTF8 = new FileWriter(testUTF8);
            fwUTF8.append(recognizedText);
            fwUTF8.flush();
            fwUTF8.close();

            File testBox = new File(datapath+ "testBox.txt");
            if(testBox.exists())
            {
                testBox.delete();
            }
            testBox = new File(datapath, "testBox.txt");
            FileWriter fwBox = new FileWriter(testBox);
            fwBox.append(boxText);
            fwBox.flush();
            fwBox.close();

        }catch(IOException e){

        }


        baseApi.end();

    }

    public String getBoxText(){
        return boxText;
    }

    private void copyFileOrDir(String path) {
        AssetManager assetManager = this.getAssets();
        String assets[] = null;
        try {
            Log.i("tag", "copyFileOrDir() "+path);
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(path);
            } else {
                String fullPath =  TARGET_BASE_PATH + path;
                Log.i("tag", "path="+fullPath);
                File dir = new File(fullPath);
                if (!dir.exists() && !path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                    if (!dir.mkdirs())
                        Log.i("tag", "could not create dir "+fullPath);
                for (int i = 0; i < assets.length; ++i) {
                    String p;
                    if (path.equals(""))
                        p = "";
                    else
                        p = path + "/";

                    if (!path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                        copyFileOrDir( p + assets[i]);
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }

    private void copyFile(String filename) {
        AssetManager assetManager = this.getAssets();

        InputStream in = null;
        OutputStream out = null;
        String newFileName = null;
        try {
            Log.i("tag", "copyFile() "+filename);
            in = assetManager.open(filename);
            if (filename.endsWith(".jpg")) // extension was added to avoid compression on APK file
                newFileName = TARGET_BASE_PATH + filename.substring(0, filename.length()-4);
            else
                newFileName = TARGET_BASE_PATH + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of "+newFileName);
            Log.e("tag", "Exception in copyFile() "+e.toString());
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (baseApi != null)
            baseApi.end();
    }

    public String getRecognizedText(){
        return recognizedText;
    }

    public Uri getImageURI(){
        return imageURI;
    }

    public Bitmap getImage(){
        return image;
    }

/*
    private Mat computeDeskew(Mat _img)
    {
        Size size=_img.size();
        Core.bitwise_not(_img, _img);//reverse black and white
        Mat lines=new Mat();
        Imgproc.HoughLinesP(_img,lines,1,Math.PI/180,70,size.width/2.f,20);
        double angle=0;
        for(int i=0;i<lines.height();i++)
        {
            for(int j=0;j<lines.width();j++)
                angle += Math.atan2(lines.get(i, j)[3] - lines.get(i, j)[1], lines.get(i, j)[2] - lines.get(i, j)[0]);
        }
        angle/=lines.size().area();

        Point center=new Point(size.width/2,size.height/2);
        Mat rotImage=Imgproc.getRotationMatrix2D(center,angle,1.0);//100% scale
        Imgproc.warpAffine(_img,_img,rotImage,size,Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS);
        return _img;
    }

    private Mat binarization(Mat _img)
    {
        Imgproc.adaptiveThreshold(_img, _img, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY,11,2);
        return _img;
    }
*/

}
