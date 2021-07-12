package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CreateRisk extends AppCompatActivity {

    RiskType[] riskTypes;
    Registry thisRegistry;
    DBHelper dpHelper = new DBHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteDatabase db = dpHelper.getReadableDatabase();
        int id = getIntent().getIntExtra("id", 0);
        Cursor cursor = db.query("risk_types", null, null,null,null,null,null);
        {
            this.riskTypes = new RiskType[cursor.getCount()];
            cursor.moveToFirst();
            int index = 0;
            int cursorId = cursor.getColumnIndex("_id");
            int cursorName = cursor.getColumnIndex("name");
            int cursorValue = cursor.getColumnIndex("value");
            if (cursor.getCount()>0) {
                do {
                    riskTypes[index] = new RiskType(cursor.getInt(cursorId), cursor.getString(cursorName), cursor.getInt(cursorValue));
                    index++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        String[] riskTypesString = new String[riskTypes.length+1];
        for (int i = 0; i<riskTypes.length; i++)
            riskTypesString[i] = riskTypes[i].name;
        riskTypesString[riskTypesString.length-1] = "Добавить";


        cursor = db.query("registries", null, "_id=?",new String[]{String.valueOf(id)},null,null,null);
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


        setContentView(R.layout.activity_create_risk);
        Spinner spinnerRiskTypes = (Spinner) findViewById(R.id.spinnerRiskType);
        ArrayAdapter<String> adapterRiskTypes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, riskTypesString);
        adapterRiskTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRiskTypes.setAdapter(adapterRiskTypes);





    }
}