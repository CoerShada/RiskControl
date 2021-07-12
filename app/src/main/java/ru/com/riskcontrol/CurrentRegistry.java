package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CurrentRegistry extends AppCompatActivity {

    Registry thisRegistry;
    DBHelper dpHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra("id", 0);
        SQLiteDatabase db = dpHelper.getReadableDatabase();
        Cursor cursor = db.query("registries", null, "_id=?",new String[]{String.valueOf(id)},null,null,null);
        {
            cursor.moveToFirst();
            int cursorId = cursor.getColumnIndex("_id");
            int cursorDateOfCreation = cursor.getColumnIndex("date_of_creation");
            int cursorRisksIds = cursor.getColumnIndex("risks_ids");
            int cursorScale = cursor.getColumnIndex("scale");
            int cursorFactory = cursor.getColumnIndex("factory_id");

            thisRegistry = new Registry(cursor.getInt(cursorId), cursor.getString(cursorDateOfCreation), cursor.getInt(cursorFactory), cursor.getString(cursorRisksIds), Float.parseFloat(cursor.getString(cursorScale).substring(0, cursor.getString(cursorScale).indexOf("/"))), Float.parseFloat(cursor.getString(cursorScale).substring(cursor.getString(cursorScale).indexOf("/") + 1)));
            cursor.close();
        }
        setContentView(R.layout.activity_current_registry);
        TextView date = findViewById(R.id.textViewDate);
        date.setText(thisRegistry.dateOfCreation);
    }

    public void buttonAddOnClick(View view){
        Intent intent = new Intent(CurrentRegistry.this, CreateRisk.class);
        intent.putExtra("id", thisRegistry.id);
        startActivity(intent);
    }
}