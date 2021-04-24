package com.cabdab.wifi;

import com.cabdab.wifi.ui.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Random;
import static org.junit.Assert.*;

public class DataTest {
    @Test
    public void endRunWhenNotStarted(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));
        DataSet dataSet = new DataSet();
        dataSet.endRun(5, 10);
        String expectedOut = "New DataSet created\n\nRun End Request Received\n\nError: Run has not started\n";
        assertEquals(expectedOut, byteArrayOutputStream.toString());
        // this would be easier if it threw an exception
    }

    @Test
    public void startRunWhenStarted(){
        DataSet dataSet = new DataSet();
        dataSet.startRun(-4, 3);
        dataSet.startRun(15, 16);
    }

    @Test
    public void insertWithNoRuns(){
        HashMap<String, Double> input = new HashMap<>();
        Random random = new Random();
        for (int i = 1; i < 10; i++){
            input.put("Test value " + String.valueOf(i), random.nextDouble());
        }
        DataSet dataSet = new DataSet();
        dataSet.insert(input);
    }
}
