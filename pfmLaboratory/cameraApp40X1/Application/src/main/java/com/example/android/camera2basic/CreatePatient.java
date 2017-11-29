package com.example.android.camera2basic;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StatFs;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
     * Debug tag
     * */
    static public String TAG = "cameraApp::";

    /**
     * UI elements
     * */
    private AutoCompleteTextView nameUserEditText;
    private Button automaticAnalisisButton;
    private Button manualAnalysisButton;
    public TextView availableSizeTextView;

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

    /**
     * Threads
     * */
    public HandlerThread mBackgroundThread;
    public Handler mBackgroundHandler;
    public Runnable myRunnable;

    /**
     * Reset UI variable to assure MTTQ connection
     * */
    public boolean handshakeWithListener = false;

    /** Constant variables */
    /**
     * Permission statements
     * */
    public int PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    public int PERMISSION_CAMERA = 2;

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
        /** Keep screen on */
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /** Ask for permissions */
        grantPermissionExternalStorage();
        grantPermissionCamera();
        /** Open database */
        mydatabase = openOrCreateDatabase(DbFeed.TABLE_NAME, MODE_PRIVATE, null);
        initializeDb();
        /** Always restart home (XY), when reaching this activity */
        publishMessage(Initializer.MACROS_TOPIC, Initializer.STAGE_RESTART_HOME);
        /** Instantiate UI elements */
        nameUserEditText = (AutoCompleteTextView) findViewById(R.id.patient_name_editText);
        automaticAnalisisButton = (Button) findViewById(R.id.automatic_button);
        manualAnalysisButton = (Button) findViewById(R.id.manual_button);
        availableSizeTextView = (TextView) findViewById(R.id.available_size_textView);
        /** Instantiatte storage size class */
        //availableSizeTextView.setText(String.valueOf(55));
        /** Initial state UI */
        nameUserEditText.setError(null);
        /** Button callbacks */
        manualAnalysisButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final String NAME_FOLDER = nameUserEditText.getText().toString();
                createFolder(NAME_FOLDER);
                /** Once sample is prepared and locked, set to initial position */
                //publishMessage(Initializer.MACROS_TOPIC, Initializer.STAGE_RESTART_INITIAL);
                /** Get folder name */
                //final String NAME_FOLDER = nameUserEditText.getText().toString();
                //if (NAME_FOLDER.isEmpty() || (NAME_FOLDER.length() < 4)) {
                //   showToast("Name is too short");
                //} else {
                //   createFolder(NAME_FOLDER);
                //   /** Start camera */
                //   Intent intent = new Intent(CreatePatient.this, ControllerAndCamera.class);
                //   startActivity(intent);
                //}
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
                    /*Intent intent = new Intent(CreatePatient.this, CameraActivity.class);
                    startActivity(intent);*/
                    /** Start autofocus servide */
                    publishMessage(Initializer.CAMERA_APP_TOPIC, Initializer.REQUEST_SERVICE_AUTOFOCUS_AUTOMATIC);
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName("pfm.improccameraautofocus", "pfm.improccameraautofocus.AutofocusActivity"));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver.register(this);
        //startBackgroundThread();
    }

    @Override
    protected void onPause(){
        super.onPause();
        receiver.unregister(this);
        //stopBackgroundThread();
    }

    @Override
    public void onBackPressed() {
        /** Back operation is not allowed */
    }

    /**
     * Support functions
     * */

    /**
     * Grants permission to WRITE_EXTERNAL_STORAGE
     * */
    public void grantPermissionExternalStorage(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    /**
     * Grants permission to CAMERA
     * */
    public void grantPermissionCamera(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Write external permission granted");
                } else {
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Camera permission granted");
                } else {
                }
                return;
            }
        }
    }

    /**
     * Checks if there is an external sd card
     * */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Creates a physical folder folder in the apps's directory
     * @param FOLDER_NAME: input string that contains the name of the folder to be created
     * */
    public void createFolder(String FOLDER_NAME){
        /** Write folder name into db table */
        mydatabase.execSQL("INSERT INTO " + DbFeed.TABLE_NAME + " VALUES('" + FOLDER_NAME + "');");
        /** Create folder */
        //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + FOLDER_NAME;
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + FOLDER_NAME;
        File folder = new File(path);
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
        final int qos = 2;
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = message.getBytes("UTF-8");
            MQTTServiceCommand.publish(CreatePatient.this, topic, encodedPayload, qos);
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
            showToast(topic + "::" + new String(payload));
            Log.e(TAG, "New message on " + topic + ":  " + new String(payload));
            /** Parse string */
            String[] paramsPayload = decodeMessage(new String(payload));
            String command = paramsPayload[0];
            String target = paramsPayload[1];
            String action = paramsPayload[2];
            String specific = paramsPayload[3];
            String message = paramsPayload[4];
            /** If listener responds */
            if (command.equals("listener") && target.equals("handshake") && action.equals("cameraApp") && specific.equals("40X")){
                automaticAnalisisButton.setEnabled(true);
                manualAnalysisButton.setEnabled(true);
                handshakeWithListener = true;
            }
            else {

            }
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

    /** Unrolls a message
     * @param pattern: input String
     * return: a String vector with the message splitted
     * */
    public String[] decodeMessage(String pattern){
        String[] messages = pattern.split(";");
        return messages;
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    public void startBackgroundThread() {
        /** Camera Thread */
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        myRunnable = new Runnable() {
            @Override
            public void run() {
                handshakeWithListener = false;
                automaticAnalisisButton.setEnabled(false);
                manualAnalysisButton.setEnabled(false);
                showToast("Handshake");
                publishMessage(Initializer.CAMERA_APP_TOPIC, Initializer.HANDSHAKE_WITH_LISTENER);
                mBackgroundHandler.postDelayed(myRunnable, 10000);
            }
        };
        mBackgroundHandler.post(myRunnable);
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    public void stopBackgroundThread() {
        /** Camera Thread */
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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
