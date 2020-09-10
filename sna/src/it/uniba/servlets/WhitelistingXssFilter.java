package it.uniba.servlets;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet Filter implementation class WhitelistingXssFilter
 */
@WebFilter(urlPatterns = "/private/*", filterName = "WhiteListingXssFilter")
public class WhitelistingXssFilter implements Filter {
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		System.out.println("WhiteListingXssFilter");
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			if (attributeHasXss((HttpServletRequest) request)) {
				((HttpServletResponse) response).sendError(400);
				return;
			}
			chain.doFilter(request, response);
		}
	}

	private static final Pattern onlyAlphaNumeric = Pattern.compile("[A-Za-z0-9 ]+");
	//private static final Pattern onlyAlphaNumeric = Pattern.compile("<script>");


	private boolean attributeHasXss(HttpServletRequest request) {

		for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			for (String value : entry.getValue()) {
				
				if (!onlyAlphaNumeric.matcher(value).matches()) {
					return true;
				}
			}
		}
		Enumeration<String> attrs = request.getAttributeNames();
		
		while (attrs.hasMoreElements()) {

			if (!onlyAlphaNumeric.matcher(attrs.nextElement()).matches()) {
				return true;
			}

		}

		return false;
	}

}
