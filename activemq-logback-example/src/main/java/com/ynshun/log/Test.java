package com.ynshun.log;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
	private static final Logger logger = LoggerFactory.getLogger(Test.class);

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 20; i++) {
			logger.debug(UUID.randomUUID().toString());
		}
		// System.exit(0);
	}
}