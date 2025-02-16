package com.mytech.virtualcourse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VirtualcourseApplication {

	public static void main(String[] args) {
		SpringApplication.run(VirtualcourseApplication.class, args);
		System.out.println("Welcome to Virtual Course");
	}

}
