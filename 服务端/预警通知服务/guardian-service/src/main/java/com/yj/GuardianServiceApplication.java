package com.yj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class GuardianServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuardianServiceApplication.class, args);
    }

}
