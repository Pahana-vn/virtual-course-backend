package com.mytech.virtualcourse;

import org.springframework.boot.SpringApplication;

public class TestVirtualcourseApplication {

	public static void main(String[] args) {
		SpringApplication.from(VirtualcourseApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
