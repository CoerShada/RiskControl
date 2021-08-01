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
    private final String[] modelsRatings = {getApplicationContext().getString(R.string.twoWay), getApplicationContext().getString(R.string.threeWay)};
    DBHelper dpHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        loadFactories();
        String[] factoriesStrings = new String[factories.length+1];
        for (int i = 0; i<factories.length; i++)
            factoriesStrings[i] = factories[i].name;
        factoriesStrings[factoriesStrings.length-1] = getApplicationContext().getString(R.string.add);

        setContentView(R.layout.activity_setting_up_registry);

        Spinner spinnerFactories = (Spinner) findViewById(R.id.spinner_factories);
        ArrayAdapter<String> adapterFactories = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, factoriesStrings);
        adapterFactories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFactories.setAdapter(adapterFactories);


        Spinner spinnerModelsRatings = (Spinner) findViewById(R.id.spinner_rating_model);
        ArrayAdapter<String> adapterModelsRatings = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modelsRatings);
        adapterModelsRatings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelsRatings.setAdapter(adapterModelsRatings);


        spinnerFactories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i==factories.length){
                    Intent intent = new Intent(SettingUpRegistryActivity.this, SettingUpFactoryActivity.class);
                    startActivity(intent);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        spinnerFactories.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(id);
                System.out.println(position);
                if (position!=factories.length){
                    Intent intent = new Intent(SettingUpRegistryActivity.this, SettingUpFactoryActivity.class);
                    intent.putExtra("id", factories[position].id);
                    startActivity(intent);
                }
                return true;
            }
        });

        if (factories.length<1){
            Intent intent = new Intent(SettingUpRegistryActivity.this, SettingUpFactoryActivity.class);
            startActivity(intent);
        }

        loadExtendsRegistry(getIntent().getIntExtra("id", -1));
    }

    private void loadFactories(){
        SQLiteDatabase db = dpHelper.getReadableDatabase();

        Cursor cursor = db.query("factories", null, null,null,null,null,null);
        {
            this.factories = new Factory[cursor.getCount()];
            cursor.moveToFirst();
            int index = 0;
            int cursorId = cursor.getColumnIndex("_id");
            if (cursor.getCount()>0) {
                do {
                    factories[index++] = new Factory(cursor.getInt(cursorId), this);
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
        if (id==-1) return;
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