package edu.sjsu.anis.alphafitness;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Cursor cursor = getActivity().getContentResolver().query(MyContentProvider.CONTENT_URI, null, "_id = ?", new String[]{"1"}, RecordContract.Contracts._ID);


        if (cursor.moveToFirst()) {

            caloriesFromDatabase = cursor.getInt(cursor.getColumnIndex(RecordContract.Contracts.KEY_CALORIES_BURNED));

        }



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
        caloriesValues.add(new Entry(1, caloriesFromDatabase));


        ArrayList<Entry> stepValues = new ArrayList<>();
        stepValues.add(new Entry(0,0f));
        stepValues.add(new Entry(1, 10f));


        LineDataSet set1 = createCaloriesSet(caloriesValues);
        LineDataSet set2 = createStepsSet(stepValues);



        ArrayList<ILineDataSet> dataset1 = new ArrayList<>();
        dataset1.add(set1);
        dataset1.add(set2);

        LineData line = new LineData(dataset1);
        mChart.setData(line);


//        // set an alternative background color
//        mChart.setBackgroundColor(Color.WHITE);
//
//        LineData data = new LineData();
//        data.setValueTextColor(Color.BLACK);
//
//        mChart.setData(data);
//
//        // get the legend (only possible after setting data)
//        Legend l = mChart.getLegend();
//
//        // modify the legend ...
//        l.setForm(Legend.LegendForm.LINE);
//        l.setTextColor(Color.BLACK);
//
//        XAxis xl = mChart.getXAxis();
//        xl.setTextColor(Color.BLACK);
//        xl.setDrawGridLines(false);
//        xl.setAxisMinimum(0f);
//        xl.setAvoidFirstLastClipping(true);
//        xl.setEnabled(true);
//        xl.setDrawLabels(true);
//        xl.setGranularity(1f);
//        xl.setXOffset(10f);
//        xl.setYOffset(0f);


//        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setTextColor(Color.BLACK);
//        leftAxis.setAxisMinimum(0f);
//        leftAxis.setDrawGridLines(true);
//        leftAxis.setGranularity(1f);
//
//        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setEnabled(false);

        updateChartUI();


         return view ;
    }

    private void updateChartUI() {
        LineData data = mChart.getData();

        if (data != null) {


            ILineDataSet caloriesSet = data.getDataSetByIndex(0);
            ILineDataSet stepsSet = data.getDataSetByIndex(1);

            caloriesSet.addEntry(new Entry(3f, 4f));
            data.addEntry(new Entry(3f, 4f), 0);


            // set.addEntry(...); // can be called as well

//            if (caloriesSet == null || stepsSet == null) {
//               // caloriesSet = createCaloriesSet();
////                stepsSet = createStepsSet();
//                data.addDataSet(caloriesSet);
//                data.addDataSet(stepsSet);
//
//                Entry e = new Entry(0f, 0);
//                data.addEntry(e, 0);
//                data.addEntry(e, 1);
////            }
//
//            //Add new data entries to Calories Set
//            int currentCaloriesSetSize = caloriesSet.getEntryCount();
//            for (int i=currentCaloriesSetSize; i<MainScreenActivity.caloriesEntries.size(); i++) {
//                Entry e = MainScreenActivity.caloriesEntries.get(i);
//                data.addEntry(e, 0);
//            }
//
//            //Add new data entries to Steps Set
//            int currentStepCountSetSize = stepsSet.getEntryCount();
//            for (int i=currentStepCountSetSize; i<MainScreenActivity.stepsEntries.size(); i++) {
//                Entry e = MainScreenActivity.stepsEntries.get(i);
//                data.addEntry(e, 1);




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
