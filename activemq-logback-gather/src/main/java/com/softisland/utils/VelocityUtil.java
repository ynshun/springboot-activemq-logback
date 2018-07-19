package com.softisland.utils;

import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class VelocityUtil {
	private static VelocityEngine engine;

	public static String replace(String template, Context context) {
		StringWriter writer = new StringWriter();

		engine.evaluate(context, writer, "", template);
		return writer.toString();
	}

	public static String replace(String template, String key, Object value) {
		StringWriter writer = new StringWriter();

		engine.evaluate(put(key, value), writer, "", template);
		return writer.toString();
	}

	public static Context put(String key, Object value) {
		Context context = new VelocityUtil().new Context();
		context.put(key, value);
		return context;
	}

	static {
		Properties props = new Properties();
		props.setProperty("input.encoding", "UTF-8");
		props.setProperty("resource.loader", "class");
		props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		engine = new VelocityEngine(props);
	}

	public class Context extends VelocityContext {
		public Context put(String key, Object value) {
			super.put(key, value);
			return this;
		}
	}
}