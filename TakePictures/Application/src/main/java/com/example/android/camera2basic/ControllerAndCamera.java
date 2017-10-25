package com.example.android.camera2basic;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.WindowManager;

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

}
