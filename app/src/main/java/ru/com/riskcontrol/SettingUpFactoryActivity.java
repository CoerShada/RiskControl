package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingUpFactoryActivity extends AppCompatActivity {


    Factory currentFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_up_factory);
        this.currentFactory = new Factory(getIntent().getIntExtra("id", -1), this);
        if (this.currentFactory.id>-1){
            EditText name = findViewById(R.id.textview_factory_name);
            EditText description = findViewById(R.id.textview_factory_description);

            name.setText(this.currentFactory.name);
            description.setText(this.currentFactory.description);
        }
    }

    public void buttonBackOnClick(View view){
        Intent intent = new Intent(SettingUpFactoryActivity.this, SettingUpRegistryActivity.class);

        startActivity(intent);
    }

    public void buttonCreateOnClick(View view){
        Intent intent = new Intent(SettingUpFactoryActivity.this, SettingUpRegistryActivity.class);

        EditText name = findViewById(R.id.textview_factory_name);
        EditText desc = findViewById(R.id.textview_factory_description);
        if (name.getText().toString().trim().length()!=0){
            this.currentFactory.name = name.getText().toString().trim();
            this.currentFactory.description=desc.getText().toString().trim();
            this.currentFactory.save(this);
            startActivity(intent);
        }


    }


}