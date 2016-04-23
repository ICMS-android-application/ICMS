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
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

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


    static {
        System.loadLibrary("opencv_java");
    }


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
                                Mat _img=new Mat(bitmapImage.getWidth(), bitmapImage.getHeight(), CvType.CV_8UC1);
                                Utils.bitmapToMat(bitmapImage, _img);
                                Imgproc.cvtColor(_img, _img, Imgproc.COLOR_RGB2GRAY);
                                _img=binarization(_img);
                                bitmapImage=Bitmap.createBitmap(_img.cols(), _img.rows(), Bitmap.Config.ARGB_8888);
                                Utils.matToBitmap(_img, bitmapImage);

                                imageView.setImageBitmap(bitmapImage);
                                Toast.makeText(getApplicationContext(),"binarization",Toast.LENGTH_SHORT).show();

                                break;

                            case 2:
                                _img=new Mat(bitmapImage.getWidth(), bitmapImage.getHeight(), CvType.CV_8UC1);
                                Utils.bitmapToMat(bitmapImage, _img);
                                Imgproc.cvtColor(_img, _img, Imgproc.COLOR_RGB2GRAY);
                                //_img=binarization(_img);
                                _img=computeDeskew(_img);
                                bitmapImage=Bitmap.createBitmap(_img.cols(), _img.rows(), Bitmap.Config.ARGB_8888);
                                Utils.matToBitmap(_img, bitmapImage);

                                imageView.setImageBitmap(bitmapImage);

                                Toast.makeText(getApplicationContext(),"deskew",Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Intent intent = new Intent(v.getContext(), ResultActivity.class);

                                Bundle extras = new Bundle();
                                extras.putString("uri", selectedImage.toString());
                                extras.putParcelable("image", bitmapImage);

                                intent.putExtras(extras);
                                startActivity(intent);
                                break;
                        }



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
            try{
                bitmapImage= MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            }catch(IOException e){

            }
            imageView.setImageBitmap(bitmapImage);
            processBtn.setEnabled(true);

        }
        else if(resultCode==RESULT_OK && requestCode==CAM_CODE)
        {
            selectedImage = data.getData();
            bitmapImage=(Bitmap) data.getExtras().get("data");
            try{
                bitmapImage= MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            }catch(IOException e){

            }
            imageView.setImageBitmap(bitmapImage);


//            imageView.setRotation(90);
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

        System.out.println("!!!!!!!!!!!!!!!"+angle+"!!!!!!!!!!!!1");


        Point center=new Point(size.width/2,size.height/2);
        Mat rotImage=Imgproc.getRotationMatrix2D(center,angle,1.0);//100% scale
        Imgproc.warpAffine(_img,_img,rotImage,size,Imgproc.INTER_CUBIC );
        return _img;
    }

    private Mat binarization(Mat _img)
    {
        Imgproc.adaptiveThreshold(_img, _img, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2.0);
        return _img;
    }








}
