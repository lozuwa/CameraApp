package ai.labomatic.ui.LabomaticCamera;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import ai.labomatic.R;

public class ControllerAndCamera extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activities_require_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Start fragment
//        if (null == savedInstanceState) {
//            getFragmentManager().beginTransaction()
//                                .replace(R.id.container, ControllerAndCameraFragment.newInstance())
//                                .commit();
//        }
        Bundle b = new Bundle();
        b.putString("folderName", "RodrigoLoza");
        ControllerAndCameraFragment fragment = new ControllerAndCameraFragment();
        fragment.setArguments(b);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, new ControllerAndCameraFragment())
                .commit();
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
        Toast.makeText(ControllerAndCamera.this, message, Toast.LENGTH_SHORT).show();
    }

}
