package com.cabdab.wifi.ui;

import java.util.HashMap;

import static java.lang.Math.round;

public class InterData implements Comparable{
    private float x,y;
    private String keyXY;
    private HashMap<String,Float> wifiData = new HashMap<>();

    public InterData( String[] macAdr, float[] rssi) {
        if (macAdr.length!=rssi.length){
            System.out.println("InterData Warning: Misaligned inputs");
            //TODO: Sanitize InterData input.
        }
        for(int i=0;i<macAdr.length;i++){
            if (macAdr[i]==null){
                System.out.println("InterData Warning: Null MAC Address Detected. Entry will be ignored");
            }
            else{
                this.wifiData.put(macAdr[i],rssi[i]);
            }
        }
    }

    public void markInterval (float x, float y){
        //TODO: Change rounding to 1-2 decimal places.
        this.x=round(x);
        this.y=round(y);
        this.keyXY = this.x+","+this.y;
    }

    @Override
    public int compareTo(Object o) {
        return this.keyXY.compareTo(((InterData)o).getKeyXY());
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getKeyXY() {
        return keyXY;
    }

    public HashMap<String, Float> getWifiData() {
        return wifiData;
    }
}