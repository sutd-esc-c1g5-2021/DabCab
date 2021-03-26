package com.cabdab.wifi;

import android.graphics.PointF;

import com.cabdab.wifi.ui.mapview.CoordManager;

import org.junit.Test;

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
       CoordManager coordManager = new CoordManager(100, 100, 50, 50;
        PointF testPoint = new PointF(1.0f, 1.0f);
        coordManager.moveBySingleTap(testPoint);
        System.out.println(testPoint);
    }
}