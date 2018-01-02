package com.mobiecode.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="com.mobiecode.webservice.controller")
@EnableAutoConfiguration
public class MobieWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(MobieWebApplication.class, args);
    }
}
