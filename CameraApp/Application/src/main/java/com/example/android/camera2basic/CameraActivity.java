/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.camera2basic;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class CameraActivity extends Activity {
    /** Variable */
    /** Instantiate fragment */
    Camera2BasicFragment fragment = new Camera2BasicFragment();
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    /** UI elements */
    public Button button;
    public EditText editText;
    public EditText editText1;
    public EditText editText2;

    /*** Constructor */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Contents */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        /** Orientation */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /** Keep screen on */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /** Instantiate UI elements */
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        /** Set on click listener */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Visibility */
                /*button.setVisibility(View.INVISIBLE);
                editText.setVisibility(View.INVISIBLE);
                editText1.setVisibility(View.INVISIBLE);*/
                /** Get folder name */
                String FOLDER = editText.getText().toString();
                /** Create folder */
                File folder = new File(getExternalFilesDir(null) + File.separator + FOLDER);
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {
                    showToast("Folder created " + getExternalFilesDir(null) + File.separator + FOLDER);
                }
                else {
                    showToast("Folder not created " + getExternalFilesDir(null) + File.separator + FOLDER);
                }
                /** Add message */
                Bundle bundle = new Bundle();
                bundle.putString("Folder", editText.getText().toString());
                bundle.putString("Broker", editText2.getText().toString());
                fragment.setArguments(bundle);
                /** Start fragment */
                transaction.replace(R.id.container, fragment);
                transaction.commit();
            }
        });
    }

    /*** Support function to display a message */
    public void showToast(String message){
        Toast.makeText(CameraActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
