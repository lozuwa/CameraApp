package com.example.android.labomatic.Camera;

import android.os.Bundle;
import android.app.Activity;
import android.view.WindowManager;

import com.example.android.labomatic.R;

public class RecoverAutomaticService extends Activity {

    /** Constructors */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Contents */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activities_require_camera);
        /** Keep screen on */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /** Start fragment */
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, RecoverAutomaticServiceFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        /** Back operation is not allowed */
    }

}
