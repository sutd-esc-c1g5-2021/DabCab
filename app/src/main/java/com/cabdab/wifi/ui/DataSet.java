package com.cabdab.wifi.ui;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DataSet {
    private HashSet<String> allMacAdr = new HashSet<>();
    private ArrayList<RunData> testSet = new ArrayList<>();
    private HashMap<String,float[]> normalSet;
    private RunData activeRun;
    private float xStart, xEnd, yStart,yEnd;

    public void startRun(float x, float y){
        this.activeRun = new RunData();
        this.xStart=x;
        this.yStart=y;
    }

    public void insert(String[] macInput, float[] rssiInput){
        this.activeRun.intervalInsert(macInput,rssiInput);
    }

    public void endRun(float x, float y){
        this.xEnd = x;
        this.yEnd = y;
        this.activeRun.resolveRun(this.xStart,this.xEnd,this.yStart,this.yEnd);
        this.testSet.add(this.activeRun);
        activeRun = null;
    }

    public void normalizeData(){
        this.normalSet = new HashMap<>();
        for(RunData rd: this.testSet){
            this.allMacAdr.addAll(rd.getRegMacAdr());
        }
        String[] tempAdrList = Arrays.copyOf(this.allMacAdr.toArray(new String[0]),this.allMacAdr.size());
        Arrays.sort(tempAdrList);
        for(RunData rd2: this.testSet){
            for(InterData idN:rd2.getRunLog()){
                if(!normalSet.containsKey(idN.getKeyXY())) {
                    HashMap<String, Float> tempWIFI = idN.getWifiData();
                    float[] tempRSSI = new float[tempAdrList.length];
                    for (int i = 0; i < tempAdrList.length; i++) {
                        if (tempWIFI.containsKey(tempAdrList[i])) {
                            tempRSSI[i] = tempWIFI.get(tempAdrList[i]);
                        } else {
                            tempRSSI[i] = 0;
                        }
                    }
                    this.normalSet.put(idN.getKeyXY(),tempRSSI);
                }
                else{
                    //TODO: Implement comparison of same values at same coordinates before updating data.
                }
            }
        }
    }

    public void printNormalSet(){
        for(Map.Entry me : this.normalSet.entrySet()){
            System.out.println("\nCoord:"+me.getKey());
            float[] out = (float[]) me.getValue();
            for(float o: out){
                System.out.println(o);
            }
        }
    }

    public HashMap<String, float[]> getNormalSet() {
        return normalSet;
    }
    public String[] getAllMacAdr(){
        String[] out = this.allMacAdr.toArray(new String[0]);
        Arrays.sort(out);
        return out;
    }
}