package it.uniba.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import javax.servlet.http.Part;

public interface JdbcFacade {
	

	Optional<String> getUserPw(String email) throws SQLException;

	Optional<String> getUserSalt(String email) throws SQLException;

	boolean setUser(String email, String pwHash, Part file) throws SQLException, IOException;

	boolean setUserSalt(String email, String salt) throws SQLException;

	OptionalInt getUserId(String email) throws SQLException;

	boolean setUserAuthToken(String selector, String validator, int userId, int seconds) throws SQLException;

	Optional<String> getUserAuth(String selector, String validator) throws SQLException;

	Optional<String> getUserImage(String email) throws SQLException, IOException;

	List<FileDTO> getAllProject(String email) throws SQLException;

	boolean setProject(String email, String name, Part file) throws SQLException, IOException;

	Optional<FileDTO> getProjectContent(String email, String projectId) throws SQLException, IOException;

	boolean updateUserAuthToken(String selector, int seconds, String validator) throws SQLException;

}
