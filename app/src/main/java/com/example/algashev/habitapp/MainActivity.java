package com.example.algashev.habitapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<View> allEds;

    private LinearLayout linear;

    TextView text1, text2, text3, text4, text5, num1, num2, num3, num4, num5;

    private String[] habits = { "Подтягивание",  "Бег",  "Приём витаминов"};

    private String[] days = { "Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"};

    private Calendar currentDate, date, newDate;

    private TextView[] texts, nums;

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

        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        num3 = findViewById(R.id.num3);
        num4 = findViewById(R.id.num4);
        num5 = findViewById(R.id.num5);
        nums = new TextView[]{ num1, num2, num3, num4, num5 };

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
            name.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(".EditActivity");
                            startActivity(intent);
                        }
                    }
            );


            initImages(viewHabit);
            allEds.add(viewHabit);
            linear.addView(viewHabit);
        }
    }

    private void initDays() {

        currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());

        date = (Calendar) currentDate.clone();
        newDate = (Calendar) currentDate.clone();

        currentDate.add(Calendar.DATE, 1);

        for (int i = 0; i < texts.length; i++) {
            texts[i].setText(days[newDate.get(Calendar.DAY_OF_WEEK) - 1]);
            nums[i].setText(Integer.toString(newDate.get(Calendar.DAY_OF_MONTH)));
            newDate.add(Calendar.DATE, -1);
        }
    }

    private void initImages(View viewHabit) {
        ImageView[] images = {
                viewHabit.findViewById(R.id.imageView1),
                viewHabit.findViewById(R.id.imageView2),
                viewHabit.findViewById(R.id.imageView3),
                viewHabit.findViewById(R.id.imageView4),
                viewHabit.findViewById(R.id.imageView5)};


        for (final ImageView img: images) {
            img.setOnLongClickListener(
                    new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            System.out.println();
                            img.setImageResource(R.drawable.good);
                            return true;
                        }
                    }
            );

            img.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Нажмите и удерживайте, чтобы установить или снять галочку",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );
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
                                initImages(viewHabit);
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

    private void updateDaysAdd() {
        date.add(Calendar.DATE, -1);
        newDate = (Calendar)date.clone();
        for (int i = 0; i < nums.length; i++) {
            texts[i].setText(days[newDate.get(Calendar.DAY_OF_WEEK) - 1]);
            nums[i].setText(Integer.toString(newDate.get(Calendar.DAY_OF_MONTH)));
            newDate.add(Calendar.DATE, -1);
        }
    }

    private void updateDaysSub() {
        date.add(Calendar.DATE, 1);
        if (date.before(currentDate)) {
            newDate = (Calendar)date.clone();
            for (int i = 0; i < nums.length; i++) {
                texts[i].setText(days[newDate.get(Calendar.DAY_OF_WEEK) - 1]);
                nums[i].setText(Integer.toString(newDate.get(Calendar.DAY_OF_MONTH)));
                newDate.add(Calendar.DATE, -1);
            }
        }
        else {
            date.add(Calendar.DATE, -1);
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

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
