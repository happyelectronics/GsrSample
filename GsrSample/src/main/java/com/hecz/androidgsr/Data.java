package com.hecz.androidgsr;

/**
 * Created by kolmanp on 2.6.15. hhhhhhhhh88888sssss
 */
public class Data {
    private int nAverage;
    private String nName;
    private int nSamples;
    //huu

    private Data() {

    }

    static private Data instance = null;

    public static Data getInstance() {
        if(instance == null) {
            instance = new Data();
        }
        return instance;
    }

    public void setNSamples(int nSamples) {
        this.nSamples = nSamples;
    }

    public void setNAverage(int nAverage)  {
        this.nAverage = nAverage;
    }

    public void setnName(String nName) {
        this.nName = nName;
    }

    public int getnSamples() {
        return nSamples;
    }

    public int getnAverage() {
        return nAverage;
    }

    public String getnName() {
        return nName;
    }}





