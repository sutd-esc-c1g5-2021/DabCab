package com.hatsumi.bluentry_declaration.ui.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.hatsumi.bluentry_declaration.R;

public class HistoryFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private HistoryViewModel historyViewModel;
    private static String TAG = HistoryFragment.class.toString();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);


        TabLayout tablayout = getActivity().findViewById(R.id.tab_layout);
        TabItem tab1Period = getActivity().findViewById(R.id.tab1Period);
        TabItem tab2Place = getActivity().findViewById(R.id.tab2Place);


        return root;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(),text, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Item selected");

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}