package edu.sjsu.anis.alphafitness;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

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


public class BlankFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {

    private LineChart mChart;

    private float caloriesFromDatabase;
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




         cursor = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI, null, "_id = ?", new String[]{"1"}, RecordContract.Contracts._ID);


        if (cursor.moveToFirst()) {

            caloriesFromDatabase = cursor.getInt(cursor.getColumnIndex(RecordContract.Contracts.KEY_CALORIES_BURNED));

        }

        oldCalories = caloriesFromDatabase;




        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
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


       workoutDetailsHandler.postDelayed(updateUI, 5000);


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


    public Runnable updateUI = new Runnable() {
        @Override
        public void run() {

            if(getActivity() != null) {
                cursor = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI, null, "_id = ?", new String[]{"1"}, RecordContract.Contracts._ID);

                if (cursor.moveToFirst()) {

                    caloriesFromDatabase = cursor.getInt(cursor.getColumnIndex(RecordContract.Contracts.KEY_CALORIES_BURNED));

                }

                updateChartUI(caloriesFromDatabase - oldCalories);
                oldCalories = caloriesFromDatabase;

                workoutDetailsHandler.postDelayed(updateUI, 25000);
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
