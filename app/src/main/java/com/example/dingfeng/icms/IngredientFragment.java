package com.example.dingfeng.icms;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class IngredientFragment extends android.support.v4.app.Fragment{

    private Uri uri;
    public ImageView imageView;
    private android.graphics.Bitmap image;

    ImageButton textBtn;
    ImageButton hlBtn;
    ImageButton vitBtn;
    TextView textView;

    RelativeLayout relativeLayout;
    public DrawView imageView2;

    int imageHeight;
    int imageWidth;
    float imageX;
    float imageY;

    String sourceText;


    ArrayList<Rect> list;


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

        sourceText = ((ResultActivity)getActivity()).gethOCRText();

        imageView = (ImageView)getActivity().findViewById(R.id.result_imageview);
/*        image = ((ResultActivity)getActivity()).getImage();
        imageView.setImageBitmap(image);*/


        uri = ((ResultActivity)getActivity()).getImageURI();
        imageView.setImageURI(uri);
        imageX = imageView.getX();
        imageY = imageView.getY();
        imageHeight = imageView.getHeight();
        imageWidth = imageView.getWidth();




        relativeLayout = (RelativeLayout)getActivity().findViewById(R.id.image_rl);
//        RelativeLayout.LayoutParams params;


        imageView2 = (DrawView)getActivity().findViewById(R.id.result_overlay);


        imageView2.requestLayout();

        imageView2.setScale(((ResultActivity) getActivity()).getScale());
        imageView2.setyDisp(((ResultActivity) getActivity()).getPushedDownHeight());
        imageView2.setVisibility(View.INVISIBLE);
/*
        imageView2.getLayoutParams().height = imageHeight;
        imageView2.getLayoutParams().width = imageWidth;*/


        hlBtn = (ImageButton)getActivity().findViewById(R.id.highlightBtn);

        hlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageView2.getVisibility() == View.INVISIBLE) {
                    imageView2.setVisibility(View.VISIBLE);
                } else {
                    imageView2.setVisibility(View.INVISIBLE);
                }

            }
        });

        vitBtn = (ImageButton)getActivity().findViewById(R.id.vitaminBtn);

        vitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageView2.getVisibility() == View.INVISIBLE) {
                    imageView2.setVisibility(View.VISIBLE);

                    clearList();
                    addRectsFromString("vitamin",sourceText);

                    imageView2.setList(list);


                } else {
                    imageView2.setVisibility(View.INVISIBLE);
                }

            }
        });

//        params = new RelativeLayout.LayoutParams(imageHeight,imageWidth);



        textBtn = (ImageButton)getActivity().findViewById(R.id.textBtn);
        textView = (TextView)getActivity().findViewById(R.id.ocr_tv);

        String OCRresult = ((ResultActivity)getActivity()).getRecognizedText();
        String textCoords = ((ResultActivity)getActivity()).getBoxText();
        textView.setText(OCRresult);
        textView.setVisibility(View.INVISIBLE);

        textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textView.getVisibility() == View.INVISIBLE) {
                    textView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        });



    }


    public void clearList(){
        list = new ArrayList<Rect>();
    }

    public void addToList(Rect r){
        list.add(r);
    }

    public void addRectsFromString(String s, String source){

        int i = source.indexOf(s);
        int tempIndex;
        int i2,i3,i4,i5,i6;
        int left, top, right, bottom;

        while (i>=0){
            tempIndex = i - 60;
            i2 = source.indexOf("bbox",tempIndex);
            i2 = i2 + 4;
            i3 = source.indexOf(" ",i2+1);
            i4 = source.indexOf(" ",i3+1);
            i5 = source.indexOf(" ",i4+1);
            i6 = source.indexOf(";",i5+1);
            left = Integer.parseInt(source.substring(i2+1,i3));
            top = Integer.parseInt(source.substring(i3+1,i4));
            right = Integer.parseInt(source.substring(i4+1,i5));
            bottom = Integer.parseInt(source.substring(i5+1,i6));

            list.add(new Rect(left,top,right,bottom));
            i = source.indexOf(s, i + 1);

        }
    }



}
