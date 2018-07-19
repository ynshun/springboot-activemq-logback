<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<input type="button" value="开始监听" onclick="initWebSocket()"> 
	<input type="button" value="暂停监听"  onclick="closeWebSocket()">
	<input type="button" value="清空消息"  onclick="clearMessage()">
	<div id="messages" contenteditable="true" style="background: black; color: white; width: 1024px; height: 500px; overflow:scroll; ">

	</div>
</body>
<script type="text/javascript">
	Date.prototype.format = function(fmt) {
		var o = {
			"M+" : this.getMonth() + 1, //月份 
			"d+" : this.getDate(), //日 
			"h+" : this.getHours(), //小时 
			"m+" : this.getMinutes(), //分 
			"s+" : this.getSeconds(), //秒 
			"q+" : Math.floor((this.getMonth() + 3) / 3), //季度 
			"S" : this.getMilliseconds()
		};
		if (/(y+)/.test(fmt)) {
			fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
		}
		for ( var k in o) {
			if (new RegExp("(" + k + ")").test(fmt)) {
				fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
			}
		}
		return fmt;
	}

	var allMsg = [];

	var websocket = '';
	var ajaxPageNum = 1;
	var last_health;
	var health_timeout = 10;
	var rightIndex;
	var userName = "ynshun";

	if (!window.WebSocket) {
		alert("该浏览器不支持下单提醒。<br/>建议使用高版本的浏览器，<br/>如 IE10、火狐 、谷歌  、搜狗等");
	}

	function initWebSocket() {
		websocket = new WebSocket(encodeURI('ws://' + document.domain + ':8887'));
		websocket.onopen = function() {
			console.log('已连接');
			websocket.send("online:" + userName);
			heartbeat_timer = setInterval(function() {
				keepalive(websocket)
			}, 60000);
		};
		websocket.onerror = function() {
			console.log('连接发生错误');
		};
		websocket.onclose = function() {
			console.log('已经断开连接');
			// initWebSocket();
		};
		// 消息接收
		websocket.onmessage = function(message) {
			var data = JSON.parse(message.data);
			var message = [];
			message.push('<div>[' + data.service_id + ']');
			message.push(new Date(data.time_stamp).format('yyyy-MM-dd hh:mm:ss'));
			message.push('[' + data.level + ']');
			message.push(data.logger_name + '(' + data.file_name + ' - line: ' + data.line_number + ')');
			message.push(data.formatted_message);
			message.push('</div>\n');

			if (allMsg.length > 200) {
				allMsg.splice(0, 1);
			}

			allMsg.push(message.join(' '));
			
			var div = document.getElementById("messages");
			div.innerHTML = allMsg.join(' ');
			div.scrollTop = div.scrollHeight;
		};
	}
	
	function closeWebSocket () {
		websocket.close();
	}
	
	function clearMessage() {
		allMsg.splice(0, allMsg.length);
		document.getElementById("messages").innerText = '';
	}
	
	// 心跳包
	function keepalive(ws) {
		var time = new Date();
		if (last_health != -1 && (time.getTime() - last_health > health_timeout)) {
			// websocket.close();
		} else {
			if (ws.bufferedAmount == 0) {
				ws.send('~HC~');
			}
		}
	}
</script>
</html>