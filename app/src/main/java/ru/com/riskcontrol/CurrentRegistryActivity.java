package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CurrentRegistryActivity extends AppCompatActivity {

    Registry thisRegistry;
    DBHelper dpHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra("id", -1);
        SQLiteDatabase db = dpHelper.getReadableDatabase();
        if (id!=-1){
            Cursor cursor = db.query("registries", null, "_id=?",new String[]{String.valueOf(id)},null,null,null);
            {
                cursor.moveToFirst();
                int cursorId = cursor.getColumnIndex("_id");


                thisRegistry = new Registry(cursor.getInt(cursorId), this);
                cursor.close();
            }
        }
        else{
            Cursor cursor = db.query("registries", null, null,null, null,null,null);
            {
                cursor.moveToLast();
                int cursorId = cursor.getColumnIndex("_id");


                thisRegistry = new Registry(cursor.getInt(cursorId), this);
                cursor.close();
            }
        }
        setContentView(R.layout.activity_current_registry);
        TextView date = findViewById(R.id.textViewDate);
        date.setText(thisRegistry.getDateOfCreation());
    }

    public void buttonAddOnClick(View view){
        Intent intent = new Intent(CurrentRegistryActivity.this, SettingUpRiskActivity.class);
        intent.putExtra("id", thisRegistry.id);
        startActivity(intent);
    }
}