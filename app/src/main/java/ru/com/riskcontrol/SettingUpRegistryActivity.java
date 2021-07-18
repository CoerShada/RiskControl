package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.DecimalFormat;


public class SettingUpRegistryActivity extends AppCompatActivity {

    private Registry currentRegistry;
    private Factory[] factories;
    private final String[] modelsRatings = {"Двухфакторная", "Трехфакторная"};
    DBHelper dpHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        loadFactories();
        int id = getIntent().getIntExtra("id", -1);
        super.onCreate(savedInstanceState);

        String[] factoriesStrings = new String[factories.length+1];
        for (int i = 0; i<factories.length; i++)
            factoriesStrings[i] = factories[i].name;
        factoriesStrings[factoriesStrings.length-1] = "Добавить";

        setContentView(R.layout.activity_setting_up_registry);

        Spinner spinnerFactories = (Spinner) findViewById(R.id.spinner_factories);
        ArrayAdapter<String> adapterFactories = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, factoriesStrings);
        adapterFactories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFactories.setAdapter(adapterFactories);


        Spinner spinnerModelsRatings = (Spinner) findViewById(R.id.spinner_rating_model);
        ArrayAdapter<String> adapterModelsRatings = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modelsRatings);
        adapterModelsRatings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelsRatings.setAdapter(adapterModelsRatings);



        spinnerModelsRatings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i==factories.length){
                    Intent intent = new Intent(SettingUpRegistryActivity.this, SettingUpFactoryActivity.class);
                    startActivity(intent);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        spinnerFactories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i==factories.length){
                    Intent intent = new Intent(SettingUpRegistryActivity.this, SettingUpFactoryActivity.class);
                    startActivity(intent);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        if (factories.length<1){
            Intent intent = new Intent(SettingUpRegistryActivity.this, SettingUpFactoryActivity.class);
            startActivity(intent);
        }

        if(id>-1)
            loadExtendsRegistry(id);
        else
            this.currentRegistry = new Registry();

    }

    private void loadFactories(){
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
    }

    private void loadExtendsRegistry(int id){

        this.currentRegistry = new Registry(id, this);

        Spinner spinnerFactoies = (Spinner) findViewById(R.id.spinner_factories);
        int index = 0;
        for (int i = 0; i<factories.length; i++){
            if(currentRegistry.getFactoryId()==factories[i].id) {
                index = i;
                break;
            }
        }
        spinnerFactoies.setSelection(index);

        Spinner spinnerModelsRatings = (Spinner) findViewById(R.id.spinner_rating_model);
        spinnerModelsRatings.setSelection(currentRegistry.getModel());

        DecimalFormat format = new DecimalFormat("0.#");

        EditText from = findViewById(R.id.editTextFrom);
        from.setText(format.format(currentRegistry.getFrom()));

        EditText to = findViewById(R.id.editTextTo);
        to.setText(format.format(currentRegistry.getTo()));

    }

    public void buttonBackOnClick(View view){
        Intent intent = new Intent(SettingUpRegistryActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void buttonSaveOnClick(View view){
        Spinner spinnerFactoies = (Spinner) findViewById(R.id.spinner_factories);
        Spinner spinnerModels = (Spinner) findViewById(R.id.spinner_rating_model);
        EditText editFrom = findViewById(R.id.editTextFrom);
        EditText editTo = findViewById(R.id.editTextTo);

        if (spinnerFactoies.getSelectedItemPosition()==factories.length) return;

        Intent intent = new Intent(SettingUpRegistryActivity.this, CurrentRegistryActivity.class);



        if (editFrom.getText().toString().trim().length()!=0 && editTo.getText().toString().trim().length()!=0){

            this.currentRegistry.setFactoryId(factories[spinnerFactoies.getSelectedItemPosition()].id);
            this.currentRegistry.setModel((short) spinnerModels.getSelectedItemPosition());
            this.currentRegistry.setFrom(Float.parseFloat(editFrom.getText().toString().trim()));
            this.currentRegistry.setTo(Float.parseFloat(editTo.getText().toString().trim()));
            this.currentRegistry.save(this);
        }

        startActivity(intent);
    }

}