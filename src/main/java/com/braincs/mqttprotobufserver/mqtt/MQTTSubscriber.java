package com.braincs.mqttprotobufserver.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class MQTTSubscriber extends MQTTConfig implements MqttCallback, IMQTTSubscriber {

    private String brokerUrl = null;
    final private String colon = ":";
    final private String clientId = "demoClient2";

    private MqttClient mqttClient = null;
    private MqttConnectOptions connectionOptions = null;
    private MemoryPersistence persistence = null;

    private static final Logger logger = LoggerFactory.getLogger(MQTTSubscriber.class);

    public MQTTSubscriber() {
        this.config();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.
     * Throwable)
     */
    @Override
    public void connectionLost(Throwable cause) {
        logger.info("Connection Lost");

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String,
     * org.eclipse.paho.client.mqttv3.MqttMessage)
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // Called when a message arrives from the server that matches any
        // subscription made by the client
        String time = new Timestamp(System.currentTimeMillis()).toString();
        System.out.println();
        System.out.println("***********************************************************************");
        System.out.println("Message Arrived at Time: " + time + "  Topic: " + topic + "  Message: "
                + new String(message.getPayload()));
        System.out.println("***********************************************************************");
        System.out.println();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho
     * .client.mqttv3.IMqttDeliveryToken)
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Leave it blank for subscriber

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.monirthought.mqtt.subscriber.MQTTSubscriberBase#subscribeMessage(java.
     * lang.String)
     */
    @Override
    public void subscribeMessage(String topic) {
        try {
            this.mqttClient.subscribe(topic, this.qos);
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.monirthought.mqtt.subscriber.MQTTSubscriberBase#disconnect()
     */
    public void disconnect() {
        try {
            this.mqttClient.disconnect();
        } catch (MqttException me) {
            logger.error("ERROR", me);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.monirthought.config.MQTTConfig#config(java.lang.String,
     * java.lang.Integer, java.lang.Boolean, java.lang.Boolean)
     */
    @Override
    protected void config(String broker, Integer port, Boolean ssl, Boolean withUserNamePass) {

        String protocal = this.TCP;
        if (true == ssl) {
            protocal = this.SSL;
        }

        this.brokerUrl = protocal + this.broker + colon + port;
        this.persistence = new MemoryPersistence();
        this.connectionOptions = new MqttConnectOptions();

        try {
            this.mqttClient = new MqttClient(brokerUrl, clientId, persistence);
            this.connectionOptions.setCleanSession(true);
            if (true == withUserNamePass) {
                if (password != null) {
                    this.connectionOptions.setPassword(this.password.toCharArray());
                }
                if (userName != null) {
                    this.connectionOptions.setUserName(this.userName);
                }
            }
            this.mqttClient.connect(this.connectionOptions);
            this.mqttClient.setCallback(this);
        } catch (MqttException me) {
            me.printStackTrace();
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.monirthought.config.MQTTConfig#config()
     */
    @Override
    protected void config() {

        this.brokerUrl = this.TCP + this.broker + colon + this.port;
        this.persistence = new MemoryPersistence();
        this.connectionOptions = new MqttConnectOptions();
        try {
            this.mqttClient = new MqttClient(brokerUrl, clientId, persistence);
            this.connectionOptions.setCleanSession(true);
            this.mqttClient.connect(this.connectionOptions);
            this.mqttClient.setCallback(this);
        } catch (MqttException me) {
            me.printStackTrace();
        }

    }

}