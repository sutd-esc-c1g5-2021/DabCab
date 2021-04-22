package com.example.selflib.wifi_algo;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SaveLoadCSV {


    public static void saveCSV(Context context, String filename, String content){
        try{
            File file = new File(context.getFilesDir(), filename);
            if(!file.exists()){
                file.createNewFile();
            }
            System.out.println("File has been saved at: "+file.getAbsolutePath());

            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public static String loadCSV(Context context, String filename){
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String readLine;
        try{
            File file = new File(context.getFilesDir(), filename);
            br = new BufferedReader(new FileReader(file));
            while((readLine = br.readLine())!=null){
                sb.append(readLine);
                sb.append("\n");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (br != null) {
                    br.close();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return sb.toString();
        }
    }
}
