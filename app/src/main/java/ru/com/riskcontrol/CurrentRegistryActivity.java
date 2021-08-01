package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CurrentRegistryActivity extends AppCompatActivity {

    Registry thisRegistry;
    Risk[] risks;
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

        TableLayout table_risks;
        table_risks = findViewById(R.id.table_risks);
        table_risks.setColumnShrinkable(0, true);
        int rows;

        Cursor cursor = db.query("risks", null, null,null,null,null,null);
        {
            if (cursor.getCount()==0) return;
            this.risks = new Risk[cursor.getCount()];

            cursor.moveToFirst();
            int index = 0;
            int cursorId= cursor.getColumnIndex("_id");
            if (cursor.getCount()>0) {
                do {

                    risks[index] = new Risk(cursor.getInt(cursorId), this);
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
            tableRow.setId(risks[i].id);

            TextView name = new TextView(this);
            name.setText(risks[i].getName());
            tableRow.addView(name, 0);


            tableRow.setOnClickListener(v -> {
                int idRisk = v.getId();
                Intent intent = new Intent(CurrentRegistryActivity.this, SettingUpRiskActivity.class);
                intent.putExtra("registry_id", thisRegistry.id);
                intent.putExtra("risk_id", idRisk);
                startActivity(intent);
            });

            TextView priority = new TextView(this);
            if(risks[i].getMagnitudeOfRisk()<30) {
                priority.setText("Незначительный");
                priority.setTextColor(Color.rgb(0, 200, 0));
            }
            else if(risks[i].getMagnitudeOfRisk()<70) {
                priority.setText("Умеренный");
                priority.setTextColor(Color.rgb(150, 150, 0));
            }
            else{
                priority.setText("Выокий");
                priority.setTextColor(Color.rgb(200, 0, 0));
            }
            tableRow.addView(priority, 1);


            table_risks.addView(tableRow, i);
        }
    }

    public void buttonAddOnClick(View view){
        Intent intent = new Intent(CurrentRegistryActivity.this, SettingUpRiskActivity.class);
        intent.putExtra("registry_id", thisRegistry.id);
        startActivity(intent);
    }
}