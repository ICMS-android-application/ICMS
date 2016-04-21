package com.example.dingfeng.icms;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {
    private Button camera;
    private Button gallery;
    private ImageView imageView;
    private static final int CAM_CODE=1111;
    private static final int PICK_CODE=1112;

    private Button processBtn;

    private android.net.Uri selectedImage;

    public final static String EXTRA_MESSAGE = "com.example.dingfeng.icms.MESSAGE";

    private Intent resultData;
    private Bitmap bitmapImage;

    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera=(Button) findViewById(R.id.camera);
        gallery=(Button) findViewById(R.id.gallery);
        imageView=(ImageView) findViewById(R.id.imageView);
        processBtn = (Button) findViewById(R.id.process_btn);
        processBtn.setEnabled(false);

        state = 0;

        camera.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cam_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cam_intent,CAM_CODE);
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

        processBtn.setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                        state++;
                        switch(state){
                            case 1:

                        }


                        Intent intent = new Intent(v.getContext(), ResultActivity.class);

                        Bundle extras = new Bundle();
                        extras.putString("uri", selectedImage.toString());
                        extras.putParcelable("image", bitmapImage);

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
            selectedImage=data.getData();
            String path=getPath(selectedImage);
            bitmapImage= BitmapFactory.decodeFile(path);
            imageView.setImageURI(selectedImage);
            processBtn.setEnabled(true);

        }
        else if(resultCode==RESULT_OK && requestCode==CAM_CODE)
        {
            selectedImage = data.getData();
            bitmapImage=(Bitmap) data.getExtras().get("data");
            imageView.setImageURI(selectedImage);
            imageView.setRotation(90);
            processBtn.setEnabled(true);
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
        Imgproc.HoughLinesP(_img, lines, 1, Math.PI / 180, 70, size.width / 2.f, 20);
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
        Imgproc.adaptiveThreshold(_img, _img, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
        return _img;
    }








}
