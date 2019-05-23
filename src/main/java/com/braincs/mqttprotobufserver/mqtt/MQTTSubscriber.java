package com.braincs.mqttprotobufserver.mqtt;

import com.braincs.kylinprotocol.pb.KylinProto;
import com.braincs.mqttprotobufserver.utils.FileUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class MQTTSubscriber extends MQTTConfig implements MqttCallback, IMQTTSubscriber {

    private String brokerUrl = null;
    final private String colon = ":";
    final private String clientId = "mqtt_server_sub";
    private final String folder = "/Users/changshuai/IdeaProjects/mqtt-protobuf-server/tmp";
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
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
        System.out.println();
        System.out.println("***********************************************************************");
        String time = new Timestamp(System.currentTimeMillis()).toString();
        if (topic.endsWith("/in/recognize")) {
            System.out.println("Message Arrived at Time: " + time + "  Topic: " + topic);
            KylinProto.Recognize recognize = KylinProto.Recognize.parseFrom(message.getPayload());
            byte[] data = recognize.getCrop().toByteArray();
            String dateStr = this.format.format(new Date());
            String saveFile = FileUtil.save(folder, "recognize_" + dateStr + ".jpg", data);
            System.out.println("saved success : " + saveFile);
        } else if (topic.endsWith("/in/snapshot")) {
            System.out.println("Message Arrived at Time: " + time + "  Topic: " + topic);
            KylinProto.SnapShot snapShot = KylinProto.SnapShot.parseFrom(message.getPayload());
            byte[] data = snapShot.getImage().toByteArray();
            String dateStr = this.format.format(new Date());
            String saveFile = FileUtil.save(folder, "snapshot_" + dateStr + ".jpg", data);
            System.out.println("saved success : " + saveFile);
        } else {
            System.out.println("Message Arrived at Time: " + time + "  Topic: " + topic + "  Message: "
                    + new String(message.getPayload()));
        }
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