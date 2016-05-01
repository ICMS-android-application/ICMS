package com.example.dingfeng.icms;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private ImageButton camera;
    private ImageButton gallery;

    private ImageButton deskew;
    private ImageButton binarization;

    private ImageButton rotate;
    private ImageButton expand;
    private ImageButton shrink;
    float currentScale;

    private ImageButton medium_filter;

    private ImageView imageView;
    private static final int CAM_CODE=1111;
    private static final int PICK_CODE=1112;

    private Button processBtn;
    private TextView infoText;

    private android.net.Uri selectedImage;

    public final static String EXTRA_MESSAGE = "com.example.dingfeng.icms.MESSAGE";

    private Intent resultData;
    private Bitmap bitmapImage;

    private int state;

    private Button rectify;
    int noOfPoints = 0;
    DrawQuadView quadView;

    int screenWidth = 0;
    int screenHeight= 0;

    int imageWidth = 0;
    int imageHeight = 0;

    float imageScale = 0;

    float innerScreenHeight = 0;

    static {
        System.loadLibrary("opencv_java");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        innerScreenHeight = (screenHeight - dptopx(120));

        camera=(ImageButton) findViewById(R.id.camera);
        gallery=(ImageButton) findViewById(R.id.gallery);

        rotate=(ImageButton) findViewById(R.id.rotate);

        currentScale = 1;

        expand=(ImageButton) findViewById(R.id.expand);
        shrink=(ImageButton) findViewById(R.id.shrink);

        deskew=(ImageButton) findViewById(R.id.Deskew);
        deskew.setVisibility(View.INVISIBLE);
        binarization=(ImageButton) findViewById(R.id.Binarization);
        binarization.setVisibility(View.INVISIBLE);
        medium_filter=(ImageButton) findViewById(R.id.medium_filter);
        medium_filter.setVisibility(View.INVISIBLE);
        expand.setVisibility(View.INVISIBLE);
        shrink.setVisibility(View.INVISIBLE);
        rotate.setVisibility(View.INVISIBLE);


        rectify=(Button) findViewById(R.id.rectify);
        rectify.setVisibility(View.INVISIBLE);
        quadView = (DrawQuadView)findViewById(R.id.drawquadview);
        quadView.setVisibility(View.INVISIBLE);
        //no of points user have touched (ranging from 0 - 4). At -1, rectify is not enabled. at 4, btn is enabled and clickable
        int noOfPoints = 0;


        imageView=(ImageView) findViewById(R.id.imageView);
        processBtn = (Button) findViewById(R.id.process_btn);
        processBtn.setEnabled(false);

        state = 0;

        infoText = (TextView) findViewById(R.id.info_tv);

        infoText.setText("iW: "+imageWidth+" | iH: "+imageHeight+" \nsW: "+screenWidth+" | sH: "+screenHeight+"\nimg scale: "+imageScale);


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

                        Imgproc.resize(_img, _img, new Size(_img.size().width * 2, _img.size().height * 2));

                        System.out.println("!!!!!!!!!!!!!!!!!!!expand" + _img.size());

                        bitmapImage=Bitmap.createBitmap(_img.cols(), _img.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(_img, bitmapImage);




                        imageWidth = bitmapImage.getWidth();
                        imageHeight = bitmapImage.getHeight();

                        if(imageHeight>imageWidth){
                            if (imageHeight >= screenHeight - 120) {
                                imageScale = screenHeight/(float)imageHeight;
                            }
                            else{
                                imageScale = screenWidth/(float)imageWidth;
                            }
                        }else if(imageHeight<imageWidth){
                            imageScale = screenWidth/(float)imageWidth;
                        }else{
                            imageScale = screenWidth/(float)imageWidth;
                        }
                        infoText.setText("iW: " + imageWidth + " | iH: " + imageHeight + " \nsW: " + screenWidth + " | sH: " + screenHeight + "\nimg scale: " + imageScale);



                        imageView.setImageBitmap(bitmapImage);

                        currentScale *=2;
                        Toast.makeText(getApplicationContext(),"expanding image by 2x. Current Scale = "+currentScale,Toast.LENGTH_SHORT).show();



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

                        Imgproc.resize(_img, _img, new Size(_img.size().width * 0.5, _img.size().height * 0.5));

                        System.out.println("!!!!!!!!!!!!!!!!!!!shrink" + _img.size());

                        bitmapImage=Bitmap.createBitmap(_img.cols(), _img.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(_img, bitmapImage);




                        imageWidth = bitmapImage.getWidth();
                        imageHeight = bitmapImage.getHeight();

                        if(imageHeight>imageWidth){
                            if (imageHeight >= screenHeight - 120) {
                                imageScale = screenHeight/(float)imageHeight;
                            }
                            else{
                                imageScale = screenWidth/(float)imageWidth;
                            }
                        }else if(imageHeight<imageWidth){
                            imageScale = screenWidth/(float)imageWidth;
                        }else{
                            imageScale = screenWidth/(float)imageWidth;
                        }
                        infoText.setText("iW: " + imageWidth + " | iH: " + imageHeight + " \nsW: " + screenWidth + " | sH: " + screenHeight + "\nimg scale: " + imageScale);



                        imageView.setImageBitmap(bitmapImage);
                        currentScale *= 0.5;
                        Toast.makeText(getApplicationContext(),"shrink by 2x. Current Scale = "+currentScale,Toast.LENGTH_SHORT).show();



                    }
                }
        );



        rectify.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Mat _img=new Mat();
                        Utils.bitmapToMat(bitmapImage, _img);
                        Log.d("icms", "rectify.getText() = " + rectify.getText());

//                        Imgproc.resize(_img, _img, new Size(_img.size().width * 0.5, _img.size().height * 0.5));

                        if (rectify.getText().equals("PlaRec")){
                            rectify.setText("0");
                            quadView.setVisibility(View.VISIBLE);
                            quadView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {



                                    if(event.getAction() == MotionEvent.ACTION_UP){
                                        Toast.makeText(getApplicationContext(), "point touched on (" + event.getX() + ", " + event.getY() + ") ",Toast.LENGTH_SHORT).show();

                                    }

                                    if (event.getAction() == android.view.MotionEvent.ACTION_UP && quadView.getNoOfPoints()<4) {
                                        quadView.addPoint((int) event.getX(), (int) event.getY());


                                        Log.d("icms", "point set on (" + event.getX() + ", " + event.getY() + ") ");


                                        rectify.setText(Integer.toString(quadView.getNoOfPoints()));
                                        Log.d("icms","no of points = "+quadView.getNoOfPoints());

                                    }

                                    return true;
                                }
                            });
                        }
                        else if (quadView.getNoOfPoints() == 4) {
                            //rectify here
                            Toast.makeText(getApplicationContext(), "Rectifying!!", Toast.LENGTH_SHORT).show();

                            _img = rectify(_img, quadView.getPoints());

                            rectify.setText("PlaRec");
                            quadView.resetCanvas();
                            quadView.setVisibility(View.INVISIBLE);
                        }
                        else{
                            int noLeft = 4 - quadView.getNoOfPoints();
                            Toast.makeText(getApplicationContext(),"Tap another "+noLeft+" more point(s)!",Toast.LENGTH_SHORT).show();

                        }


//                        System.out.println("!!!!!!!!!!!!!!!!!!!shrink" + _img.size());
                        bitmapImage=Bitmap.createBitmap(_img.cols(), _img.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(_img, bitmapImage);


                        imageWidth = bitmapImage.getWidth();
                        imageHeight = bitmapImage.getHeight();

                        if(imageHeight>imageWidth){
                            if (imageHeight >= screenHeight - 120) {
                                imageScale = screenHeight/(float)imageHeight;
                            }
                            else{
                                imageScale = screenWidth/(float)imageWidth;
                            }
                        }else if(imageHeight<imageWidth){
                            imageScale = screenWidth/(float)imageWidth;
                        }else{
                            imageScale = screenWidth/(float)imageWidth;
                        }
                        infoText.setText("iW: " + imageWidth + " | iH: " + imageHeight + " \nsW: " + screenWidth + " | sH: " + screenHeight + "\nimg scale: " + imageScale);

                        imageView.setImageBitmap(bitmapImage);
//                        currentScale *= 0.5;
//                        Toast.makeText(getApplicationContext(),"shrink by 2x. Current Scale = "+currentScale,Toast.LENGTH_SHORT).show();




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

    private void addRectPoints(float x, float y){

        noOfPoints++;

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

                imageHeight = bitmapImage.getHeight();
                imageWidth = bitmapImage.getWidth();


                if(imageHeight>imageWidth){
                    if (imageHeight >= screenHeight - 120) {
                        imageScale = screenHeight/(float)imageHeight;
                    }
                    else{
                        imageScale = screenWidth/(float)imageWidth;
                    }
                }else if(imageHeight<imageWidth){
                    imageScale = screenWidth/(float)imageWidth;
                }else{
                    imageScale = screenWidth/(float)imageWidth;
                }

            }catch(IOException e){

            }
            imageView.setImageBitmap(bitmapImage);
            processBtn.setEnabled(true);
            binarization.setVisibility(View.VISIBLE);
            deskew.setVisibility(View.VISIBLE);
            medium_filter.setVisibility(View.VISIBLE);
            expand.setVisibility(View.VISIBLE);
            shrink.setVisibility(View.VISIBLE);
            rotate.setVisibility(View.VISIBLE);
            rectify.setVisibility(View.VISIBLE);


            infoText.setText("iW: " + imageWidth + " | iH: " + imageHeight + " \nsW: " + screenWidth + " | sH: " + screenHeight + "\nimg scale: " + imageScale);

        }
        else if(resultCode==RESULT_OK && requestCode==CAM_CODE)
        {

            Log.d("icms","--------New Cam Pic---------------");
            selectedImage = data.getData();
            try{
                bitmapImage= MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);


                imageHeight = bitmapImage.getHeight();
                imageWidth = bitmapImage.getWidth();


                if(imageHeight>imageWidth){
                    if (imageHeight >= screenHeight - 120) {
                        imageScale = screenHeight/(float)imageHeight;
                    }
                    else{
                        imageScale = screenWidth/(float)imageWidth;
                    }
                }else if(imageHeight<imageWidth){
                    imageScale = screenWidth/(float)imageWidth;
                }else{
                    imageScale = screenWidth/(float)imageWidth;
                }
            }catch(IOException e){

            }
            imageView.setImageBitmap(bitmapImage);


            processBtn.setEnabled(true);

            binarization.setVisibility(View.VISIBLE);
            deskew.setVisibility(View.VISIBLE);
            medium_filter.setVisibility(View.VISIBLE);

            //dingfeng did u purposely leave this visibilities out?
            medium_filter.setVisibility(View.VISIBLE);
            expand.setVisibility(View.VISIBLE);
            shrink.setVisibility(View.VISIBLE);
            rotate.setVisibility(View.VISIBLE);
            rectify.setVisibility(View.VISIBLE);

            infoText.setText("iW: "+imageWidth+" | iH: "+imageHeight+" \nsW: "+screenWidth+" | sH: "+screenHeight+"\nimg scale: "+imageScale);

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
        return after_img;
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

    private Mat rectify(Mat _img, ArrayList<android.graphics.Point> pts)
    {
        Point p1 = new Point(pts.get(0).x, pts.get(0).y);
        Point p2 = new Point(pts.get(1).x, pts.get(1).y);
        Point p3 = new Point(pts.get(2).x, pts.get(2).y);
        Point p4 = new Point(pts.get(3).x, pts.get(3).y);

        double cropWidth = Math.max(Math.abs(p1.x - p2.x),Math.abs(p3.x-p4.x));
        double cropHeight = Math.max(Math.abs(p1.y - p4.y),Math.abs(p2.y-p3.y));

        double aspectRatio = cropHeight/cropWidth;


        double dstX1, dstX2, dstY1, dstY2;
        double imWidth, imHeight;

        //landscape
        if (cropWidth>=cropHeight){

            imWidth = screenWidth;
            imHeight = (screenHeight -dptopx(120))* aspectRatio;

            dstX1 = 0.0;
            dstX2 = screenWidth;
            dstY1 = innerScreenHeight/2 - imHeight/2;
            dstY2 = innerScreenHeight/2 + imHeight/2;

        }//portrait
        else{

            imWidth = screenWidth * (1/aspectRatio);
            imHeight = (screenHeight -dptopx(120));

            dstX1 = screenWidth/2 - imWidth/2;
            dstX2 = screenWidth/2 + imWidth/2;
            dstY1 = 0.0;
            dstY2 = screenHeight;
        }

        Mat src_mat = new Mat(4,1, CvType.CV_32FC2);
        Mat dst_mat = new Mat(4,1, CvType.CV_32FC2);

        src_mat.put(0,0,p1.x,p1.y,p2.x,p2.y,p4.x,p4.y,p3.x,p3.y);
        dst_mat.put(0,0,dstX1,dstY1,dstX2,dstY1,dstX1,dstY2,dstX2,dstY2);

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(src_mat,dst_mat);

        Mat dst = _img.clone();

        Imgproc.warpPerspective(_img, dst, perspectiveTransform, new Size(dstX2, dstY2), Imgproc.INTER_CUBIC);

        return dst;
    }

    public float dptopx(int dp){
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
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
