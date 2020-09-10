package it.uniba.utils;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Part;

import it.uniba.dao.FileDTO;
import it.uniba.dao.JdbcFacadeImpl;

public class Account {

	private final String email;
	private static final byte SALT_LENGTH = 12;
	private JdbcFacadeImpl database = new JdbcFacadeImpl();

	public Account(String email) {

		this.email = email;

	}

	public boolean checkIfExists(char[] chars) throws SQLException {

		byte[] pass = HashUtils.toBytes(chars);
		byte[] salt = Base64.getDecoder().decode(loadSalt().orElseThrow());

		byte[] input = HashUtils.appendArrays(pass, salt);
		MessageDigest msgDigest = HashUtils.getDigest();
		byte[] inputHash = msgDigest.digest(input);

		HashUtils.clearArray(pass);
		HashUtils.clearArray(input);

		byte[] pwHash = Base64.getDecoder().decode(loadPassword().orElseThrow());

		boolean arraysEqual = Arrays.equals(inputHash, pwHash);
		HashUtils.clearArray(inputHash);
		HashUtils.clearArray(pwHash);

		return arraysEqual;
	}

	public void create(char[] pw1, SanityFile fileManager) throws SQLException, IOException {

		byte[] pass = HashUtils.toBytes(pw1);
		byte[] salt = HashUtils.generateSalt(SALT_LENGTH);
		byte[] pass_salt = HashUtils.appendArrays(pass, salt);

		// hash passowrd and hash
		MessageDigest msgDigest = HashUtils.getDigest();
		byte[] hash_PwSalt = msgDigest.digest(pass_salt);

		// Encode the string and salt
		String base64_Salt = Base64.getEncoder().encodeToString(salt);
		String base64_PwSalt = Base64.getEncoder().encodeToString(hash_PwSalt);

		// Commit transaction on DB
		saveBytes(base64_PwSalt, base64_Salt,fileManager.getFile());

		HashUtils.clearArray(pass);
		HashUtils.clearArray(salt);
		HashUtils.clearArray(pass_salt);
		HashUtils.clearArray(hash_PwSalt);

	}
	

	private void saveBytes(String base64Pw, String base64Salt, Part file) throws SQLException, IOException {

		if (!database.setUser(this.email, base64Pw, file) || !database.setUserSalt(this.email, base64Salt)) {
			throw new IllegalArgumentException();
		}

	}

	private Optional<String> loadPassword() throws SQLException {
		return database.getUserPw(this.email);

	}

	private Optional<String> loadSalt() throws SQLException {

		return database.getUserSalt(this.email);

	}
	
	public Optional<String> loadImage() throws SQLException, IOException {

		return database.getUserImage(this.email);

	}
	
	public List<FileDTO> getProjectList() throws SQLException
	{
		return database.getAllProject(this.email);
	}
	
	public Optional<FileDTO> getProjectContent(String projectId) throws SQLException, IOException {

		return database.getProjectContent(this.email, projectId);

	}
	

	
	

}