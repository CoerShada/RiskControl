package ru.com.riskcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Risk {

    public final int id;
    private String name;
    private int riskTypeId;
    private float probabilityOfOccurrence = 0;
    private float detectionProbabilityEstimate = 0;
    private float severityAssessment = 0;
    private float magnitudeOfRisk = 0;

    public Risk(int id, Context context){
        this.id = id;
        if (id==-1) return;
        DBHelper dpHelper = new DBHelper(context);
        SQLiteDatabase db = dpHelper.getReadableDatabase();
        Cursor cursor = db.query("risks", null, "_id=?", new String[]{String.valueOf(id)},null,null,null);
        {
            int cursorName = cursor.getColumnIndex("name");
            int cursorRiskTypeIds = cursor.getColumnIndex("risk_type_id");
            int cursorProbabilityOfOccurrence = cursor.getColumnIndex("probability_of_occurrence");
            int cursorDetectionProbabilityEstimate = cursor.getColumnIndex("detection_probability_estimate");
            int cursorSeverityAssessment= cursor.getColumnIndex("severity_assessment");
            int cursorMagnitudeOfRisk= cursor.getColumnIndex("magnitude_of_risk");
            cursor.moveToFirst();

            this.name = cursor.getString(cursorName);
            this.riskTypeId = cursor.getInt(cursorRiskTypeIds);
            this.probabilityOfOccurrence = cursor.getFloat(cursorProbabilityOfOccurrence);
            this.detectionProbabilityEstimate = cursor.getFloat(cursorDetectionProbabilityEstimate);
            this.severityAssessment = cursor.getFloat(cursorSeverityAssessment);
            this.magnitudeOfRisk = cursor.getFloat(cursorMagnitudeOfRisk);

            cursor.close();
        }
    }

    public void setName(String name){
        this.name = name;
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
        cv.put("detection_probability_estimate", this.severityAssessment);
        cv.put("severity_assessment", this.detectionProbabilityEstimate);
        cv.put("magnitude_of_risk", this.magnitudeOfRisk);
        System.out.println(this.name);
        System.out.println(this.riskTypeId);

        if (this.id == -1)
            db.insert("risks", null, cv);
        else
            db.update("risks", cv, "_id = ?", new String[]{String.valueOf(this.id)});

    }
}
