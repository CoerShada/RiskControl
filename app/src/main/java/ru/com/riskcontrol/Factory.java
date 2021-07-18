package ru.com.riskcontrol;

import android.content.Context;

public class Factory {
    public final int id;
    public String name;
    public String description;

    public Factory(int id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void Factory(int id, Context context){

    }



}
