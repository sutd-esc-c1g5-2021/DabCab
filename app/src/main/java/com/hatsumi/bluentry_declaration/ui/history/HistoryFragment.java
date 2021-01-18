package com.hatsumi.bluentry_declaration.ui.history;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hatsumi.bluentry_declaration.LoginPageActivity;
import com.hatsumi.bluentry_declaration.R;

import java.util.Objects;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;

    public final static String LOGINSTATUS = "LOGINSTATUS";



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);
//        final TextView textView = root.findViewById(R.id.text_history);

        TabLayout tablayout = root.findViewById(R.id.tab_layout);

        final ViewPager viewPager = root.findViewById(R.id.viewPager);

        viewPager.setAdapter(new PagerAdapter(getFragmentManager(), tablayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));
        tablayout.setupWithViewPager(viewPager);
        tablayout.setTabMode(TabLayout.MODE_FIXED);
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//
//        Spinner spinner = root.findViewById(R.id.spinnerPeriod);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.date,android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);


        historyViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s)
            {
//                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // OPTIONS page
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        ImageButton profileButton = getView().findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open options page
                View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_popup, null, false);
                PopupWindow popupWindow = new PopupWindow(popupView, (int) (width * 0.48), WindowManager.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                popupWindow.showAtLocation(popupView, Gravity.LEFT, 0, 0);

                // Open help page
                Button helpButton = popupView.findViewById(R.id.help_button);
                helpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View helpView = LayoutInflater.from(getActivity()).inflate(R.layout.help_page, null, false);
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

                // Open about page
                Button aboutButton = popupView.findViewById(R.id.about_button);
                aboutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View aboutView = LayoutInflater.from(getActivity()).inflate(R.layout.about_page, null, false);
                        PopupWindow aboutPopup = new PopupWindow(aboutView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        aboutPopup.showAtLocation(aboutView, Gravity.CENTER, 0, 0);

                        // Close about page
                        Button aboutBack = aboutView.findViewById(R.id.about_back);
                        aboutBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                aboutPopup.dismiss();
                            }
                        });
                    }
                });
                // Logout button
                Button logoutButton = popupView.findViewById(R.id.logout_button);
                logoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent decToLogin = new Intent(getActivity(), LoginPageActivity.class);
                        decToLogin.putExtra(LOGINSTATUS, 0);
                        Objects.requireNonNull(getActivity()).finish();
                        startActivity(decToLogin);
                    }
                });
            }
        });
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        private String[] tabTitles = new String[]{"Period", "Place"};

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PeriodFragment();
                case 1:
                    return new PlaceFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

}

