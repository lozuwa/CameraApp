package com.example.android.camera2basic;

import android.app.Application;

import net.igenius.mqttservice.MQTTService;

/**
 * Created by HP on 18/10/2017.
 */

public class Initializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /** Initialize variables for MQTT Service */
        MQTTService.NAMESPACE = "com.example.android.camera2basic"; //or BuildConfig.APPLICATION_ID;
        MQTTService.KEEP_ALIVE_INTERVAL = 15; //in seconds
        MQTTService.CONNECT_TIMEOUT = 30; //in seconds
        /**MQTT Topics */
        String AUTOFOCUS_APP_TOPIC = "/autofocusApp";
        String CAMERA_APP_TOPIC = "/cameraApp";
        String ZDOWN_TOPIC = "/zd";
        String ZUP_TOPIC = "/zu";
        String XRIGHT_TOPIC = "/xr";
        String XLEFT_TOPIC = "/xl";
        String YUP_TOPIC = "/yu";
        String YDOWN_TOPIC = "/yd";
        String EXTRA_ACTIONS_TOPIC = "/extra";
    }
}