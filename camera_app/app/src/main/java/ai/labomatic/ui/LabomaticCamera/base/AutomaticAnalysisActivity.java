package ai.labomatic.ui.LabomaticCamera.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import ai.labomatic.R;
import ai.labomatic.ui.LabomaticCamera.composer.AutomaticAnalysisFragment;

/**
 * Loads the automatic camera fragment.
 * */
public class AutomaticAnalysisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activities_require_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Start fragment
        if (null == savedInstanceState) {
            Fragment myFragment = null;
            Class fragmentClass;
            fragmentClass = AutomaticAnalysisFragment.class;
            try {
                myFragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, myFragment)
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
        // Back operation is not allowed
    }

}
