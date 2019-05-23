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
     * publish message to topic with qos
     * @param topic to deliver the message to, for example "finance/stock/ibm"
     * @param message message to sub
     * @param qos the Quality of Service to deliver the message at. Valid values
     *           are 0, 1 or 2.
     * @param retained whether or not this message should be retained by the server.
     */
    public void publish(String topic, byte[] message, int qos, boolean retained);
    /**
     * Disconnect MQTT Client
     */
    public void disconnect();
}
