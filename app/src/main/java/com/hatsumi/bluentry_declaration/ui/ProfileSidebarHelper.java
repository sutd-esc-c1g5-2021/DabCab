package com.hatsumi.bluentry_declaration.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.hatsumi.bluentry_declaration.PreferencesUtils;
import com.hatsumi.bluentry_declaration.R;
import com.hatsumi.bluentry_declaration.ui.splash.SplashActivity;

import androidx.fragment.app.Fragment;

public class ProfileSidebarHelper {

    public static void setupProfileForView(Fragment fragment, int width) {
        ImageButton profileButton = fragment.getView().findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open options page
                View popupView = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.profile_popup, null, false);
                PopupWindow popupWindow = new PopupWindow(popupView, (int) (width * 0.48), WindowManager.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                popupWindow.showAtLocation(popupView, Gravity.LEFT, 0, 0);

                // Open help page
                Button helpButton = popupView.findViewById(R.id.help_button);
                helpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View helpView = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.help_page, null, false);
                        PopupWindow helpPopup = new PopupWindow(helpView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        helpPopup.showAtLocation(helpView, Gravity.CENTER, 0, 0);

                        // Close help page
                        Button helpBack = helpView.findViewById(R.id.help_back);
                        helpBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                helpPopup.dismiss();
                            }
                        });
                    }
                });

                Button aboutButton = popupView.findViewById(R.id.about_button);
                aboutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View aboutView = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.about_page, null, false);
                        PopupWindow aboutPopup = new PopupWindow(aboutView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        aboutPopup.showAtLocation(aboutView, Gravity.CENTER, 0, 0);

                        /*// Close about page
                        Button aboutBack = aboutView.findViewById(R.id.about_back);
                        aboutBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                aboutPopup.dismiss();
                            }
                        });*/
                    }
                });

                //Logout Page
                Button logoutButton = popupView.findViewById(R.id.logout_button);
                logoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PreferencesUtils preferencesUtils = new PreferencesUtils(fragment.getContext());
                        preferencesUtils.removeSession();
                        Intent intent = new Intent(fragment.getActivity(), SplashActivity.class);
                        fragment.startActivity(intent);
                        fragment.getActivity().finish();
                    }
                });
            }
        });
    }

}
