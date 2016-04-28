package com.example.dingfeng.icms;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.w3c.dom.Attr;

import java.util.ArrayList;

/**
 * Created by Aaron on 4/27/2016.
 */
public class DrawView extends View{

    Paint paint  = new Paint();
    float scale = 0;

    Canvas canvas;

    ArrayList<Rect> list = new ArrayList<>();
    float yDisp = 0;

    public DrawView(Context context){
        super(context);
    }

    public DrawView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs,defStyle);
    }

    public void setScale(float s){
        scale = s;
    }

    public void setyDisp(float yDisp) {
        this.yDisp = yDisp;
    }

    @Override
    public void onDraw(Canvas canvas){

        this.canvas = canvas;
        paint.setStrokeWidth(0);
        paint.setColor(getResources().getColor(R.color.semiYellow));
        /*
//        "S"oybean Hulls
        canvas.drawRect((241* scale), (33* scale) + yDisp, (310* scale), (51* scale) + yDisp, paint);
        paint.setColor(getResources().getColor(R.color.semiCyan));
//        "O"at mill*/

        Log.d("icms", "Size of list = "+list.size());
        Log.d("icms", "List elements = ");

        for (int i =0; i<list.size();i++){

            Log.d("icms", "Left = "+ (list.get(i).left) + " right= " + ((list.get(i).top))+ " top = " +(list.get(i).right) + " bottom = "+ ((list.get(i).bottom)));
            Log.d("icms", "Scaled: Left = "+ (list.get(i).left* scale) + " right= " + ((list.get(i).top* scale)+ yDisp)+ " top = " +(list.get(i).right * scale) + " bottom = "+ ((list.get(i).bottom* scale) + yDisp));
            canvas.drawRect((int) (list.get(i).left * scale), (int) (list.get(i).top * scale) + yDisp, (int)(list.get(i).right * scale), (int)(list.get(i).bottom * scale) + yDisp, paint);
        }
    }

    public void clearCanvas(){
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }

    public void setList(ArrayList<Rect> rects){

        list = rects;
        invalidate();
    }

}
