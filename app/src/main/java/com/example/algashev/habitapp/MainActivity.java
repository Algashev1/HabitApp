package com.example.algashev.habitapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<View> allEds;

    private LinearLayout linear;

    TextView text1, text2, text3, text4, text5;

    private String[] habits = { "Подтягивание",  "Бег",  "Приём витаминов"};

    private String[] days = { "Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"};

    private TextView[] texts;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linear = findViewById(R.id.linear);
        allEds = new ArrayList<>();
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        text5 = findViewById(R.id.text5);
        texts = new TextView[]{ text1, text2, text3, text4, text5 };

        init();
        initDays();
        addHabit();




        final GestureDetector gdt = new GestureDetector(new GestureListener());
        final LinearLayout layout  = findViewById(R.id.date);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }
        });
    }

    public void init() {
        for (String nameHabits: habits) {
            final View viewHabit = getLayoutInflater().inflate(R.layout.layoutcustom_edittext_layout, null);
            TextView name = viewHabit.findViewById(R.id.name);
            name.setText(nameHabits);
            allEds.add(viewHabit);
            linear.addView(viewHabit);
        }
    }

    public void addHabit() {
        FloatingActionButton addButton = findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
                final View addView = getLayoutInflater().inflate(R.layout.add_habit, null);
                a_builder.setView(addView);
                a_builder.setCancelable(false)
                        .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick (DialogInterface dialog, int which) {
                                final View viewHabit = getLayoutInflater().inflate(R.layout.layoutcustom_edittext_layout, null);
                                TextView name = viewHabit.findViewById(R.id.name);
                                EditText newName = addView.findViewById(R.id.habitNewName);
                                name.setText(newName.getText());
                                allEds.add(viewHabit);
                                linear.addView(viewHabit);
                            }
                        })
                        .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = a_builder.create();
                alert.setTitle("Добавить привычку");
                alert.show();
            }
        });
    }




    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                updateDaysAdd();
                return false;
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                updateDaysSub();
                return false;
            }
            return false;
        }
    }

    private void initDays() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int index = c.get(Calendar.DAY_OF_WEEK) - 1;
        int number = 0;

        for (int i = index; i < days.length && number < 5; i++) {
            texts[number].setText(days[i]);
            number++;
        }

        for (int i = 0; i < index && number < 5; i++) {
            texts[number].setText(days[i]);
            number++;
        }
    }

    private void updateDaysAdd() {
        int index = Arrays.asList(days).indexOf(text1.getText());
        int number = 0;

        for (int i = index + 1; i < days.length && number < 5; i++) {
            texts[number].setText(days[i]);
            number++;
        }

        for (int i = 0; i < index && number < 5; i++) {
            texts[number].setText(days[i]);
            number++;
        }
    }

    private void updateDaysSub() {
        int index = Arrays.asList(days).indexOf(text5.getText());
        int number = 4;

        for (int i = index - 1; i >= 0 && number >= 0 ; i--) {
            texts[number].setText(days[i]);
            number--;
        }

        for (int i = days.length - 1; i > index && number >= 0; i--) {
            texts[number].setText(days[i]);
            number--;
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
