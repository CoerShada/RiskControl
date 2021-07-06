package ru.com.riskcontrol;

public class Registry {
    public final int id;
    public final String dateOfCreation;
    private int[] risksIds;
    public final float from;
    public final float to;

    public Registry(int id, String dateOfCreation, String[] risksIds, float from, float to){
        this.id = id;
        this.dateOfCreation = dateOfCreation;
        this.risksIds = new int[risksIds.length];
        for (int i = 0; i<risksIds.length; i++){
            this.risksIds[i] = Integer.parseInt(risksIds[i]);
        }
        this.from = from;
        this.to = to;
    }



}
