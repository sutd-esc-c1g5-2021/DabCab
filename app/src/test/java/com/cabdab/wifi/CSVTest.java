package com.cabdab.wifi;

import com.cabdab.wifi.ui.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import static org.junit.Assert.*;

public class CSVTest {
    @Test
    public void makeCSV(){
        SaveLoadCSV.saveCSV("Test1", "Hello world!");
        String string = SaveLoadCSV.loadCSV("Test1");
        System.out.println(string);
        assertEquals("Hello world!\n", string);
    }

    @Test
    public void commaSaveCSV(){
        String toSave = "A:1, B:2, C:3, D:4, E:5, F:6, G:7, H:8, I:9, J:10,";
        SaveLoadCSV.saveCSV("Test2", toSave);
        String string = SaveLoadCSV.loadCSV("Test2");
        System.out.println(string);
        assertEquals(toSave + "\n", string);
    }


    @Test
    public void monkeyStrings(){
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        for (int j = 0; j < 10; j++){
            for (int i = 0; i < 50; i++) {
                int index = (int) (AlphaNumericString.length() * Math.random());
                sb.append(AlphaNumericString.charAt(index));
            }
            sb.append("\n");
        }
        String toSave = sb.toString();
        //System.out.println(toSave);
        SaveLoadCSV.saveCSV("Test3", toSave);
        String string = SaveLoadCSV.loadCSV("Test3");
        assertEquals(toSave, string);
    }

    @Test
    public void saveNothing(){
        String toSave = "";
        SaveLoadCSV.saveCSV("Test4", toSave);
        String string = SaveLoadCSV.loadCSV("Test4");
        System.out.println(string);
        assertEquals(toSave, string);
    }
}
