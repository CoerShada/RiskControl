package ru.com.riskcontrol;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Registry {


    public final int id;
    private String dateOfCreation;
    private int factoryId;
    private ArrayList<Integer> risksIds;
    private float from;
    private float to;
    private short model;

    public Registry(int id, Context context){
        DBHelper dpHelper = new DBHelper(context);
        this.id = id;
        SQLiteDatabase db = dpHelper.getReadableDatabase();

        Cursor cursor = db.query("registries", null, "_id=?",new String[]{String.valueOf(id)},null,null,null);
        {

            int cursorDateOfCreation = cursor.getColumnIndex("date_of_creation");
            int cursorRisksIds = cursor.getColumnIndex("risks_ids");
            int cursorModels = cursor.getColumnIndex("model");
            int cursorScale = cursor.getColumnIndex("scale");
            int cursorFactory = cursor.getColumnIndex("factory_id");
            cursor.moveToFirst();

            this.dateOfCreation = cursor.getString(cursorDateOfCreation);
            this.factoryId = cursor.getInt(cursorFactory);
            this.model = cursor.getShort(cursorModels);
            risksIds = new ArrayList<>();
            /*if (!cursor.getString(cursorRisksIds).equals("")) {
                String[] risksIdsBuf = cursor.getString(cursorRisksIds).split(",");

                for (String s : risksIdsBuf) {
                    this.risksIds.add(Integer.parseInt(s));
                }
            }*/


            this.from = Float.parseFloat(cursor.getString(cursorScale).substring(0, cursor.getString(cursorScale).indexOf("/")));
            this.to = Float.parseFloat(cursor.getString(cursorScale).substring(cursor.getString(cursorScale).indexOf("/") + 1));


            cursor.close();
        }
    }

    public Registry(){
        this.id = -1;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        Date date = cal.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
        this.dateOfCreation = format1.format(date);

    }


    public String getDateOfCreation(){
        return dateOfCreation;
    }


    public int getFactoryId(){
        return factoryId;
    }

    public void setFactoryId(int factoryId){

        if (canUpdate()){
            this.factoryId = factoryId;
        }
    }

    public float getFrom(){
        return from;
    }

    public void setFrom(float from){
        if (canUpdate()){
            this.from = from;
        }
    }

    public float getTo(){
        return to;
    }

    public void setTo(float to){
        if (canUpdate()){
            this.to = to;
        }
    }

    public short getModel(){
        return model;
    }

    public void setModel(short model){
        if (canUpdate()){
            this.model = model;
        }
    }

    public List<Integer> getRisksIds(){
        return risksIds;
    }

    public void addRisk(int riskId){
        if (canUpdate()){

                //risksIds = new ArrayList<>(0);
            this.risksIds.add(riskId);
        }
    }

    public boolean canUpdate(){
        return true;
        /*if (dateOfCreation.equals("")) return true;
        Calendar cal= Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = cal.getTime();
        Date dateOfCreation;
        try {
            dateOfCreation = sdf.parse(this.dateOfCreation);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        int interval = (int) ((currentDate.getTime() - dateOfCreation.getTime())/1000*60*60*24);
        return interval <= 31;*/
    }

    public boolean save(Context context) {
        if (!canUpdate()) return false;

        DBHelper dpHelper = new DBHelper(context);
        SQLiteDatabase db = dpHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();


        cv.put("factory_id", this.factoryId);
        cv.put("model", this.getModel());
        cv.put("date_of_creation", this.dateOfCreation);

        StringBuilder risksIdsString = new StringBuilder();
        if (risksIds!=null && risksIds.size() > 0) {
            for (int item : risksIds) {
                risksIdsString.append(item).append(",");
            }
            risksIdsString = new StringBuilder(risksIdsString.substring(0, risksIdsString.length() - 1));
        }


        cv.put("risks_ids", String.valueOf(risksIdsString));
        cv.put("scale", this.from + "/" + this.to);

        if (this.id == -1){
            System.out.println(db.insert("registries", null, cv));
        }
        else
            db.update("registries", cv, "_id = ?", new String[]{String.valueOf(id)});

        return true;
    }
}
