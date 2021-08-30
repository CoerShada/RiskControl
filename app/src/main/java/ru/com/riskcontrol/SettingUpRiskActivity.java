package ru.com.riskcontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SettingUpRiskActivity extends AppCompatActivity {

    RiskType[] riskTypes;
    MinimizationMeasure[] MinimizationMeasures;
    Registry currentRegistry;
    Risk currentRisk;
    DBHelper dpHelper = new DBHelper(this);

    Spinner spinnerRiskTypes;
    TextView name;
    SeekBar seekBarDetectionProbabilityEstimate;
    TextView textViewDetectionProbabilityEstimateValue;
    SeekBar seekBarProbabilityOfOccurrence;
    TextView textViewProbabilityOfOccurrenceValue;
    SeekBar seekBarSeverityAssessment;
    TextView textViewSeverityAssessmentValue;
    TextView result;
    TableLayout table_minimization_measure;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_up_risk);
        SQLiteDatabase db = dpHelper.getReadableDatabase();
        int registry_id = getIntent().getIntExtra("registryId", -1);
        int risk_id = getIntent().getIntExtra("riskId", -1);
        boolean isLast = getIntent().getBooleanExtra("isLast", false);
        currentRegistry = new Registry(registry_id, this);
        loadViews(isLast);
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
        riskTypesString[riskTypesString.length-1] = getApplicationContext().getString(R.string.add);

        Spinner spinnerRiskTypes = (Spinner) findViewById(R.id.spinner_risk_type);
        ArrayAdapter<String> adapterRiskTypes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, riskTypesString);
        adapterRiskTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRiskTypes.setAdapter(adapterRiskTypes);


        spinnerRiskTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (l==riskTypes.length){
                    Intent intent = new Intent(SettingUpRiskActivity.this, SettingUpRiskTypeActivity.class);
                    intent.putExtra("registryId", currentRegistry.id);
                    startActivity(intent);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        calculateResults();
    }

    @SuppressLint("WrongViewCast")
    private void loadViews(boolean isLast){

        this.spinnerRiskTypes = findViewById(R.id.spinner_risk_type);
        this.name = findViewById(R.id.textinput_risk_name);

        this.seekBarDetectionProbabilityEstimate= findViewById(R.id.seekbar_detection_probability_estimate);
        this.textViewDetectionProbabilityEstimateValue = findViewById(R.id.textview_detection_probability_estimate_value);
        seekBarDetectionProbabilityEstimate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentRisk.setDetectionProbabilityEstimate(seekBarDetectionProbabilityEstimate.getProgress());
                textViewDetectionProbabilityEstimateValue.setText(String.valueOf(currentRegistry.getTransformedResults(progress)));
                calculateResults();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });



        this.seekBarProbabilityOfOccurrence = findViewById(R.id.seekbar_probability_of_occurrence);
        this.textViewProbabilityOfOccurrenceValue = findViewById(R.id.textview_probability_of_occurrence_value);

        seekBarProbabilityOfOccurrence.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentRisk.setProbabilityOfOccurrence(seekBarProbabilityOfOccurrence.getProgress());
                textViewProbabilityOfOccurrenceValue.setText(String.valueOf(currentRegistry.getTransformedResults(progress)));
                calculateResults();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        this.seekBarSeverityAssessment= findViewById(R.id.seekbar_severity_assessment);
        this.textViewSeverityAssessmentValue = findViewById(R.id.textview_severity_assessment_value);

        seekBarSeverityAssessment.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentRisk.setSeverityAssessment(seekBarSeverityAssessment.getProgress());
                textViewSeverityAssessmentValue.setText(String.valueOf(currentRegistry.getTransformedResults(progress)));
                calculateResults();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        if (currentRegistry.getModel()==0) {
            findViewById(R.id.textview_detection_probability_estimate).setVisibility(View.GONE);
            seekBarDetectionProbabilityEstimate.setVisibility(View.GONE);
            textViewDetectionProbabilityEstimateValue.setVisibility(View.GONE);
        }
        this.result = findViewById(R.id.magnitude_of_risk);

        if (isLast) return;
        //Дописать!


        table_minimization_measure = findViewById(R.id.table_minimization_measure);
        table_minimization_measure.setColumnShrinkable(0, true);


    }

    private void loadRisk(int id){
        this.currentRisk = new Risk(id, currentRegistry.id,this);
        if (this.currentRisk.id==-1) return;
        TextView name = findViewById(R.id.textinput_risk_name);
        name.setText(this.currentRisk.getName());


        float probabilityOfOccurrence = currentRegistry.getTransformedResults(currentRisk.getProbabilityOfOccurrence());
        float detectionProbabilityEstimate = currentRegistry.getTransformedResults(currentRisk.getDetectionProbabilityEstimate());
        float severityAssessment = currentRegistry.getTransformedResults(currentRisk.getSeverityAssessment());

        textViewProbabilityOfOccurrenceValue.setText(String.valueOf(probabilityOfOccurrence));
        seekBarProbabilityOfOccurrence.setProgress((int) this.currentRisk.getProbabilityOfOccurrence());


        textViewDetectionProbabilityEstimateValue.setText(String.valueOf(detectionProbabilityEstimate));
        seekBarDetectionProbabilityEstimate.setProgress((int) this.currentRisk.getDetectionProbabilityEstimate());

        textViewSeverityAssessmentValue.setText(String.valueOf(severityAssessment));
        seekBarSeverityAssessment.setProgress((int) this.currentRisk.getSeverityAssessment());

        calculateResults();

        SQLiteDatabase db = dpHelper.getReadableDatabase();

        Cursor cursor = db.query("minimization_measure", null, "risk_id=?", new String[]{String.valueOf(currentRisk.id)},null,null,null);
        {
            System.out.println("[MY1]" + cursor.getCount());
            if (cursor.getCount()==0) return;

            this.MinimizationMeasures = new MinimizationMeasure[cursor.getCount()];

            cursor.moveToFirst();
            int index = 0;
            int cursorId= cursor.getColumnIndex("_id");
            int cursorRiskId= cursor.getColumnIndex("_id");
            if (cursor.getCount()>0) {
                do {
                    TableRow tableRow = new TableRow(this);
                    MinimizationMeasures[index] = new MinimizationMeasure(cursor.getInt(cursorId), cursor.getInt(cursorRiskId), this);
                    tableRow.setId(MinimizationMeasures[index].id);

                    TextView nameTable = new TextView(this);
                    nameTable.setText(MinimizationMeasures[index].name);
                    tableRow.addView(nameTable, 1);

                    TextView dateTable = new TextView(this);
                    dateTable.setText(MinimizationMeasures[index].date);
                    tableRow.addView(dateTable, 2);

                    tableRow.setOnClickListener(v -> {
                        int idRow = v.getId();
                        Intent intent = new Intent(SettingUpRiskActivity.this, SettingUpMinimizationMeasure.class);
                        intent.putExtra("risk_id", currentRisk.id);
                        intent.putExtra("minimization_measure_id", idRow);
                        startActivity(intent);
                    });

                    table_minimization_measure.addView(tableRow, index);
                    index++;

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void calculateResults(){

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
        resultString +=(Math.round(currentRisk.getMagnitudeOfRisk()));
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


        if (name.getText().toString().trim().length()>0){
            Intent intent = new Intent(SettingUpRiskActivity.this, CurrentRegistryActivity.class);
            this.currentRisk.setName(name.getText().toString().trim());
            this.currentRisk.setRiskTypeId(this.riskTypes[spinnerRiskTypes.getSelectedItemPosition()].id);
            this.currentRisk.save(this);
            this.currentRegistry.save(this);

            startActivity(intent);
        }
    }

    public void onClickAddMinimizationMeasure(View view){
        Intent intent = new Intent(SettingUpRiskActivity.this, SettingUpMinimizationMeasure.class);
        intent.putExtra("riskId", true);
        startActivity(intent);
    }
}