package com.example.dingfeng.icms;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

/**
 * Created by Aaron on 4/1/2016.
 */
public class PagerAdapter extends FragmentPagerAdapter{

    public PagerAdapter(android.support.v4.app.FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position){

            case 0:

                fragment = new IngredientFragment();

                break;

            case 1:

                fragment = new VisualisationFragment();

                break;

        }

        return fragment;
    }


    @Override
    public int getCount() {
        return 2;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();

        switch (position) {

            case 0:

                return "Ingredient";

            case 1:

                return "Nutrition";
            default:
                return null;
        }
    }
}
