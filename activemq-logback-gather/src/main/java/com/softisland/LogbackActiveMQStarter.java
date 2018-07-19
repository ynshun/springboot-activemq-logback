package com.softisland;

import org.springframework.boot.SpringApplication;

import com.softisland.config.RootConfiguration;

public class LogbackActiveMQStarter {

	public static void main(String[] args) {
		SpringApplication.run(RootConfiguration.class, args);
	}
}
