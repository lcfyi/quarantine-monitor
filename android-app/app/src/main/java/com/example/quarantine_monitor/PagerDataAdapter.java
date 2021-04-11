package com.example.quarantine_monitor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/*
    Acts as an adapter for individual pages in the statistics dashboard
 */
public class PagerDataAdapter extends PagerAdapter implements OnChartGestureListener, OnChartValueSelectedListener {

    private ActiveUserStats model;
    private LayoutInflater layoutInflater;
    private Context context;
    private LineChart chart_test;
    private Boolean chart_test_format_hours = false;

    private static final int INDEX_TEST_PERCENTAGE = 0;

    public PagerDataAdapter(Context context) {
        this.context = context;
    }

    public void setModel(ActiveUserStats model) {
        this.model = model;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        layoutInflater = LayoutInflater.from(context);
        View view;

        // Calls the create function for each individual
        if (position == INDEX_TEST_PERCENTAGE) {
            view = createTestPercentageCard(layoutInflater.inflate(R.layout.card_testsuccessrate, container, false));
        } else {
            view = createTimeGraph(layoutInflater.inflate(R.layout.card_test_graph, container, false));
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    // Creates card containing test success percentage
    private View createTestPercentageCard(View view) {
        // Calculate percentage
        int total = model.testStatusMap.values().size();
        int success = (int) model.testStatusMap.values().stream().filter(status -> status.booleanValue()).count();
        double percentage = 0;
        String quarantine_status = new Date().getTime() > model.unixEndTime ? "COMPLETED" : "ACTIVE";

        if (total > 0) {
            percentage = success * 100.0 / total;
        } else if (success == 0) {
            percentage = 100;
        }

        TextView text_percentage = view.findViewById(R.id.text_test_percentage);
        TextView text_detail = view.findViewById(R.id.text_test_message);
        TextView text_created = view.findViewById(R.id.text_account_created);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.ENGLISH);

        text_created.setText("Created: " + sdf.format(new Date(model.unixStartTime)) + "\n\nQuarantine Status: " + quarantine_status);
        text_percentage.setText(String.format("%d%%", (int) percentage));
        text_detail.setText(String.format("Test Success Rate\n( %d of %d )", success, total));
        return view;
    }

    // Creates line chart for test completion history
    private View createTimeGraph(View view) {
        chart_test = view.findViewById(R.id.chart_test);
        chart_test.setOnChartValueSelectedListener(this);
        chart_test.setDrawBorders(true);

        chart_test.setDrawGridBackground(false);
        chart_test.getDescription().setEnabled(false);

        // enable touch gestures
        chart_test.setTouchEnabled(true);

        // enable scaling and dragging
        chart_test.setDragEnabled(true);
        chart_test.setScaleEnabled(false);
        chart_test.setPinchZoom(false);

        Legend l = chart_test.getLegend();
        l.setEnabled(false);

        ArrayList<Entry> testStatusValues = new ArrayList<>();
        ArrayList<Integer> circleColors = new ArrayList<>();

        for (Date d : model.testStatusMap.keySet()) {
            testStatusValues.add(new Entry(d.getTime()/60000f, Boolean.compare(model.testStatusMap.get(d), false)));
            if (model.testStatusMap.get(d)) {
                circleColors.add(Color.GREEN);
            } else {
                circleColors.add(Color.RED);
            }
        }

        LineDataSet set = new LineDataSet(testStatusValues, "Test Status");
        set.setLineWidth(2f);
        set.setCircleRadius(3f);
        set.setCircleColors(circleColors.stream().mapToInt(i -> i).toArray());
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set.setDrawFilled(true);
        Drawable drawable = ContextCompat.getDrawable(this.context, R.drawable.fade_blue);
        set.setFillDrawable(drawable);
        set.setDrawCircleHole(false);
        set.setDrawValues(false);
        set.setHighlightLineWidth(3);
        set.setDrawHorizontalHighlightIndicator(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        LineData data = new LineData(dataSets);
        chart_test.setData(data);

        XAxis xAxis = chart_test.getXAxis();
        xAxis.setValueFormatter(new GraphDateFormatter());
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(14f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(false);
        xAxis.setCenterAxisLabels(true);

        long endtime = Math.min(model.unixEndTime, new Date().getTime());
        long starttime = model.unixStartTime;

        // Round to nearest midnight and convert to minutes unit
        starttime = new Date(starttime - starttime % (24*60*60*1000)).getTime() / 60000l;
        endtime = new Date(endtime - endtime % (24*60*60*1000)).getTime() / 60000l;

        // Hot fix for crash when account created very recently in the past
        if (starttime >= endtime) {
            endtime += 24*60;
        }

        // Creates slider below the card
        RangeSlider slider = view.findViewById(R.id.time_slider);
        slider.setValueFrom((float) starttime);
        slider.setValueTo((float) endtime);
        slider.setStepSize(24*60);
        slider.setValues(new ArrayList<Float>(Arrays.asList((float) starttime, (float) endtime)));
        slider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                Date date = new Date((long)value*60000);
                //Specify the format you'd like
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d yyyy", Locale.ENGLISH);
                return sdf.format(date);
            }
        });
        slider.addOnChangeListener(sliderChangeListener);

        chart_test.getAxisLeft().setAxisMinimum(0);
        chart_test.getAxisLeft().setAxisMaximum(1f);
        chart_test.getAxisLeft().setEnabled(false);
        chart_test.getAxisRight().setEnabled(false);

        chart_test.setExtraOffsets(5f,0f,5f,40f);

        GraphMarkerView mv = new GraphMarkerView(context, R.layout.custom_marker_view_layout);
        chart_test.setMarkerView(mv);

        xAxis.setLabelRotationAngle(60f);
        xAxis.setAxisMinimum(starttime-720f);
        xAxis.setAxisMaximum(endtime+720f);
        xAxis.setGranularity(1440);
        xAxis.setLabelCount((int) Math.floor((endtime - starttime) / 1440), true);
        chart_test.animateY(1000);
        return view;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) { }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) { }

    @Override
    public void onChartLongPressed(MotionEvent me) { }

    @Override
    public void onChartDoubleTapped(MotionEvent me) { }

    @Override
    public void onChartSingleTapped(MotionEvent me) { }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) { }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) { }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) { }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
    }

    @Override
    public void onNothingSelected() { }

    // Slider change listener for when range slider is configured
    private final RangeSlider.OnChangeListener sliderChangeListener = new RangeSlider.OnChangeListener() {
        @Override
        public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
            ArrayList<Float> values = (ArrayList) slider.getValues();
            chart_test.getXAxis().setAxisMinimum(values.get(0)-480f);
            chart_test.getXAxis().setAxisMaximum(values.get(1)+720f);
            chart_test.moveViewToAnimated(values.get(0), 0.5f, YAxis.AxisDependency.LEFT, 500);
            chart_test.notifyDataSetChanged();
            scaleChartLabels(chart_test);
            chart_test.invalidate();
        }
    };

    // Sets the granularity and label count depending on the slider selected values
    private void scaleChartLabels(LineChart chart) {
        float max = chart.getHighestVisibleX();
        float min = chart.getLowestVisibleX();
        float totalXVisible = max - min;

        float granularity = 24*60;
        int labelCount = 0;

        if (totalXVisible < 48*60) {
            granularity = 60;
            chart_test_format_hours = true;
            labelCount = 8;
        } else {
            labelCount = (int) Math.floor(totalXVisible / 1440);
            chart_test_format_hours = false;
        }

        chart.getXAxis().setGranularity(granularity);
        chart.getXAxis().setLabelCount(labelCount);
    }

    // Formats the x-axis values in the line graph
    class GraphDateFormatter extends IndexAxisValueFormatter {

        @Override
        public String getFormattedValue(float value) {

            Date date = new Date((long)value*60000);
            //Specify the format you'd like
            if (chart_test_format_hours) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d HH:mm", Locale.ENGLISH);
                return sdf.format(date);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d", Locale.ENGLISH);
                return sdf.format(date);
            }


        }
    }

    // Formats the data values in the line graph
    class GraphMarkerView extends MarkerView {

        private TextView tvContent;

        public GraphMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);

            // find your layout components
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            Date date = new Date((long)e.getX()*60000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.ENGLISH);
            tvContent.setText(sdf.format(date));

            // this will perform necessary layouting
            super.refreshContent(e, highlight);
        }

        private MPPointF mOffset;

        @Override
        public MPPointF getOffset() {

            if(mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
            }

            return mOffset;
        }
    }
}




