package com.ynshun.websocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.java_websocket.WebSocket;

public class WsPool {
	private static final Map<WebSocket, String> wsUserMap = new HashMap<WebSocket, String>();

	public static String getUserByWs(WebSocket conn) {
		return wsUserMap.get(conn);
	}

	public static WebSocket getWsByUser(String userName) {
		Set<WebSocket> keySet = wsUserMap.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				String cuser = wsUserMap.get(conn);
				if (cuser.equals(userName)) {
					return conn;
				}
			}
		}
		return null;
	}

	public static void addUser(String userName, WebSocket conn) {
		wsUserMap.put(conn, userName);
	}

	public static Collection<String> getOnlineUser() {
		List<String> setUsers = new ArrayList<String>();
		Collection<String> setUser = wsUserMap.values();
		for (String u : setUser) {
			setUsers.add(u);
		}
		return setUsers;
	}

	public static boolean removeUser(WebSocket conn) {
		if (wsUserMap.containsKey(conn)) {
			wsUserMap.remove(conn); // 移除连接
			return true;
		} else {
			return false;
		}
	}

	public static void sendMessageToUser(WebSocket conn, String message) {
		if (null != conn && null != wsUserMap.get(conn)) {
			conn.send(message);
		}
	}

	public static void sendMessageToAll(String message) {
		Set<WebSocket> keySet = wsUserMap.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				String user = wsUserMap.get(conn);
				if (user != null) {
					conn.send(message);
				}
			}
		}
	}

}