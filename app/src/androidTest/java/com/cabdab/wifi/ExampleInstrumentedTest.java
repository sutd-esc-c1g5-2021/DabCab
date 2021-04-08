package com.cabdab.wifi;

import android.content.Context;
import android.graphics.PointF;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cabdab.wifi.ui.mapview.CoordManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {


    static CoordManager coordManager;

    @Test
    public void testCoordManagerInit() {
        // Initialize CoordManager
        coordManager = new CoordManager(100, 100, 50, 50);
        System.out.println(coordManager.currentSCoord.x);
        assertEquals(coordManager.currentSCoord.x, 0.0, 0);
        assertEquals(coordManager.currentSCoord.y, 0.0, 0);
        assertEquals(coordManager.pixelHeight, 50, 0);
        assertEquals(coordManager.pixelWidth, 50, 0);
        // Scaling factor should be 0.5 => 100/50 = 0.5
        assertEquals(coordManager.widthScale, 0.5, 0);
    }

    @Test
    public void testCoordManagerMove() {
        coordManager.moveBySingleTap(new PointF(1, 1));
        // As scaling factor is 0.5,
        // After moving by single tap, it should move by 1 on true coordinate, 0.5 on picture
        assertEquals(coordManager.currentTCoord.x, 0.0, 0);
        assertEquals(coordManager.currentTCoord.y, 1, 0);


    }
}