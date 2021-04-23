package com.example.selflib.wifi_algo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KNNTool {
    private final int DIMENSIONS = 2;
    private String[] allMacAdr;
    private double[] rssiIn;
    private HashMap<String,double[]> testData;

    public void trainKNN(String[] allMacAdr, HashMap<String,double[]> normalTest){
        this.allMacAdr = allMacAdr;
        this.testData = normalTest;
    }

    public void testKNN (HashMap<String,Double> hashIn){
        this.rssiIn = new double[this.allMacAdr.length];
        for(int i = 0; i<this.allMacAdr.length; i++){
            if(hashIn.keySet().contains(allMacAdr[i])){
                this.rssiIn[i] = hashIn.get(allMacAdr[i]);
            }
            else{
                this.rssiIn[i] = 0;
            }
            System.out.println("\nLoop Test "+this.allMacAdr[i]+": "+this.rssiIn[i]);
        }

    }

    //Calculate Euclidean distance before finding 3-Nearest Neighbours and return estimated location
    public int[] getKNN (){
        double min1 = 999999999;
        double min2 = 999999999;
        double min3 = 999999999;
        String estimate1 = "";
        String estimate2 = "";
        String estimate3 = "";
        for(Map.Entry me : this.testData.entrySet()){
            double range = euclidean(this.rssiIn, (double[]) me.getValue());

            if (range <min1){
                min3 = min2;
                estimate3 = estimate2;
                min2 = min1;
                estimate2 = estimate1;

                min1 = range;
                estimate1 = me.getKey().toString();
                System.out.println("\nNew Min1 Detected: "+min1+ " at "+estimate1);
                for(double f: (double[])me.getValue()){
                    System.out.println(f);
                }
            }
            else if(range<min2){
                min3 = min2;
                estimate3 = estimate2;

                min2 = range;
                estimate2 = me.getKey().toString();
                System.out.println("\nNew Min2 Detected: "+min2+ " at "+estimate2);
            }
            else if(range < min3){
                min3 = range;
                estimate3 = me.getKey().toString();
                System.out.println("\nNew Min3 Detected: "+min3+ " at "+estimate3);
            }
        }
        System.out.println("\nClosest Values:\n"+estimate1+":"+min1+"\n"+estimate2+":"+min2+"\n"+estimate3+":"+min3);


        int[] testReturn = new int[2];
        String[] nnEst = estimate1.split(",");
        testReturn[0] = Integer.parseInt(nnEst[0]);
        testReturn[1] = Integer.parseInt(nnEst[1]);
        return testReturn;
    }

    //Calculate Euclidean distance between 2 points a and b
    private double euclidean(double[] a, double[] b){
        if(a.length!=b.length){
            System.out.println("KNN Error: Euclidean Inputs not of Same Length");
            return -1;
        }
        double sum=0;
        for(int i =0; i<a.length;i++){
            sum += Math.pow(b[i]-a[i],2);
        }
        return (double) Math.sqrt(sum);
    }


    //Constructors
    public KNNTool(){
    }
}
