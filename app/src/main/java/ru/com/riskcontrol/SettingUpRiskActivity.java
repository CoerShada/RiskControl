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
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingUpRiskActivity extends AppCompatActivity {

    RiskType[] riskTypes;
    Registry currentRegistry;
    Risk currentRisk;
    DBHelper dpHelper = new DBHelper(this);


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_up_risk);
        SQLiteDatabase db = dpHelper.getReadableDatabase();
        int registry_id = getIntent().getIntExtra("registry_id", -1);
        int risk_id = getIntent().getIntExtra("risk_id", -1);
        currentRegistry = new Registry(registry_id, this);
        loadRisk(risk_id);

        Cursor cursor = db.query("risk_types", null, null,null,null,null,null);
        {
            this.riskTypes = new RiskType[cursor.getCount()];
            cursor.moveToFirst();
            int index = 0;
            int cursorId = cursor.getColumnIndex("_id");
            if (cursor.getCount()>0) {
                do {
                    riskTypes[index++] = new RiskType(cursor.getInt(cursorId), this);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        String[] riskTypesString = new String[riskTypes.length+1];
        for (int i = 0; i<riskTypes.length; i++)
            riskTypesString[i] = riskTypes[i].name;
        riskTypesString[riskTypesString.length-1] = "Добавить";

        Spinner spinnerRiskTypes = (Spinner) findViewById(R.id.spinner_risk_type);
        ArrayAdapter<String> adapterRiskTypes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, riskTypesString);
        adapterRiskTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRiskTypes.setAdapter(adapterRiskTypes);


        spinnerRiskTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (l==riskTypes.length){
                    Intent intent = new Intent(SettingUpRiskActivity.this, SettingUpRiskTypeActivity.class);
                    intent.putExtra("registry_id", currentRegistry.id);
                    startActivity(intent);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        SeekBar seekBarProbabilityOfOccurrence= findViewById(R.id.seekbar_probability_of_occurrence);
        SeekBar seekBarDetectionProbabilityEstimate= findViewById(R.id.seekbar_detection_probability_estimate);
        SeekBar seekBarSeverityAssessment= findViewById(R.id.seekbar_severity_assessment);

        seekBarProbabilityOfOccurrence.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentRisk.setProbabilityOfOccurrence(seekBarProbabilityOfOccurrence.getProgress());
                TextView textViewProbabilityOfOccurrenceValue = findViewById(R.id.textview_probability_of_occurrence_value);
                textViewProbabilityOfOccurrenceValue.setText(String.valueOf((currentRegistry.getTo() - currentRegistry.getFrom()) / 100 * progress + currentRegistry.getFrom()));
                CalculateResults();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarDetectionProbabilityEstimate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentRisk.setDetectionProbabilityEstimate(seekBarDetectionProbabilityEstimate.getProgress());
                TextView textViewDetectionProbabilityEstimateValue = findViewById(R.id.textview_detection_probability_estimate_value);
                textViewDetectionProbabilityEstimateValue.setText(String.valueOf((currentRegistry.getTo() - currentRegistry.getFrom()) / 100 * progress + currentRegistry.getFrom()));
                CalculateResults();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarSeverityAssessment.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentRisk.setSeverityAssessment(seekBarSeverityAssessment.getProgress());
                TextView textViewSeverityAssessmentValue = findViewById(R.id.textview_severity_assessment_value);
                textViewSeverityAssessmentValue.setText(String.valueOf((currentRegistry.getTo() - currentRegistry.getFrom()) / 100 * progress + currentRegistry.getFrom()));
                CalculateResults();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

    private void loadRisk(int id){
        this.currentRisk = new Risk(id, this);
        if (this.currentRisk.id==-1) return;
        System.out.println("риск найден");
        TextView name = findViewById(R.id.textinput_risk_name);
        name.setText(this.currentRisk.getName());

        TextView magnitudeOfRisk = findViewById(R.id.magnitude_of_risk);
        magnitudeOfRisk.setText((int) this.currentRisk.getMagnitudeOfRisk());

        SeekBar seekBarProbabilityOfOccurrence= findViewById(R.id.seekbar_probability_of_occurrence);
        SeekBar seekBarDetectionProbabilityEstimate= findViewById(R.id.seekbar_detection_probability_estimate);
        SeekBar seekBarSeverityAssessment= findViewById(R.id.seekbar_severity_assessment);

        seekBarProbabilityOfOccurrence.setProgress((int) this.currentRisk.getProbabilityOfOccurrence());
        seekBarDetectionProbabilityEstimate.setProgress((int) this.currentRisk.getDetectionProbabilityEstimate());
        seekBarSeverityAssessment.setProgress((int) this.currentRisk.getSeverityAssessment());

    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void CalculateResults(){
        TextView result = findViewById(R.id.magnitude_of_risk);


        //calculate magnitude of risk in scale from 0 to 100
        currentRisk.calculateMagnitudeOfRisk(currentRegistry.getModel()==0);

        //output converted results for users

        int red;
        int green;

        if (currentRisk.getMagnitudeOfRisk() > 50) {
            red = 255;
            green = (int) (255 - (currentRisk.getMagnitudeOfRisk() * 5.1));
        } else {
            red = (int) (currentRisk.getMagnitudeOfRisk() * 5.1);
            green = 255;
        }

        String resultString = getApplicationContext().getString(R.string.magnitude_of_risk) + ": ";
        resultString +=(currentRegistry.getTo() - currentRegistry.getFrom()) / 100 * currentRisk.getMagnitudeOfRisk() + currentRegistry.getFrom();
        resultString+="\n";
        resultString += getApplicationContext().getString(R.string.prioritizing_risk)+": " ;

        if (currentRisk.getMagnitudeOfRisk()<30)
            resultString+=getApplicationContext().getString(R.string.low);
        else if (currentRisk.getMagnitudeOfRisk()<70)
            resultString+=getApplicationContext().getString(R.string.normal);
        else
            resultString+=getApplicationContext().getString(R.string.high);

        result.setTextColor(Color.rgb(red, green, 0));

        result.setText(resultString);


    }

    public void onClickSave(View view){

        TextView name = findViewById(R.id.textinput_risk_name);
        Spinner riskTypes = findViewById(R.id.spinner_risk_type);
        if (name.getText().toString().trim().length()>0){
            Intent intent = new Intent(SettingUpRiskActivity.this, CurrentRegistryActivity.class);
            this.currentRisk.setName(name.getText().toString().trim());
            this.currentRisk.setRiskTypeId(this.riskTypes[riskTypes.getSelectedItemPosition()].id);
            this.currentRisk.save(this);
            this.currentRegistry.addRisk(this);
            this.currentRegistry.save(this);

            startActivity(intent);
        }
    }
}