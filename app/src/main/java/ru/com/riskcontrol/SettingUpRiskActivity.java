package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingUpRiskActivity extends AppCompatActivity {

    RiskType[] riskTypes;
    Registry thisRegistry;
    DBHelper dpHelper = new DBHelper(this);
    double[] resultsValues = new double[]{0, 0, 0};


    @SuppressLint("SetTextI18n")
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

        thisRegistry = new Registry(id, this);

        setContentView(R.layout.activity_setting_up_risk);
        Spinner spinnerRiskTypes = (Spinner) findViewById(R.id.spinnerRiskType);
        ArrayAdapter<String> adapterRiskTypes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, riskTypesString);
        adapterRiskTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRiskTypes.setAdapter(adapterRiskTypes);

        TextView[] results = new TextView[]{findViewById(R.id.magnitude_of_risk),
                                            findViewById(R.id.significance_of_risk),
                                            findViewById(R.id.prioritizing_risk)};

        String[] resultsDescription = new String[]{ getApplicationContext().getString(R.string.magnitude_of_risk),
                                                    getApplicationContext().getString(R.string.significance_of_risk),
                                                    getApplicationContext().getString(R.string.prioritizing_risk)};

        SeekBar[] seekBars = new SeekBar[] {findViewById(R.id.seekBar_assessment_of_the_likelihood_of_occurrence),
                                            findViewById(R.id.seekBar_detection_probability_estimate),
                                            findViewById(R.id.seekBar_severity_assessment)};

        TextView[] values = new TextView[]{ findViewById(R.id.assessment_of_the_likelihood_of_occurrence_value),
                                            findViewById(R.id.detection_probability_estimate_value),
                                            findViewById(R.id.severity_assessment_value)};

        spinnerRiskTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (l==riskTypes.length){
                    Intent intent = new Intent(SettingUpRiskActivity.this, SettingUpRiskTypeActivity.class);
                    startActivity(intent);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        for (int i = 0; i<seekBars.length; i++) {
            values[i].setText(String.valueOf(thisRegistry.getFrom()));
            final int finalI = i;
            seekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @SuppressLint({"SetTextI18n", "DefaultLocale"})
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    values[finalI].setText(String.valueOf(thisRegistry.getFrom() + (thisRegistry.getTo() - thisRegistry.getFrom()) / 100 * progress));
                    SeekBar seekBar_assessment_of_the_likelihood_of_occurrence = findViewById(R.id.seekBar_assessment_of_the_likelihood_of_occurrence);

                    SeekBar seekBar_severity_assessment = findViewById(R.id.seekBar_severity_assessment);

                    //calculate magnitude of risk in scale from 0 to 100
                    if (thisRegistry.getModel()==0) {
                        resultsValues[0] = Math.pow(seekBar_assessment_of_the_likelihood_of_occurrence.getProgress() * seekBar_severity_assessment.getProgress(), 0.5);
                    }
                    else{
                        SeekBar seekBar_detection_probability_estimate = findViewById(R.id.seekBar_detection_probability_estimate);
                        resultsValues[0] = Math.pow(seekBar_assessment_of_the_likelihood_of_occurrence.getProgress() * seekBar_severity_assessment.getProgress() * seekBar_detection_probability_estimate.getProgress(), 0.33333);
                    }


                    //output converted results for users
                    for (int i = 0; i<results.length; i++) {
                        int red;
                        int green;


                        if (resultsValues[i] > 50) {
                            red = 255;
                            green = (int) (255 - (resultsValues[i] * 5.1));
                        } else {
                            red = (int) (resultsValues[i] * 5.1);
                            green = 255;
                        }
                        results[i].setTextColor(Color.rgb(red, green, 0));

                        results[i].setText(resultsDescription[i] + ": " + String.format("%.3f", (thisRegistry.getTo() - thisRegistry.getFrom()) / 100 * resultsValues[i] + thisRegistry.getFrom()));

                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

        }
    }
}