package com.example.dingfeng.icms;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ResultActivity extends AppCompatActivity{


    ViewPager mViewPager;
    PagerAdapter pagerAdapter;
    SlidingTabLayout mSlidingTabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(pagerAdapter);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_layout);
        mSlidingTabLayout.setDistributeEvenly(true);
//        mSlidingTabLayout.setSelectedIndicatorColors(R.color.bittersweet);
        mSlidingTabLayout.setViewPager(mViewPager);


    }

}
