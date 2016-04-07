package com.example.dingfeng.icms;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URI;

public class VisualisationFragment extends android.support.v4.app.Fragment {

    public static final String ARG_OBJECT = "object";
    private ImageView image_view;
    private Uri uri;
    private String result;
    private TextView text_view;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.visualisation_fragment, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        image_view = (ImageView)getActivity().findViewById(R.id.image_view);
        text_view=(TextView)getActivity().findViewById(R.id.text_view);

        uri = ((ResultActivity)getActivity()).getImageURI();
        image_view.setImageURI(uri);

        result=((ResultActivity)getActivity()).getRecognizedText();
        text_view.setText(result);


    }
}
