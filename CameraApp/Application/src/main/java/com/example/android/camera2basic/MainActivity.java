package com.example.android.camera2basic;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener {
    /*** Input activity that defines the name of a patient and creates a folder. It
     * also defines the name of the broker to connect to.
     * */

    /** UI Elements */
    public EditText nameEditText;
    public EditText brokerEditText;
    public Button connectButton;
    public Button guestButton;
    public ProgressBar progressBar;
    /** Strings */
    public String name;
    public String broker;
    /** Constants */
    public String TAG = "CAMERA:APP";

    /*** Constructor */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Contents */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /** UI Elements */
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        brokerEditText = (EditText) findViewById(R.id.brokerEditText);
        connectButton = (Button) findViewById(R.id.connectButton);
        guestButton = (Button) findViewById(R.id.guestButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        /** Callbacks UIs */
        connectButton.setOnClickListener(this);
    }

    /*** UI callbacks */
    @Override
    public void onClick(View v) {
        if (v.equals(connectButton)) {
            /** Get name and broker */
            name = nameEditText.getText().toString();
            broker = brokerEditText.getText().toString();
            /** Create folder and change activity */
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressBar.setVisibility(View.VISIBLE);
                }
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                protected Void doInBackground(Void... params) {
                    for (int i = 0; i <= 1; i++){
                        SystemClock.sleep(500);
                    }
                    /** Create folder */
                    createFolder(name);
                    /** Move to another activity */
                    Intent intent = new Intent(getBaseContext(), CameraActivity.class);
                    intent.putExtra("Name", name);
                    intent.putExtra("Broker", broker);
                    startActivity(intent);
                    return null;
                }
            }.execute();
        }
        else if (v.equals(guestButton)) {
            /** Get name and broker */
            name = "nothing";//nameEditText.getText().toString();
            broker = brokerEditText.getText().toString();
            /** Move to another activity */
            Intent intent = new Intent(getBaseContext(), CameraActivity.class);
            intent.putExtra("Name", name);
            intent.putExtra("Broker", broker);
            startActivity(intent);
        }
        else {
            //continue;
        }
    }

    /*** Support funtions */
    public void createFolder(String nameFolder) {
        /*** Creates a folder
         * @param String folder: String containing the name of the folder to be created
         */
        File folder = new File(getExternalFilesDir(null) + File.separator + nameFolder);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            Log.i(TAG, "Folder created " + getExternalFilesDir(null) + File.separator + name);
            //showToast("Folder created " + getExternalFilesDir(null) + File.separator + name);
        }
        else {
            Log.e(TAG, "Folder created " + getExternalFilesDir(null) + File.separator + name);
            //showToast("Folder not created " + getExternalFilesDir(null) + File.separator + name);
        }
    }

    public void showToast(String message) {
        /*** Displays a message as a toast
         * @param String message: String containing a message
         * @return: nothing
         * */
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}

