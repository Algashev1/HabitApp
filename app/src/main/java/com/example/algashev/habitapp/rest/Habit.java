package com.example.algashev.habitapp.rest;

import java.util.Calendar;

public class Habit {

    private int id;
    private String name;
    private String question;
    private String time;
    private Calendar creationDate;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getQuestion() {
        return question;
    }

    public String getTime() {
        return time;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public boolean check(Calendar c, int i) {
        Calendar d = (Calendar)c.clone();
        d.add(Calendar.DATE, -i);
        if (creationDate.get(Calendar.YEAR) <= d.get(Calendar.YEAR) &&
                creationDate.get(Calendar.MONTH) <= d.get(Calendar.MONTH) &&
                creationDate.get(Calendar.DATE) <= d.get(Calendar.DATE)) {
            return true;
        }
        return false;
    }
}

