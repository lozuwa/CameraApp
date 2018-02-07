package com.example.android.camera2basic.Camera;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.android.camera2basic.R;

public class ControllerAndCamera extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Contents */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_and_camera);
        /** Orientation */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        /** Keep screen on */
       getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /** Start fragment */
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                                .replace(R.id.container, ControllerAndCameraFragment.newInstance())
                                .commit();
        }
    }

    @Override
    public void onBackPressed() {
        /** Back operation is not allowed */
    }

    /**
     * Shows a specific message in a toast
     * @param message: input string that defines the message to be displayed
     * */
    public void showToast(String message){
        Toast.makeText(ControllerAndCamera.this, message, Toast.LENGTH_SHORT).show();
    }

}
