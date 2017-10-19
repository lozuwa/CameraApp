package com.example.android.camera2basic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.igenius.mqttservice.MQTTService;
import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceLogger;
import net.igenius.mqttservice.MQTTServiceReceiver;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

/**
 * A login screen that offers login via email/password.
 */
public class CreatePatient extends Activity implements OnShowcaseEventListener {

    /**
     * MQTT Receiver
     * */
    private MQTTServiceReceiver receiver = new MQTTServiceReceiver() {

        private static final String TAG = "Receiver";

        @Override
        public void onSubscriptionSuccessful(Context context, String requestId, String topic) {
            Log.e(TAG, "Subscribed to " + topic);
            JsonObject request = new JsonObject();
            request.addProperty("question", "best time to post");
            request.addProperty("lang", "en");
            request.addProperty("request_uid", "testAndroid/" + new Date().getTime());
            byte[] payload = new Gson().toJson(request).getBytes();

            MQTTServiceCommand.publish(context, "/random_topic_with_no_intention", payload);
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
            showToast("Connected");
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

    /**
     * UI Shit
     * */
    private AutoCompleteTextView nameUserEditText;
    private Button automaticAnalisisButton;
    private Button manualAnalysisButton;

    /** Showcase */
    public ShowcaseView sv;
    public RelativeLayout.LayoutParams lps;
    public ViewTarget target;

    /** Constant variables */
    /* Broker */
    static public String BROKER = "tcp://192.168.0.105:1883";
    /** MQTT Topics */
    static public String AUTOFOCUS_APP_TOPIC = "/autofocusApp";
    static public String CAMERA_APP_TOPIC = "/cameraApp";
    static public String ZDOWN_TOPIC = "/zd";
    static public String ZUP_TOPIC = "/zu";
    static public String XRIGHT_TOPIC = "/xr";
    static public String XLEFT_TOPIC = "/xl";
    static public String YUP_TOPIC = "/yu";
    static public String YDOWN_TOPIC = "/yd";
    static public String EXTRA_ACTIONS_TOPIC = "/extra";

    /** Constructor */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Contents */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_patient);
        /** Instantiate UI elements */
        nameUserEditText = (AutoCompleteTextView) findViewById(R.id.patient_name_editText);
        automaticAnalisisButton = (Button) findViewById(R.id.automatic_button);
        manualAnalysisButton = (Button) findViewById(R.id.manual_button);
        /** Initial state UI */
        nameUserEditText.setError(null);
        /** Configure showcase */
        lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);
        target = new ViewTarget(R.id.automatic_button, this);
        /** Show first button */
        sv = new ShowcaseView.Builder(CreatePatient.this)
                .withMaterialShowcase()
                .setTarget(target)
                .setContentTitle("Prepare the sample")
                .setContentText("Press the button to take the stage out")
                .setStyle(R.style.CustomShowcaseTheme3)
                .setShowcaseEventListener(CreatePatient.this)
                .replaceEndButton(R.layout.view_custom_button)
                .build();
        sv.setButtonPosition(lps);
        /** Connect MQTT */
        String username = "pfm";
        String password = "161154029";
        String clientId = UUID.randomUUID().toString();
        int qos = 2;
        MQTTService.NAMESPACE = "com.example.android.camera2basic";
        MQTTServiceLogger.setLogLevel(MQTTServiceLogger.LogLevel.DEBUG);
        MQTTServiceCommand.connectAndSubscribe(CreatePatient.this,
                                                BROKER,
                                                clientId,
                                                username,
                                                password,
                                                qos,
                                                true,
                                                CAMERA_APP_TOPIC);
        attemptUserCreation();
        /** Button callbacks */
        automaticAnalisisButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Put arguments */
                //Bundle bundle = new Bundle();
                //bundle.putString("edttext", "From Activity");
                /** Publish folder name */
                final String NAME_FOLDER = nameUserEditText.getText().toString();
                publishMessage(CAMERA_APP_TOPIC, "createFolder;"+NAME_FOLDER);
                /** Start camera */
                Intent intent = new Intent(CreatePatient.this, PrepareAndLoadSample.class);
                startActivity(intent);
            }
        });

        manualAnalysisButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Put arguments */
                //Bundle bundle = new Bundle();
                //bundle.putString("edttext", "From Activity");
                /** Publish folder name */
                final String NAME_FOLDER = nameUserEditText.getText().toString();
                publishMessage(CAMERA_APP_TOPIC, "createFolder;"+NAME_FOLDER);
                /** Start camera */
                Intent intent = new Intent(CreatePatient.this, ControllerAndCamera.class);
                startActivity(intent);
            }
        });

    }

    /** Callback on resume */
    @Override
    protected void onResume(){
        super.onResume();
        receiver.register(this);
    }

    /** Callback on pause */
    @Override
    protected void onPause(){
        super.onPause();
        receiver.unregister(this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptUserCreation() {
        // Reset errors.
        nameUserEditText.setError(null);

        // Store values at the time of the login attempt.
        String patient = nameUserEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(patient) || (patient.length() < 4)) {
            nameUserEditText.setError(getString(R.string.error_field_required));
            focusView = nameUserEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            publishMessage("/cameraApp", "shit");
            //Intent intent = new Intent(CreatePatient.this, CameraActivity.class);
            //startActivity(intent);
        }
    }

    /**
     * Support function
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

    /****************************************Showcase callbacks***************************************************/
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
    /*************************************************************************************************************/

    /**
     SQLiteDatabase db = mDbHelper.getWritableDatabase();

     // New value for one column
     ContentValues values = new ContentValues();
     values.put(FeedEntry.COLUMN_NAME_TITLE, title);

     // Which row to update, based on the title
     String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
     String[] selectionArgs = { "MyTitle" };

     int count = db.update(
     FeedReaderDbHelper.FeedEntry.TABLE_NAME,
     values,
     selection,
     selectionArgs);
     **/
}

