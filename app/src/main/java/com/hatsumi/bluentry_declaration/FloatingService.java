package com.hatsumi.bluentry_declaration;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.siddharthks.bubbles.FloatingBubbleConfig;
import com.siddharthks.bubbles.FloatingBubbleService;

public class FloatingService extends FloatingBubbleService {

    static final String ACTION_HIDE_BUBBLE = "com.hatsumi.floatingservice.hide";


    @Override
    protected FloatingBubbleConfig getConfig() {
        Context context = getApplicationContext();
        return new FloatingBubbleConfig.Builder()
                // Set the drawable for the bubble
                .bubbleIcon(ContextCompat.getDrawable(context, R.drawable.ic_logo_bubble))

                // Set the drawable for the remove bubble
                .removeBubbleIcon(ContextCompat.getDrawable(context, R.drawable.ic_remove_bubble))

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
                .expandableColor(Color.GRAY)

                // The color of the triangular layout
                .triangleColor(Color.WHITE)

                // Horizontal gravity of the bubble when expanded
                .gravity(Gravity.END)

                // The view which is visible in the expanded view
                .expandableView(getInflater().inflate(R.layout.fragment_check_in, null))

                // Set the alpha value for the remove bubble icon
                .removeBubbleAlpha(0.75f)

                // Building
                .build();
    }


    @Override
    protected boolean onGetIntent(@NonNull Intent intent) {
        // your logic to get information from the intent

        

        Log.d(TAG, "Intent action " + intent.getAction());

        //this.bubbleView.setVisibility(View.INVISIBLE);
        return true;
    }
}