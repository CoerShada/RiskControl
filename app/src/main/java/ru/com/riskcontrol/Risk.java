package ru.com.riskcontrol;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Risk {

    public final int id;
    private String name;
    private int riskTypeId;
    private float probabilityOfOccurrence = 0;
    private float detectionProbabilityEstimate = 0;
    private float severityAssessment = 0;
    private float magnitudeOfRisk = 0;
    private Registry parentRegistry;
    private String dateOfCreation;

    public Risk(int id, int parentRegistryId, Context context){

        DBHelper dpHelper = new DBHelper(context);
        SQLiteDatabase db = dpHelper.getReadableDatabase();
        if (id==-1){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            Date date = cal.getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
            this.dateOfCreation = format1.format(date);
            this.parentRegistry = new Registry(parentRegistryId, context);
            Cursor cursor = db.query("risks", null, null, null,null,null,null);
            {
                if (cursor.getCount()==0){
                    this.id = 0;
                    return;
                }
                cursor.moveToLast();
                int cursorId = cursor.getColumnIndex("_id");
                this.id = cursor.getInt(cursorId)+1;
                cursor.close();
            }
            return;
        }
        this.id = id;
        Cursor cursor = db.query("risks", null, "_id=?", new String[]{String.valueOf(id)},null,null,null);
        {
            int cursorName = cursor.getColumnIndex("name");
            int cursorRiskTypeIds = cursor.getColumnIndex("risk_type_id");
            int cursorProbabilityOfOccurrence = cursor.getColumnIndex("probability_of_occurrence");
            int cursorDetectionProbabilityEstimate = cursor.getColumnIndex("detection_probability_estimate");
            int cursorSeverityAssessment= cursor.getColumnIndex("severity_assessment");
            int cursorDate= cursor.getColumnIndex("date_of_creation");
            int registryIdDate= cursor.getColumnIndex("registry_id");

            cursor.moveToFirst();

            this.name = cursor.getString(cursorName);
            this.riskTypeId = cursor.getInt(cursorRiskTypeIds);
            this.probabilityOfOccurrence = cursor.getFloat(cursorProbabilityOfOccurrence);
            this.detectionProbabilityEstimate = cursor.getFloat(cursorDetectionProbabilityEstimate);
            this.severityAssessment = cursor.getFloat(cursorSeverityAssessment);
            this.parentRegistry = new Registry(parentRegistryId, context);
            this.calculateMagnitudeOfRisk(parentRegistry.getModel()==0);
            this.dateOfCreation = cursor.getString(cursorDate);
            this.parentRegistry = new Registry(cursor.getInt(registryIdDate), context);
            cursor.close();
        }

    }


    public void setName(String name){
        this.name = name;
    }

    public Registry getParentRegistry(){
        return parentRegistry;
    }

    public String getDateOfCreation(){
        return dateOfCreation;
    }

    public String getName(){
        return this.name;
    }

    public void setRiskTypeId(int riskTypeId){
        this.riskTypeId = riskTypeId;
    }

    public int getRiskTypeId(){
        return this.riskTypeId;
    }

    public void setProbabilityOfOccurrence(float probabilityOfOccurrence){
        this.probabilityOfOccurrence = probabilityOfOccurrence;
    }

    public float getProbabilityOfOccurrence(){

        return this.probabilityOfOccurrence;
    }

    public float getDetectionProbabilityEstimate(){

        return this.detectionProbabilityEstimate;
    }

    public void setDetectionProbabilityEstimate(float detectionProbabilityEstimate){
        this.detectionProbabilityEstimate = detectionProbabilityEstimate;
    }

    public float getSeverityAssessment(){
        return this.severityAssessment;
    }

    public void setSeverityAssessment(float severityAssessment){
        this.severityAssessment = severityAssessment;
    }

    public float getMagnitudeOfRisk(){
        return this.magnitudeOfRisk;
    }

    public void calculateMagnitudeOfRisk(boolean twoWay){
        if (twoWay)
            this.magnitudeOfRisk = (float) Math.pow(this.probabilityOfOccurrence * this.severityAssessment, 0.5);
        else
            this.magnitudeOfRisk = (float) Math.pow(this.probabilityOfOccurrence * this.severityAssessment * this.detectionProbabilityEstimate, 0.333333);
    }


    public void save(Context context){
        DBHelper dpHelper = new DBHelper(context);
        SQLiteDatabase db = dpHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", this.name);
        cv.put("risk_type_id", this.riskTypeId);
        cv.put("probability_of_occurrence", this.probabilityOfOccurrence);
        cv.put("detection_probability_estimate", this.detectionProbabilityEstimate);
        cv.put("severity_assessment", this.severityAssessment);
        cv.put("date_of_creation", this.dateOfCreation);
        cv.put("registry_id", this.parentRegistry.id);
        if (this.id == -1)
            db.insert("risks", null, cv);
        else
            db.update("risks", cv, "_id = ?", new String[]{String.valueOf(this.id)});

    }
}
