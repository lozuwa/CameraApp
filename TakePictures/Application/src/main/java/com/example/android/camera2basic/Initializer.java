package com.example.android.camera2basic;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
 * Subclass that initializes the mqtt service
 */

public class Initializer extends Application {
    /**
     * Broker
     * */
    static public String BROKER = "tcp://192.168.0.103:1883";

    /**
     * MQTT Topics
     * */
    static public String AUTOFOCUS_APP_TOPIC = "/autofocusApp";
    static public String CAMERA_APP_TOPIC = "/cameraApp";
    static public String EXTRA_ACTIONS_TOPIC = "/extra";

    /**
     * Static variables
     * */
    public static int KEEP_ALIVE_TIMING = 15;
    public static int CONNECT_TIMEOUT = 60;

    /**
     * Constructor
     * */
    @Override
    public void onCreate() {
        super.onCreate();
        /** Initialize variables for MQTT Service */
        MQTTService.NAMESPACE = "com.example.android.camera2basic";
        MQTTService.KEEP_ALIVE_INTERVAL = KEEP_ALIVE_TIMING;
        MQTTService.CONNECT_TIMEOUT = CONNECT_TIMEOUT;
        /** Connect to server */
        /** Connect MQTT */
        String username = "pfm";
        String password = "161154029";
        String clientId = UUID.randomUUID().toString();
        int qos = 2;
        MQTTServiceLogger.setLogLevel(MQTTServiceLogger.LogLevel.DEBUG);
        MQTTServiceCommand.connectAndSubscribe(Initializer.this,
                                                BROKER,
                                                clientId,
                                                username,
                                                password,
                                                qos,
                                                true,
                                                CAMERA_APP_TOPIC);
    }

    /**
     * MQTT Receiver
     * */
    private MQTTServiceReceiver receiver = new MQTTServiceReceiver() {

        private static final String TAG = "Receiver";

        @Override
        public void onSubscriptionSuccessful(Context context, String requestId, String topic) {
            /** Info */
            Log.i(TAG, "Subscribed to " + topic);
            /** Authenticate connection */
            publishMessage(CAMERA_APP_TOPIC, "oath;cameraApp");
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
            /** Info */
            //showToast(topic);
            Log.i(TAG, "New message on " + topic + ":  " + new String(payload));
            /** Incoming messages */
            final String message = new String(payload);
            String[] messages = message.split(";");
            String command = messages[0];
            String action = messages[1];
            if (command.equals("autofocusApp")){
                if (action.equals("start")){
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName("pfm.improccameraautofocus", "pfm.improccameraautofocus.MainActivity"));
                    startActivity(intent);
                }
            }
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
            Log.i(TAG, "Connection statis is " + String.valueOf(connected));
        }
    };

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
            MQTTServiceCommand.publish(Initializer.this, topic, encodedPayload, qos);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}