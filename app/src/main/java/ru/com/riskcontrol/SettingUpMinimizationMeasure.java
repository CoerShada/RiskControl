package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;

public class SettingUpMinimizationMeasure extends AppCompatActivity {

    Risk currentRisk;
    MinimizationMeasure currentMinimizationMeasure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_up_minimization_measure);
        this.currentRisk = new Risk(getIntent().getIntExtra("risk_id", -1), -1 ,this);
        this.currentMinimizationMeasure = new MinimizationMeasure(getIntent().getIntExtra("minimization_measure_id", -1), currentRisk.id ,this);
        EditText date;
        date = (EditText)findViewById(R.id.editTextMinimizationMeasureDate);

        TextWatcher tw = new TextWatcher() {


            private String current = "";
            private String ddmmyyyy = "ДДММГГГГ";
            private Calendar cal = Calendar.getInstance();


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : Math.min(mon, 12);
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : Math.min(year, 2100);
                        cal.set(Calendar.YEAR, year);

                        day = Math.min(day, cal.getActualMaximum(Calendar.DATE));
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s.%s.%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = Math.max(sel, 0);
                    current = clean;
                    date.setText(current);
                    date.setSelection(Math.min(sel, current.length()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        };
        date.addTextChangedListener(tw);
    }

    public void buttonSaveOnClick(View view){
        Intent intent = new Intent(SettingUpMinimizationMeasure.this, SettingUpRiskActivity.class);

        EditText name = findViewById(R.id.editTextMinimizationMeasureName);
        EditText date = findViewById(R.id.editTextMinimizationMeasureDate);
        EditText responsible = findViewById(R.id.editTextMinimizationMeasureResponsible);
        if (name.getText().toString().trim().length()!=0){
            this.currentMinimizationMeasure.name = name.getText().toString().trim();
            this.currentMinimizationMeasure.date=date.getText().toString().trim();
            this.currentMinimizationMeasure.responsible = responsible.getText().toString().trim();
            this.currentMinimizationMeasure.save(this);
            intent.putExtra("risk_id", this.currentRisk.id);
            startActivity(intent);
        }


    }


}