package it.uniba.servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import it.uniba.dao.JdbcFacadeImpl;
import it.uniba.utils.Account;
import it.uniba.utils.SanityFile;

/**
 * Servlet implementation class ViewServlet
 */
@WebServlet(urlPatterns = "/private/view", name = "View")
@MultipartConfig(maxFileSize = 1000000, maxRequestSize = 1000000)
public class ViewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// LOAD new FILE on database
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
		//Guard CLAUSES 
		JdbcFacadeImpl database = new JdbcFacadeImpl();
		HttpSession session = request.getSession(false);

		if (request.getPart("project").getSize() == 0) {
			throw new ServletException("Register KO: Please select a valid file!");
		}

		SanityFile fileManager;
		try {
			fileManager = new SanityFile(request.getPart("project"), "text/plain");
		} catch (IOException | SAXException | TikaException e) {
			throw new ServletException("Register KO: Please select only a txt file!");
		}

		String name = request.getParameter("name");

		try {
			//LOAD PROJECT FILE ON DATABASE
			if (database.setProject((String) session.getAttribute("email"), name, fileManager.getFile())) {
				response.sendRedirect("/sna/private/view");
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}

	}

	// GET User's private homepage and project list
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		Account account = new Account((String) session.getAttribute("email"));
		try {
			//GET USER IMAGE FROM DATABASE
			if (account.loadImage().isPresent()) {
				request.setAttribute("image", account.loadImage().get());
			}
			request.setAttribute("project", account.getProjectList());

		} catch (SQLException e) {
			e.printStackTrace();
		}

		String page = "/WEB-INF/welcome.jsp";
		RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher(page);
		requestDispatcher.forward(request, response);

	}

}
