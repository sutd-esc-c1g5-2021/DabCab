package com.cabdab.wifi.ui;

import java.util.ArrayList;
import java.util.HashSet;

public class RunData {
    private ArrayList<InterData> runLog;
    private HashSet<String> regMacAdr;
    private int intervalCount;

    public void intervalInsert(String[] macAdr,float[] rssi){
        this.runLog.add(new InterData(macAdr,rssi));
        this.intervalCount++;
    }

    public void resolveRun(float x1, float x2, float y1, float y2){
        int i = 1;
        for(InterData id : this.runLog){
            this.regMacAdr.addAll(id.getWifiData().keySet());
            id.markInterval(partLine(x1,x2,i,this.intervalCount), partLine(y1,y2,i,this.intervalCount));
            i++;
        }
    }

    private float partLine(float x1, float x2, int i, int c) {return (x1+((x2-x1)*i/c));}

    public RunData() {
        this.runLog = new ArrayList<>();
        this.regMacAdr = new HashSet<>();
        this.intervalCount=0;
    }

    public ArrayList<InterData> getRunLog() {
        return runLog;
    }

    public HashSet<String> getRegMacAdr() {
        return regMacAdr;
    }
}