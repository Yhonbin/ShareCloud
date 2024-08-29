package com.firefly.sharemount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ShareCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShareCloudApplication.class, args);
    }
}
