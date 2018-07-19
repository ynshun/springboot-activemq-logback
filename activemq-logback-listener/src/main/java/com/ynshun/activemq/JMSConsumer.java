package com.ynshun.activemq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.ynshun.websocket.WsPool;

import javax.jms.*;

/**
 * 消息消费者
 */
public class JMSConsumer implements Runnable {
	// 默认连接用户名
	private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
	// 默认连接密码
	private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
	// 默认连接地址
	private static final String BROKER_URL = "failover:(tcp://172.16.20.112:61616)?initialReconnectDelay=1000&maxReconnectDelay=30000";

	@Override
	public void run() {
		// 连接工厂
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKER_URL);
		try {
			// 连接
			Connection connection = connectionFactory.createConnection();
			// 启动连接
			connection.start();
			// 创建session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// 消息目的地
			Destination destination = session.createQueue("purchase.mall.log.queue");
			// 消息消费者
			MessageConsumer consumer = session.createConsumer(destination);
			while (true) {
				TextMessage message = (TextMessage) consumer.receive();
				if (message != null) {
					WsPool.sendMessageToAll(message.getText());
				} else {
					break;
				}
			}
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}