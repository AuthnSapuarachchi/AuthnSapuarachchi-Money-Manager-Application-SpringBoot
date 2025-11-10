package com.authcodelab.smartmoneymanageapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SmartmoneymanageappApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartmoneymanageappApplication.class, args);
	}

}
