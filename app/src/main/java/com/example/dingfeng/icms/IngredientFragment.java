package com.example.dingfeng.icms;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class IngredientFragment extends android.support.v4.app.Fragment{

    private Uri uri;
    public ImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ingredient_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageView = (ImageView)getActivity().findViewById(R.id.result_imageview);
        uri = ((ResultActivity)getActivity()).getImageURI();
        imageView.setImageURI(uri);
        imageView.setRotation(90);
    }


}
