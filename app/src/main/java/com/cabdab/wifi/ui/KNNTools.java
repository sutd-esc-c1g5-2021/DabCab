package com.cabdab.wifi.ui;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KNNTools {
    private String[] allMacAdr;
    private float[] rssiIn;
    private HashMap<String,float[]> testData;

    public void trainKNN(String[] allMacAdr, HashMap<String,float[]> normalTest){
        this.allMacAdr = allMacAdr;
        this.testData = normalTest;
    }

    public void testKNN (String[] macAdrIn ,float[] input){
        this.rssiIn = new float[allMacAdr.length];
        List<String> tempMacIn = Arrays.asList(macAdrIn);
        for(int i = 0;i<this.allMacAdr.length;i++){
            if (tempMacIn.contains(this.allMacAdr[i])){
                this.rssiIn[i] = input[tempMacIn.indexOf(this.allMacAdr[i])];
            }
            else{
                this.rssiIn[i] = 0;
            }
            System.out.println("\nLoop test "+this.allMacAdr[i]+" "+this.rssiIn[i]);
        }
    }

    public String getKNN (int k){
        //TODO: Implement K>1 function
        float min = 99999;
        String estimate ="";
        for(Map.Entry me : this.testData.entrySet()){
            float range = euclidean(this.rssiIn,(float[])me.getValue());
            if (range <min){
                min = range;
                estimate = me.getKey().toString();
                System.out.println("\nNew Min Detected: "+min+ " at "+estimate);
                for(float f: (float[])me.getValue()){
                    System.out.println(f);
                }
            }
        }
        return estimate;
    }

    private float euclidean(float[] a, float[] b){
        if(a.length!=b.length){
            System.out.println("KNN Error: Euclidean Inputs not of Same Length");
            return -1;
        }
        float sum=0;
        for(int i =0; i<a.length;i++){
            sum += Math.pow(b[i]-a[i],2);
        }
        return (float) Math.sqrt(sum);
    }
    public KNNTools(){

    }
}