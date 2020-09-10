package it.uniba.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.uniba.dao.FileDTO;
import it.uniba.utils.Account;

/**
 * Servlet implementation class ProjectServlet
 */
@WebServlet(urlPatterns = "/private/project", name = "ProjectServlet")
@MultipartConfig(maxFileSize = 1000000, maxRequestSize = 1000000)
public class ProjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// SHOW PROJECT FILE
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		try {

			// Already sanitized by WhiteListingFilter
			String projectId = request.getParameter("projectId");
			Account account = new Account((String) session.getAttribute("email"));
			Optional<FileDTO> projectFile = account.getProjectContent(projectId);

			if (projectFile.isPresent()) {

				request.setAttribute("project", projectFile.get());

			}
			String page = "/WEB-INF/projectView.jsp";
			RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher(page);
			requestDispatcher.forward(request, response);

		} catch (SQLException | IOException e) {
			e.printStackTrace();
			response.sendError(500);

		}

	}

}
