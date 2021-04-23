package com.cabdab.wifi;

import android.graphics.PointF;

import com.cabdab.wifi.ui.mapview.CoordManager;

import org.junit.Test;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CoordManagerTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testCoordManager() {
        // Initialize CoordManager
        CoordManager coordManager = new CoordManager(100, 100, 50, 50);
        PointF testPoint = new PointF(1.0f, 1.0f);
        coordManager.moveBySingleTap(testPoint);
        System.out.println(testPoint);
    }

    @Test
    public void monkeyCoordManager(){
        // Initialize CoordManager
        CoordManager coordManager = new CoordManager(100, 100, 50, 50);
        Random random = new Random();
        // keep tapping randomly lots of times, not really a monkey test but you don't want to crash the app
        for (int i = 0; i <= 128; i++) {
            float random1 = random.nextFloat() * (random.nextBoolean() ? -1 : 1);
            float random2 = random.nextFloat() * (random.nextBoolean() ? -1 : 1);
            PointF testPoint = new PointF(random1, random2);
            coordManager.moveBySingleTap(testPoint);
            System.out.println(testPoint);
            /* you might need this if it taps TOO fast
            try{
                Thread.sleep(100);
            }
            catch (InterruptedException ex){
                continue;
            }
             */
        }
    }
}