package com.example.dingfeng.icms;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class ResultActivity extends AppCompatActivity{


    ViewPager mViewPager;
    PagerAdapter pagerAdapter;
    SlidingTabLayout mSlidingTabLayout;

    Uri imageURI;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String uriMessage = intent.getStringExtra("uri");
        imageURI = Uri.parse(uriMessage);

        image = intent.getParcelableExtra("image");

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(pagerAdapter);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_layout);
        mSlidingTabLayout.setDistributeEvenly(true);
//        mSlidingTabLayout.setSelectedIndicatorColors(R.color.bittersweet);
        mSlidingTabLayout.setViewPager(mViewPager);

/*        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(BuildConfig.DEBUG);
        baseApi.init(getFilesDir().getAbsolutePath() + File.separator, "eng");
        baseApi.setImage(image);
        String recognizedText = baseApi.getUTF8Text().trim();*/


    }

    public Uri getImageURI(){
        return imageURI;
    }

    public Bitmap getImage(){
        return image;
    }

}
