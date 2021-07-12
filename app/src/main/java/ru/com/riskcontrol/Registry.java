package ru.com.riskcontrol;

public class Registry {
    public final int id;
    public final String dateOfCreation;
    public final int factory_id;
    private int[] risksIds;
    public final float from;
    public final float to;

    public Registry(int id, String dateOfCreation, int factory_id, String risksIds, float from, float to){
        this.id = id;
        this.dateOfCreation = dateOfCreation;

        this.factory_id = factory_id;
        if (risksIds!=null) {
            String[] risksIdsBuf = risksIds.split(",");
            this.risksIds = new int[risksIdsBuf.length];
            for (int i = 0; i < risksIdsBuf.length; i++) {
                this.risksIds[i] = Integer.parseInt(risksIdsBuf[i]);
            }
        }
        this.from = from;
        this.to = to;
    }



}
