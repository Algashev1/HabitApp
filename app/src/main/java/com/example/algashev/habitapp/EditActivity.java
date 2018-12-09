package com.example.algashev.habitapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.algashev.habitapp.rest.Habit;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class EditActivity extends AppCompatActivity {

    int id_habit;
    String name_habit, question_habit;

    TextView name;
    ImageView delete, edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Bundle arguments = getIntent().getExtras();
        id_habit = (int) arguments.get("id_habit");
        name_habit = arguments.get("name_habit").toString();
        question_habit = arguments.get("question_habit").toString();

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
                            public void onClick (DialogInterface dialog, int which) {
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
                            public void onClick (DialogInterface dialog, int which) {
                                EditText newName = addView.findViewById(R.id.habitNewName);
                                EditText newQuestion = addView.findViewById(R.id.habitNewQuestion);
                                if (!newName.getText().toString().equals("")) {
                                    new UpdateHabitTask().execute(newName.getText().toString(),
                                            newQuestion.getText().toString());
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
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(EditActivity.this, MainActivity.class );
        startActivity(intent);
    }

    private class DeleteHabitTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                final String url = "http://192.168.1.3:8080/deleteHabit?id=" + id_habit;
                restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Habit>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void task) {
            Intent intent = new Intent(EditActivity.this, MainActivity.class );
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
                final String url = "http://192.168.1.3:8080/updateHabit?id=" + id_habit + "&name=" + params[0] + "&question=" + params[1];
                restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Habit>>() {});
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
}
