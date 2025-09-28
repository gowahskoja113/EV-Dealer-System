package com.swp391.evdealersystem;

import com.swp391.evdealersystem.entity.Role;
import com.swp391.evdealersystem.entity.User;
import com.swp391.evdealersystem.repository.RoleRepository;
import com.swp391.evdealersystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EvdealersystemApplication {

	public static void main(String[] args) {
        SpringApplication.run(EvdealersystemApplication.class, args);
	}
}
