package com.romeh.ignitemanager;

import com.romeh.ignitemanager.entities.AlertsConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EnableRetry
public class AlertManagerApplication {


    public static void main(String[] args) {

        SpringApplication.run(AlertManagerApplication.class, args);


    }



}
