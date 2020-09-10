package it.uniba.dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.Cookie;

import it.uniba.utils.HashUtils;

public class CookieUtils {

	private static Map<String, Cookie> cookieMap = new HashMap<>();
	private Cookie cookieSelector;
	private Cookie cookieValidator;
	private JdbcFacadeImpl database = new JdbcFacadeImpl();
	private static final int VALIDATOR_LENGTH = 70;
	private static final int SELECTOR_LENGTH = 30;
	private static final int COOKIE_EXPIRATION_SECONDS = 60 * 60 * 24;
	private static final int EXPIRED = -1;

	/**
	 * @return the cookieSelector
	 */

	public void cookieLogout() {
		createValidator("", EXPIRED);
		createSelector("", EXPIRED);

	}

	public Cookie getCookieSelector() {

		return cookieSelector;
	}

	/**
	 * @return the cookieValidator
	 */
	public Cookie getCookieValidator() {
		return cookieValidator;
	}

	private static Object findCookie(Cookie[] cookies, String value) {
		cookieMap.clear();
		if (cookies != null) {
			for (Cookie cookie : cookies) {

				cookieMap.put(cookie.getName(), cookie);
			}
			if (cookieMap.get(value) != null) {
				return cookieMap.get(value).getValue();
			}
		}
		return null;
	}

	public static String getSelectorValue(Cookie[] cookies) {

		return (String) findCookie(cookies, "selector");

	}

	public static String getValidatorValue(Cookie[] cookies) {
		return (String) findCookie(cookies, "validator");

	}

	private void createSelector(String selector, int maxAge) {
		cookieSelector = new Cookie("selector", selector);
		cookieSelector.setMaxAge(maxAge);
		cookieSelector.setHttpOnly(true);
		cookieSelector.setPath("/");
	}

	private void createValidator(String validator, int maxAge) {
		cookieValidator = new Cookie("validator", validator);
		cookieValidator.setMaxAge(maxAge);
		cookieValidator.setHttpOnly(true);
		cookieValidator.setPath("/");

	}

	public boolean createAuthCookie(int userId) throws SQLException {

		System.out.println("Cookie creation!");
		//Generate a pair selector-validator of random strings 
		String plaintextSelector = generateSelectorValue();
		String plaintextValidator = generateValidatorValue();
		//Hash the validator value
		//Set on database the pair selector-HashedValidator
		//Set the cookie expiry on database
		if (database.setUserAuthToken(plaintextSelector, getDigest(plaintextValidator), userId,
				COOKIE_EXPIRATION_SECONDS-60)) {
			//Generate cookie Object for client
			System.out.println("Cookie Created!!");
			createSelector(plaintextSelector, COOKIE_EXPIRATION_SECONDS);
			createValidator(plaintextValidator, COOKIE_EXPIRATION_SECONDS);
			return true;
		}
		return false;
	}

	
	
	public boolean updateAuthCookie(String selector) throws SQLException {

		String plaintextValidator = generateValidatorValue();

		if (database.updateUserAuthToken(selector, COOKIE_EXPIRATION_SECONDS-60, getDigest(plaintextValidator))) {

			createValidator(plaintextValidator, COOKIE_EXPIRATION_SECONDS);
			createSelector(selector, COOKIE_EXPIRATION_SECONDS);
			return true;
		}
		return false;

	}

	public Optional<String> checkAuthCookie(String plaintextSelector, String plaintextValidator) throws SQLException {
		String hashedValidator = getDigest(plaintextValidator);

		return database.getUserAuth(plaintextSelector, hashedValidator);
	}

	
	private String generateValidatorValue() {
		return Base64.getEncoder().encodeToString(HashUtils.generateSalt(VALIDATOR_LENGTH));
	}

	private String generateSelectorValue() {

		return Base64.getEncoder().encodeToString(HashUtils.generateSalt(SELECTOR_LENGTH));

	}

	private String getDigest(String random) {
		MessageDigest msgDigest = HashUtils.getDigest();
		msgDigest.reset();
		msgDigest.update(random.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(msgDigest.digest());

	}

}
