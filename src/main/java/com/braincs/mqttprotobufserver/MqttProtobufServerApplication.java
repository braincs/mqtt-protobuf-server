package com.braincs.mqttprotobufserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@Configuration
//@EnableConfigurationProperties(MqttProperties.class)
public class MqttProtobufServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttProtobufServerApplication.class, args);
    }

}
