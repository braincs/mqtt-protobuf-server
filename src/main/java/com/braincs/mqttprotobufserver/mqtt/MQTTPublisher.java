package com.braincs.mqttprotobufserver.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MQTTPublisher extends MQTTConfig implements MqttCallback, IMQTTPublisher {

    private String brokerUrl = null;

    final private String colon = ":";
    final private String clientId = "mqtt_server_pub";

    private MqttClient mqttClient = null;
    private MqttConnectOptions connectionOptions = null;
    private MemoryPersistence persistence = null;

    private static final Logger logger = LoggerFactory.getLogger(MQTTPublisher.class);

    /**
     * Private default constructor
     */
    private MQTTPublisher() {
        this.config();
    }

    /**
     * Private constructor
     */
    private MQTTPublisher(String broker, Integer port, Boolean ssl, Boolean withUserNamePass) {
        this.config(broker, port, ssl, withUserNamePass);
    }

    /**
     * Factory method to get instance of MQTTPublisher
     *
     * @return MQTTPublisher
     */
    public static MQTTPublisher getInstance() {
        return new MQTTPublisher();
    }

    /**
     * Factory method to get instance of MQTTPublisher
     *
     * @param broker
     * @param port
     * @param ssl
     * @param withUserNamePass
     * @return MQTTPublisher
     */
    public static MQTTPublisher getInstance(String broker, Integer port, Boolean ssl, Boolean withUserNamePass) {
        return new MQTTPublisher(broker, port, ssl, withUserNamePass);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.bjitgroup.jasmysp.mqtt.publisher.MQTTPublisherBase#configurePublisher()
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
            logger.error("ERROR", me);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.bjitgroup.jasmysp.mqtt.publisher.MQTTPublisherBase#configurePublisher(
     * java.lang.String, java.lang.Integer, java.lang.Boolean, java.lang.Boolean)
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
            logger.error("ERROR", me);
        }
    }


    /*
     * (non-Javadoc)
     * @see com.monirthought.mqtt.publisher.MQTTPublisherBase#publishMessage(java.lang.String, java.lang.String)
     */
    @Override
    public void publishMessage(String topic, String message) {

        try {
            MqttMessage mqttmessage = new MqttMessage(message.getBytes());
            mqttmessage.setQos(this.qos);
            this.mqttClient.publish(topic, mqttmessage);
        } catch (MqttException me) {
            logger.error("ERROR", me);
        }

    }

    public void publish(String topic, byte[] message, int qos, boolean retained){
        try {
            this.mqttClient.publish(topic, message, qos, retained );
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    /*
     * (non-Javadoc)
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
     */
    @Override
    public void connectionLost(Throwable arg0) {
        logger.info("Connection Lost");

    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        logger.info("delivery completed");

    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String, org.eclipse.paho.client.mqttv3.MqttMessage)
     */
    @Override
    public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
        // Leave it blank for Publisher

    }

    /*
     * (non-Javadoc)
     * @see com.monirthought.mqtt.publisher.MQTTPublisherBase#disconnect()
     */
    @Override
    public void disconnect() {
        try {
            this.mqttClient.disconnect();
        } catch (MqttException me) {
            logger.error("ERROR", me);
        }
    }

}
