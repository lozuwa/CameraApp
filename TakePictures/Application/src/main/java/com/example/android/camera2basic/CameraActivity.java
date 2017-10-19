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
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import net.igenius.mqttservice.MQTTService;
import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceLogger;
import net.igenius.mqttservice.MQTTServiceReceiver;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class CameraActivity extends Activity {

    /**
     * MQTT Receiver
     * */
    private MQTTServiceReceiver receiver = new MQTTServiceReceiver() {

        private static final String TAG = "Receiver";

        @Override
        public void onSubscriptionSuccessful(Context context, String requestId, String topic) {
            publishMessage("/authenticate", "oath;cameraApp");
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


    /** Constructors */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Contents */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        /** Keep screen on */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /** Reconnect MQTT */
        String username = "pfm";
        String password = "161154029";
        String clientId = UUID.randomUUID().toString();
        int qos = 2;
        MQTTService.NAMESPACE = "com.example.android.camera2basic";
        MQTTServiceLogger.setLogLevel(MQTTServiceLogger.LogLevel.DEBUG);
        MQTTServiceCommand.connectAndSubscribe(CameraActivity.this,
                                                CreatePatient.BROKER,
                                                clientId,
                                                username,
                                                password,
                                                qos,
                                                true,
                                                CreatePatient.CAMERA_APP_TOPIC);
        /** Start fragment */
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                                .replace(R.id.container, Camera2BasicFragment.newInstance())
                                .commit();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        receiver.register(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        receiver.unregister(this);
    }

    /** Support methods */
    /**
     * Support function
     * @param message: input string that defines the message to be displayed
     * */
    public void showToast(String message){
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
