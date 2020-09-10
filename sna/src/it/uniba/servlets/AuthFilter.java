package it.uniba.servlets;

import java.io.IOException;

import java.sql.SQLException;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.uniba.dao.CookieUtils;

@WebFilter(urlPatterns = { "/private/*" }, filterName = "AuthFilter", servletNames = { "LoginServlet", "RegisterServlet" })
public class AuthFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession(false);

		// check user's login status in the session
		boolean loggedIn = session != null && session.getAttribute("email") != null;
		Cookie[] cookies = httpRequest.getCookies();
		System.out.println("--------------------------");
		System.out.println("Auth filter!");
		System.out.println("servlet name: " + httpRequest.getHttpServletMapping().getServletName());
		System.out.println("loggedIn: " + loggedIn);
		System.out.println("uri name: " + httpRequest.getRequestURI());
		System.out.println("context path: " + httpRequest.getContextPath());
		System.out.println(CookieUtils.getSelectorValue(cookies));
		System.out.println(CookieUtils.getValidatorValue(cookies));
		System.out.println("--------------------------");
		String servletName = httpRequest.getHttpServletMapping().getServletName();

		// IF logged && (log on again || register) THEN REDIRECT welcome
		if (loggedIn) {
			// Prevent from login and register again when logged
			if (servletName.equalsIgnoreCase("LoginServlet") || servletName.equalsIgnoreCase("RegisterServlet")) {
				System.out.println("Already authenticated! Redirected to welcome");
				//request.getServletContext().getNamedDispatcher("View").forward(request, response);
				((HttpServletResponse) response).sendRedirect("/sna/private/view");

			} else {
				System.out.println("User authenticated!");
				chain.doFilter(request, response);
			}
		}
		// IF !logged && cookies != null THEN authenticate
		else if (CookieUtils.getSelectorValue(cookies) != null && CookieUtils.getValidatorValue(cookies) != null
				&& !servletName.equalsIgnoreCase("RegisterServlet"))

		{
			// USER NOT logged && has cookies
			String selector = CookieUtils.getSelectorValue(cookies);
			String validator = CookieUtils.getValidatorValue(cookies);
			CookieUtils cookieManager = new CookieUtils();

			try {
				// CHECK IF COOKIES ARE VALID
				Optional<String> email = cookieManager.checkAuthCookie(selector, validator);
				if (email.isPresent()) {
					// USER EXISTS
					System.out.println("User exists");
					// NEW SESSION
					session = ((HttpServletRequest) request).getSession(true);
					session.setAttribute("email", email.get());
					// COOKIE UPDATE
					if (cookieManager.updateAuthCookie(selector)) {
						((HttpServletResponse) response).addCookie(cookieManager.getCookieValidator());
						((HttpServletResponse) response).addCookie(cookieManager.getCookieSelector());
					}
					// FORWARD TO the requested resource
					if (httpRequest.getRequestURI().contains(httpRequest.getContextPath() + "/private/")) {
						System.out.println("Cookie login: Chain continue");
						chain.doFilter(request, response);
					}
					// FORWARD TO Home, prevent from logging/registering again
					else {
						System.out.println("Cookie login: authenticated!");
						((HttpServletResponse) response).sendRedirect("/sna/private/view");
					}

					// Chain continue for expired cookies, public/login, public/register
				} else if (httpRequest.getRequestURI().contains(httpRequest.getContextPath() + "/public/")) {
					System.out.println("Cookie login failed: User does not exist");
					chain.doFilter(request, response);

				}

			} catch (SQLException e) {
				//request.getServletContext().getNamedDispatcher("LoginServlet").forward(request, response);
				((HttpServletResponse) response).sendError(500);
				e.printStackTrace();
			}

		} else if (servletName.equalsIgnoreCase("LoginServlet") || servletName.equalsIgnoreCase("RegisterServlet")) {

			// FOR REGISTER AND LOGIN
			System.out.println("User not authenticated!");
			chain.doFilter(request, response);

		}
		else
		{
			((HttpServletResponse) response).sendError(401);
		}

	}

}