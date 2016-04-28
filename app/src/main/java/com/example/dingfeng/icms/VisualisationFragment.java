package com.example.dingfeng.icms;

import com.jjoe64.graphview.DefaultLabelFormatter;
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



        if(index!=0)
        {

            for(int i=0;i<index;i++)
            {
                text_view.setText(text_view.getText()+"\n"+Integer.toString(i)+". "+name[i]);
            }

            ave_value /= index;
            DataPoint[] data = new DataPoint[index];
            for (int i = 0; i < index; i++) {
                data[i] = new DataPoint(i+1, percentage[i]);
            }

            BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(data);
            graph.addSeries(series);

            graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);


            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(ave_value);
            graph.getViewport().setYAxisBoundsManual(true);


            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(index+1);
            graph.getViewport().setXAxisBoundsManual(true);

            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(getContext(), name[(int) dataPoint.getX()-1], Toast.LENGTH_SHORT).show();
                }
            });




            series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
                }
            });

            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        // show normal x values
                        return super.formatLabel(value, isValueX);
                    } else {
                        // show currency for y values
                        return super.formatLabel(value, isValueX) + "%";
                    }
                }
            });


            series.setSpacing(40);

            series.setDrawValuesOnTop(true);
            series.setValuesOnTopColor(Color.RED);

        }
        else
        {
            text_view.setText("Did not detect nutrition list");
        }


    }


    private void process_result(String input, ArrayList<String> name_arraylist,ArrayList<Integer> percentage_arraylist)
    {
        String[] lists=input.split("\n");
        for(String s : lists) {
            if (s.contains("%")&&s.replaceAll("\\D","").length()!=0)
            {
                if(s.contains(".")||s.contains("-")) {
                    String[] output = s.split("[.-]");

                    for(String i:output)
                    {
                        String temp = i.replaceAll("\\d+[g|9]", "");//remove  "10g"

                        temp = temp.replaceAll("\\d+mg", "");       //remove  "10mg"

                        temp=temp.replaceAll("m[g|c][g]*","");

                        String t1 = temp.replaceAll("[^(\\d+%)]", "");//only save "10%"
                        if(t1.length()<1)
                            continue;

                        t1=t1.substring(0,t1.indexOf('%'));

                        if(t1.matches("[0-9]+")&&t1.length()>0)
                            percentage_arraylist.add(Integer.parseInt(t1));//remove % and store the percentage
                        else
                            continue;

                        temp = temp.replaceAll("\\d+%", "");        //remove "10%"
                        temp = temp.replaceAll("\\d", "");          //remove any other digits

                        name_arraylist.add(temp);                   //store the string name
                    }

                }
                else
                {

                    String temp = s.replaceAll("\\d+[g|9]", "");//remove  "10g"

                    temp = temp.replaceAll("\\d+mg", "");       //remove  "10mg"

                    temp=temp.replaceAll("m[g|c][g]*","");

                    String t1 = temp.replaceAll("[^(\\d+%)]", "");//only save "10%"



                    if(t1.length()<1)
                        continue;

                    t1=t1.substring(0,t1.indexOf('%'));

                    if(t1.matches("[0-9]+")&&t1.length()>0)
                        percentage_arraylist.add(Integer.parseInt(t1));//store the percentage
                    else
                        continue;

                    temp = temp.replaceAll("\\d+%", "");        //remove "10%"
                    temp = temp.replaceAll("\\d", "");          //remove any other digits

                    name_arraylist.add(temp);                   //store the string name
                }

            }
        }









    }



}
