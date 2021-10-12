package com.cos.realtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RealtimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealtimeApplication.class, args);
	}

}
