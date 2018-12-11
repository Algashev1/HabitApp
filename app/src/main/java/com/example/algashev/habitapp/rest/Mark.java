package com.example.algashev.habitapp.rest;

import java.util.Calendar;

public class Mark {
    private int id;
    private Calendar date;
    private int idHabit;

    public int getId() {
        return id;
    }

    public Calendar getDate() {
        return date;
    }

    public int getIdHabit() {
        return idHabit;
    }

    public boolean check(Calendar c) {
        if (date.get(Calendar.YEAR) == c.get(Calendar.YEAR) &&
                date.get(Calendar.MONTH) == c.get(Calendar.MONTH) &&
                date.get(Calendar.DATE) == c.get(Calendar.DATE)) {
            return true;
        }
        return false;
    }
}
