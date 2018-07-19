package com.ynshun.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.java_websocket.WebSocketImpl;

import com.ynshun.activemq.JMSConsumer;
import com.ynshun.websocket.WsServer;

@WebFilter(urlPatterns = { "/*" })
public class URLFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		String servletPath = request.getRequestURI();
		if (!servletPath.equals("/index.jsp")) {
			response.sendRedirect("/index.jsp");
		} else {
			chain.doFilter(request, response);
		}

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		WebSocketImpl.DEBUG = false;
		int port = 8887; // 端口
		WsServer s = new WsServer(port);
		s.start();
		
		new Thread(new JMSConsumer()).start();
	}

}
