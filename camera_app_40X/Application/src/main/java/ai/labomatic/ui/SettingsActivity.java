package ai.labomatic.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ai.labomatic.R;
import ai.labomatic.data.local.SettingsDatabaseHandler;
import ai.labomatic.data.model.NameSettings;
import ai.labomatic.data.model.Setting;

public class SettingsActivity extends Activity {

    // Static variables
    private static final String TAG = "SettingsActivity::";

    // UI elements
    public Button applySettingsButton;
    public Button resetSettingsButton;
    public Button dropSettingsButton;
    public RadioGroup automaticSettingsRadioGroup;
    public RadioButton dummyRadioButton;
    public RadioButton automaticYesRadioButton;
    public RadioButton automaticNoRadioButton;
    public Spinner dataSpinner;

    // Database
    public SettingsDatabaseHandler settingsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Database
        settingsDB = new SettingsDatabaseHandler(this);
        // UI elements
        automaticSettingsRadioGroup = (RadioGroup) findViewById(R.id.radio_group_automatic_settings);
        resetSettingsButton = (Button) findViewById(R.id.reset_settings_button);
        applySettingsButton = (Button) findViewById(R.id.apply_settings_button);
        dropSettingsButton = (Button) findViewById(R.id.drop_settings_button);
        automaticYesRadioButton = (RadioButton) findViewById(R.id.enable_automatic_radio_button);
        automaticNoRadioButton = (RadioButton) findViewById(R.id.disable_automatic_radio_button);
        dataSpinner = (Spinner) findViewById(R.id.data_spinner);
        // Load initial states for UI elements
        Setting settAut = settingsDB.readSettingByName(NameSettings.AUTOMATIC_START);
        if (settAut.getValue().equals("1")){
            automaticYesRadioButton.setChecked(true);
            automaticNoRadioButton.setChecked(false);
        } else if (settAut.getValue().equals("0")){
            automaticYesRadioButton.setChecked(false);
            automaticNoRadioButton.setChecked(true);
        }
        loadDataSpinner();
        // Click listeners
        applySettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Automatic settings
                int selectedId = automaticSettingsRadioGroup.getCheckedRadioButtonId();
                dummyRadioButton = (RadioButton) findViewById(selectedId);
                // showToast(dummyRadioButton.getText().toString());
                if (dummyRadioButton.getText().toString().equals("Yes")){
                    // Read the setting
                    Setting sett = settingsDB.readSettingByName(NameSettings.AUTOMATIC_START);
                    sett.setValue("1");
                    // Rewrite the setting
                    int result = settingsDB.updateSetting(sett);
                    Setting sett1 = settingsDB.readSettingByName(NameSettings.AUTOMATIC_START);
//                    showToast(String.valueOf(result) + ", " + sett1.getConfigName() + ", " + sett1.getValue());
                    finish();
                } else{
                    // Read the setting
                    Setting sett = settingsDB.readSettingByName(NameSettings.AUTOMATIC_START);
                    sett.setValue("0");
                    // Rewrite the setting
                    int result = settingsDB.updateSetting(sett);
                    Setting sett1 = settingsDB.readSettingByName(NameSettings.AUTOMATIC_START);
//                    showToast(String.valueOf(result) + ", " + sett1.getConfigName() + ", " + sett1.getValue());
                    finish();
                }
            }
        });
        resetSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDB.dropTable();
                settingsDB.createConfiguration(new Setting(NameSettings.AUTOMATIC_START, "0"));
                loadDataSpinner();
            }
        });
        dropSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDB.dropTable();
                loadDataSpinner();
            }
        });
    }

    public void loadDataSpinner(){
        // List to hold values
        List<String> list = new ArrayList<String>();
        // Read all settings
        List<Setting> allSettings = settingsDB.readAllSettings();
        for (Setting sett: allSettings){
            String toString = sett.getId() + "," + sett.getConfigName() + "," + sett.getValue();
            list.add(toString);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SettingsActivity.this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataSpinner.setAdapter(dataAdapter);
    }

    public void showToast(String message){
        Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
