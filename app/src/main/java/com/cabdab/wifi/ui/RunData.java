package com.cabdab.wifi.ui;

import java.util.ArrayList;
import java.util.Arrays;

public class RunData{
    private int xStart,xEnd,yStart,yEnd;
    private ArrayList<String[]> macAddr = new ArrayList<>();
    private ArrayList<double[]> rssiList = new ArrayList<>();
    private ArrayList<String> xyCoord = new ArrayList<>();

    //Empty constructor
    public RunData(){
        this.xStart = 0;
        this.xEnd = 0;
        this.yStart = 0;
        this.yEnd = 0;
    }

    //replaces start() method
    public RunData(int x, int y){
        System.out.println("\nStart Successful!\n");
        this.xStart = x;
        this.xEnd = 0;
        this.yStart = y;
        this.yEnd = 0;
    }

    public RunData(String csv){
        this.reverseCSV(csv);
    }

    //End data and distribute Start to End Coordinates
    public void end(int x, int y){
        this.xEnd = x;
        this.yEnd = y;
        int interval = this.macAddr.size()-1;
        for(int i = 0; i <= interval; i++){
            int midX = (int) Math.round(this.xStart+i*(this.xEnd-this.xStart)/interval);
            int midY = (int) Math.round(this.yStart+i*(this.yEnd-this.yStart)/interval);
            this.xyCoord.add(midX+","+midY);
        }
    }

    //store intermediate data
    public void insert(String[] mac, double[] rssi){
        this.macAddr.add(mac);
        this.rssiList.add(rssi);
    }

    public String toCSV(){
        StringBuilder sb = new StringBuilder();
        sb.append("xyValues,"+this.xStart+","+this.xEnd+","+this.yStart+","+this.yEnd+"\n");

        sb.append("xyCoord");
        for(String xyc : this.xyCoord){
            sb.append(",".concat(xyc));
        }
        sb.append("\n");

        for(String[] sa : this.macAddr){
            sb.append("macAddr");
            for(int i = 0;i<sa.length;i++){
                sb.append(",".concat(sa[i]));
            }
            sb.append("\n");
        }

        for(double[] da : this.rssiList){
            sb.append("rssiList");
            for(int i = 0; i<da.length;i++){
                sb.append(",");
                sb.append(da[i]);
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    //TODO: Check input
    public void reverseCSV(String input){
        String[] lines = input.split("\n");
        String[] macInput;
        for (String line : lines){
            if(line == null){
                continue;
            }
            String[] tempBreak = line.split(",");
            if (tempBreak[0].contains("xyValues")) {
                xStart = Integer.parseInt(tempBreak[1]);
                xEnd = Integer.parseInt(tempBreak[2]);
                yStart = Integer.parseInt(tempBreak[3]);
                yEnd = Integer.parseInt(tempBreak[4]);
            }

            else if ("xyCoord".equals(tempBreak[0])) {
                for(int i = 0;i<(tempBreak.length-1)/2;i++){
                    this.xyCoord.add(tempBreak[1+i*2]+","+tempBreak[2+i*2]);
                }
            }

            else if ("macAddr".equals(tempBreak[0])) {
                this.macAddr.add(Arrays.copyOfRange(tempBreak, 1, tempBreak.length));
            }

            else if ("rssiList".equals(tempBreak[0])) {
                double[] tempInput = new double[tempBreak.length - 1];
                for (int i = 1; i < tempBreak.length; i++) {
                    tempInput[i - 1] = Double.parseDouble(tempBreak[i]);
                }
                this.rssiList.add(tempInput);
            }
        }
    }

    //Getters
    public ArrayList<String[]> getMacAddr() {
        return macAddr;
    }

    public ArrayList<double[]> getRssiList() {
        return rssiList;
    }

    public ArrayList<String> getXyCoord() {
        return xyCoord;
    }

    public int getxStart() {
        return xStart;
    }

    public int getxEnd() {
        return xEnd;
    }

    public int getyStart() {
        return yStart;
    }

    public int getyEnd() {
        return yEnd;
    }
}
