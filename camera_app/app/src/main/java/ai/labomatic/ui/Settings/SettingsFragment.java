package ai.labomatic.ui.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.igenius.mqttservice.MQTTService;
import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.labomatic.R;
import ai.labomatic.data.local.ClientsDatabaseHandler;
import ai.labomatic.data.local.SettingsDatabaseHandler;
import ai.labomatic.data.local.UsersDatabaseHandler;
import ai.labomatic.data.model.Folder;
import ai.labomatic.data.model.Image;
import ai.labomatic.data.model.NameSettings;
import ai.labomatic.data.model.Setting;
import ai.labomatic.data.model.User;
import ai.labomatic.util.Initializer;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        View.OnClickListener{

    // Static strings
    private static final String TAG = "DatabaseManager:";

    // Dynamic variables
    public String selectedTable;

    // UI elements
    private Spinner tablesSpinner;
    private Spinner dataSpinner;
    private Button selectTableButton;
    private Button dropTableButton;

    // UI elements
    public Button applySettingsButton;
    public Button resetSettingsButton;
    public Button dropSettingsButton;
    public RadioGroup automaticSettingsRadioGroup;
    public RadioButton automaticYesRadioButton;
    public RadioButton automaticNoRadioButton;
    public RadioButton dummyRadioButton;
    public RadioGroup automaticUploadSettingsRadioGroup;
    public RadioButton automaticUploadYesRadioButton;
    public RadioButton automaticUploadNoRadioButton;
    public Spinner dataSpinnerSetttings;
    public EditText IPEditText;
    public Button applyIPButton;

    // Databases
    public ClientsDatabaseHandler clientsDB;
    public UsersDatabaseHandler usersDB;
    public SettingsDatabaseHandler settingsDB;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Database settings
        clientsDB = new ClientsDatabaseHandler(getActivity());
        usersDB = new UsersDatabaseHandler(getActivity());
        settingsDB = new SettingsDatabaseHandler(getActivity());

        // UI Elements
        // Database
        tablesSpinner = (Spinner) view.findViewById(R.id.tables_spinner);
        dataSpinner = (Spinner) view.findViewById(R.id.data_in_table_spinner);
        selectTableButton = (Button) view.findViewById(R.id.select_table_button);
        dropTableButton = (Button) view.findViewById(R.id.drop_table_button);
        loadTablesSpinner();

        // Settings
        automaticSettingsRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group_automatic_settings);
        resetSettingsButton = (Button) view.findViewById(R.id.reset_settings_button);
        applySettingsButton = (Button) view.findViewById(R.id.apply_settings_button);
        dropSettingsButton = (Button) view.findViewById(R.id.drop_settings_button);
        automaticYesRadioButton = (RadioButton) view.findViewById(R.id.enable_automatic_radio_button);
        automaticNoRadioButton = (RadioButton) view.findViewById(R.id.disable_automatic_radio_button);
        dataSpinnerSetttings = (Spinner) view.findViewById(R.id.data_spinner);
        automaticUploadSettingsRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group_automatic_upload_settings);
        automaticUploadYesRadioButton = (RadioButton) view.findViewById(R.id.enable_automatic_upload_radio_button);
        automaticUploadNoRadioButton = (RadioButton) view.findViewById(R.id.disable_automatic_upload_radio_button);
        applyIPButton = (Button) view.findViewById(R.id.apply_ip_button);
        IPEditText = (EditText) view.findViewById(R.id.ip_edit_text);

        // Set onClick listeners
        applySettingsButton.setOnClickListener(this);
        resetSettingsButton.setOnClickListener(this);
        dropSettingsButton.setOnClickListener(this);
        selectTableButton.setOnClickListener(this);
        dropTableButton.setOnClickListener(this);
        dataSpinner.setOnItemSelectedListener(this);
        tablesSpinner.setOnItemSelectedListener(this);
        applyIPButton.setOnClickListener(this);

        // Load initial states for UI elements
        Setting setting = settingsDB.readSettingByName(NameSettings.AUTOMATIC_START);
        if (setting.getValue() != null){
            if (setting.getValue().equals("1")){
                automaticYesRadioButton.setChecked(true);
                automaticNoRadioButton.setChecked(false);
            } else if (setting.getValue().equals("0")){
                automaticYesRadioButton.setChecked(false);
                automaticNoRadioButton.setChecked(true);
            }
        }
        setting = settingsDB.readSettingByName(NameSettings.AUTOMATIC_UPLOAD);
        if (setting.getValue() != null) {
            if (setting.getValue().equals("1")) {
                automaticUploadYesRadioButton.setChecked(true);
                automaticUploadNoRadioButton.setChecked(false);
            } else if (setting.getValue().equals("0")) {
                automaticUploadYesRadioButton.setChecked(false);
                automaticUploadNoRadioButton.setChecked(true);
            }
        }
        loadDataSpinner();
        // Load EditText
        Setting sett = settingsDB.readSettingByName(NameSettings.IP_ADDRESS_MQTT);
        Pattern p = Pattern.compile("[0-9]+.[0-9]+.[0-9]+.[0-9]+");
        Matcher m = p.matcher(sett.getValue());
        String foundIP = "";
        while (m.find()) {
            foundIP += m.group();
        }
        IPEditText.setText(foundIP);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void loadTablesSpinner(){
        List<String> list = new ArrayList<String>();
        list.add("None");
        list.add("folders");
        list.add("images");
        list.add("users");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tablesSpinner.setAdapter(dataAdapter);
    }

    public void loadDataSpinner(){
        // List to hold values
        List<String> list = new ArrayList<String>();
        // Read all settings
        List<Setting> allSettings = settingsDB.readAllSettings();
        for (Setting sett: allSettings){
            String toString = sett.getId() + "," + sett.getName() + "," + sett.getValue();
            list.add(toString);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataSpinnerSetttings.setAdapter(dataAdapter);
    }

    public void restartMQTTConnection(){
        // Read ip on database
        Setting setting = settingsDB.readSettingByName(NameSettings.IP_ADDRESS_MQTT);
        String IPAdress = setting.getValue();
        // MQTT configurations
        MQTTService.NAMESPACE = "com.example.android.labomatic";
        MQTTService.KEEP_ALIVE_INTERVAL = Initializer.KEEP_ALIVE_TIMING;
        MQTTService.CONNECT_TIMEOUT = Initializer.CONNECT_TIMEOUT;
        String username = "pfm";
        String password = "161154029";
        String clientId = UUID.randomUUID().toString();
        int qos = 2;
        MQTTServiceLogger.setLogLevel(MQTTServiceLogger.LogLevel.DEBUG);
        MQTTServiceCommand.connectAndSubscribe(getActivity().getApplicationContext(),
                IPAdress,
                clientId,
                username,
                password,
                qos,
                true,
                Initializer.CAMERA_APP_TOPIC);
        getActivity().finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if(spinner.getId() == R.id.tables_spinner) {
            selectedTable = adapterView.getItemAtPosition(i).toString();
            Log.i(TAG, "Selected table: " + selectedTable);
        } else if (spinner.getId() == R.id.data_in_table_spinner){

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.select_table_button: {
                // Load the ai.labomatic.data in the dataSpinner
                if (selectedTable.equals("None")){
                    List<String> list = new ArrayList<String>();
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    dataSpinner.setAdapter(dataAdapter);
                } else if (selectedTable.equals("folders")){
                    // Get the folders
                    List<Folder> folders = clientsDB.readAllFolders();
                    Log.i(TAG, "Amount of folders: " + String.valueOf(folders.size()));
                    // Load the ai.labomatic.data as a string
                    List<String> list = new ArrayList<String>();
                    // Add the ai.labomatic.data to the String list
                    for (Folder fold: folders){
                        String toString = String.valueOf(fold.getId())
                                + ", " + String.valueOf(fold.getfolderName());
                        list.add(toString);
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    dataSpinner.setAdapter(dataAdapter);
                } else if (selectedTable.equals("images")){
                    // Get the images
                    List <Image> imgs = clientsDB.readAllImages();
                    Log.i(TAG, "Amount of images: " + String.valueOf(imgs.size()));
                    // Load the ai.labomatic.data to a String list
                    List<String> list = new ArrayList<String>();
                    for (Image img: imgs){
                        String toString = img.getId() + "," + img.getImageName().split("ple")[0]
                                + "," + img.getFolderName();
                        list.add(toString);
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    dataSpinner.setAdapter(dataAdapter);
                } else if (selectedTable.equals("users")){
                    // Get the users
                    List<User> allUsers = usersDB.readAllUsers();
                    Log.i(TAG, "Amount of users: " + String.valueOf(allUsers.size()));
                    // Load users into a list
                    List<String> list = new ArrayList<String>();
                    for (User user: allUsers){
                        String toString = user.getId() + ","
                                + user.getEmail() + "," + user.getPassword();
                        list.add(toString);
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    dataSpinner.setAdapter(dataAdapter);
                } else{
                    // Do nothing
                }
                break;
            }
            case R.id.drop_table_button:{
                if (selectedTable.equals("images")){
                    clientsDB.dropImagesTable();
                } else if (selectedTable.equals("folders")){
                    clientsDB.dropFoldersTable();
                } else if (selectedTable.equals("users")){
                    // Get users
                    List<User> allUsers = usersDB.readAllUsers();
                    if (allUsers.size() == 0){
                        // Do nothing
                    } else{
                        User userr = allUsers.get(0);
                        // Remove user from firebase
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        // Get auth credentials from the user for re-authentication. The example below shows
                        // email and password credentials but there are multiple possible providers,
                        // such as GoogleAuthProvider or FacebookAuthProvider.
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(userr.getEmail(), userr.getPassword());

                        // Prompt the user to re-provide their sign-in credentials
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        user.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "User account deleted.");
                                                            usersDB.dropTableUsers();
                                                        } else{
                                                            Log.d(TAG,
                                                                    "User account not deleted.",
                                                                    task.getException());
                                                        }
                                                    }
                                                });

                                    }
                                });
                    }
                }else{
                    String columns[] = clientsDB.readColumnsImagesTable();
                    for (String col: columns){
                        showToast(col);
                    }
                }
                break;
            }
            case R.id.reset_settings_button:{
                settingsDB.dropTable();
                settingsDB.createSetting(new Setting(NameSettings.AUTOMATIC_START, "0"));
                settingsDB.createSetting(new Setting(NameSettings.AUTOMATIC_UPLOAD, "0"));
                settingsDB.createSetting(new Setting(NameSettings.IP_ADDRESS_MQTT,
                        "tcp://192.168.0.108:1883"));
                loadDataSpinner();
                break;
            }
            case R.id.apply_ip_button:{
                String newIP = IPEditText.getText().toString();
                newIP = "tcp://" + newIP + ":1883";
                Setting setting = settingsDB.readSettingByName(NameSettings.IP_ADDRESS_MQTT);
                setting.setValue(newIP);
                settingsDB.updateSetting(setting);
                restartMQTTConnection();
                break;
            }
            case R.id.apply_settings_button:{
                // Automatic upload settings
                int selectedId = automaticUploadSettingsRadioGroup.getCheckedRadioButtonId();
                dummyRadioButton = (RadioButton) view.findViewById(selectedId);
                if (dummyRadioButton.getText().toString().equals("Yes")){
                    Setting setting = settingsDB.readSettingByName(NameSettings.AUTOMATIC_UPLOAD);
                    setting.setValue("1");
                    int result = settingsDB.updateSetting(setting);
                } else{
                    Setting setting = settingsDB.readSettingByName(NameSettings.AUTOMATIC_UPLOAD);
                    setting.setValue("0");
                    int result = settingsDB.updateSetting(setting);
                }
                // Automatic settings
                selectedId = automaticSettingsRadioGroup.getCheckedRadioButtonId();
                dummyRadioButton = (RadioButton) view.findViewById(selectedId);
                // showToast(dummyRadioButton.getText().toString());
                if (dummyRadioButton.getText().toString().equals("Yes")){
                    // Read the setting
                    Setting sett = settingsDB.readSettingByName(NameSettings.AUTOMATIC_START);
                    sett.setValue("1");
                    // Rewrite the setting
                    int result = settingsDB.updateSetting(sett);
                    Setting sett1 = settingsDB.readSettingByName(NameSettings.AUTOMATIC_START);
//                    showToast(String.valueOf(result) + ", " + sett1.getConfigName() + ", " + sett1.getValue());
                } else{
                    // Read the setting
                    Setting sett = settingsDB.readSettingByName(NameSettings.AUTOMATIC_START);
                    sett.setValue("0");
                    // Rewrite the setting
                    int result = settingsDB.updateSetting(sett);
                    Setting sett1 = settingsDB.readSettingByName(NameSettings.AUTOMATIC_START);
//                    showToast(String.valueOf(result) + ", " + sett1.getConfigName() + ", " + sett1.getValue());
                }
                getActivity().finish();
                break;
            }
            case R.id.drop_settings_button:{
                settingsDB.dropTable();
                loadDataSpinner();
                break;
            }
        }
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
