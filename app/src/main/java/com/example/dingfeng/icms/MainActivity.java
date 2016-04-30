package com.example.dingfeng.icms;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button camera;
    private Button gallery;

    private Button deskew;
    private Button binarization;

    private ImageButton rotate;
    private ImageButton expand;
    private ImageButton shrink;

    private Button medium_filter;

    private ImageView imageView;
    private static final int CAM_CODE=1111;
    private static final int PICK_CODE=1112;

    private Button processBtn;

    private android.net.Uri selectedImage;

    public final static String EXTRA_MESSAGE = "com.example.dingfeng.icms.MESSAGE";

    private Intent resultData;
    private Bitmap bitmapImage;

    private int state;


    static {
        System.loadLibrary("opencv_java");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera=(Button) findViewById(R.id.camera);
        gallery=(Button) findViewById(R.id.gallery);

        rotate=(ImageButton) findViewById(R.id.rotate);



        expand=(ImageButton) findViewById(R.id.expand);
        shrink=(ImageButton) findViewById(R.id.shrink);

        deskew=(Button) findViewById(R.id.Deskew);
        deskew.setVisibility(View.INVISIBLE);
        binarization=(Button) findViewById(R.id.Binarization);
        binarization.setVisibility(View.INVISIBLE);
        medium_filter=(Button) findViewById(R.id.medium_filter);
        medium_filter.setVisibility(View.INVISIBLE);
        expand.setVisibility(View.INVISIBLE);
        shrink.setVisibility(View.INVISIBLE);


        imageView=(ImageView) findViewById(R.id.imageView);
        processBtn = (Button) findViewById(R.id.process_btn);
        processBtn.setEnabled(false);

        state = 0;

        camera.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cam_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cam_intent, CAM_CODE);
                    }
                }
        );


        gallery.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Send an intent to launch the media picker.;
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_CODE);
                    }
                }
        );

        deskew.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        //Mat _img=new Mat(bitmapImage.getWidth(), bitmapImage.getHeight(), CvType.CV_8UC1);
                        Mat _img=new Mat();
                        Utils.bitmapToMat(bitmapImage, _img);
                        Imgproc.cvtColor(_img, _img, Imgproc.COLOR_RGB2GRAY);
                        //_img=binarization(_img);
                        _img=computeDeskew(_img);
                        bitmapImage=Bitmap.createBitmap(_img.cols(), _img.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(_img, bitmapImage);

                        imageView.setImageBitmap(bitmapImage);

                        Toast.makeText(getApplicationContext(),"deskew",Toast.LENGTH_SHORT).show();
                    }
                }
        );


        binarization.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        //Mat _img=new Mat(bitmapImage.getWidth(), bitmapImage.getHeight(), CvType.CV_8UC1);
                        Mat _img=new Mat();
                        Utils.bitmapToMat(bitmapImage, _img);
                        Imgproc.cvtColor(_img, _img, Imgproc.COLOR_RGB2GRAY);
                        _img=binarization(_img);
                        bitmapImage=Bitmap.createBitmap(_img.cols(), _img.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(_img, bitmapImage);

                        imageView.setImageBitmap(bitmapImage);
                        Toast.makeText(getApplicationContext(),"binarization",Toast.LENGTH_SHORT).show();
                    }
                }
        );

        rotate.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        //Mat _img=new Mat(bitmapImage.getWidth(), bitmapImage.getHeight(), CvType.CV_8UC1);
                        Mat _img=new Mat();
                        Utils.bitmapToMat(bitmapImage, _img);
                        _img=rotation(_img);
                        bitmapImage=Bitmap.createBitmap(_img.cols(), _img.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(_img, bitmapImage);

                        imageView.setImageBitmap(bitmapImage);
                        Toast.makeText(getApplicationContext(),"rotate 90 degree",Toast.LENGTH_SHORT).show();
                    }
                }
        );


        medium_filter.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Mat _img=new Mat();
                        Utils.bitmapToMat(bitmapImage, _img);
                        Imgproc.medianBlur(_img,_img,3);


                        bitmapImage=Bitmap.createBitmap(_img.cols(), _img.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(_img, bitmapImage);

                        imageView.setImageBitmap(bitmapImage);
                        Toast.makeText(getApplicationContext(),"medium filter",Toast.LENGTH_SHORT).show();
                    }

                }
        );
        expand.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Mat _img=new Mat();
                        Utils.bitmapToMat(bitmapImage, _img);

                        Imgproc.resize(_img,_img,new Size(_img.size().width*2,_img.size().height*2));

                        System.out.println("!!!!!!!!!!!!!!!!!!!expand" + _img.size());

                        bitmapImage=Bitmap.createBitmap(_img.cols(), _img.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(_img, bitmapImage);

                        imageView.setImageBitmap(bitmapImage);
                        Toast.makeText(getApplicationContext(),"expand",Toast.LENGTH_SHORT).show();



                    }
                }
        );

        shrink.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Mat _img=new Mat();
                        Utils.bitmapToMat(bitmapImage, _img);

                        Imgproc.resize(_img,_img,new Size(_img.size().width*0.5,_img.size().height*0.5));

                        System.out.println("!!!!!!!!!!!!!!!!!!!shrink" + _img.size());

                        bitmapImage=Bitmap.createBitmap(_img.cols(), _img.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(_img, bitmapImage);

                        imageView.setImageBitmap(bitmapImage);
                        Toast.makeText(getApplicationContext(),"expand",Toast.LENGTH_SHORT).show();



                    }
                }
        );




        processBtn.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(v.getContext(), ResultActivity.class);

                        Bundle extras = new Bundle();

                        Uri image_uri = storeImage(bitmapImage);

                        if (image_uri == null)
                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!null image uri");
                        else
                            extras.putString("uri", image_uri.toString());

                        intent.putExtras(extras);
                        startActivity(intent);

                    }
                }
        );


    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        resultData = data;
        if(resultCode==RESULT_OK && requestCode==PICK_CODE)
        {
            Log.d("icms","------------New Gallery Pic-------------");
            selectedImage=data.getData();
            try{
                bitmapImage= MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            }catch(IOException e){

            }
            imageView.setImageBitmap(bitmapImage);
            processBtn.setEnabled(true);
            binarization.setVisibility(View.VISIBLE);
            deskew.setVisibility(View.VISIBLE);
            medium_filter.setVisibility(View.VISIBLE);
            expand.setVisibility(View.VISIBLE);
            shrink.setVisibility(View.VISIBLE);

        }
        else if(resultCode==RESULT_OK && requestCode==CAM_CODE)
        {

            Log.d("icms","--------New Cam Pic---------------");
            selectedImage = data.getData();
            try{
                bitmapImage= MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            }catch(IOException e){

            }
            imageView.setImageBitmap(bitmapImage);


            processBtn.setEnabled(true);

            binarization.setVisibility(View.VISIBLE);
            deskew.setVisibility(View.VISIBLE);
            medium_filter.setVisibility(View.VISIBLE);
        }

    }


    public String getPath(Uri uri){
        String[] filePathColumn={MediaStore.Images.Media.DATA};
        Cursor cursor=getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
        return cursor.getString(columnIndex);
    }




    private Mat computeDeskew(Mat _img)
    {
        Size size=_img.size();
        Core.bitwise_not(_img, _img);//reverse black and white
        Mat lines=new Mat();
        Imgproc.HoughLinesP(_img, lines, 1, Math.PI / 180, 100, size.width / 2.f, 20);
        double angle=0;
        for(int i=0;i<lines.rows(); i++)
        {
            double[] vec=lines.get(i,0);
            angle+=Math.atan2(vec[3]-vec[1],vec[2]-vec[0]);
                //angle += Math.atan2(lines.get(i, j)[3] - lines.get(i, j)[1], lines.get(i, j)[2] - lines.get(i, j)[0]);
        }
        angle/=lines.rows();
        angle=angle / Math.PI * 180.0;

        Point center=new Point(size.width/2, size.height/2);
        Mat rotImage=Imgproc.getRotationMatrix2D(center, angle, 1.0);//100% scale
        Mat after_img=new Mat();
        Imgproc.warpAffine(_img,after_img,rotImage,after_img.size(),Imgproc.INTER_CUBIC,Imgproc.BORDER_TRANSPARENT, new Scalar(0));
        return _img;
    }

    private Mat binarization(Mat _img)
    {
        Imgproc.adaptiveThreshold(_img, _img, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2.0);
        return _img;
    }

    private Mat rotation(Mat _img)
    {
        Core.transpose(_img, _img);
        Core.flip(_img, _img, 1);
        return _img;
    }


    private Uri storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!"+"Error creating media file, check storage permissions: ");
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!" + e.getMessage());
        } catch (IOException e) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!" + e.getMessage());
        }


        return Uri.fromFile(pictureFile);
    }


    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/tesseract/");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }











}
