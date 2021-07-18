package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingUpFactoryActivity extends AppCompatActivity {



    DBHelper dpHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_up_factory);

    }

    public void buttonBackOnClick(View view){
        Intent intent = new Intent(SettingUpFactoryActivity.this, SettingUpRegistryActivity.class);

        startActivity(intent);
    }

    public void buttonCreateOnClick(View view){
        Intent intent = new Intent(SettingUpFactoryActivity.this, SettingUpRegistryActivity.class);

        EditText name = findViewById(R.id.label_factory_name);
        if (name.getText().toString().trim().length()!=0){
            EditText desc = findViewById(R.id.description);
            SQLiteDatabase db = dpHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put("name", name.getText().toString());
            cv.put("description", desc.getText().toString());
            db.insert("factories", null , cv);

        }

        startActivity(intent);
    }
}