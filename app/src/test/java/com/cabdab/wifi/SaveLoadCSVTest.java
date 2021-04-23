package com.example.selflib.wifi_algo;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;
import java.util.Base64;
import java.util.Random;

public class SaveLoadCSVTest {
    private final String FILENAME = "example";
    private final int PARA_RUN_TIME = 10;
    private String input = "";
    private String output = "";

    @Before
    public void setup(){

    }

    @After
    public void shutdown(){
        File file = new File(FILENAME+".csv");
        if(file.exists()){
            if(file.delete()){
                System.out.println("File Deleted");
            }
            else{
                System.out.println("File Not Deleted");
            }
        }
        else {
            System.out.println("File Missing");
        }
        input = "";
        output = "";
    }

    //File is Successfully created
    @Test
    public void fileCreationTest(){
        try{
            input = "1,2,3";
            SaveLoadCSV.saveCSV(FILENAME, input);
            File check = new File(FILENAME+".csv");
            assertTrue(check.exists());
        }
        catch (Exception e){
            System.out.println("fileCreationTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void loadCSVIntegrityTest(){
        try{
            input = "1,2,3";
            SaveLoadCSV.saveCSV(FILENAME, input);
            output = SaveLoadCSV.loadCSV(FILENAME);
            assertEquals(input.length(),output.length()-1);     //Exception for final \n character
            assertEquals(input.split(",").length,output.split(",").length);
        }
        catch (Exception e){
            System.out.println("loadCSVIntegrityTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void emptyStringTest(){
        try{
            SaveLoadCSV.saveCSV(FILENAME, input);
            output = SaveLoadCSV.loadCSV(FILENAME);
            assertEquals(input.length(),output.length());
            assertEquals(input.split(",").length,output.split(",").length);
        }
        catch (Exception e){
            System.out.println("loadCSVIntegrityTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void randomStringTest(){
        try {
            for (int i = 0; i<PARA_RUN_TIME; i++){
                byte[] array = new byte[50];
                new Random().nextBytes(array);
                //String intermediate = new String(array, Charset.forName("UTF-8"));
                input = Base64.getEncoder().encodeToString(array)+"\n";
                System.out.println(i+1+" "+input);
                SaveLoadCSV.saveCSV(FILENAME, input);
                output = SaveLoadCSV.loadCSV(FILENAME);
                assertEquals(input.length(),output.length());
                assertEquals(input,output);
                assertEquals(input.split(",").length,output.split(",").length);
                assertEquals(input.split("\n").length,output.split("\n").length);
            }
        }
        catch (Exception e){
            System.out.println("randomStringTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

}
