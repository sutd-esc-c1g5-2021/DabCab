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

        int[] testReturn = getAverage(estimate1,min1,estimate2,min2,estimate3,min3);
        if(testReturn != null){
            System.out.println("\nClosest Average: "+testReturn[0]+","+testReturn[1]);
        }
        testReturn = new int[2];
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

    //Function below doesn't work. Remains here for possible future updates
    private int[] getAverage(String est1, double range1, String est2, double range2, String est3, double range3){
        String[] in1 = est1.split(",",2);
        String[] in2 = est2.split(",",2);
        String[] in3 = est3.split(",",2);
        int[] coord1 = new int[this.DIMENSIONS];
        int[] coord2 = new int[this.DIMENSIONS];
        int[] coord3 = new int[this.DIMENSIONS];

        if(in1.length!=this.DIMENSIONS || in2.length!=this.DIMENSIONS || in3.length!=this.DIMENSIONS){
            System.out.println("KNN Error: Dimension Misalignment in KNN Test Data Estimate");
            return null;
        }

        for (int i = 0; i<this.DIMENSIONS; i++){
            coord1[i] = Integer.parseInt(in1[i]);
            coord2[i] = Integer.parseInt(in2[i]);
            coord3[i] = Integer.parseInt(in3[i]);
        }

        double a1 = 2*(coord1[0]-coord2[0]);
        double b1 = 2*(coord1[1]-coord2[1]);

        double a2 = 2*(coord2[0]-coord3[0]);
        double b2 = 2*(coord2[1]-coord3[1]);

        double c1 = Math.pow(range2,2)-Math.pow(range1,2)-Math.pow(coord2[0],2)-Math.pow(coord2[1],2)+Math.pow(coord1[0],2)+Math.pow(coord1[1],2);
        double c2 = Math.pow(range3,2)-Math.pow(range2,2)-Math.pow(coord3[0],2)-Math.pow(coord3[1],2)+Math.pow(coord2[0],2)+Math.pow(coord2[1],2);

        double determinant = a1*b2-b1*a2;
        if(determinant == 0){
            System.out.println("KNN Error: Determinant is 0");
            return null;
        }
        int x = (int) Math.round((c1*b2-b1*c2)/determinant);
        int y = (int) Math.round((a1*c2-c1*a2)/determinant);

        return new int[]{x,y};
    }

    //Constructors
    public KNNTool(){
    }
}
