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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class CameraActivity extends Activity {
    /** Variable */
    /** Instantiate fragment */
    Camera2BasicFragment fragment = new Camera2BasicFragment();
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    /** UI elements */
    public ProgressBar progressBar;
    /** Constant variables */
    public String FOLDER;
    public String BROKER;

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
        /** Initialize UI components */
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        /** Set values */
        FOLDER = "a";
        BROKER = "192.168.0.104";

        /** Add message */
        Bundle bundle = new Bundle();
        bundle.putString("Folder", FOLDER);
        bundle.putString("Broker", BROKER);
        fragment.setArguments(bundle);
        /** Start fragment */
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    /*** Support function to display a message */
    public void showToast(String message){
        Toast.makeText(CameraActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
