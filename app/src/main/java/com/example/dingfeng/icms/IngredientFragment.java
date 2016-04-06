package com.example.dingfeng.icms;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class IngredientFragment extends android.support.v4.app.Fragment{

    private Uri uri;
    public ImageView imageView;
    private android.graphics.Bitmap image;

    ImageButton textBtn;
    TextView textView;

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
/*        image = ((ResultActivity)getActivity()).getImage();
        imageView.setImageBitmap(image);*/

        uri = ((ResultActivity)getActivity()).getImageURI();
        imageView.setImageURI(uri);

        textBtn = (ImageButton)getActivity().findViewById(R.id.textBtn);
        textView = (TextView)getActivity().findViewById(R.id.ocr_tv);

        String OCRresult = ((ResultActivity)getActivity()).getRecognizedText();
        textView.setText(OCRresult);

        textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textView.getVisibility() == View.INVISIBLE) {
                    textView.setVisibility(View.VISIBLE);
                }
                else
                {
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


}
