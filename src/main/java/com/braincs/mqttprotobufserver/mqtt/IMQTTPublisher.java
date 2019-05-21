package com.braincs.mqttprotobufserver.mqtt;

public interface IMQTTPublisher {
    /**
     * Publish message
     *
     * @param topic
     * @param String Message
     */
    public void publishMessage(String topic, String message);

    /**
     * Disconnect MQTT Client
     */
    public void disconnect();
}
