package it.uniba.servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import it.uniba.dao.CookieUtils;
import it.uniba.utils.Account;
import it.uniba.utils.Sanity;
import it.uniba.utils.SanityFile;

@WebServlet(urlPatterns = "/public/register", name = "RegisterServlet")
@MultipartConfig(maxFileSize = 3000000, maxRequestSize = 3000000)//TOTAL MAX FILE SIZE = 1Mb
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		getServletContext().getRequestDispatcher("/WEB-INF/register2.jsp").forward(req, resp);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// GUARD CLAUSES

		if (request.getPart("propic").getSize() == 0) {
			throw new ServletException("Register KO: Missing file!");
		}

		SanityFile fileManager;
		try {
			fileManager = new SanityFile(request.getPart("propic"), "image/jpeg");
		} catch (IOException | SAXException | TikaException e) {
			throw new ServletException("Register KO: Please select a valid file!");
		}

		if (!Sanity.regexMail(request.getParameter("email"))
				|| !Sanity.matchingPassword(request.getParameter("pw1").toCharArray(),
						request.getParameter("pw2").toCharArray())) {
			throw new ServletException("Register KO: Please fill all fields!");
		}

		try {

			Account account = new Account(request.getParameter("email"));

			account.create(request.getParameter("pw1").toCharArray(), fileManager);
			System.out.println("Registration complete!");
			// Reset Cookies
			CookieUtils cookieManager = new CookieUtils();
			cookieManager.cookieLogout();
			response.addCookie(cookieManager.getCookieSelector());
			response.addCookie(cookieManager.getCookieValidator());
			request.setAttribute("message", "REGISTER OK");
			
			String page = "/WEB-INF/status.jsp";
			getServletContext().getRequestDispatcher(page).forward(request, response);

			

		} catch (SQLException e) {
			throw new ServletException("REGISTER KO: some errors occurred!");

		}

	}

}
