package com.softisland.config.activemq;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.Message;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.softisland.config.database.DataSourceUtil;
import com.softisland.config.database.SqlFactory;
import com.softisland.utils.VelocityUtil;
import com.softisland.utils.VelocityUtil.Context;

import net.sf.json.JSONObject;

@Component
public class Receiver {
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	
	

	// 监听指定消息主题
	@JmsListener(destination = "purchase.mall.log.topic", containerFactory = "jmsListenerContainerTopic")
	public void receiveTopic(String message) {
		System.err.println("接受到Topic消息: " + message);
	}

	// 监听指定消息主题
	@JmsListener(destination = "purchase.mall.log.queue11111", containerFactory = "jmsListenerContainerQueue")
	public void receiveQueue(Message msg) {
		try {
			if (msg instanceof ActiveMQTextMessage) {
				ActiveMQTextMessage _msg = (ActiveMQTextMessage) msg;

				String messageBody = _msg.getText();

				JSONObject jsonObject = JSONObject.fromObject(messageBody);

				// 创建数据表
				String tableName = this.getTableName(jsonObject);
				String createTableSql = VelocityUtil.replace(SqlFactory.createTableSQL, "tableName", tableName);
				DataSourceUtil.excuteUpdateSQL(createTableSql, null);
				
				// 插入数据到数据表中
				Context replaceContext = VelocityUtil.put("tableName", tableName);
				
				String insertDataSql = VelocityUtil.replace(SqlFactory.insertLogDataSQL, replaceContext);
				DataSourceUtil.excuteUpdateSQL(insertDataSql, DataSourceUtil.addParam(jsonObject.get("from_host"))
																			.addParam(jsonObject.get("service_id"))
																			.addParam(new Date(jsonObject.getLong("time_stamp")))
																			.addParam(jsonObject.get("formatted_message"))
																			.addParam(jsonObject.get("level"))
																			.addParam(jsonObject.get("message"))
																			.addParam(jsonObject.get("logger_name"))
																			.addParam(jsonObject.get("thread_name"))
																			.addParam(jsonObject.get("method_name"))
																			.addParam(jsonObject.get("file_name"))
																			.addParam(jsonObject.get("line_number"))
																			.addParam(jsonObject.get("class_name"))
																			.addParam(jsonObject.get("declaring_class"))
																			.addParam(jsonObject.get("throwable_message")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private String getTableName(JSONObject jsonObject) {
		return jsonObject.getString("service_id") + "_" + format.format(jsonObject.getDouble("time_stamp"));
	}

}