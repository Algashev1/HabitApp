package com.example.algashev.habitapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.algashev.habitapp.rest.Habit;
import com.example.algashev.habitapp.rest.Mark;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String ip = "192.168.1.2";

    private List<View> allEds;

    private LinearLayout linear;

    TextView text1, text2, text3, text4, text5, num1, num2, num3, num4, num5;

    List<Habit> habits;

    List<Mark> marks;

    private String[] days = { "Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"};

    private Calendar currentDate, date, newDate;

    private TextView[] texts, nums;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new MarksTask().execute();

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
        linear.removeAllViews();
        for (final Habit item: habits) {
            final View viewHabit = getLayoutInflater().inflate(R.layout.layoutcustom_edittext_layout, null);
            TextView name = viewHabit.findViewById(R.id.name);
            name.setText(item.getName());
            name.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(".EditActivity");
                            intent.putExtra("id_habit", item.getId());
                            intent.putExtra("name_habit", item.getName());
                            intent.putExtra("question_habit", item.getQuestion());
                            intent.putExtra("time_habit", item.getTime());
                            startActivity(intent);
                        }
                    }
            );

            initImages(viewHabit, item.getId());
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

    private void initImages(View viewHabit, final int id) {
        final ImageView[] images = {
                viewHabit.findViewById(R.id.imageView1),
                viewHabit.findViewById(R.id.imageView2),
                viewHabit.findViewById(R.id.imageView3),
                viewHabit.findViewById(R.id.imageView4),
                viewHabit.findViewById(R.id.imageView5)};

        int i = 0;
        for (final ImageView img: images) {
            Calendar c = (Calendar)date.clone();
            c.add(Calendar.DATE, -i);
            boolean flag = false;
            int idMark =  -1;
            for (int j = 0; j < marks.size(); j++) {
                if (marks.get(j).getIdHabit() == id && marks.get(j).check(c)) {
                    img.setImageResource(R.drawable.good);
                    flag = true;
                    idMark = marks.get(j).getId();
                    break;
                } else if (i > 0)
                    img.setImageResource(R.drawable.init);


            }

            final int finalI = i;
            final boolean finalFlag = flag;
            final int finalIdMark = idMark;
            i++;
            img.setOnLongClickListener(
                    new View.OnLongClickListener() {
                        boolean status = finalFlag;
                        int idMark = finalIdMark;
                        @Override
                        public boolean onLongClick(View v) {
                            if (!status) {
                                new AddMarkTask().execute(id, finalI);
                            }
                            else {
                                new DeleteMarkTask().execute(idMark);
                            }

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
                                EditText newName = addView.findViewById(R.id.habitNewName);
                                EditText newQuestion = addView.findViewById(R.id.habitNewQuestion);
                                if (!newName.getText().toString().equals("")) {
                                    AddHabitTask a = new AddHabitTask();
                                    a.execute(newName.getText().toString(), newQuestion.getText().toString());
                                }
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
        new MarksTask().execute();
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
        new MarksTask().execute();
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



    ///////////////////////////////////////////////////////////////////////////////////////////////
    private class HabitsTask extends AsyncTask<Void, Void, List<Habit>> {
        @Override
        protected List<Habit> doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                final String url = "http://" + ip + ":8080/habits";
                ResponseEntity<List<Habit>> habitResponse = restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Habit>>() {});
                habits = habitResponse.getBody();
                return habits;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Habit> habits) {
            init();
        }
    }

    private class AddHabitTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                final String url = "http://" + ip + ":8080/addHabit?name="
                        + params[0] + "&question=" + params[1];
                restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Habit>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void task) {
            new HabitsTask().execute();
        }
    }

    private class AddMarkTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            try {
                Calendar d = (Calendar)date.clone();
                d.add(Calendar.DATE, -params[1]);
                RestTemplate restTemplate = new RestTemplate();
                final String url = "http://" + ip + ":8080/addMark?id="
                        + params[0] + "&year=" + d.get(Calendar.YEAR) + "&month=" + d.get(Calendar.MONTH)
                        + "&day=" + d.get(Calendar.DATE);
                restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Habit>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void task) {
            new MarksTask().execute();
        }
    }

    private class MarksTask extends AsyncTask<Void, Void, List<Mark>> {
        @Override
        protected List<Mark> doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                final String url = "http://" + ip + ":8080/marks?year=" + date.get(Calendar.YEAR) + "&month=" + date.get(Calendar.MONTH)
                        + "&day=" + date.get(Calendar.DATE);
                ResponseEntity<List<Mark>> markResponse = restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Mark>>() {});
                marks = markResponse.getBody();
                return marks;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Mark> marks) {
            new HabitsTask().execute();
        }
    }

    private class DeleteMarkTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                final String url = "http://" + ip + ":8080/deleteMark?id=" + params[0];
                restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Habit>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void task) {
            new MarksTask().execute();
        }
    }
}
