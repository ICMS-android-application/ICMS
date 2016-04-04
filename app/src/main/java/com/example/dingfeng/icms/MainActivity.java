package com.example.dingfeng.icms;

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

public class MainActivity extends AppCompatActivity {
    private Button camera;
    private Button gallery;
    private ImageView imageView;
    private static final int CAM_CODE=1111;
    private static final int PICK_CODE=1112;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera=(Button) findViewById(R.id.camera);
        gallery=(Button) findViewById(R.id.gallery);
        imageView=(ImageView) findViewById(R.id.imageView);

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


    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==PICK_CODE)
        {
            Uri selectedImage=data.getData();
            String path=getPath(selectedImage);
            Bitmap bitmapImage= BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bitmapImage);
        }
        else if(resultCode==RESULT_OK && requestCode==CAM_CODE)
        {
            Bitmap bp=(Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bp);
        }

    }
    public String getPath(Uri uri){
        String[] filePathColumn={MediaStore.Images.Media.DATA};
        Cursor cursor=getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
        return cursor.getString(columnIndex);
    }










}
