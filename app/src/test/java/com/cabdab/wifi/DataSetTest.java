package com.example.selflib.wifi_algo;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.HashMap;


public class DataSetTest {
    private DataSet dataset;
    private HashMap<String,Double> hashMapInputs;

    //Actively create new instances
    @Before
    public void setup(){
        dataset = new DataSet();
    }

    //Auto clear all references
    @After
    public void shutdown(){
        dataset = null;
        hashMapInputs = null;
    }


    //Test Start
    @Test
    public void startRunTest(){
        try{
            dataset.startRun(-1,20);
            assertNotNull(dataset.getActiveRun());
            assertEquals(-1,dataset.getActiveRun().getxStart());
            assertEquals(20,dataset.getActiveRun().getyStart());
        }
        catch (Exception e){
            System.out.println("startRunTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    //Input an expression
    @Test
    public void doubleInputTest(){
        try{
            dataset.startRun(3/2,3);
        }
        catch(Exception e){
            System.out.println("doubleInputTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    //Input overflow
    @Test
    public void longInputTest(){
        try{
            dataset.startRun(999999999*999999999,3);
            System.out.println(dataset.getActiveRun().getxStart());
        }
        catch(Exception e){
            System.out.println("longInputTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    //Test Multiple Starts
    //Post-Condition: Only first start is applied.
    @Test
    public void doubleStartRunTest(){
        try{
            dataset.startRun(1,2);
            assertNotNull(dataset.getActiveRun());
            dataset.startRun(3,4);
            assertEquals(1,dataset.getActiveRun().getxStart());
            assertEquals(2,dataset.getActiveRun().getyStart());
            assertNotEquals(3,dataset.getActiveRun().getxEnd());
            assertNotEquals(4,dataset.getActiveRun().getyEnd());
        }
        catch (Exception e){
            System.out.println("doubleStartRunTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    //Input Wifi AP Data
    @Test
    public void insertTest(){
        try{
            dataset.startRun(20,21);
            assertNotNull(dataset.getActiveRun());

            hashMapInputs = new HashMap<>();
            hashMapInputs.put("MA:CA:DD:R1",100.0);
            hashMapInputs.put("MA:CA:DD:R2",99.5);
            hashMapInputs.put("MA:CA:DD:R3",99.2);

            dataset.insert(hashMapInputs);
            assertEquals(1,dataset.getActiveRun().getRssiList().size());
            assertEquals(1,dataset.getActiveRun().getMacAddr().size());
            assertEquals(3,dataset.getAllMACAddr().size());

            assertEquals("MA:CA:DD:R1",dataset.getActiveRun().getMacAddr().get(0)[0]);
            assertEquals(99.5,dataset.getActiveRun().getRssiList().get(0)[1],1);
        }
        catch (Exception e){
            System.out.println("insertTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    //Input 2 Wifi AP Data without overwrite
    @Test
    public void doubleInsertTest(){
        try{
            dataset.startRun(20,21);
            assertNotNull(dataset.getActiveRun());

            hashMapInputs = new HashMap<>();
            hashMapInputs.put("MA:CA:DD:R1",100.0);
            hashMapInputs.put("MA:CA:DD:R2",99.5);
            hashMapInputs.put("MA:CA:DD:R3",99.2);

            dataset.insert(hashMapInputs);
            assertEquals(1,dataset.getActiveRun().getRssiList().size());
            assertEquals(1,dataset.getActiveRun().getMacAddr().size());
            assertEquals(3,dataset.getActiveRun().getRssiList().get(0).length);
            assertEquals(3,dataset.getActiveRun().getMacAddr().get(0).length);

            dataset.insert(hashMapInputs);
            assertEquals(2,dataset.getActiveRun().getRssiList().size());
            assertEquals(2,dataset.getActiveRun().getMacAddr().size());
            assertEquals(3,dataset.getActiveRun().getRssiList().get(1).length);
            assertEquals(3,dataset.getActiveRun().getMacAddr().get(1).length);
            assertEquals(3,dataset.getAllMACAddr().size());
        }
        catch (Exception e){
            System.out.println("doubleInsertTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    //Input 2 Duplicate wifi AP Data
    @Test
    public void dupMACTest(){
        try{
            dataset.startRun(20,21);
            assertNotNull(dataset.getActiveRun());

            hashMapInputs = new HashMap<>();
            hashMapInputs.put("MA:CA:DD:R1",100.0);
            hashMapInputs.put("MA:CA:DD:R1",99.5);
            hashMapInputs.put("MA:CA:DD:R3",99.2);

            dataset.insert(hashMapInputs);
            assertEquals(1,dataset.getActiveRun().getRssiList().size());
            assertEquals(1,dataset.getActiveRun().getMacAddr().size());
            assertNotEquals(3,dataset.getActiveRun().getRssiList().get(0).length);
            assertNotEquals(3,dataset.getActiveRun().getMacAddr().get(0).length);
            assertEquals(2,dataset.getAllMACAddr().size());
        }
        catch (Exception e){
            System.out.println("dupMACTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    //End after Valid data insertions (Minimum 2 inputs required for valid mapping run)
    @Test
    public void endRunTest(){
        try{
            dataset.startRun(20,21);
            assertNotNull(dataset.getActiveRun());

            hashMapInputs = new HashMap<>();
            hashMapInputs.put("MA:CA:DD:R1",100.0);
            hashMapInputs.put("MA:CA:DD:R2",99.5);
            hashMapInputs.put("MA:CA:DD:R3",99.2);

            dataset.insert(hashMapInputs);
            assertEquals(1,dataset.getActiveRun().getRssiList().size());
            assertEquals(1,dataset.getActiveRun().getMacAddr().size());

            dataset.insert(hashMapInputs);
            assertEquals(2,dataset.getActiveRun().getRssiList().size());
            assertEquals(2,dataset.getActiveRun().getMacAddr().size());

            dataset.endRun(3,4);
            assertNull(dataset.getActiveRun());
            assertEquals(1,dataset.getMapData().size());

            assertEquals(20,dataset.getMapData().get(0).getxStart());
            assertEquals(21,dataset.getMapData().get(0).getyStart());
            assertEquals(3,dataset.getMapData().get(0).getxEnd());
            assertEquals(4,dataset.getMapData().get(0).getyEnd());
        }
        catch (Exception e){
            System.out.println("endTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    //End Run with insufficient data
    @Test
    public void preemptEndTest(){
        try{
            dataset.startRun(20,21);
            assertNotNull(dataset.getActiveRun());

            hashMapInputs = new HashMap<>();
            hashMapInputs.put("MA:CA:DD:R1",100.0);
            hashMapInputs.put("MA:CA:DD:R2",99.5);
            hashMapInputs.put("MA:CA:DD:R3",99.2);

            dataset.insert(hashMapInputs);
            assertEquals(1,dataset.getActiveRun().getRssiList().size());
            assertEquals(1,dataset.getActiveRun().getMacAddr().size());

            dataset.endRun(6,7);
            assertNotNull(dataset.getActiveRun());
            assertEquals(0,dataset.getActiveRun().getxEnd());
            assertEquals(0,dataset.getActiveRun().getyEnd());
            assertEquals(0,dataset.getMapData().size());
        }
        catch (Exception e){
            System.out.println("preemptEndTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    //Start > End > Start
    @Test
    public void secondStartTest(){
        try{
            dataset.startRun(20,21);
            assertNotNull(dataset.getActiveRun());
            hashMapInputs = new HashMap<>();
            hashMapInputs.put("MA:CA:DD:R1",100.0);
            hashMapInputs.put("MA:CA:DD:R2",99.5);
            hashMapInputs.put("MA:CA:DD:R3",99.2);

            dataset.insert(hashMapInputs);
            assertEquals(1,dataset.getActiveRun().getRssiList().size());
            assertEquals(1,dataset.getActiveRun().getMacAddr().size());

            dataset.insert(hashMapInputs);
            assertEquals(2,dataset.getActiveRun().getRssiList().size());
            assertEquals(2,dataset.getActiveRun().getMacAddr().size());

            dataset.endRun(3,4);
            assertNull(dataset.getActiveRun());
            assertEquals(1,dataset.getMapData().size());

            dataset.startRun(5,6);
            assertNotNull(dataset.getActiveRun());
            assertEquals(1,dataset.getMapData().size());

            assertEquals(20,dataset.getMapData().get(0).getxStart());
            assertEquals(3,dataset.getMapData().get(0).getxEnd());
            assertEquals(5,dataset.getActiveRun().getxStart());
        }
        catch(Exception e){
            System.out.println("secondStartTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    //Start > End > Start > End
    @Test
    public void secondEndTest(){
        try{
            dataset.startRun(20,21);
            assertNotNull(dataset.getActiveRun());

            hashMapInputs = new HashMap<>();
            hashMapInputs.put("MA:CA:DD:R1",100.0);
            hashMapInputs.put("MA:CA:DD:R2",99.5);
            hashMapInputs.put("MA:CA:DD:R3",99.2);

            dataset.insert(hashMapInputs);
            assertEquals(1,dataset.getActiveRun().getRssiList().size());
            assertEquals(1,dataset.getActiveRun().getMacAddr().size());

            dataset.insert(hashMapInputs);
            assertEquals(2,dataset.getActiveRun().getRssiList().size());
            assertEquals(2,dataset.getActiveRun().getMacAddr().size());

            dataset.endRun(3,4);
            assertNull(dataset.getActiveRun());
            assertEquals(1,dataset.getMapData().size());

            dataset.startRun(5,6);
            assertNotNull(dataset.getActiveRun());
            assertEquals(1,dataset.getMapData().size());

            dataset.insert(hashMapInputs);
            assertEquals(1,dataset.getActiveRun().getRssiList().size());
            assertEquals(1,dataset.getActiveRun().getMacAddr().size());

            dataset.insert(hashMapInputs);
            assertEquals(2,dataset.getActiveRun().getRssiList().size());
            assertEquals(2,dataset.getActiveRun().getMacAddr().size());

            dataset.endRun(7,8);
            assertNull(dataset.getActiveRun());
            assertEquals(2,dataset.getMapData().size());

            assertEquals(20,dataset.getMapData().get(0).getxStart());
            assertEquals(3,dataset.getMapData().get(0).getxEnd());
            assertEquals(5,dataset.getMapData().get(1).getxStart());
            assertEquals(7,dataset.getMapData().get(1).getxEnd());
        }
        catch (Exception e){
            System.out.println("secondEndTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    //Normalize Data of Same Length
    @Test
    public void normalizeRunTest(){
        try{
            dataset.startRun(20,21);
            assertNotNull(dataset.getActiveRun());

            hashMapInputs = new HashMap<>();
            hashMapInputs.put("MA:CA:DD:R1",100.0);
            hashMapInputs.put("MA:CA:DD:R2",99.5);
            hashMapInputs.put("MA:CA:DD:R3",99.2);

            dataset.insert(hashMapInputs);
            assertEquals(1,dataset.getActiveRun().getRssiList().size());
            assertEquals(1,dataset.getActiveRun().getMacAddr().size());

            dataset.insert(hashMapInputs);
            assertEquals(2,dataset.getActiveRun().getRssiList().size());
            assertEquals(2,dataset.getActiveRun().getMacAddr().size());

            dataset.endRun(3,4);
            assertNull(dataset.getActiveRun());
            assertEquals(1,dataset.getMapData().size());

            dataset.normalizeData();
            assertEquals(2,dataset.getNormalData().size());
            assertEquals(3,dataset.getNormalMACAddr().length);
        }
        catch (Exception e){
            System.out.println("normalizeRunTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    //Normalize Data of different lengths
    @Test
    public void normalizeDifferenceTest(){
        try{
            dataset.startRun(20,21);
            assertNotNull(dataset.getActiveRun());

            hashMapInputs = new HashMap<>();
            hashMapInputs.put("MA:CA:DD:R1",100.0);
            hashMapInputs.put("MA:CA:DD:R2",99.5);

            dataset.insert(hashMapInputs);
            assertEquals(1,dataset.getActiveRun().getRssiList().size());
            assertEquals(1,dataset.getActiveRun().getMacAddr().size());
            assertEquals(2,dataset.getActiveRun().getRssiList().get(0).length);
            assertEquals(2,dataset.getAllMACAddr().size());

            hashMapInputs.put("MA:CA:DD:R3",99.2);
            dataset.insert(hashMapInputs);
            assertEquals(2,dataset.getActiveRun().getRssiList().size());
            assertEquals(2,dataset.getActiveRun().getMacAddr().size());
            assertEquals(3,dataset.getAllMACAddr().size());

            dataset.endRun(3,4);
            assertNull(dataset.getActiveRun());
            assertEquals(1,dataset.getMapData().size());

            dataset.normalizeData();
            assertEquals(2,dataset.getNormalData().size());
            assertEquals(3,dataset.getNormalData().get("20,21").length);
            assertEquals(3,dataset.getNormalData().get("3,4").length);
            assertEquals(3,dataset.getNormalMACAddr().length);

            assertEquals(20,dataset.getMapData().get(0).getxStart());
            assertEquals(21,dataset.getMapData().get(0).getyStart());
            assertEquals(3,dataset.getMapData().get(0).getxEnd());
            assertEquals(4,dataset.getMapData().get(0).getyEnd());
        }
        catch (Exception e){
            System.out.println("normalizeDifferenceTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    //Recreate the same Dataset from CSV String created from previous Dataset
    @Test
    public void csvIntegrityTest(){
        try{
            dataset.startRun(20,21);
            assertNotNull(dataset.getActiveRun());

            hashMapInputs = new HashMap<>();
            hashMapInputs.put("MA:CA:DD:R1",100.0);
            hashMapInputs.put("MA:CA:DD:R2",99.5);
            hashMapInputs.put("MA:CA:DD:R3",99.2);

            dataset.insert(hashMapInputs);
            assertEquals(1,dataset.getActiveRun().getRssiList().size());
            assertEquals(1,dataset.getActiveRun().getMacAddr().size());

            dataset.insert(hashMapInputs);
            assertEquals(2,dataset.getActiveRun().getRssiList().size());
            assertEquals(2,dataset.getActiveRun().getMacAddr().size());

            dataset.endRun(3,4);
            assertNull(dataset.getActiveRun());
            assertEquals(1,dataset.getMapData().size());

            dataset.normalizeData();
            assertEquals(2,dataset.getNormalData().size());
            assertEquals(3,dataset.getNormalMACAddr().length);

            String csv = dataset.toCSV();
            dataset = new DataSet(csv);

            assertEquals(1,dataset.getMapData().size());
            assertEquals(2,dataset.getNormalData().size());
            assertEquals(3,dataset.getNormalMACAddr().length);
            assertEquals(3,dataset.getNormalData().get("20,21").length);
            assertEquals(3,dataset.getNormalData().get("3,4").length);
        }
        catch (Exception e){
            System.out.println("csvIntegrityTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }

    public void templateTestCopy(){
        try{

        }
        catch (Exception e){
            System.out.println("XXXTest Unexpected Exception Caught");
            e.printStackTrace();
            fail();
        }
    }
}
