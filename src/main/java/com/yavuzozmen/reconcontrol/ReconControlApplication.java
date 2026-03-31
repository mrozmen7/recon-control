package com.yavuzozmen.reconcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReconControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReconControlApplication.class, args);
    }
}
