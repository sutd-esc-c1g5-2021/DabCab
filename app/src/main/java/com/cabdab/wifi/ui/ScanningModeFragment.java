package com.cabdab.wifi.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.cabdab.wifi.R;
import com.cabdab.wifi.ui.declaration.DeclarationViewModel;
import com.cabdab.wifi.ui.mapview.PinView;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ScanningModeFragment extends Fragment {

    private DeclarationViewModel declarationViewModel;
    SwipeRefreshLayout swipeRefreshLayout;

    private static final int REQUEST_PICK_MAP = 1;

    private PinView mapView;

    private static String TAG = ScanningModeFragment.class.toString();


    public final static String LOGINSTATUS = "LOGINSTATUS";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        declarationViewModel =
                ViewModelProviders.of(this).get(DeclarationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_scanningmode, container, false);

        declarationViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        Button pickMapButton = root.findViewById(R.id.pick_map_button);
        mapView = root.findViewById(R.id.mapImageView);
        pickMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Selected Map upload");
                selectMapFromPhone();
            }
        });


        return root;
    }
    public void selectMapFromPhone() {
        Toast.makeText(getContext(), "Please select image", Toast.LENGTH_SHORT).show();
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_PICK_MAP);  //one can be replaced with any action code
    }
    private static final int TTSWebActivityValue = 1;



    private void setMapWidthHeight(final Uri selectedImage) {
        loadMapImage(selectedImage, 100, 100);
    }


    private void loadMapImage(final Uri selectedImage, float width, float height) {
        Bitmap bitmap = null;
        try {

            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            mapView.setImage(ImageSource.bitmap(bitmap));
            mapView.initialCoordManager(width, height);
            mapView.setCurrentTPosition(new PointF(1.0f, 1.0f)); //initial current position
            setGestureDetectorListener(true);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICK_MAP:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    setMapWidthHeight(selectedImage);
                } else {
                    Log.d(TAG, "No map found");
                }
                break;

            default:
                break;
        }

    }
    private GestureDetector gestureDetector = null;

    private void setGestureDetectorListener(boolean enable) {
        if (!enable)
            mapView.setOnTouchListener(null);
        else {
            if (gestureDetector == null) {
                gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if (mapView.isReady()) {
                            mapView.moveBySingleTap(e);
                            Log.d(TAG, "Moving map view by single tap");

                        } else {
                            Log.d(TAG, "MapView is not ready");
                        }
                        return true;
                    }
                });
            }

            mapView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return gestureDetector.onTouchEvent(motionEvent);
                }
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}