package com.example.android.camera2basic;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceReceiver;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * A login screen that offers login via email/password.
 */
public class CreatePatient extends Activity implements OnShowcaseEventListener {

    /**
     * UI elements
     * */
    private AutoCompleteTextView nameUserEditText;
    private Button automaticAnalisisButton;
    private Button manualAnalysisButton;
    private Button justCameraButton;
    private Button readDbButton;

    /**
     * Showcase
     * */
    public ShowcaseView sv;
    public RelativeLayout.LayoutParams lps;
    public ViewTarget target;

    /**
     * SQLite
     * */
    public SQLiteDatabase mydatabase;

    /** Constant variables */

    /**
     * Constructors
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Contents */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_patient);
        /** Orientation */
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /** Open database */
        mydatabase = openOrCreateDatabase(DbFeed.TABLE_NAME, MODE_PRIVATE, null);
        initializeDb();
        /** Always restart home (XY), when reaching this activity */
        publishMessage(Initializer.MACROS_TOPIC, Initializer.STAGE_RESTART_HOME);
        /** Instantiate UI elements */
        nameUserEditText = (AutoCompleteTextView) findViewById(R.id.patient_name_editText);
        automaticAnalisisButton = (Button) findViewById(R.id.automatic_button);
        manualAnalysisButton = (Button) findViewById(R.id.manual_button);
        justCameraButton = (Button) findViewById(R.id.just_camera_button);
        readDbButton = (Button) findViewById(R.id.read_db_button);
        /** Initial state UI */
        nameUserEditText.setError(null);
        readDbButton.setVisibility(View.GONE);
        /** Configure showcase */
        lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);
        target = new ViewTarget(R.id.automatic_button, this);
        /** Show first button */
        /*sv = new ShowcaseView.Builder(CreatePatient/.this)
                .withMaterialShowcase()
                .setTarget(target)
                .setContentTitle("Prepare the sample")
                .setContentText("Press the button to take the stage out")
                .setStyle(R.style.CustomShowcaseTheme3)
                .setShowcaseEventListener(CreatePatient.this)
                .replaceEndButton(R.layout.view_custom_button)
                .build();*/
        //sv.setButtonPosition(lps);
        /** Button callbacks */
        manualAnalysisButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Once sample is prepared and locked, set to initial position */
                publishMessage(Initializer.MACROS_TOPIC, Initializer.STAGE_RESTART_INITIAL);
                /** Get folder name */
                final String NAME_FOLDER = nameUserEditText.getText().toString();
                if (NAME_FOLDER.isEmpty() || (NAME_FOLDER.length() < 4)) {
                    showToast("Name is too short");
                } else {
                    createFolder(NAME_FOLDER);
                    /** Start camera */
                    Intent intent = new Intent(CreatePatient.this, ControllerAndCamera.class);
                    startActivity(intent);
                }
            }
        });

        automaticAnalisisButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Once sample is prepared and locked, set to initial position */
                publishMessage(Initializer.MACROS_TOPIC, Initializer.STAGE_RESTART_INITIAL);
                /** Get folder name */
                final String NAME_FOLDER = nameUserEditText.getText().toString();
                if (NAME_FOLDER.isEmpty() || (NAME_FOLDER.length() < 4)) {
                    showToast("Name is too short");
                } else {
                    createFolder(NAME_FOLDER);
                    /** Start camera */
                    //Intent intent = new Intent(CreatePatient.this, CameraActivity.class);
                    //startActivity(intent);
                    /** Start activity CameraActivity from cameraApp */
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName("pfm.improccameraautofocus", "pfm.improccameraautofocus.CameraAndController"));
                    startActivity(intent);
                }
            }
        });

        justCameraButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Start camera */
                Intent intent = new Intent(CreatePatient.this, JustCamera.class);
                startActivity(intent);
            }
        });

        readDbButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Fetch db and show it */
                try {
                    Cursor resultSet = mydatabase.rawQuery("Select * from " + DbFeed.TABLE_NAME, null);
                    resultSet.moveToFirst();
                    String folderName = resultSet.getString(0);
                    showToast(folderName);
                } catch (Exception e){
                    showToast("Db is empty");
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver.register(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        receiver.unregister(this);
    }

    @Override
    public void onBackPressed() {
        /** Back operation is not allowed */
    }

    /**
     * Support functions
     * */

    /**
     * Creates a physical folder folder in the apps's directory
     * @param FOLDER_NAME: input string that contains the name of the folder to be created
     * */
    public void createFolder(String FOLDER_NAME){
        /** Write folder name into db table */
        mydatabase.execSQL("INSERT INTO " + DbFeed.TABLE_NAME + " VALUES('" + FOLDER_NAME + "');");
        /** Create folder */
        //File folder = new File(this.getExternalFilesDir(null), File.separator + FOLDER_NAME);
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), File.separator + FOLDER_NAME);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        } else {
            showToast("Folder already exists " + FOLDER_NAME);
        }
        if (success) {
            showToast("Folder successfully created");
        } else {
            showToast("Folder was not created, something happened");
            Log.e("CreatePatient", "Folder not created");
        }
    }

    /**
     * First drops the table info just in case any unexpected crashes have happened, then creates a clean table
     *
     * */
    public void initializeDb() {
        /** Clean table */
        mydatabase.execSQL("DROP TABLE IF EXISTS " + DbFeed.TABLE_NAME);
        /** Create table */
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " + DbFeed.TABLE_NAME + "(" + DbFeed.COLUMN_NAME_TITLE + " VARCHAR);");
    }

     /**
     * Shows a specific message in a toast
     * @param message: input string that defines the message to be displayed
     * */
    public void showToast(String message){
        Toast.makeText(CreatePatient.this, message, Toast.LENGTH_SHORT).show();
    }

    /** Publish a message
     * @param topic: input String that defines the target topic of the mqtt client
     * @param message: input String that contains a message to be published
     * @return no return
     * */
    public void publishMessage(String topic, String message) {
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = message.getBytes("UTF-8");
            MQTTServiceCommand.publish(CreatePatient.this, topic, encodedPayload, 2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * MQTT Receiver
     * */
    private MQTTServiceReceiver receiver = new MQTTServiceReceiver() {

        private static final String TAG = "Receiver";

        @Override
        public void onSubscriptionSuccessful(Context context, String requestId, String topic) {
            /** Info */
            Log.i(TAG, "Subscribed to " + topic);
            /** Publish authentication */
            //publishMessage(CAMERA_APP_TOPIC, "oath;cameraApp");
        }

        @Override
        public void onSubscriptionError(Context context, String requestId, String topic, Exception exception) {
            Log.e(TAG, "Can't subscribe to " + topic, exception);
        }

        @Override
        public void onPublishSuccessful(Context context, String requestId, String topic) {
            Log.e(TAG, "Successfully published on topic: " + topic);
        }

        @Override
        public void onMessageArrived(Context context, String topic, byte[] payload) {
            //showToast(topic);
            Log.e(TAG, "New message on " + topic + ":  " + new String(payload));
        }

        @Override
        public void onConnectionSuccessful(Context context, String requestId) {
            showToast("Connected (CreatePatient.class)");
            Log.e(TAG, "Connected!");
        }

        @Override
        public void onException(Context context, String requestId, Exception exception) {
            exception.printStackTrace();
            Log.e(TAG, requestId + " exception");
        }

        @Override
        public void onConnectionStatus(Context context, boolean connected) {

        }
    };

    /** Showcase callbacks */
    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

    }

}
