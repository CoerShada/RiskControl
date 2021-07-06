package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    DBHelper dpHelper = new DBHelper(this);
    Registry[] registries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SQLiteDatabase db = dpHelper.getReadableDatabase();

        Cursor cursor = db.query("registries", null, null,null,null,null,null);
        {
            this.registries = new Registry[cursor.getCount()];
            cursor.moveToFirst();
            int index = 0;
            int cursorId= cursor.getColumnIndex("_id");
            int dateOfCreation = cursor.getColumnIndex("dateOfCreation");
            int risksIds = cursor.getColumnIndex("risksIds");
            int scale = cursor.getColumnIndex("scale");
            if (cursor.getCount()>0) {
                do {
                    registries[index] = new Registry(cursor.getInt(cursorId), cursor.getString(dateOfCreation), cursor.getString(risksIds).split(","), Float.parseFloat(cursor.getString(scale).substring(0, cursor.getString(scale).indexOf("/"))), Float.valueOf(cursor.getString(scale).substring(cursor.getString(scale).indexOf("/"))));
                    index++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }



    }

    public void buttonAddOnClick(View view){
        Intent intent = new Intent(MainActivity.this, CreateRegistry.class);
        startActivity(intent);
    }
}