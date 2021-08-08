package ru.com.riskcontrol;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MinimizationMeasure {

    public final int id;
    public String name;
    public String responsible;
    public String date;
    public String dateOfCreation;

    public MinimizationMeasure(int id, Context context){
        this.id = id;
        if (id==-1) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            Date date = cal.getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
            this.dateOfCreation = format1.format(date);
            return;
        }

        DBHelper dpHelper = new DBHelper(context);
        SQLiteDatabase db = dpHelper.getReadableDatabase();

        Cursor cursor = db.query("minimization_measure", null, "_id=?", new String[]{String.valueOf(id)},null,null,null);
        {
            int cursorName = cursor.getColumnIndex("name");
            int cursorResponsible = cursor.getColumnIndex("responsible");
            int cursorDateOfCreation = cursor.getColumnIndex("date_of_creation");
            int cursorDate = cursor.getColumnIndex("date");

            cursor.moveToFirst();

            this.name = cursor.getString(cursorName);
            this.responsible = cursor.getString(cursorResponsible);
            this.dateOfCreation = cursor.getString(cursorDateOfCreation);
            this.date = cursor.getString(cursorDate);


            cursor.close();
        }
    }

    public boolean save(Context context) {
        //if (!canUpdate()) return false;

        DBHelper dpHelper = new DBHelper(context);
        SQLiteDatabase db = dpHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();


        cv.put("name", this.name);
        cv.put("responsible", this.responsible);
        cv.put("date", this.date);
        cv.put("date_of_creation", this.dateOfCreation);


        if (this.id == -1)
            db.insert("minimization_measure", null, cv);

        else
            db.update("minimization_measure", cv, "_id = ?", new String[]{String.valueOf(id)});

        return true;
    }
}
