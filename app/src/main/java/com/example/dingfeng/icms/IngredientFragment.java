package com.example.dingfeng.icms;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IngredientFragment extends android.support.v4.app.Fragment{

    private Uri uri;
    public ImageView imageView;
    private android.graphics.Bitmap image;

    ImageButton textBtn;
    ImageButton diaryBtn;
    ImageButton vitBtn;
    ImageButton peanutBtn;
    ImageButton soyaBtn;
    ImageButton shellfishBtn;
    ImageButton treenutBtn;
    TextView textView;

    RelativeLayout relativeLayout;
    public DrawView imageView2;

    int imageHeight;
    int imageWidth;
    float imageX;
    float imageY;

    //for post processing

    String sourceText;
    String recognizedText;

    Set<String> allIngredients;


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

        final List<String> dairy = Arrays.asList("milk", "cheese", "casein", "caseinate", "butter", "cream", "curds", "custards", "dairy", "ghee", "half", "hydrolysates", "lactate", "lactose", "pudding", "sour cream", "whey", "yogurt", "quark", "nougat", "nisin preparation", "lactulose");
        final List<String> shellfish = Arrays.asList("barnacle","crab","prawn","shrimp","krill","lobster","crawfish");
        final List<String> peanut = Arrays.asList("arachc oil","arachis","peanut", "goober", "ground nuts", "mandelonas", "nut meat", "beer nuts");
        final List<String> soya = Arrays.asList("bean curd","edamame","miso","hydrolyzed soy protein","natto","okara","shoyu","soy","soya","supro","tamari","tempeh","teriyaki","tofu","yakidofu","yuba");
        final List<String> treenut = Arrays.asList("almond","beechnut","brazil nut","bush nut","butternut","cashew","chestnut","coconut","filbert","ginko nut","hazelnut","hickory nut","lichee nut","macadamia nut","nangai nut","pecan","pine nut","pistachio","shea nut","walnut","nougat","Lychee nut");

        final List<String> misc = Arrays.asList("contain", "contains","ingredient", "ingredients", "may","calcium","iron","niacin","thiamin","agents");

        allIngredients = new HashSet<>();

        allIngredients.addAll(dairy);
        allIngredients.addAll(shellfish);
        allIngredients.addAll(peanut);
        allIngredients.addAll(soya);
        allIngredients.addAll(treenut);




        sourceText = ((ResultActivity)getActivity()).gethOCRText();
        recognizedText = ((ResultActivity)getActivity()).getRecognizedText();

//        sourceText = postProcessText(sourceText, recognizedText, allIngredients);

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


        diaryBtn = (ImageButton)getActivity().findViewById(R.id.dairyBtn);

        diaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageView2.getVisibility() == View.INVISIBLE) {
                    imageView2.setVisibility(View.VISIBLE);
                } else {
                    imageView2.setVisibility(View.INVISIBLE);
                }

                clearList();
                for (int i = 0; i < dairy.size(); i++) {
                    addRectsFromString(dairy.get(i), sourceText);
                }
                imageView2.setList(list);
                if (list.isEmpty()) {
                    Toast.makeText(getActivity(), "There is no dairy products found",
                            Toast.LENGTH_SHORT).show();
                    imageView2.setVisibility(View.INVISIBLE);
                }

            }
        });

        treenutBtn = (ImageButton)getActivity().findViewById(R.id.treenutBtn);

        treenutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageView2.getVisibility() == View.INVISIBLE) {
                    imageView2.setVisibility(View.VISIBLE);
                } else {
                    imageView2.setVisibility(View.INVISIBLE);
                }

                clearList();
                for (int i = 0; i < treenut.size(); i++) {
                    addRectsFromString(treenut.get(i), sourceText);
                }
                imageView2.setList(list);
                if(list.isEmpty()){
                    Toast.makeText(getActivity(), "There is no tree nut products found",
                            Toast.LENGTH_SHORT).show();
                    imageView2.setVisibility(View.INVISIBLE);
                }

            }
        });

        soyaBtn = (ImageButton)getActivity().findViewById(R.id.soyaBtn);

        soyaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageView2.getVisibility() == View.INVISIBLE) {
                    imageView2.setVisibility(View.VISIBLE);
                } else {
                    imageView2.setVisibility(View.INVISIBLE);
                }

                clearList();
                for (int i = 0; i < soya.size(); i++) {
                    addRectsFromString(soya.get(i), sourceText);
                }
                imageView2.setList(list);
                if(list.isEmpty()){
                    Toast.makeText(getActivity(), "There is no soya products found",
                            Toast.LENGTH_SHORT).show();
                    imageView2.setVisibility(View.INVISIBLE);
                }

            }
        });
        shellfishBtn = (ImageButton)getActivity().findViewById(R.id.shellfishBtn);

        shellfishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageView2.getVisibility() == View.INVISIBLE) {
                    imageView2.setVisibility(View.VISIBLE);
                } else {
                    imageView2.setVisibility(View.INVISIBLE);
                }

                clearList();
                for (int i = 0; i < shellfish.size(); i++) {
                    addRectsFromString(shellfish.get(i), sourceText);
                }
                imageView2.setList(list);
                if(list.isEmpty()){
                    Toast.makeText(getActivity(), "There is no shellfish products found",
                            Toast.LENGTH_SHORT).show();
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
                    if(list.isEmpty()){
                        Toast.makeText(getActivity(), "There is no vitamins found",
                                Toast.LENGTH_SHORT).show();
                        imageView2.setVisibility(View.INVISIBLE);
                    }

                } else {
                    imageView2.setVisibility(View.INVISIBLE);
                }

            }
        });
        peanutBtn = (ImageButton)getActivity().findViewById(R.id.peanutBtn);

        peanutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageView2.getVisibility() == View.INVISIBLE) {
                    imageView2.setVisibility(View.VISIBLE);

                    clearList();
                    for (int i = 0; i < peanut.size(); i++) {
                        addRectsFromString(peanut.get(i), sourceText);
                    }
                    imageView2.setList(list);
                    if(list.isEmpty()){
                        Toast.makeText(getActivity(), "There is no peanut products found",
                                Toast.LENGTH_SHORT).show();
                        imageView2.setVisibility(View.INVISIBLE);
                    }

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

    public String postProcessText(String hocr, String recognizedText, Set<String> words){

        String result =hocr;
        return result;
    }


    public void clearList(){
        list = new ArrayList<Rect>();
    }

    public void addToList(Rect r){
        list.add(r);
    }

    public void addRectsFromString(String s, String source){
//        Log.d("icms","Searching for String "+s);
        int i = source.indexOf(s);
        int tempIndex;
        int i2,i3,i4,i5,i6;
        int left, top, right, bottom;

        while (i>=0){
            tempIndex = i - 100;
            i2 = source.indexOf("bbox",tempIndex);
            i2 = i2 + 4;
            i3 = source.indexOf(" ",i2+1);
            i4 = source.indexOf(" ",i3+1);
            i5 = source.indexOf(" ",i4+1);
            i6 = source.indexOf(";",i5+1);
            left = Integer.parseInt(source.substring(i2 + 1, i3));
            top = Integer.parseInt(source.substring(i3+1,i4));
            right = Integer.parseInt(source.substring(i4+1,i5));
            bottom = Integer.parseInt(source.substring(i5+1,i6));

            list.add(new Rect(left,top,right,bottom));
            i = source.indexOf(s, i + 1);

            Log.d("icms","Found "+s+" with Rect("+left+", "+top+", "+right+", "+bottom+")");
        }
    }



}
