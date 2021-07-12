package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    DBHelper dpHelper = new DBHelper(this);
    Registry[] registries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TableLayout main_table = null;
        main_table = findViewById(R.id.main_table);
        main_table.setColumnShrinkable(0, true);
        int rows;

        SQLiteDatabase db = dpHelper.getReadableDatabase();


        Cursor cursor = db.query("registries", null, null,null,null,null,null);
        {
            if (cursor.getCount()==0) return;
            this.registries = new Registry[cursor.getCount()];

            cursor.moveToFirst();
            int index = 0;
            int cursorId= cursor.getColumnIndex("_id");
            int cursorDateOfCreation = cursor.getColumnIndex("date_of_creation");
            int cursorRisksIds = cursor.getColumnIndex("risks_ids");
            int cursorScale = cursor.getColumnIndex("scale");
            int cursorFactory = cursor.getColumnIndex("factory_id");
            if (cursor.getCount()>0) {
                do {

                    registries[index] = new Registry(cursor.getInt(cursorId), cursor.getString(cursorDateOfCreation), cursor.getInt(cursorFactory) ,cursor.getString(cursorRisksIds), Float.parseFloat(cursor.getString(cursorScale).substring(0, cursor.getString(cursorScale).indexOf("/"))), Float.parseFloat(cursor.getString(cursorScale).substring(cursor.getString(cursorScale).indexOf("/")+1)));
                    index++;
                } while (cursor.moveToNext());
            }
            rows = index;
            cursor.close();
        }


        for (int i = 0; i<rows; i++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                    ));
            tableRow.setId(registries[i].id);

            TextView date = new TextView(this);
            date.setText(registries[i].dateOfCreation);
            tableRow.addView(date, 0);

            tableRow.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    int id = v.getId();
                    Intent intent = new Intent(MainActivity.this, CurrentRegistry.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                    return true;
                }
            });


            cursor = db.query("factories", null, "_id=?",new String[]{String.valueOf(registries[i].factory_id)},null,null,null);
            cursor.moveToFirst();
            int cursorName = cursor.getColumnIndex("name");

            TextView factory = new TextView(this);
            factory.setText(cursor.getString(cursorName));
            cursor.close();
            tableRow.addView(factory, 1);

            main_table.addView(tableRow, i);
        }



    }

    public void buttonAddOnClick(View view){
        Intent intent = new Intent(MainActivity.this, CreateRegistry.class);
        startActivity(intent);
    }
}