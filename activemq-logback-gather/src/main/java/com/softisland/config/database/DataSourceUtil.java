package com.softisland.config.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.softisland.data.MapData;

import net.sf.json.JSONNull;

@Component
public class DataSourceUtil {
	private static final boolean autoCommitTransaction = true;

	private static DataSource dataSource;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		DataSourceUtil.dataSource = dataSource;
	}

	/**
	 * 执行SQL
	 * 
	 * @param sql
	 * @return
	 */
	public static int excuteUpdateSQL(String sql) {
		return excuteUpdateSQL(sql, null);
	}

	/**
	 * 执行SQL
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public static int excuteUpdateSQL(String sql, BDParam params) {
		int count = 0;
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		try {
			conn = DataSourceUtil.getConnection(autoCommitTransaction);
			psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			if (null != params && params.size() > 0) {
				for (int i = 0; i < params.size(); i++) {
					if (params.get(i) == null || params.get(i) instanceof JSONNull) {
						psmt.setNull(i + 1, Types.NULL);
					} else {
						psmt.setObject(i + 1, params.get(i));
					}
				}
			}
			count = psmt.executeUpdate();

			rs = psmt.getGeneratedKeys();

			if (rs.next()) {
				// System.err.println("主键：" + rs.getObject(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(conn, psmt, rs);
		}
		return count;
	}

	public static MapData queryOne(String sql) {
		return queryOne(sql, null);
	}

	public static MapData queryOne(String sql, BDParam params) {
		List<MapData> list = DataSourceUtil.queryList(sql, params);
		return null != list && list.size() > 0 ? list.get(0) : null;
	}

	public static List<MapData> queryList(String sql) {
		return queryList(sql, null);
	}

	public static List<MapData> queryList(String sql, BDParam params) {
		List<MapData> list = new ArrayList<MapData>();
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		
		try {
			conn = DataSourceUtil.getConnection(autoCommitTransaction);
			psmt = conn.prepareStatement(sql);
			if (null != params && params.size() > 0) {
				for (int i = 0; i < params.size(); i++) {
					psmt.setObject(i + 1, params.get(i));
				}
			}
			rs = psmt.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();

			String columnLabel;
			Object columnValue;
			while (rs.next()) {
				MapData data = new MapData();
				for (int i = 0; i < columnCount; i++) {
					columnLabel = metaData.getColumnLabel(i + 1);
					columnValue = rs.getObject(columnLabel);
					data.put(columnLabel, columnValue);
				}
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(conn, psmt, rs);
		}
		return list;
	}

	public static BDParam addParam(Object param) {
		return getDBParamInstance().addParam(param);
	}

	public static BDParam getDBParamInstance() {
		return new DataSourceUtil().new BDParam();
	}

	public class BDParam extends ArrayList<Object> implements List<Object> {
		private static final long serialVersionUID = 1L;

		public BDParam addParam(Object e) {
			super.add(e);
			return this;
		}
	}

	/**
	 * 获取数据库连接
	 * 
	 * @param autoCommit
	 *            连接是否自动提交事务
	 * @return
	 * @throws SQLException
	 */
	private static Connection getConnection(boolean autoCommit) throws SQLException {
		Connection conn = dataSource.getConnection();
		conn.setAutoCommit(autoCommit);
		return conn;
	}

	private static void release(Connection conn, PreparedStatement psmt, ResultSet res) {
		try {
			if (res != null) {
				res.close();
			}
			if (psmt != null) {
				psmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
