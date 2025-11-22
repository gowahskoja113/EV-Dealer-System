package com.swp391.evdealersystem;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
public class EvdealersystemApplication {
	public static void main(String[] args) {
        SpringApplication.run(EvdealersystemApplication.class, args);
//        byte[] key = new byte[64];
//        new SecureRandom().nextBytes(key);
//        String secret = Base64.getEncoder().encodeToString(key);
//        System.out.println(secret);
//    }
	}
}
