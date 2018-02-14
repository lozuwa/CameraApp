package com.example.android.labomatic.Utils;

import android.app.Application;

import com.example.android.labomatic.Initializer;

import net.igenius.mqttservice.MQTTService;
import net.igenius.mqttservice.MQTTServiceCommand;
import net.igenius.mqttservice.MQTTServiceLogger;

import java.util.UUID;

/**
 * Create an instance of this class everytime
 */

public class MQTTUtils extends Application{

    @Override
    public void onCreate(){
        super.onCreate();
        // MQTT stuff
        // Initialize variables for MQTT Service
        MQTTService.NAMESPACE = "com.example.android.labomatic";
        MQTTService.KEEP_ALIVE_INTERVAL = Initializer.KEEP_ALIVE_TIMING;
        MQTTService.CONNECT_TIMEOUT = Initializer.CONNECT_TIMEOUT;
        // Connect MQTT
        String username = "pfm";
        String password = "161154029";
        String clientId = UUID.randomUUID().toString();
        int qos = 2;
        MQTTServiceLogger.setLogLevel(MQTTServiceLogger.LogLevel.DEBUG);
        MQTTServiceCommand.connectAndSubscribe(MQTTUtils.this,
                                                Initializer.BROKER,
                                                clientId,
                                                username,
                                                password,
                                                qos,
                                                true,
                                                Initializer.CAMERA_APP_TOPIC);
    }

}
