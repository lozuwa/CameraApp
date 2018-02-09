package com.example.android.camera2basic.UI;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.camera2basic.Camera.ControllerAndCamera;
import com.example.android.camera2basic.Databases.Clients.ClientsDatabaseHandler;
import com.example.android.camera2basic.Databases.Clients.Folders;
import com.example.android.camera2basic.Databases.Users.User;
import com.example.android.camera2basic.Initializer;
import com.example.android.camera2basic.R;
import com.example.android.camera2basic.Utils.MQTTUtils;

import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceReceiver;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class CreatePatient extends Activity {

    /**
     * Debug tag
     * */
    static public String TAG = "CreatePatientActivity ::";

    /**
     * UI elements
     * */
    private AutoCompleteTextView nameUserEditText;
    private Button automaticAnalisisButton;
    private Button manualAnalysisButton;
    public Button readQRCodeButton;
    public Button dbManagerButton;

    /**
     * SQLite
     * */
    public SQLiteDatabase mydatabase;
    public ClientsDatabaseHandler clientsDB;

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
    public int PERMISSION_READ_EXTERNAL_STORAGE = 3;

    /**
     * Constructors
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_patient);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Ask for permissions
        grantPermissions();
        // Open auxiliar db
        mydatabase = openOrCreateDatabase(User.TABLE_NAME, MODE_PRIVATE, null);
        initializeDb();
        // Create an instance of ClientsDB
        clientsDB = new ClientsDatabaseHandler(this);
        // Extract from bundle
        final String EXTRACTED_FOLDER_NAME = getIntent().getStringExtra("FOLDER");
        final String AUTOMATIC = getIntent().getStringExtra("AUTOMATIC");
        Log.i(TAG, "Extracted folder name: " + EXTRACTED_FOLDER_NAME);
        Log.i(TAG, "Automatic? " + AUTOMATIC);
        // showToast(EXTRACTED_FOLDER_NAME + ", " + AUTOMATIC);
        // Instantiate UI elements
        nameUserEditText = (AutoCompleteTextView) findViewById(R.id.patient_name_editText);
        automaticAnalisisButton = (Button) findViewById(R.id.automatic_button);
        manualAnalysisButton = (Button) findViewById(R.id.manual_button);
        readQRCodeButton = (Button) findViewById(R.id.read_qr_button);
        dbManagerButton = (Button) findViewById(R.id.db_manager_button);
        // Initial states
        if (EXTRACTED_FOLDER_NAME == null){
            nameUserEditText.setText("");
            automaticAnalisisButton.setEnabled(false);
            manualAnalysisButton.setEnabled(false);
        } else{
            nameUserEditText.setText("muestra"+EXTRACTED_FOLDER_NAME);
            automaticAnalisisButton.setEnabled(true);
            manualAnalysisButton.setEnabled(true);
            if (AUTOMATIC == null){
                //do nothing
            } else if (AUTOMATIC.equals("1")) {
                // Once sample is prepared and locked, set to initial position
                publishMessage(Initializer.MACROS_TOPIC, Initializer.STAGE_RESTART_INITIAL);
                // Get folder name
                final String NAME_FOLDER = nameUserEditText.getText().toString();
                if (NAME_FOLDER.isEmpty() || (NAME_FOLDER.length() < 4)) {
                    showToast("Name is too short");
                } else {
                    createFolder(NAME_FOLDER);
                    // Start autofocus activity
                    publishMessage(Initializer.CAMERA_APP_TOPIC, Initializer.REQUEST_SERVICE_AUTOFOCUS_AUTOMATIC);
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName("com.example.root.autofocus_app", "com.example.root.autofocus_app.AutofocusActivity"));
                    startActivity(intent);
                }
            }
        }

        // Button callbacks
        readQRCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make sure listener is working
                publishMessage(Initializer.CAMERA_APP_TOPIC, Initializer.HANDSHAKE_WITH_LISTENER);
            }
        });

        manualAnalysisButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Once the sample is prepared and locked, set to initial position
                publishMessage(Initializer.MACROS_TOPIC, Initializer.STAGE_RESTART_INITIAL);
                // Get folder name
                final String NAME_FOLDER = nameUserEditText.getText().toString();
                if (NAME_FOLDER.isEmpty() || (NAME_FOLDER.length() < 4)) {
                   showToast("Name is too short");
                } else {
                    // Create folder
                   createFolder(NAME_FOLDER);
                   // Close db connection
                    mydatabase.close();
                   // Start camera
                   Intent intent = new Intent(CreatePatient.this, ControllerAndCamera.class);
                   startActivity(intent);
                }
            }
        });

        automaticAnalisisButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Once the sample is prepared and locked, set to initial position
                publishMessage(Initializer.MACROS_TOPIC, Initializer.STAGE_RESTART_INITIAL);
                // Get folder name
                final String NAME_FOLDER = nameUserEditText.getText().toString();
                if (NAME_FOLDER.isEmpty() || (NAME_FOLDER.length() < 4)) {
                    showToast("Name is too short");
                } else {
                    // Create folder
                    createFolder(NAME_FOLDER);
                    // Close db connection
                    mydatabase.close();
                    // Start autofocus service
                    publishMessage(Initializer.CAMERA_APP_TOPIC, Initializer.REQUEST_SERVICE_AUTOFOCUS_AUTOMATIC);
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName("com.example.root.autofocus_app", "com.example.root.autofocus_app.AutofocusActivity"));
                    startActivity(intent);
                }
            }
        });

        dbManagerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent DBManagerActivity = new Intent(CreatePatient.this, DBManager.class);
                startActivity(DBManagerActivity);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver.register(this);
    }

    @Override
    protected void onPause() {
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
     * Grants permissions to WRITE_EXTERNAL_STORAGE and CAMERA
     * */
    public void grantPermissions(){
        // Permission for External Storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            }
        }
        // Permission for Internal Storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
            }
        }
        // Permission for camera
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
                    showToast("Write external storage permission granted");
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
            case 3: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Read external storage permission granted");
                } else {
                }
                return;
            }
        }
    }

    /**
     * Creates a physical folder folder in the apps's directory
     * @param FOLDER_NAME: input string that contains the name of the folder to be created
     * */
    public void createFolder(String FOLDER_NAME) {
        // Write folder name into db table
        mydatabase.execSQL("INSERT INTO " + User.TABLE_NAME + " VALUES('" + FOLDER_NAME + "');");
        // Create folder
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + FOLDER_NAME;
        File folder = new File(path);
        boolean success = false;
        if (!folder.exists()) {
            success = folder.mkdir();
        } else {
            success = true;
            //showToast("Folder already exists " + FOLDER_NAME);
        }
        if (success) {
            showToast("Folder successfully created :: " + folder.getAbsolutePath());
            // Create folder in folders table
            Folders folderInstance = new Folders(folder.getName());
            clientsDB.createFolder(folderInstance);
        } else {
            showToast("Folder was not created, something happened :: " + path);
            Log.e("CreatePatient", "Folder not created");
        }
    }

    /**
     * First drops the table info just in case any unexpected crashes have happened, then creates a clean table
     * */
    public void initializeDb() {
        // Clean table
        mydatabase.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
        // Create table again
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " + User.TABLE_NAME + "(" + User.COLUMN_NAME_TITLE + " VARCHAR);");
    }

     /**
     * Shows a specific message in a toast
     * @param message: input string that defines the message to be displayed
     * */
    public void showToast(String message) {
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
            // Info
            Log.i(TAG, "Subscribed to " + topic);
            // Publish authentication
            //publishMessage(CAMERA_APP_TOPIC, "oath;cameraApp");
        }

        @Override
        public void onSubscriptionError(Context context, String requestId, String topic, Exception exception) {
            Log.i(TAG, "Can't subscribe to " + topic, exception);
        }

        @Override
        public void onPublishSuccessful(Context context, String requestId, String topic) {
            Log.i(TAG, "Successfully published on topic: " + topic);
        }

        @Override
        public void onMessageArrived(Context context, String topic, byte[] payload) {
//            showToast(topic + "::" + new String(payload));
            Log.i(TAG, "New message on " + topic + ":  " + new String(payload));
            // Parse string
            String[] paramsPayload = decodeMessage(new String(payload));
            String command = paramsPayload[0];
            String target = paramsPayload[1];
            String action = paramsPayload[2];
            String specific = paramsPayload[3];
            String message = paramsPayload[4];
            // Hardware response
            if (command.equals("listener") && target.equals("handshake") && action.equals("cameraApp") && specific.equals("40X")){
                // If the handshake is authenticated, then we can continue.
                handshakeWithListener = true;
                // Always restart home (XY), when opening QR activity.
                publishMessage(Initializer.MACROS_TOPIC, Initializer.STAGE_RESTART_HOME);
                // Start barcode acticity
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.google.android.gms.samples.vision.barcodereader", "com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity"));
                startActivity(intent);
            } else {

            }
        }

        @Override
        public void onConnectionSuccessful(Context context, String requestId) {
            showToast("Connected");
            Log.i(TAG, "Connected!");
        }

        @Override
        public void onException(Context context, String requestId, Exception exception) {
            exception.printStackTrace();
            Log.i(TAG, requestId + " exception");
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

}
