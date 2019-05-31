package com.braincs.mqttprotobufserver.controller;

import com.braincs.kylinprotocol.pb.KylinProto;
import com.braincs.mqttprotobufserver.mqtt.IMQTTPublisher;
import com.braincs.mqttprotobufserver.mqtt.IMQTTSubscriber;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@Slf4j
@RestController
public class MQTTRestController {

    @Autowired
    IMQTTPublisher publisher;

    @Autowired
    IMQTTSubscriber subscriber;


    private boolean isOpen = false;
    public static String IP_PREFIX = "panel/m5/";
    public static String CLIENTID = "12345";
    //region topics
    public static String TOPIC_LOOP_TEST = "mqtt/loop/message";
    public static String TOPIC_PANEL_SATAUS = IP_PREFIX + CLIENTID + "/out/start";//status 主题
    public static String TOPIC_PANEL_CHECK = IP_PREFIX + CLIENTID + "/in/check";
    ;//check 主题
    public static String TOPIC_PANEL_RECOGNIZE = IP_PREFIX + CLIENTID + "/in/recognize";//recognize 主题
    public static String TOPIC_PANEL_ADD_GROUP = IP_PREFIX + CLIENTID + "/out/add_group";//group 主题
    public static String TOPIC_PANEL_ADD_GROUP_RES = IP_PREFIX + CLIENTID + "/in/add_group/+";//group 主题
    public static String TOPIC_PANEL_DEL_GROUP = IP_PREFIX + CLIENTID + "/out/del_group/";//group 主题
    public static String TOPIC_PANEL_DEL_GROUP_RES = IP_PREFIX + CLIENTID + "/in/del_group/+";//group 主题
    public static String TOPIC_PANEL_ADD_FACE = IP_PREFIX + CLIENTID + "/out/add_face";//add_face 主题
    public static String TOPIC_PANEL_ADD_FACE_RES = IP_PREFIX + CLIENTID + "/in/add_face/+/+";//add_face 返回主题
    public static String TOPIC_PANEL_DEL_FACE = IP_PREFIX + CLIENTID + "/out/del_face/+/+";//del_face 主题
    public static String TOPIC_PANEL_DEL_FACE_RES = IP_PREFIX + CLIENTID + "/in/del_face/+/+";//del_face 返回主题
    public static String TOPIC_PANEL_SNAPSHOT = IP_PREFIX + CLIENTID + "/out/snapshot";//snapshot 主题
    public static String TOPIC_PANEL_SNAPSHOT_RES = IP_PREFIX + CLIENTID + "/in/snapshot";//snapshot result 主题

    //endregion

    //region last_will
    public static String LAST_WILL_PANEL_SATAUS = IP_PREFIX + CLIENTID + "/out/stop";//status 遗言
    public static String LAST_WILL_PANEL_CHECK = IP_PREFIX + CLIENTID + "/in/disconnect";//check 遗言
    //endregion

    @PostConstruct
    public void init() {
        subscriber.subscribeMessage(TOPIC_LOOP_TEST);
        subscriber.subscribeMessage(TOPIC_PANEL_CHECK);
        subscriber.subscribeMessage(TOPIC_PANEL_RECOGNIZE);
        subscriber.subscribeMessage(TOPIC_PANEL_ADD_GROUP_RES);
        subscriber.subscribeMessage(TOPIC_PANEL_DEL_GROUP_RES);
        subscriber.subscribeMessage(TOPIC_PANEL_ADD_FACE_RES);
        subscriber.subscribeMessage(TOPIC_PANEL_DEL_FACE_RES);
        subscriber.subscribeMessage(TOPIC_PANEL_SNAPSHOT_RES);
    }

    private static final Logger logger = LoggerFactory.getLogger(MQTTRestController.class);

    @RequestMapping(value = "/mqtt/loop/message", method = RequestMethod.POST)
    public String index(@RequestBody String data) {
        publisher.publishMessage(TOPIC_LOOP_TEST, data);
        return "Success";
    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public String status(@RequestParam String modeStr, @RequestParam int health_check_interval) {
        int mode = 1;
        if (modeStr.contains("capture")) {
            mode = 0;
        }
//        else if (modeStr.contains("recognize")) {
//            mode = 1;
//        }
        String res = "mode = " + mode + ", health_check_interval = " + health_check_interval;
        System.out.println(res);
        //construct message via protobuf
        isOpen = !isOpen;
        KylinProto.Start.Builder builder = KylinProto.Start.newBuilder();
        KylinProto.Start start = builder.setIsOpen(isOpen)
                .setHealthCheckInterval(health_check_interval)
                .setMode(KylinProto.Start.Mode.recognize)
                .build();

        try {
            KylinProto.Start parseStart = KylinProto.Start.parseFrom(start.toByteArray());
            boolean isOpen = parseStart.getIsOpen();
            KylinProto.Start.Mode startMode = parseStart.getMode();
            int healthCheckInterval = parseStart.getHealthCheckInterval();
            System.out.println("isOpen: " + isOpen + ", Mode: " + startMode.name() + ", interval: " + healthCheckInterval);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        publisher.publish(TOPIC_PANEL_SATAUS, start.toByteArray(), 2, false);
//        publisher.publish(TOPIC_PANEL_SATAUS, res.getBytes(), 2, false);
//        publisher.publishMessage(TOPIC_PANEL_SATAUS, res);
        return "Success";
    }

    @RequestMapping(value = "/group/add", method = RequestMethod.POST)
    public String groupAdd(@RequestParam String url,
                           @RequestParam String group_name,
                           @RequestParam float threshold,
                           @RequestParam int top) {
//        System.out.println("url = " + url);
//        System.out.println("group_name = " + group_name);
        System.out.println("--收到 groupAdd RestFul请求--");
        String[] split = url.split("@");
        String topic = split[split.length - 1];
        topic = topic + "/out/add_group";
        System.out.println("topic = " + topic);

        KylinProto.Group.Builder builder = KylinProto.Group.newBuilder();
        KylinProto.Group group = builder.setThreshold(threshold)
                .setTop(top)
                .setGroup(group_name).build();
        System.out.println("pub group: " + group.toString());

        publisher.publish(topic, group.toByteArray(), 2, false);
        return "Success";
    }


    @RequestMapping(value = "/group/delete", method = RequestMethod.DELETE)
    public String groupDelete(@RequestParam String url, @RequestParam String group_name) {
        System.out.println("--收到 groupDelete RestFul请求--");

//        System.out.println("url = " + url);
//        System.out.println("group_name = " + group_name);
        String[] split = url.split("@");
        String topic = split[split.length - 1];
        topic = topic + "/out/del_group/" + group_name;

        System.out.println("pub del_group topic: " + topic);
        publisher.publishMessage(topic, "del_group:group_name");
        return "Success";
    }

    @RequestMapping(value = "/face/add", method = RequestMethod.POST)
    public String faceAdd(@RequestParam String url,
                          @RequestParam String group_name,
                          @RequestParam String cert_no,
                          @RequestParam String name,
                          @RequestParam String image_url) {
        System.out.println("--收到 faceAdd RestFul请求--");

        System.out.println("url = " + url);
        System.out.println("group_name = " + group_name + ", cert_no = " + cert_no + ", name = " + name + ", image_url = " + image_url);
        String[] split = url.split("@");
        String topic = split[split.length - 1];
        topic = topic + "/out/add_face";
        System.out.println("topic = " + topic);
        KylinProto.Face.Builder builder = KylinProto.Face.newBuilder();
        KylinProto.Face face = builder.setGroup(group_name)
                .setFace(cert_no)
                .setName(name)
                .setUrl(image_url).build();
        System.out.println("pub face: " + face.toString());

        publisher.publish(topic, face.toByteArray(), 2, false);
        return "Success";
    }

    @RequestMapping(value = "/face/delete", method = RequestMethod.DELETE)
    public String faceDelete(@RequestParam String url, @RequestParam String group_name, @RequestParam String cert_no) {
        System.out.println("--收到 faceDelete RestFul请求--");

//        System.out.println("url = " + url);
//        System.out.println("group_name = " + group_name + ", cert_no = " + cert_no);
        String[] split = url.split("@");
        String topic = split[split.length - 1];
        topic = topic + "/out/del_face/" + group_name + "/" + cert_no;
        System.out.println("pub del_face topic: " + topic);

        publisher.publishMessage(topic, "del_face:" + group_name + "/" + cert_no);
        return "Success";
    }

    @RequestMapping(value = "/snapshot", method = RequestMethod.POST)
    public String snapshot(@RequestParam String url) {
//        System.out.println("url = " + url);
//        System.out.println("group_name = " + group_name);
        System.out.println("--收到 snapshot RestFul请求--");

        String[] split = url.split("@");
        String topic = split[split.length - 1];
        topic = topic + "/out/snapshot";
        System.out.println("pub snapshot topic: " + topic);
        publisher.publishMessage(topic, "snapshot");
        return "Success";
    }
}
