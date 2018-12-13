package com.example.algashev.habitapp;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.algashev.habitapp.rest.Mark;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.PieChartView;

public class InfoActivity extends AppCompatActivity {

    private PieChartView chart;
    private PieChartData data;
    private boolean hasLabels = false;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = true;
    private boolean hasCenterText1 = true;
    private boolean hasCenterText2 = true;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;

    int id_habit;
    String name_habit;
    Calendar creationDate;

    TextView name;

    List<Mark> marks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Bundle arguments = getIntent().getExtras();
        id_habit = (int) arguments.get("id_habit");
        name_habit = arguments.get("name_habit").toString();
        creationDate = (Calendar) arguments.get("creationDate");

        new MarksTask().execute();

        name = findViewById(R.id.name);
        name.setText(name_habit);

    }

    private class MarksTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                final String url = "http://" + MainActivity.ip + ":8080/marksByHabit?id=" + id_habit;
                ResponseEntity<List<Mark>> markResponse = restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Mark>>() {
                        });
                marks = markResponse.getBody();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void task) {
            Calendar curentDate = Calendar.getInstance();
            long a = curentDate.getTimeInMillis() - creationDate.getTimeInMillis();

            long days = a / 1000 / 60 / 60 / 24 + 1;

            chart = findViewById(R.id.chart);
            int numValues = 2;
            List<SliceValue> values = new ArrayList<>();

            SliceValue sliceValue = new SliceValue((float) days-marks.size(), ChartUtils.pickColor());
            values.add(sliceValue);
            sliceValue = new SliceValue((float) marks.size(), ChartUtils.pickColor());
            values.add(sliceValue);

            data = new PieChartData(values);
            data.setHasLabels(hasLabels);
            data.setHasLabelsOnlyForSelected(hasLabelForSelected);
            data.setHasLabelsOutside(hasLabelsOutside);
            data.setHasCenterCircle(hasCenterCircle);

            if (isExploded) {
                data.setSlicesSpacing(24);
            }

            if (hasCenterText1) {
                data.setCenterText1(String.valueOf(marks.size()));
                data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                        (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
            }

//            if (hasCenterText2) {
//                data.setCenterText2("Charts (Roboto Italic)");
//                data.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
//                        (int) getResources().getDimension(R.dimen.pie_chart_text2_size)));
//            }

            chart.setPieChartData(data);
        }
    }
}
