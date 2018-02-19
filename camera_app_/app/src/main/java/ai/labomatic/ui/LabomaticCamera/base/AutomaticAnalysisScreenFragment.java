package ai.labomatic.ui.LabomaticCamera.base;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceReceiver;

import java.io.UnsupportedEncodingException;

import ai.labomatic.R;
import ai.labomatic.data.local.ClientsDatabaseHandler;
import ai.labomatic.data.local.SettingsDatabaseHandler;
import ai.labomatic.data.model.Folder;
import ai.labomatic.data.model.Setting;
import ai.labomatic.ui.BarcodeReader.BarcodeCaptureActivity;
import ai.labomatic.util.Initializer;
import ai.labomatic.util.LabomaticCamera.FileUtils;

/**
 * Automatic analysis fragment
 * */
public class AutomaticAnalysisScreenFragment extends Fragment
        implements View.OnClickListener{

    // TAG
    public static final String TAG = "AutAnalysisFragment:";

    // Databases
    public ClientsDatabaseHandler clientsDB;
    public SettingsDatabaseHandler settingsDB;

    // UI Elements
    private EditText nameUserEditText;
    private Button automaticAnalisisButton;
    public Button readQRCodeButton;

    // State communication variable
    public boolean handshakeWithListener = false;

    // Global variables
    String folderName = null;

    public AutomaticAnalysisScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_automatic_analysis_screen, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Databases
        clientsDB = new ClientsDatabaseHandler(getActivity().getApplicationContext());
        settingsDB = new SettingsDatabaseHandler(getActivity().getApplicationContext());
        // UI elements
        nameUserEditText = (EditText) view.findViewById(R.id.id_edit_text);
        automaticAnalisisButton = (Button) view.findViewById(R.id.start_automatic_button);
        readQRCodeButton = (Button) view.findViewById(R.id.read_qr_button);
        // Click listeners
        view.findViewById(R.id.read_qr_button).setOnClickListener(this);
        view.findViewById(R.id.start_automatic_button).setOnClickListener(this);
        // Initial states
        nameUserEditText.setText("");
        automaticAnalisisButton.setEnabled(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        receiver.register(getActivity());
    }

    @Override
    public void onPause(){
        super.onPause();
        receiver.unregister(getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Pipeline to start the automatic analysis
     * */
    public void startAutomaticAnalysis(){
        // Once the sample is prepared and locked, set to initial position
        publishMessage(Initializer.MACROS_TOPIC, Initializer.STAGE_RESTART_INITIAL);
        // Get folder name
        final String folderName = nameUserEditText.getText().toString();
        boolean result = FileUtils.validateFolderName(folderName);
        if (result) {
            // Create folder
            result = FileUtils.createFolder(folderName);
            if (result){
                // Create folder in folders table
                Folder folderInstance = new Folder(folderName);
                clientsDB.createFolder(folderInstance);
                // If there is one, remove the folder
                settingsDB.deleteSettingByName("tmpFolderName");
                // Save in temp setting the name of the folder
                settingsDB.createSetting(new Setting("tmpFolderName",folderName));
                // Start autofocus service
                publishMessage(Initializer.CAMERA_APP_TOPIC,
                        Initializer.REQUEST_SERVICE_AUTOFOCUS_AUTOMATIC);
                // Start autofocus activity
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.example.root.autofocus_app",
                        "com.example.root.autofocus_app.AutofocusActivity"));
                startActivity(intent);
            } else {
                showToast("Folder was not created. Try again.");
            }
        } else {
            // Do nothing
        }
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
            MQTTServiceCommand.publish(getActivity(), topic, encodedPayload, qos);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * MQTT Receiver
     * */
    private MQTTServiceReceiver receiver =
            new MQTTServiceReceiver() {
        private static final String TAG = "Receiver";
        @Override
        public void onSubscriptionSuccessful(Context context, String requestId, String topic) {
            Log.i(TAG, "Subscribed to " + topic);
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
                // Start barcode activity
                Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
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

    /**
     * Click listener
     * */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.read_qr_button: {
                // Make sure listener is working, then open barcode reader activity
                publishMessage(Initializer.CAMERA_APP_TOPIC,
                        Initializer.HANDSHAKE_WITH_LISTENER);
                break;
            }
            case R.id.start_automatic_button: {
                startAutomaticAnalysis();
                break;
            }
        }
    }

    /** Unrolls a message
     * @param pattern: input String
     * return: a String vector with the message splitted
     * */
    public String[] decodeMessage(String pattern){
        String[] messages = pattern.split(";");
        return messages;
    }

    /**
     * Shows a {@link Toast} on the UI thread.
     * @param text The message to show
     */
    private void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
