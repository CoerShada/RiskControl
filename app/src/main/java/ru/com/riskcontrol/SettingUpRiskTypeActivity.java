package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingUpRiskTypeActivity extends AppCompatActivity {

    private int registryId;
    RiskType currentRiskType;
    DBHelper dpHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.registryId = getIntent().getIntExtra("registryId", -1);
        this.currentRiskType = new RiskType(getIntent().getIntExtra("id", -1), this);
        if (this.currentRiskType.id>-1){

            EditText name = findViewById(R.id.textview_risk_type_name);
            EditText value = findViewById(R.id.textview_risk_type_value);

            name.setText(this.currentRiskType.name);
            value.setText(this.currentRiskType.value);
        }

        setContentView(R.layout.activity_setting_up_risk_type);
    }

    public void buttonBackOnClick(View view){
        Intent intent = new Intent(SettingUpRiskTypeActivity.this, SettingUpRiskActivity.class);
        intent.putExtra("registryId", this.registryId);
        startActivity(intent);
    }

    public void buttonCreateOnClick(View view){
        Intent intent = new Intent(SettingUpRiskTypeActivity.this, SettingUpRiskActivity.class);
        intent.putExtra("registryId", this.registryId);
        EditText name = findViewById(R.id.textview_risk_type_name);
        EditText value = findViewById(R.id.textview_risk_type_value);

        if (name.getText().toString().trim().length()!=0 && value.getText().toString().trim().length()!=0){
            currentRiskType.name = name.getText().toString();
            currentRiskType.value = Integer.parseInt(value.getText().toString());
            currentRiskType.save(this);

        }
        startActivity(intent);
    }
}