package com.hatsumi.bluentry_declaration;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;

import androidx.core.content.ContextCompat;

import com.siddharthks.bubbles.FloatingBubbleConfig;
import com.siddharthks.bubbles.FloatingBubbleService;

public class FloatingService extends FloatingBubbleService {

    @Override
    protected FloatingBubbleConfig getConfig() {
        Context context = getApplicationContext();
        return new FloatingBubbleConfig.Builder()
                // Set the drawable for the bubble
                .bubbleIcon(ContextCompat.getDrawable(context, R.drawable.bluetooth_rounded_corner))

                // Set the drawable for the remove bubble
                .removeBubbleIcon(ContextCompat.getDrawable(context, R.drawable.bluetooth_rounded_corner))

                // Set the size of the bubble in dp
                .bubbleIconDp(64)

                // Set the size of the remove bubble in dp
                .removeBubbleIconDp(64)

                // Set the padding of the view from the boundary
                .paddingDp(4)

                // Set the radius of the border of the expandable view
                .borderRadiusDp(4)

                // Does the bubble attract towards the walls
                .physicsEnabled(true)

                // The color of background of the layout
                .expandableColor(Color.WHITE)

                // The color of the triangular layout
                .triangleColor(Color.WHITE)

                // Horizontal gravity of the bubble when expanded
                .gravity(Gravity.END)

                // The view which is visible in the expanded view
                .expandableView(getInflater().inflate(R.layout.activity_login, null))

                // Set the alpha value for the remove bubble icon
                .removeBubbleAlpha(0.75f)

                // Building
                .build();
    }
}