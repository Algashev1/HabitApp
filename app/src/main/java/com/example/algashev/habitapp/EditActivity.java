package com.example.algashev.habitapp;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.algashev.habitapp.rest.Habit;
import com.example.algashev.habitapp.rest.Mark;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    int id_habit;
    String name_habit, question_habit, time_habit;
    Calendar creationDate;

    List<Mark> marks;

    TextView name, time;
    ImageView delete, edit, info;
    MaterialCalendarView calendarView;
    Switch switch_habit;

    private BootReceiver alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Bundle arguments = getIntent().getExtras();
        id_habit = (int) arguments.get("id_habit");
        name_habit = arguments.get("name_habit").toString();
        question_habit = arguments.get("question_habit").toString();
        time_habit = arguments.get("time_habit").toString();
        creationDate = (Calendar) arguments.get("creationDate");

        name = findViewById(R.id.name);
        name.setText(name_habit);

        delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(EditActivity.this);
                a_builder.setCancelable(false)
                        .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new DeleteHabitTask().execute();
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = a_builder.create();
                alert.setTitle("Удалить привычку");
                alert.setMessage("Вы действительно хотите удалить привычку?");
                alert.show();
            }
        });

        edit = findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(EditActivity.this);
                final View addView = getLayoutInflater().inflate(R.layout.add_habit, null);
                EditText newName = addView.findViewById(R.id.habitNewName);
                newName.setText(name_habit);
                EditText newQuestion = addView.findViewById(R.id.habitNewQuestion);
                newQuestion.setText(question_habit);
                a_builder.setView(addView);
                a_builder.setCancelable(false)
                        .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText newName = addView.findViewById(R.id.habitNewName);
                                EditText newQuestion = addView.findViewById(R.id.habitNewQuestion);
                                if (!newName.getText().toString().equals("")) {
                                    new UpdateHabitTask().execute(newName.getText().toString(),
                                            newQuestion.getText().toString(), time.getText().toString());
                                }
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = a_builder.create();
                alert.setTitle("Изменить привычку");
                alert.show();
            }
        });

        new MarksTask().execute();

        info = findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(".InfoActivity");
                intent.putExtra("id_habit", id_habit);
                intent.putExtra("name_habit", name_habit);
                intent.putExtra("creationDate", creationDate);
                startActivity(intent);
            }
        });

        time = findViewById(R.id.time);
        time.setText(time_habit);
        time.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callTimePicker();
                    }
                }
        );


        switch_habit = findViewById(R.id.switch1);
        switch_habit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SQLiteDatabase db;
                    DBHelper dbHelper = new DBHelper(EditActivity.this, DBHelper.TABLE_NAME, null, 2);
                    try {
                        db = dbHelper.getWritableDatabase();

                        Cursor cursor = db.query(DBHelper.TABLE_NAME, null, DBHelper.ID + "=" + id_habit,
                                null, null, null, null, null);

                        if (cursor.getCount() < 0) {
                            ContentValues newValues = new ContentValues();
                            newValues.put(DBHelper.ID, id_habit);
                            newValues.put(DBHelper.DAYS, "1,2,3,4,5,6,7");
                            newValues.put(DBHelper.STATUS, 1);
                            db.insert(DBHelper.TABLE_NAME, null, newValues);
                        }
                        else {
                            ContentValues newValues = new ContentValues();
                            newValues.put(DBHelper.DAYS, "1,2,3,4,5,6,7");
                            newValues.put(DBHelper.STATUS, 1);
                            String where = DBHelper.ID + "=" + id_habit;
                            db.update(DBHelper.TABLE_NAME, newValues, where, null);
                        }
                        alarm = new BootReceiver();
                        Context context = EditActivity.this.getApplicationContext();
                        alarm.setAlarm(context, String.valueOf(id_habit), name_habit, question_habit, time_habit, Calendar.getInstance());
                    }
                    catch (SQLiteException ex){
                        db = dbHelper.getReadableDatabase();
                    }
                }
                else {
                    Context context = EditActivity.this.getApplicationContext();
                    alarm.cancelAlarm(context);
                }
            }
        });
    }

    private void callTimePicker() {
        final Calendar cal = Calendar.getInstance();
        int mHour = cal.get(Calendar.HOUR_OF_DAY);
        int mMinute = cal.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String editTextTimeParam = hourOfDay + ":" + minute;
                        time.setText(editTextTimeParam);
                        time_habit = editTextTimeParam;
                        new UpdateTimeHabitTask().execute(editTextTimeParam);
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private class DeleteHabitTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                final String url = "http://" + MainActivity.ip + ":8080/deleteHabit?id=" + id_habit;
                restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Habit>>() {
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void task) {
            SQLiteDatabase db;
            DBHelper dbHelper = new DBHelper(EditActivity.this, DBHelper.TABLE_NAME, null, 2);
            try {
                db = dbHelper.getWritableDatabase();

                Cursor cursor = db.query(DBHelper.TABLE_NAME, null, DBHelper.ID + "=" + id_habit,
                        null, null, null, null, null);

                if (cursor.getCount() < 0) {
                    db.delete(DBHelper.TABLE_NAME, DBHelper.ID + "=" + id_habit, null);
                }
            }
            catch (SQLiteException ex){
                db = dbHelper.getReadableDatabase();
            }


            Intent intent = new Intent(EditActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private class UpdateHabitTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                name_habit = params[0];
                question_habit = params[1];
                final String url = "http://" + MainActivity.ip + ":8080/updateHabit?id=" + id_habit + "&name=" + params[0] + "&question=" + params[1] + "&time=" + params[2];
                restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Habit>>() {
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void task) {
            name = findViewById(R.id.name);
            name.setText(name_habit);
        }
    }

    private class UpdateTimeHabitTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                final String url = "http://" + MainActivity.ip + ":8080/updateTimeHabit?id=" + id_habit + "&time=" + params[0];
                restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Habit>>() {
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
             return null;
        }

        @Override
        protected void onPostExecute(Void task) {
            name = findViewById(R.id.name);
            name.setText(name_habit);
        }
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
            calendarView = findViewById(R.id.calendarView);

            for (Mark item: marks) {
                calendarView.setDateSelected(item.getDate(), true);
            }

            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);


        }
    }
}
