package com.hatsumi.bluentry_declaration.ui.splash;

import android.content.SharedPreferences;
import android.os.Build;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
//import android.view.WindowManager; //unused import from deprecated NEXT function.
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.hatsumi.bluentry_declaration.R;

import com.hatsumi.bluentry_declaration.LoginPageActivity;


public class OnBoardingActivity extends AppCompatActivity {

    //Variables
    ViewPager viewPager;
    LinearLayout dotsLayout;
    SliderAdapter sliderAdapter;
    TextView[] dots;
    Button getStartedButton;
    Animation animation;
    int currentPos;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        //Hooks
        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        getStartedButton = findViewById(R.id.getstarted);

        //Call adapter
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        //Dots
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
    }

    //TODO (upon merge): change SecondActivity.class to the LoginScreen.
    public void skip(View view) {
        startActivity(new Intent(this, LoginPageActivity.class));
        finish();
    }

    public void getStarted(View view){
        startActivity(new Intent(this, LoginPageActivity.class));
        finish();
        // both methods go to the login screen aka "second activity", if you want to make them redirect to other places you can of course
    }

    // Deprecated next button code. RIP. Left in case someone really needs a next button.
    /*public void next(View view) {
        viewPager.setCurrentItem(currentPos + 1);
    }
    */


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void addDots(int position) {

        dots = new TextView[4];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("•"));
            dots[i].setTextSize(35);
            dots[i].setGravity(Gravity.CENTER);

            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.bluEntry_main));
            // you can change the primary colour in styles.xml or add a new colour in colors.xml
        }

    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPos = position;

            if (position == 0 || position == 1 || position == 2) {
                getStartedButton.setVisibility(View.INVISIBLE);
            } else {
                animation = AnimationUtils.loadAnimation(OnBoardingActivity.this, R.anim.bottom_anim);
                getStartedButton.setAnimation(animation);
                getStartedButton.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}