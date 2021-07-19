package ru.com.riskcontrol;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Factory {
    public final int id;
    public String name;
    public String description;


    public Factory(int id, Context context){
        this.id = id;
        if (id==-1) return;
        DBHelper dpHelper = new DBHelper(context);
        SQLiteDatabase db = dpHelper.getReadableDatabase();

        Cursor cursor = db.query("factories",  null, "_id=?", new String[]{String.valueOf(id)},null,null,null);
        {
            cursor.moveToFirst();
            int cursorName = cursor.getColumnIndex("name");
            int cursorDesc = cursor.getColumnIndex("description");

            this.name = cursor.getString(cursorName);
            this.description = cursor.getString(cursorDesc);
            cursor.close();
        }
    }

    public void save(Context context){
        DBHelper dpHelper = new DBHelper(context);
        SQLiteDatabase db = dpHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", this.name);
        cv.put("description", this.description);
        if (this.id == -1)
            db.insert("factories", null, cv);
        else
            db.update("factories", cv, "_id = ?", new String[]{String.valueOf(this.id)});
    }



}
