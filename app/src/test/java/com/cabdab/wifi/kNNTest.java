package com.cabdab.wifi;

import com.cabdab.wifi.ui.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;
import static org.junit.Assert.*;

public class kNNTest {
    @Test//(expected = Exception.class)
    public void misaligned_data() throws Exception {
        String[] strings = new String[]{"myHome", "SUTD", "PublicWifi1", "PublicWifi2"};
        float[] floats = new float[]{0.2f, 0.54f, 1.678f};
        InterData interData = new InterData(strings, floats);
    }

    @Test
    public void empty_strings() throws Exception{
        String[] strings = new String[4];
        float[] floats = new float[]{0.2f, 0.54f, 1.678f, 9864.5f};
        InterData interData = new InterData(strings, floats);
    }

    @Test
    // should we be concerned about null floats?
    public void empty_floats() throws Exception{
        String[] strings = new String[]{"myHome", "SUTD", "PublicWifi1", "PublicWifi2"};
        float[] floats = new float[4];
        InterData interData = new InterData(strings, floats);
    }

    @Test
    public void testRoundings() throws Exception{
        String[] strings = new String[]{"myHome", "SUTD", "PublicWifi1", "PublicWifi2"};
        float[] floats = new float[]{0.2f, 0.54f, 1.678f, 9864.5f};
        InterData interData = new InterData(strings, floats);
        interData.markInterval(123f,-76.4516f);
        assertEquals(-76.45f, interData.getY(), 0.01f);
        assertEquals(123.0f, interData.getX(), 0.01f);
    }

    @Test
    public void testGetXY() throws Exception {
        String[] strings = new String[]{"myHome", "SUTD", "PublicWifi1", "PublicWifi2"};
        float[] floats = new float[]{0.2f, 0.54f, 1.678f, 9864.5f};
        InterData interData = new InterData(strings, floats);
        interData.markInterval(123f, -76.4516f);
        assertEquals("123.0, -76.45", interData.getKeyXY());
    }
}
