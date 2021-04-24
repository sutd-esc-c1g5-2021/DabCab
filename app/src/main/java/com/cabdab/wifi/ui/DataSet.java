package com.cabdab.wifi.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DataSet {
    private HashSet<String> allMACAddr = new HashSet<>();
    private ArrayList<RunData> mapData = new ArrayList<>();
    private HashMap<String,double[]> normalData = new HashMap<>();
    private List<String> normalMACAddr;
    private RunData activeRun;
    private String floorplan;   //To store floorplan filename for saving and loading later

    //Start a new run
    public void startRun(int x,int y){
        System.out.println("\nRun Start Request Received\n");
        if (this.activeRun!=null){
            this.activeRun = new RunData(x,y);
        }
        else{
            System.out.println("\nRun Already Started\n");
        }

    }

    //End current run
    public void endRun(int x,int y){
        System.out.println("\nRun End Request Received\n");
        if(this.activeRun==null){
            System.out.println("Error: Run has not started");
        }
        else{
            this.activeRun.end(x,y);
            mapData.add(activeRun);
            this.activeRun = null;
        }
    }

    //Insert new data while maintaining integrity
    public void insert(HashMap<String,Double> input){
        allMACAddr.addAll(input.keySet());
        int tempSize = input.size();
        String[] addr = new String[tempSize];
        double[] rssi = new double[tempSize];
        int i = 0;
        DecimalFormat df = new DecimalFormat("#.####");

        for(Map.Entry me : input.entrySet()){
            addr[i] = (String) me.getKey();
            rssi[i] = (Double) me.getValue();
            i++;
        }

        if(this.activeRun!=null){
            activeRun.insert(addr,rssi);
        }
        else{
            System.out.println("Error: No Mapping Runs are active");
        }
    }

    //Normalizes all data gathered
    public void normalizeData(){
        System.out.println("Normalizing...");
        this.normalData = new HashMap<>();
        int normalSize = this.allMACAddr.size();
        this.normalMACAddr = new ArrayList<String>(allMACAddr);
        Collections.sort(this.normalMACAddr);

        //per RunData
        for(RunData rd : this.mapData){
            //per Coordinate point
            for(int i = 0; i<rd.getMacAddr().size(); i++){
                List<String> scanAddr = Arrays.asList(rd.getMacAddr().get(i));
                double[] scanRssi = rd.getRssiList().get(i);
                String xyKey = rd.getXyCoord().get(i);
                double[] tempRssi = new double[normalSize];

                //per Wifi AP
                for(int j = 0; j<this.normalMACAddr.size(); j++){
                    if(scanAddr.contains(this.normalMACAddr.get(j))){
                        tempRssi[j]=scanRssi[scanAddr.indexOf(this.normalMACAddr.get(j))];
                    }
                    else{
                        tempRssi[j]=0;
                    }
                }
                this.normalData.put(xyKey,tempRssi);
            }
        }
    }

    public String toCSV() {
        StringBuilder output = new StringBuilder();

        output.append("floorplan,");
        output.append(this.floorplan.concat("\n"));

        output.append("allMACAddr");
        for(String allMAC : this.allMACAddr){
            output.append(",");
            output.append(allMAC);
        }
        output.append("\n");

        output.append("normalMACAddr");
        for(String mac : this.normalMACAddr){
            output.append(",");
            output.append(mac);
        }
        output.append("\n");

        for(Map.Entry me : this.normalData.entrySet()){
            output.append("normalData,");
            output.append(me.getKey());
            for(double rssiRead : (double[]) me.getValue()){
                output.append(",");
                output.append(rssiRead);
            }
            output.append("\n");
        }

        for(RunData rd : this.mapData){
            output.append("mapData\n");
            output.append(rd.toCSV());
        }

        return output.toString();
    }

    //TODO: Check input String
    public void reverseCSV(String input){
        List<String> lines = new ArrayList<String>(Arrays.asList(input.split("\n")));
        int readStatus = 0;
        StringBuilder sb = new StringBuilder();

        System.out.println("Final Test Count: "+lines.size());
        for(Iterator<String> i = lines.iterator(); i.hasNext();){
            String line = i.next();
            if(line == null){
                continue;
            }

            if(readStatus == 0){
                String[] tempBreak = line.split(",");

                switch(tempBreak[0]){
                    case "floorplan":
                        this.setFloorplan(tempBreak[1]);
                        break;

                    case "allMACAddr":
                        this.allMACAddr.addAll(Arrays.asList(tempBreak).subList(1, tempBreak.length));
                        break;

                    case "mapData":
                        readStatus = 1;
                        break;
                }
            }
            else if (readStatus == 1){
                System.out.println("Entering mapData Region\n");
                sb = new StringBuilder();
                sb.append(line.concat("\n"));
                readStatus = 2;
            }
            else if (readStatus == 2){
                if(line.contains("mapData")){
                    //readStatus = 1;
                    mapData.add(new RunData(sb.toString()));
                    sb.setLength(0);
                }
                else{
                    sb.append(line.concat("\n"));
                }
                if(!i.hasNext()){
                    mapData.add(new RunData(sb.toString()));
                }
            }
        }
        this.normalizeData();
    }

    //Constructor
    public DataSet() {
        System.out.println("New DataSet created");
    }

    public DataSet(String csv){
        this.reverseCSV(csv);
    }

    //Getters
    public HashMap<String, double[]> getNormalData() {
        return normalData;
    }

    public String[] getNormalMACAddr() {
        String[] result = new String[this.normalMACAddr.size()];
        this.normalMACAddr.toArray(result);
        return result;
    }

    public String getFloorplan() {
        return floorplan;
    }

    public HashSet<String> getAllMACAddr() {
        return allMACAddr;
    }

    public ArrayList<RunData> getMapData() {
        return mapData;
    }

    public RunData getActiveRun() {
        return activeRun;
    }

    //Setters
    public void setFloorplan(String floorplan) {
        this.floorplan = floorplan;
    }
}
