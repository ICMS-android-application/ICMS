package com.example.dingfeng.icms;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import android.graphics.Color;
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
    private GraphView graph;



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.visualisation_fragment, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        image_view = (ImageView)getActivity().findViewById(R.id.image_view);
        text_view=(TextView)getActivity().findViewById(R.id.text_view);
        graph=(GraphView) getActivity().findViewById(R.id.graph);

        uri = ((ResultActivity)getActivity()).getImageURI();
        image_view.setImageURI(uri);

        result=((ResultActivity)getActivity()).getRecognizedText();
        text_view.setText(result);

        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, -1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
            }
        });

        series.setSpacing(25);

        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);




    }
}
