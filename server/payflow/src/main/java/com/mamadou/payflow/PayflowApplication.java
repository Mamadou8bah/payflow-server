package com.mamadou.payflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class PayflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayflowApplication.class, args);
	}

}
