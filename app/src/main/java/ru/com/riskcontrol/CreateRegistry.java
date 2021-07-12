package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CreateRegistry extends AppCompatActivity {

    private Date date;
    private Factory[] factories;
    private String[] modelsRatings = {"Двухфакторная", "Трехфакторная"};
    DBHelper dpHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int currentFactory = getIntent().getIntExtra("currentFactory", 0);
        int currentModelsRating = getIntent().getIntExtra("currentModelsRating", 0);
        this.date = new Date();

        SQLiteDatabase db = dpHelper.getReadableDatabase();

        Cursor cursor = db.query("factories", null, null,null,null,null,null);
        {
            this.factories = new Factory[cursor.getCount()];
            cursor.moveToFirst();
            int index = 0;
            int cursorId = cursor.getColumnIndex("_id");
            int cursorName = cursor.getColumnIndex("name");
            int cursorDesc = cursor.getColumnIndex("description");
            if (cursor.getCount()>0) {
                do {
                    factories[index] = new Factory(cursor.getInt(cursorId), cursor.getString(cursorName), cursor.getString(cursorDesc));
                    index++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        super.onCreate(savedInstanceState);

        String[] factoriesStrings = new String[factories.length+1];
        for (int i = 0; i<factories.length; i++)
            factoriesStrings[i] = factories[i].name;
        factoriesStrings[factoriesStrings.length-1] = "Добавить";

        setContentView(R.layout.activity_create_registry);

        Spinner spinnerFactoies = (Spinner) findViewById(R.id.spinner_factories);
        ArrayAdapter<String> adapterFactories = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, factoriesStrings);
        adapterFactories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFactoies.setAdapter(adapterFactories);
        spinnerFactoies.setSelection(currentFactory);

        Spinner spinnerModelsRatings = (Spinner) findViewById(R.id.spinner_rating_model);
        ArrayAdapter<String> adapterModelsRatings = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, modelsRatings);
        adapterModelsRatings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelsRatings.setAdapter(adapterModelsRatings);
        spinnerModelsRatings.setSelection(currentModelsRating);



        Spinner spinner_factories = findViewById(R.id.spinner_factories);
        spinner_factories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (l==factories.length){
                    Intent intent = new Intent(CreateRegistry.this, CreateFactory.class);

                    intent.putExtra("currentModelsRating", currentModelsRating);
                    EditText editFrom = findViewById(R.id.editTextFrom);
                    EditText editTo = findViewById(R.id.editTextTo);
                    intent.putExtra("editFrom", editFrom.getText());
                    intent.putExtra("editTo", editTo.getText());
                    intent.putExtra("lastIndexFactory", factories.length-1);
                    startActivity(intent);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

                return;
            }
        });
        if (factories.length<1){
            Intent intent = new Intent(CreateRegistry.this, CreateFactory.class);

            intent.putExtra("currentModelsRating", currentModelsRating);
            EditText editFrom = findViewById(R.id.editTextFrom);
            EditText editTo = findViewById(R.id.editTextTo);
            intent.putExtra("editFrom", editFrom.getText());
            intent.putExtra("editTo", editTo.getText());
            intent.putExtra("lastIndexFactory", factories.length-1);
            startActivity(intent);
        }


    }

    public void buttonBackOnClick(View view){
        Intent intent = new Intent(CreateRegistry.this, MainActivity.class);
        startActivity(intent);
    }

    public void buttonCreateOnClick(View view){
        Spinner spinnerFactoies = (Spinner) findViewById(R.id.spinner_factories);
        Spinner spinnerModels = (Spinner) findViewById(R.id.spinner_rating_model);
        EditText editFrom = findViewById(R.id.editTextFrom);
        EditText editTo = findViewById(R.id.editTextTo);
        if (spinnerFactoies.getSelectedItemPosition()==factories.length) return;

        Intent intent = new Intent(CreateRegistry.this, CurrentRegistry.class);

        int idFactory = factories[spinnerFactoies.getSelectedItemPosition()].id;
        int model = spinnerModels.getSelectedItemPosition();


        if (editFrom.getText().toString().trim().length()!=0 && editTo.getText().toString().trim().length()!=0){

            SQLiteDatabase db = dpHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put("factory_id", idFactory);
            cv.put("model", model);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            Date date = cal.getTime();
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
            String inActiveDate = format1.format(date);

            cv.put("date_of_creation", inActiveDate);
            cv.put("scale", editFrom.getText().toString().trim() + "/" + editTo.getText().toString().trim());
            db.insert("registries", null , cv);
            Cursor cursor = db.query("registries", null, null,null,null,null,null);
            {
                cursor.moveToLast();
                int cursorId= cursor.getColumnIndex("_id");
                intent.putExtra("id", cursor.getInt(cursorId));
                cursor.close();
            }
        }

        startActivity(intent);
    }



}