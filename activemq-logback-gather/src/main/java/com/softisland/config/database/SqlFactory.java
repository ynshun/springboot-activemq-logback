package com.softisland.config.database;

public class SqlFactory {

	public static final String createTableSQL = "CREATE TABLE IF NOT EXISTS ${tableName} (" +
			"`id` int (11) NOT NULL AUTO_INCREMENT," +
			"`from_host` VARCHAR (100)," +
			"`service_id` VARCHAR (50)," +
			"`time_stamp` datetime," +
			"`formatted_message` longtext," +
			"`level` VARCHAR (10)," +
			"`message` longtext," +
			"`logger_name` VARCHAR (200)," +
			"`thread_name` VARCHAR (100)," +
			"`method_name` VARCHAR (100)," +
			"`file_name` VARCHAR (100)," +
			"`line_number` INT (11)," +
			"`class_name` VARCHAR (200)," +
			"`declaring_class` VARCHAR (200)," +
			"`throwable_message` text," +
			"PRIMARY KEY (`id`)" +
		");";

	public static final String insertLogDataSQL = "INSERT INTO ${tableName}(" +
				"from_host," +
				"service_id," +
				"time_stamp," +
				"formatted_message," +
				"level," +
				"message," +
				"logger_name," +
				"thread_name," +
				"method_name," +
				"file_name," +
				"line_number," +
				"class_name," +
				"declaring_class," +
				"throwable_message) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
}
