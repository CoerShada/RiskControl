package ru.com.riskcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RiskType {

    public final int id;
    public String name;
    public int value;


    public RiskType(int id, Context context){
        this.id = id;
        if (id==-1) return;
        DBHelper dpHelper = new DBHelper(context);
        SQLiteDatabase db = dpHelper.getWritableDatabase();
        Cursor cursor = db.query("risk_types",  null, "_id=?", new String[]{String.valueOf(id)},null,null,null);
        {
            cursor.moveToFirst();
            int cursorName = cursor.getColumnIndex("name");
            int cursorValue= cursor.getColumnIndex("value");

            this.name = cursor.getString(cursorName);
            this.value = cursor.getInt(cursorValue);
            cursor.close();
        }
    }

    public void save(Context context){
        DBHelper dpHelper = new DBHelper(context);
        SQLiteDatabase db = dpHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", this.name);
        cv.put("value", this.value);
        if (id==-1)
            db.insert("risk_types", null , cv);
        else
            db.update("risk_types", cv, "_id = ?", new String[]{String.valueOf(this.id)});
    }
}
