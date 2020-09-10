package it.uniba.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import javax.servlet.http.Part;

import org.apache.commons.codec.binary.Hex;

import it.uniba.utils.HashUtils;

public final class JdbcFacadeImpl implements JdbcFacade {

	DbSingleton instance = null;

	public JdbcFacadeImpl() {
		instance = DbSingleton.getInstance();
	}

	@Override
	public boolean setUser(String email, String pwHash, Part file) throws SQLException, IOException {
		Connection con = instance.getConnectionSna();
		String query = "INSERT INTO user (email,password,propic) VALUES (?,?,?)";
		try (PreparedStatement ps = con.prepareStatement(query); InputStream inputStream = file.getInputStream()) {

			ps.setString(1, email);
			ps.setString(2, pwHash);
			ps.setBlob(3, inputStream);
			return ps.executeUpdate() == 1;
		}

	}

	@Override
	public Optional<FileDTO> getProjectContent(String email, String projectId) throws SQLException, IOException {

		String query = "SELECT name,project_id,project_file FROM project WHERE project_id=? AND email=?";
		Connection con = instance.getConnectionSna();

		try (PreparedStatement ps = con.prepareStatement(query);) {
			ps.setString(1, projectId);
			ps.setString(2, email);
			System.out.println(ps);
			try (ResultSet result = ps.executeQuery();) {

				// RETURN wrapped as optional
				Optional<FileDTO> projectFile = Optional.empty();

				// IMPORTANT to call first()
				if (result.first()) {

					Blob projectBlob = result.getBlob("project_file");
					try (InputStream inputStream = projectBlob.getBinaryStream();
							ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
						
						StringBuilder sb = new StringBuilder();
						byte[] buffer = new byte[(int) projectBlob.length()];

						while ((inputStream.read(buffer)) != -1) {
							sb.append(new String(buffer)).append("\n");
						}

						// Replace newline with br for displaying .replace("\n", "<br />")
						String content = sb.toString();

						FileDTO file = new FileDTO(result.getString("name"), result.getString("project_id"));
						file.setContent(content);
						// Wrapped as Optional
						projectFile = Optional.of(file);

					}

				}
				return projectFile;

			}
		}
	}

	@Override
	public List<FileDTO> getAllProject(String email) throws SQLException {
		String query = "SELECT name,project_id FROM project WHERE email=?";
		Connection con = instance.getConnectionSna();

		try (PreparedStatement ps = con.prepareStatement(query);) {
			ps.setString(1, email);
			try (ResultSet result = ps.executeQuery();) {
				List<FileDTO> fileList = new ArrayList<>();

				while (result.next()) {
					FileDTO file = new FileDTO(result.getString("name"),result.getString("project_id"));
					fileList.add(file);
				}
				return fileList;
			}
		}
	}

	@Override
	public boolean setProject(String email, String name, Part file) throws SQLException, IOException {
		Connection con = instance.getConnectionSna();
		
		// hash passowrd and hash
		MessageDigest msgDigest = HashUtils.getDigest();
		String projectId = Hex.encodeHexString(msgDigest.digest(HashUtils.generateSalt(30)));
		
		String query = "INSERT INTO project (email,name,project_file,project_id) VALUES (?,?,?,?)";
		try (PreparedStatement ps = con.prepareStatement(query); InputStream inputStream = file.getInputStream()) {

			ps.setString(1, email);
			ps.setString(2, name);
			ps.setBlob(3, inputStream);
			ps.setString(4, projectId);
			return ps.executeUpdate() == 1;
		}

	}

	@Override
	public Optional<String> getUserImage(String email) throws SQLException, IOException {

		String query = "SELECT propic FROM user WHERE email=?";
		Connection con = instance.getConnectionSna();

		try (PreparedStatement ps = con.prepareStatement(query);) {
			ps.setString(1, email);
			try (ResultSet result = ps.executeQuery();) {
				// IMPORTANT to call first()
				result.first();
				Blob blob = result.getBlob("propic");

				try (InputStream inputStream = blob.getBinaryStream();
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
					byte[] buffer = new byte[(int) blob.length()];
					int bytesRead = -1;

					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
					}
					//Image base64 encoded
					return Optional.of(Base64.getEncoder().encodeToString(outputStream.toByteArray()));
				}

			}
		}
	}

	@Override
	public Optional<String> getUserPw(String email) throws SQLException {

		String query = "SELECT password FROM user WHERE email=?";
		Connection con = instance.getConnectionSna();

		try (PreparedStatement ps = con.prepareStatement(query);) {
			ps.setString(1, email);
			try (ResultSet result = ps.executeQuery();) {
				Optional<String> password = Optional.empty();
				if (result.first()) {
					password = Optional.of(result.getString("password"));
				}
				return password;
			}
		}
	}

	@Override
	public Optional<String> getUserSalt(String email) throws SQLException {
		String query = "SELECT salt FROM user_hash WHERE email=?";
		Connection con = instance.getConnectionSnaSalted();

		try (PreparedStatement ps = con.prepareStatement(query);) {
			ps.setString(1, email);
			try (ResultSet result = ps.executeQuery();) {
				Optional<String> salt = Optional.empty();

				if (result.first()) {
					salt = Optional.of(result.getString("salt"));

				}
				return salt;
			}
		}
	}

	@Override
	public boolean setUserSalt(String email, String salt) throws SQLException {
		String query = "INSERT INTO user_hash (email, salt) VALUES (?,?)";
		Connection con = instance.getConnectionSnaSalted();
		try (PreparedStatement ps = con.prepareStatement(query);) {
			ps.setString(1, email);
			ps.setString(2, salt);
			return ps.executeUpdate() == 1;
		}
	}

	@Override
	public Optional<String> getUserAuth(String selector, String validator) throws SQLException {
		Connection con = instance.getConnectionSna();
		String query = "SELECT email FROM user_token " + "JOIN user ON user_token.user_id_fk=user.id "
				+ "WHERE selector=? AND validator=? AND (CURRENT_TIMESTAMP() <  user_token.time)";
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setString(1, selector);
			ps.setString(2, validator);
			try (ResultSet result = ps.executeQuery();) {

				Optional<String> email = Optional.empty();
				if (result.first()) {
					email = Optional.of(result.getString("email"));
				}
				return email;

			}

		}
	}

	@Override
	public boolean setUserAuthToken(String selector, String validator, int userId, int seconds) throws SQLException {
		Connection con = instance.getConnectionSna();
		String query = "INSERT INTO user_token (selector, validator, user_id_fk, time) VALUES (?,?,?,TIMESTAMPADD(SECOND,?,NOW()))";
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setString(1, selector);
			ps.setString(2, validator);
			ps.setInt(3, userId);
			ps.setInt(4, seconds);
			return ps.executeUpdate() == 1;
		}
	}

	@Override
	public boolean updateUserAuthToken(String selector,int seconds, String validator) throws SQLException {
		Connection con = instance.getConnectionSna();
		String query = "UPDATE user_token SET validator=? , time=TIMESTAMPADD(SECOND,?,NOW()) WHERE selector=?";
		try (PreparedStatement ps = con.prepareStatement(query)) {
			ps.setString(1, validator);
			ps.setInt(2, seconds);
			ps.setString(3, selector);
			return ps.executeUpdate() == 1;
		}

	}

	@Override
	public OptionalInt getUserId(String email) throws SQLException {
		String query = "SELECT id FROM user WHERE email=?";
		Connection con = instance.getConnectionSna();
		try (PreparedStatement ps = con.prepareStatement(query);) {
			ps.setString(1, email);
			try (ResultSet result = ps.executeQuery();) {
				OptionalInt userId = OptionalInt.empty();
				if (result.first()) {
					userId = OptionalInt.of(result.getInt("id"));
				}
				return userId;
			}

		}
	}

}
