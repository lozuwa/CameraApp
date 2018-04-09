package ai.labomatic.ui.LabomaticCamera.composer;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.util.Log;

import net.igenius.mqttservice.MQTTServiceReceiver;

import ai.labomatic.data.local.ClientsDatabaseHandler;
import ai.labomatic.data.local.SettingsDatabaseHandler;
import ai.labomatic.data.model.Setting;

/**
 * Presenter for the automatic analysis screen.
 */
public class AutomaticAnalysisFragmentPresenter extends Application {
    // TAG
    public static final String TAG = "AutoAnFragPresenter";

    // Databases
    public ClientsDatabaseHandler clientsDB;
    public SettingsDatabaseHandler settingsDB;

    // Folder to use in this mode
    public String folderName = null;

    // Constructor
    public AutomaticAnalysisFragmentPresenter(){
        // Initialize databases
        clientsDB = new ClientsDatabaseHandler(this);
        settingsDB = new SettingsDatabaseHandler(this);
        // Read designated folder
        folderName = readFolderFromDB();
        if (folderName == null){
            Log.i(TAG, "Folder name does not exist");
            // TODO: call finish activity method in fragment
        }
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        super.registerComponentCallbacks(callback);
        receiver.register(this);
    }

    @Override
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        super.unregisterActivityLifecycleCallbacks(callback);
        receiver.unregister(this);
    }

    public String readFolderFromDB(){
        Setting setting = settingsDB.readSettingByName("tmpFolderName");
        if (setting.getName() != null){
            return setting.getName();
        } else{
            return null;
        }
    }

    private MQTTServiceReceiver receiver = new MQTTServiceReceiver() {

        @Override
        public void onSubscriptionSuccessful(Context context, String requestId, String topic) {
            Log.i(TAG, "MQTT connection has been successful");
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
            // Feedback
            Log.i(TAG, "New message on " + topic + ": " + new String(payload));
        }

        @Override
        public void onConnectionSuccessful(Context context, String requestId) {
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

}
