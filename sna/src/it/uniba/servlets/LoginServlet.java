package it.uniba.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.OptionalInt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.uniba.dao.CookieUtils;
import it.uniba.dao.JdbcFacadeImpl;
import it.uniba.utils.Account;
import it.uniba.utils.Sanity;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(urlPatterns = "/public/login", name = "LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public LoginServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);

	}

	@Override
	protected void doPost(HttpServletRequest newRequest, HttpServletResponse newResponse)
			throws IOException, ServletException {

		HttpServletRequest request = newRequest;
		HttpServletResponse response = newResponse;

		// Check credentials
		// Regex mail and password
		if (!Sanity.regexMail(request.getParameter("email"))
				|| !Sanity.regexPassword(request.getParameter("pw1").toCharArray())) {
			throw new ServletException("Login KO: invalid credentials");

		}

		try

		{
			String email = request.getParameter("email");

			// Check if account exists
			Account account = new Account(email);
			if (account.checkIfExists(request.getParameter("pw1").toCharArray())) {

				System.out.println("account exists!");

				HttpSession session = request.getSession(true);
				session.invalidate();
				session = request.getSession(true);
				session.setAttribute("email", email);

				if (request.getParameter("rememberMe") != null && Boolean.valueOf(request.getParameter("rememberMe")))

				{
					System.out.println("LoginServlet: remember me check");

					JdbcFacadeImpl database = new JdbcFacadeImpl();
					CookieUtils cookieManager = new CookieUtils();

					OptionalInt userId = database.getUserId(email);

					if (userId.isPresent() && cookieManager.createAuthCookie(userId.getAsInt())) {

						response.addCookie(cookieManager.getCookieSelector());
						response.addCookie(cookieManager.getCookieValidator());
					}

				}
				response.sendRedirect("/sna/private/view");
			}
			else
			{
				throw new ServletException("Login KO: invalid credentials");

			}


		} catch (SQLException | NoSuchElementException e) {
			e.printStackTrace();
			throw new ServletException("LOGIN KO: some errors occurred!");

		}

	}

}
