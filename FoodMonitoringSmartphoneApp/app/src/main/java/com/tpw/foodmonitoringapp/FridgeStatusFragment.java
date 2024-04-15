package com.tpw.foodmonitoringapp;



import android.annotation.SuppressLint;
        import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


//import android.se.omapi.Session;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

        import java.time.LocalTime;
import java.util.ArrayList;


public class FridgeStatusFragment extends Fragment {

    DatabaseReference ref;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ref = FirebaseDatabase.getInstance().getReference("/itemStore");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_second, container, false);

        assert getArguments() != null;
        String userPath = getArguments().getString("userPath");
        TextView tempDisplay = rootView.findViewById(R.id.tempDisplay);
        TextView humidityDisplay = rootView.findViewById(R.id.humidityDisplay);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        String dataPath = userPath + "/sensorData/sensorDataOverTime/";
        DatabaseReference listenerGraph = db.getReference(dataPath);
        DatabaseReference temp = db.getReference(userPath + "/sensorData/TemperatureReadout/data");

        GraphView graphView = rootView.findViewById(R.id.idGraphView);



        listenerGraph.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                GraphView graph = new GraphView(getContext());
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(0);
                graph.getViewport().setMaxX(2);
                graph.getGridLabelRenderer().setHumanRounding(false);
                graph.getGridLabelRenderer().setNumHorizontalLabels(3);
                StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
                staticLabelsFormatter.setHorizontalLabels(getHourStringArray().toArray(new String[0]));
                graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
                graphView.setTitleTextSize(18);


                ArrayList<String> currentValue = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren())
                {
                    currentValue.add(snap.getValue(String.class));
                }

                ArrayList<Integer> hourIntArray = getHourIntArray();

                ArrayList<String> pointArray = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    String[] split = currentValue.get(hourIntArray.get(i)).split(",");
                    pointArray.add(split[0]);
                }

                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                        new DataPoint(0, Double.parseDouble(pointArray.get(0))),
                        new DataPoint(1, Double.parseDouble(pointArray.get(1))),
                        new DataPoint(2, Double.parseDouble(pointArray.get(2))),
                        new DataPoint(3, Double.parseDouble(pointArray.get(3))),
                        new DataPoint(4, Double.parseDouble(pointArray.get(4))),
                        new DataPoint(5, Double.parseDouble(pointArray.get(5))),

                });

                // Creating the graph
                graphView.addSeries(series);

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        Switch graphSwitch = rootView.findViewById(R.id.switch1);

        graphSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                listenerGraph.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int value = 0;

                        if(isChecked) {
                            value = 1;
                            graphSwitch.setText("Humidity");
                        } else {
                            value = 0;
                            graphSwitch.setText("Temperature");
                        }


                        GraphView graph = new GraphView(getContext());
                        graph.getViewport().setXAxisBoundsManual(true);
                        graph.getViewport().setMinX(0);
                        graph.getViewport().setMaxX(2);
                        graph.getGridLabelRenderer().setHumanRounding(false);
                        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
                        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
                        staticLabelsFormatter.setHorizontalLabels(getHourStringArray().toArray(new String[0]));
                        graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
                        graphView.setTitleTextSize(18);

                        ArrayList<String> currentValue = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren())
                        {
                            currentValue.add(snap.getValue(String.class));
                        }

                        ArrayList<Integer> hourIntArray = getHourIntArray();

                        ArrayList<String> pointArray = new ArrayList<>();
                        for (int i = 0; i < 6; i++) {
                            String[] split = currentValue.get(hourIntArray.get(i)).split(",");

                            pointArray.add(split[value]);
                        }

                        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                                new DataPoint(0, Double.parseDouble(pointArray.get(0))),
                                new DataPoint(1, Double.parseDouble(pointArray.get(1))),
                                new DataPoint(2, Double.parseDouble(pointArray.get(2))),
                                new DataPoint(3, Double.parseDouble(pointArray.get(3))),
                                new DataPoint(4, Double.parseDouble(pointArray.get(4))),
                                new DataPoint(5, Double.parseDouble(pointArray.get(5))),

                        });


                        graphView.removeAllSeries();
                        graphView.addSeries(series);

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

            }
        });



        temp.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                assert value != null;
                String[] tempHumidity = value.split(",");

                tempDisplay.setText(tempHumidity[0] + " °F");
                humidityDisplay.setText(tempHumidity[1] + "%");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rootView;
    }

    private ArrayList<Integer> getHourIntArray() {
        LocalTime currentTime = LocalTime.now();

        int currentHour = currentTime.getHour();

        ArrayList<Integer> integerArrayList = new ArrayList<>();
        integerArrayList.add(0);
        integerArrayList.add(0);
        integerArrayList.add(0);
        integerArrayList.add(0);
        integerArrayList.add(0);
        integerArrayList.add(0);

        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add("");
        stringArrayList.add("");
        stringArrayList.add("");
        stringArrayList.add("");
        stringArrayList.add("");
        stringArrayList.add("");


        int pmHour = currentHour > 12 ? currentHour - 12 : currentHour;
        String strHour = pmHour + (currentHour >= 12 ? "PM" : "AM");
        if(strHour.equals("0AM")) {
            strHour = "12AM";
        }
        stringArrayList.set(5, strHour);


        integerArrayList.set(5, currentHour);

        int currentNumber = currentHour;
        for(int i = 4; i >= 0; i--) {

            if(currentNumber == 0) {
                currentNumber = 24;
            }
            currentNumber = currentNumber-1;


            integerArrayList.set(i, currentNumber);

            pmHour = currentNumber > 12 ? currentNumber - 12 : currentNumber;
            strHour = pmHour + (currentNumber >= 12 ? "PM" : "AM");
            if(strHour.equals("0AM")) {
                strHour = "12AM";
            }
            stringArrayList.set(i, strHour);
        }

        return integerArrayList;
    }

    private ArrayList<String> getHourStringArray() {
        LocalTime currentTime = LocalTime.now();


        int currentHour = currentTime.getHour();

        ArrayList<Integer> integerArrayList = new ArrayList<>();
        integerArrayList.add(0);
        integerArrayList.add(0);
        integerArrayList.add(0);
        integerArrayList.add(0);
        integerArrayList.add(0);
        integerArrayList.add(0);

        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add("");
        stringArrayList.add("");
        stringArrayList.add("");
        stringArrayList.add("");
        stringArrayList.add("");
        stringArrayList.add("");


        int pmHour = currentHour > 12 ? currentHour - 12 : currentHour;
        String strHour = pmHour + (currentHour >= 12 ? "PM" : "AM");
        stringArrayList.set(5, strHour);

        integerArrayList.set(5, currentHour);

        int currentNumber = currentHour;
        for(int i = 4; i >= 0; i--) {
            currentNumber = currentNumber-1;

            if(currentNumber == 0) {
                currentNumber = 24;
            }
            integerArrayList.set(i, currentNumber);

            pmHour = currentNumber > 12 ? currentNumber - 12 : currentNumber;
            strHour = pmHour + (currentNumber >= 12 ? "PM" : "AM");
            stringArrayList.set(i, strHour);
        }

        return stringArrayList;
    }
}