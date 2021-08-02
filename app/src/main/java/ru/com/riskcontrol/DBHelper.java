package ru.com.riskcontrol;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 20;
    private static final String DATABASE_NAME="db_main";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table factories (_id integer primary key, name text, description text)");
        db.execSQL("create table registries (_id integer primary key, factory_id integer, risks_ids text, model integer,scale text, date_of_creation text)");
        db.execSQL("create table risks (_id integer primary key, name text, risk_type_id integer, probability_of_occurrence real, detection_probability_estimate real, severity_assessment real, date_of_creation text)");
        db.execSQL("create table risk_types (_id integer primary key, name text, value integer)");
        db.execSQL("create table minimization_measure (_id integer primary key, name text, responsible text, date text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists factories");
        db.execSQL("drop table if exists registries");
        db.execSQL("drop table if exists risks");
        db.execSQL("drop table if exists risk_types");
        db.execSQL("drop table if exists minimization_measure");
        this.onCreate(db);
    }
}
