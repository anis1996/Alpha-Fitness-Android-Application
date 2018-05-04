package edu.sjsu.anis.alphafitness;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

import edu.sjsu.anis.alphafitness.DataBase.RecordContract;

//supposed to be Named Workout Details
public class BlankFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {

    private LineChart mChart;


    //UI Components
    TextView averageUI ;
    TextView maxUI;
    TextView minUI;


    //Shared Preference
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor edit ;
    int minValue = 0;
    int maxValue = 0;

    

    //Database
    private float caloriesFromDatabase;
    private long timeFromDatabase;
    private float distanceFromDatabase;

    private float oldCalories = 0;

    Cursor cursor;





    //Handler
    Handler workoutDetailsHandler = new Handler() ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedPreferences = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();
        minValue = sharedPreferences.getInt("MIN", 0);
        maxValue = sharedPreferences.getInt("MAX", 0);


         cursor = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI, null, "_id = ?", new String[]{"1"}, RecordContract.Contracts._ID);


        if (cursor.moveToFirst()) {

            caloriesFromDatabase = cursor.getFloat(cursor.getColumnIndex(RecordContract.Contracts.KEY_CALORIES_BURNED));
            timeFromDatabase = cursor.getLong(cursor.getColumnIndex(RecordContract.Contracts.ALL_TIME_KEY_TIME));
            distanceFromDatabase = cursor.getFloat(cursor.getColumnIndex(RecordContract.Contracts.ALL_TIME_KEY_DISTANCE));

        }

        oldCalories = caloriesFromDatabase;




        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        //Intialize UI
        averageUI = (TextView) view.findViewById(R.id.avgValue);
        maxUI = (TextView) view.findViewById(R.id.maxValue);
        minUI = (TextView) view.findViewById(R.id.minValue);


        int minMinutes = minValue/60;
        int minSeconds = minValue%60;
        String minString = String.format("" + String.format("%02d", minMinutes) + ":" + String.format("%02d", minSeconds));
        minUI.setText(minString);

        int maxMinutes = maxValue/60;
        int maxSeconds = maxValue%60;
        String maxString = String.format("" + String.format("%02d", maxMinutes) + ":" + String.format("%02d", maxSeconds));
        maxUI.setText(maxString);


        mChart = (LineChart) view.findViewById(R.id.chart);
       mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(true);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);

        ArrayList<Entry> caloriesValues = new ArrayList<>();
        caloriesValues.add(new Entry(0,0f));


        ArrayList<Entry> stepValues = new ArrayList<>();
        stepValues.add(new Entry(0,0f));


        LineDataSet set1 = createCaloriesSet(caloriesValues);
        LineDataSet set2 = createStepsSet(stepValues);



        ArrayList<ILineDataSet> dataset1 = new ArrayList<>();
        dataset1.add(set1);
        dataset1.add(set2);

        LineData line = new LineData(dataset1);
        mChart.setData(line);

       workoutDetailsHandler.postDelayed(runnableRealTimeData, 1000);
       workoutDetailsHandler.postDelayed(runnableChartUI, 5000);


         return view ;
    }

    private void updateChartUI(float caloriesBurned) {
        LineData data = mChart.getData();

        if (data != null) {


            ILineDataSet caloriesSet = data.getDataSetByIndex(0);
            ILineDataSet stepsSet = data.getDataSetByIndex(1);

           //caloriesSet.addEntry(new Entry(3f, 4f));
            int steps = (int) (caloriesBurned/0.05);

            data.addEntry(new Entry(data.getDataSetByIndex(0).getEntryCount()*5, caloriesBurned), 0);
            data.addEntry(new Entry(data.getDataSetByIndex(1).getEntryCount()*5, steps), 1);

            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(10);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            if (data.getEntryCount() >= 5)
                mChart.moveViewToX(data.getEntryCount()*5);

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }


    private LineDataSet createCaloriesSet(ArrayList<Entry> cal) {

        LineDataSet set = new LineDataSet(cal, "Calories Burned");


        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.BLUE);
        set.setCircleColor(Color.BLUE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(Color.BLUE);
        set.setHighLightColor(Color.rgb(117, 117, 117));
        set.setValueTextColor(Color.BLUE);
        set.setValueTextSize(9f);
        return set;
    }

    private LineDataSet createStepsSet(ArrayList<Entry> st) {
        LineDataSet set = new LineDataSet(st, "Step Counts");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.RED);
        set.setCircleColor(Color.RED);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(Color.RED);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.RED);
        set.setValueTextSize(9f);
        return set;
    }


    private void calculateAVGandPostToUi()
    {
        int timeInSecond = (int) (timeFromDatabase/1000);
        int avg = (int) (timeInSecond/distanceFromDatabase);

        int minutes = avg/60;
        int seconds = avg%60;
        String s = String.format("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));


        minValue = sharedPreferences.getInt("MIN", 0);
        maxValue = sharedPreferences.getInt("MAX", 0);



        if(avg > minValue)
        {
            minValue = avg;
            edit.putInt("MIN", minValue);

            int minMinutes = minValue/60;
            int minSeconds = minValue%60;
            String minString = String.format("" + String.format("%02d", minMinutes) + ":" + String.format("%02d", minSeconds));
            minUI.setText(minString);

        }else if(avg < maxValue)
        {
            maxValue = avg ;
            edit.putInt("MAX", maxValue);

            int maxMinutes = maxValue/60;
            int maxSeconds = maxValue%60;
            String maxString = String.format("" + String.format("%02d", maxMinutes) + ":" + String.format("%02d", maxSeconds));
            maxUI.setText(maxString);
        }
        edit.apply();

        averageUI.setText(s);

    }



    public Runnable runnableChartUI = new Runnable() {
        @Override
        public void run() {

            if(getActivity() != null) {
                cursor = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI, null, "_id = ?", new String[]{"1"}, RecordContract.Contracts._ID);

                if (cursor.moveToFirst()) {

                    caloriesFromDatabase = cursor.getInt(cursor.getColumnIndex(RecordContract.Contracts.KEY_CALORIES_BURNED));

                }

                updateChartUI(caloriesFromDatabase - oldCalories);
                oldCalories = caloriesFromDatabase;

                workoutDetailsHandler.postDelayed(runnableChartUI, 5000);
            }

        }
    };


    public Runnable runnableRealTimeData = new Runnable() {
        @Override
        public void run() {
            if(getActivity() != null) {
                cursor = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI, null, "_id = ?", new String[]{"1"}, RecordContract.Contracts._ID);

                if (cursor.moveToFirst()) {
                    timeFromDatabase = cursor.getLong(cursor.getColumnIndex(RecordContract.Contracts.ALL_TIME_KEY_TIME));
                    distanceFromDatabase = cursor.getFloat(cursor.getColumnIndex(RecordContract.Contracts.ALL_TIME_KEY_DISTANCE));

                }
                calculateAVGandPostToUi();
                workoutDetailsHandler.postDelayed(runnableRealTimeData, 1000);
            }

        }
    };

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
