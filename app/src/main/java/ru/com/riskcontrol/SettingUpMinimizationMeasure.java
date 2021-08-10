package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SettingUpMinimizationMeasure extends AppCompatActivity {

    Risk currentRisk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_up_minimization_measure);

        this.currentRisk = new Risk(getIntent().getIntExtra("risk_id", -1), this);
    }
}