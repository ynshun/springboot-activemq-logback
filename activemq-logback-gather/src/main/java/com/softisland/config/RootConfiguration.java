package com.softisland.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(value = "com.softisland", excludeFilters = { @Filter(Controller.class),
		@Filter(type = FilterType.ASSIGNABLE_TYPE, value = { RootConfiguration.class }) })
public class RootConfiguration extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(this.getClass());
	}

}