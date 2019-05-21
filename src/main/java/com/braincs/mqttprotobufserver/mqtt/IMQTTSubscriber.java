package com.braincs.mqttprotobufserver.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IMQTTSubscriber {
    public static final Logger logger = LoggerFactory.getLogger(IMQTTSubscriber.class);

    /**
     * Subscribe message
     *
     * @param topic
     * @param jasonMessage
     */
    public void subscribeMessage(String topic);

    /**
     * Disconnect MQTT Client
     */
    public void disconnect();
}
