package ai.labomatic.ui.LabomaticCamera.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import ai.labomatic.R;

public class ManualAnalysisLandscapeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activities_require_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Start fragment
        if (savedInstanceState == null ){
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new ManualAnalysisLandscapeFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        // Back operation is not allowed
    }

    /**
     * Shows a specific message in a toast
     * @param message: input string that defines the message to be displayed
     * */
    public void showToast(String message){
        Toast.makeText(ManualAnalysisLandscapeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
