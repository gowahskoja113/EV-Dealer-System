package com.swp391.evdealersystem;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class EvdealersystemApplication {

	public static void main(String[] args) {
        SpringApplication.run(EvdealersystemApplication.class, args);
	}
}
