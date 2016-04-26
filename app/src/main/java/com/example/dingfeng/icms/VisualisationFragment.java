package com.example.dingfeng.icms;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

public class VisualisationFragment extends android.support.v4.app.Fragment {

    public static final String ARG_OBJECT = "object";
    private ImageView image_view;
    private Uri uri;
    private String result;
    private TextView text_view;
    private GraphView graph;



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.visualisation_fragment, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        text_view=(TextView)getActivity().findViewById(R.id.text_view);
        text_view.setText("");
        graph=(GraphView) getActivity().findViewById(R.id.graph);

        uri = ((ResultActivity)getActivity()).getImageURI();

        result=((ResultActivity)getActivity()).getRecognizedText();

        ArrayList<String> name_arrayList=new ArrayList<String>();
        ArrayList<Integer> percentage_arrayList=new ArrayList<Integer>();

        process_result(result, name_arrayList, percentage_arrayList);

        final String[] name=new String[name_arrayList.size()];
        name_arrayList.toArray(name);
        int[] percentage=new int[percentage_arrayList.size()];

        int index=0;
        int ave_value=0;

        for(Integer i:percentage_arrayList) {
            percentage[index] = i;
            ave_value+=i;
            index++;
        }

        for(int i=0;i<index;i++)
        {
            text_view.setText(text_view.getText()+"\n"+Integer.toString(i)+". "+name[i]);
        }



        /*
        for(String s:output)
        {
            if(s!=null) {
                if(Integer.parseInt(s.replaceAll("\\D",""))!=0) {
                    percentage[index] = Integer.parseInt(s.replaceAll("\\D", ""));

                    ave_value+=percentage[index];
                    name[index] = s.replaceAll("\\S+%", "");
                    text_view.setText(text_view.getText()+"\n"+Integer.toString(index)+". "+name[index]);
                    index++;
                }
            }
        }*/




        ave_value/=index;
        DataPoint[] data=new DataPoint[index];
        for(int i=0;i<index;i++)
        {
            data[i]=new DataPoint(i,percentage[i]);
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(data);
        graph.addSeries(series);

        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);



        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(ave_value);
        graph.getViewport().setYAxisBoundsManual(true);


        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(index-1);
        graph.getViewport().setXAxisBoundsManual(true);

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getContext(), "Series: On Data Point clicked: " + name[(int)dataPoint.getX()], Toast.LENGTH_SHORT).show();
            }
        });


        LineGraphSeries<DataPoint> line_series=new LineGraphSeries<DataPoint>(
                new DataPoint[]
                        {
                                new DataPoint(0, 100),
                                new DataPoint(1, 100),
                                new DataPoint(2, 100),
                                new DataPoint(3, 100),
                                new DataPoint(4, 100)
                        }
        );
        graph.addSeries(line_series);
        line_series.setColor(Color.RED);



        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
            }
        });

        series.setSpacing(70);

        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);




    }


    private void process_result(String input, ArrayList<String> name_arraylist,ArrayList<Integer> percentage_arraylist)
    {
        String[] lists=input.split("\n");
        ArrayList<String> output=new ArrayList<String>();
        String food="";
        for(String s : lists) {
            if (s.contains("%")&&s.replaceAll("\\D","").length()!=0) {

                String temp=s.replaceAll("\\d+[g|9]","");
                temp=temp.replaceAll("\\d+mg","");
                System.out.println("!!!!!!!!!!!!"+temp+"!!!!!!!!!!!");
                String t1=temp.replaceAll("[^(\\d+%)]", "");

                percentage_arraylist.add(Integer.parseInt(t1.substring(0, t1.length() - 1)));
                temp=temp.replaceAll("\\d+%", "");
                temp=temp.replaceAll("\\d","");

                name_arraylist.add(temp);

/*
                if(s.contains("-|."))
                    output.addAll(Arrays.asList(s.split("-|.")));
                else
                    output.add(s);
                */

                //food=food+s+"\n";


                //System.out.println("!!!!!!!!!!!!"+s.replaceAll("[^(\\d+%)]","")+"!!!!!!!!!!!");

            }
        }









    }



}
