package com.example.dingfeng.icms;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import java.util.ArrayList;

/**
 * Created by Aaron on 4/30/2016.
 */
public class DrawQuadView extends View {


    Paint paint  = new Paint();

    ArrayList<Point> points = new ArrayList<>();

    int noOfPoints = 0;

    Canvas canvas;

    public DrawQuadView(Context context){
        super(context);
    }

    public DrawQuadView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public DrawQuadView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs,defStyle);
    }


    @Override
    public void onDraw(Canvas canvas){
        this.canvas = canvas;

        paint.setStrokeWidth(0);
        paint.setColor(getResources().getColor(R.color.colorPrimaryDark));



        for(int i=0; i<points.size();i++){
            canvas.drawCircle(points.get(i).x, points.get(i).y,5, paint);

            //if there is more than 1 point
            if (i!=0){
                canvas.drawLine(points.get(i).x,points.get(i).y, points.get(i-1).x, points.get(i-1).y, paint);
            }
            //connect the last point to the first
            if(i == 3){
                canvas.drawLine(points.get(i).x,points.get(i).y, points.get(0).x, points.get(0).y, paint);
            }
        }

    }

    public void addPoint(int x, int y){


        points.add(new Point(x,y));

        Log.d("icms", "new point added at " + x + ", " + y + ". Total # of points: " + points.size());

        noOfPoints = points.size();
        invalidate();
    }

    public int getNoOfPoints() {
        return noOfPoints;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void resetCanvas(){
        points = new ArrayList<>();
        noOfPoints = 0;
        invalidate();
    }

    public void clearCanvas(){
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }

}
