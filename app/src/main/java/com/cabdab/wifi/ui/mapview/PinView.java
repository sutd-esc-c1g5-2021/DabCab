package com.cabdab.wifi.ui.mapview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.cabdab.wifi.R;


import java.util.List;
import android.util.Log;

public class PinView extends SubsamplingScaleImageView {

    public static String TAG = "PinView";

    private final Paint paint = new Paint();
    private final PointF vPin = new PointF();
    private final PointF ePin = new PointF();

    private PointF currentPoint;
    private PointF endPoint;
    private List<PointF> fingerprintPoints;

    private Bitmap currentPin;
    private Bitmap completedPin;
    private Bitmap beaconPin;
    private Bitmap endPin;

    private CoordManager coordManager;
    private CoordManager coordManager_end;

    public PinView(Context context) {
        this(context, null);
        initialise();
    }

    public PinView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    private void initialise() {
        float density = getResources().getDisplayMetrics().densityDpi;
        setMaximumDpi((int) density);

        currentPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.location_marker);
        float w = (density / 1500f) * currentPin.getWidth();
        float h = (density / 1500f) * currentPin.getHeight();
        currentPin = Bitmap.createScaledBitmap(currentPin, (int) w, (int) h, true);

        endPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.location_marker_end);
        endPin = Bitmap.createScaledBitmap(endPin, (int) w, (int) h, true);


        completedPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.completed_point);
        w = (density / 6000) * completedPin.getWidth();
        h = (density / 6000f) * completedPin.getHeight();
        completedPin = Bitmap.createScaledBitmap(completedPin, (int) w, (int) h, true);
    }



    public void setCurrentTPosition(PointF p) {
        coordManager.setCurrentTCoord(p);
        this.currentPoint = coordManager.getCurrentSCoord();
        invalidate();
    }

    public void setCurrentTPosition_end(PointF p) {
        coordManager_end.setCurrentTCoord(p);
        this.endPoint = coordManager_end.getCurrentSCoord();
        invalidate();
    }


    public PointF getCurrentTCoord_end() {
        return coordManager_end.getCurrentTCoord();
    }

    public PointF getCurrentTCoord() {
        return coordManager.getCurrentTCoord();
    }

    public void initialCoordManager(float width, float height) {
        this.coordManager = new CoordManager(width, height, this.getSWidth(), this.getSHeight());
        this.coordManager_end = new CoordManager(width, height, this.getSWidth(), this.getSHeight());
    }

    public void setStride(float stride) {
        this.coordManager.setStride(stride);
        this.coordManager_end.setStride(stride);
    }

    public void moveBySingleTap(MotionEvent e, int mode) {
        Log.d(TAG, "Moving by single tap");
        if (mode == 0) {
            Log.d(TAG, "Moving the start position");
            //Setting the start position
            PointF sCoord = this.viewToSourceCoord(e.getX(), e.getY());
            coordManager.moveBySingleTap(sCoord);
            this.setCurrentTPosition(coordManager.getCurrentTCoord());
        }
        else {
            //Setting the end position
            Log.d(TAG, "Moving the end position");
            PointF sCoord = this.viewToSourceCoord(e.getX(), e.getY());
            Log.d(TAG, "The new position of end Point " + coordManager_end.getCurrentTCoord());
            coordManager_end.moveBySingleTap(sCoord);
            this.setCurrentTPosition_end(coordManager_end.getCurrentTCoord());
            Log.d(TAG, "The new position of end Point " + coordManager_end.getCurrentTCoord());
        }
    }

    public PointF getEventPosition(MotionEvent e) {
        PointF sCoord = this.viewToSourceCoord(e.getX(), e.getY());
        return coordManager.sCoordToTCoord(sCoord);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        paint.setAntiAlias(true);

        if (currentPoint != null && currentPin != null) {
            sourceToViewCoord(currentPoint, vPin);
            float vX = vPin.x - (currentPin.getWidth() / 2);
            float vY = vPin.y - (currentPin.getHeight() / 2);
            Log.d(TAG, "Going to draw the startpoint");
            canvas.drawBitmap(currentPin, vX, vY, paint);
        }

        if (endPoint != null && endPin != null) {
            sourceToViewCoord(endPoint, ePin);
            float eX = ePin.x - (endPin.getWidth() / 2);
            float eY = ePin.y - (endPin.getHeight() / 2);
            Log.d(TAG, "Going to draw the endpoint");
            canvas.drawBitmap(endPin, eX, eY, paint);
        }

        if (fingerprintPoints != null && completedPin != null)
            for (PointF pointF : fingerprintPoints) {
                sourceToViewCoord(pointF, vPin);
                float vX = vPin.x - (completedPin.getWidth() / 2);
                float vY = vPin.y - (completedPin.getHeight() / 2);
                canvas.drawBitmap(completedPin, vX, vY, paint);
            }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
