package com.bank.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

public class ServletFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	 HttpServletRequest httpRequest = (HttpServletRequest) request;
         HttpServletResponse httpResponse = (HttpServletResponse) response;

        
         HttpSession session = httpRequest.getSession(false);
          System.out.println(httpRequest.getRequestURI());
          String uri = httpRequest.getRequestURI();
          
          switch (uri) {
		case "/WeBank/app/login":
			
			break;

		default:
			
			 if( session == null || session.getAttribute("userId") == null) {
	        		httpResponse.sendRedirect("/WeBank/app/login");
	        		return;
	        	 }
			break;
		}
        
        chain.doFilter(request, response); 
    }

    @Override
    public void destroy() {
        // Cleanup code, if needed
    }
}