package com.ynshun.log;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import net.sf.json.JSONObject;

public class ActiveMQAppender extends AppenderBase<ILoggingEvent> {
	private static final ExecutorService threadPool = Executors.newFixedThreadPool(20);

	private String serviceid = "MyAPP";
	private String disname = "purchase.mall.log.queue";
	private String username;
	private String password;
	private String brokenurl;

	MessageProducer producer = null;
	Session session = null;

	@Override
	protected void append(ILoggingEvent arg0) {
		try {
			StackTraceElement stackTraceElement = arg0.getCallerData()[0];

			Map<String, Object> message = new HashMap<String, Object>();
			message.put("from_host", InetAddress.getLocalHost().getHostAddress());
			message.put("service_id", this.serviceid);
			message.put("thread_name", arg0.getThreadName());
			message.put("level", arg0.getLevel().levelStr);
			message.put("message", arg0.getMessage());
			message.put("formatted_message", arg0.getFormattedMessage());
			message.put("logger_name", arg0.getLoggerName());
			message.put("class_name", arg0.getThrowableProxy() == null ? null : arg0.getThrowableProxy().getClassName());
			message.put("throwable_message", arg0.getThrowableProxy() == null ? null : arg0.getThrowableProxy().getClassName());
			message.put("time_stamp", arg0.getTimeStamp());
			message.put("declaring_class", stackTraceElement.getClassName());
			message.put("line_number", stackTraceElement.getLineNumber());
			message.put("file_name", stackTraceElement.getFileName());
			message.put("method_name", stackTraceElement.getMethodName());

			this.sendMessage(JSONObject.fromObject(message).toString());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void init() {
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(username, password, brokenurl);
			Connection connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Queue queue = session.createQueue(disname);

			producer = session.createProducer(queue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String msg) {
		if (producer == null) {
			init();
		}

		threadPool.submit(new Runnable() {
			@Override
			public void run() {
				try {
					TextMessage message = session.createTextMessage();
					message.setText(msg);
					producer.send(message);
				} catch (JMSException e) {
					System.err.println("消息发送异常，请注意.....");
					init();
					sendMessage(msg);
				}
			}
		});
	}

	public void setDisname(String disname) {
		this.disname = disname;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setServiceid(String serviceid) {
		this.serviceid = serviceid;
	}

	public void setBrokenurl(String brokenurl) {
		this.brokenurl = brokenurl;
	}

}