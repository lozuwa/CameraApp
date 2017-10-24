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
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import net.igenius.mqttservice.MQTTServiceCommand;

import java.io.UnsupportedEncodingException;

public class CameraActivity extends Activity {

    public SQLiteDatabase mydatabase;

    /** Constructors */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Contents */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        /** Instantiate db */
        mydatabase = openOrCreateDatabase(DbFeed.TABLE_NAME, MODE_PRIVATE, null);
        /** Keep screen on */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /** Start fragment */
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                                .replace(R.id.container, Camera2BasicFragment.newInstance())
                                .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //receiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //receiver.unregister(this);
    }

    @Override
    public void onBackPressed() {
        mydatabase.execSQL("DROP TABLE IF EXISTS " + DbFeed.TABLE_NAME);
        Intent intent = new Intent(CameraActivity.this, CreatePatient.class);
        startActivity(intent);
        /*int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }*/
    }

    /** Support methods */
    /**
     * Support function
     * @param message: input string that defines the message to be displayed
     * */
    public void showToast(String message) {
        Toast.makeText(CameraActivity.this, message, Toast.LENGTH_SHORT).show();
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
            MQTTServiceCommand.publish(CameraActivity.this, topic, encodedPayload, 2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
