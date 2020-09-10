package it.uniba.utils;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Sanity {

	private Sanity() {
	}

	public static boolean regexMail(String email) {
		
		//RFC 5322 without ' | ` =
		//Both the local part and the domain name can contain one or more dots, but no two dots can appear right next to each other. 
		//Furthermore, the first and last characters in the local part and in the domain name must not be dots.
		//Domain name must include at least one dot, and that the part of the domain name after the last dot can only consist of letters.
		
		String regex = "^[\\w!#$%&*+?{}~^-]+(?:\\.[\\w!#$%&*+?{}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		System.out.println("Valid EMAIL->" + matcher.matches());
		return matcher.matches();

	}
	
	public static boolean regexPassword(char[] chars) {

//		Be between 8 and 40 characters long
//		Contain at least one digit.
//		Contain at least one lower case character.
//		Contain at least one upper case character.
//		Contain at least on special character from [ @ # $ % ! . ].
		final String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})";

		System.out.println("Valid PSW->" + Pattern.matches(PASSWORD_PATTERN, CharBuffer.wrap(chars)));
		return Pattern.matches(PASSWORD_PATTERN, CharBuffer.wrap(chars));

	}

	public static boolean matchingPassword(char[] pw1, char[] pw2) {

		return (regexPassword(pw1) && Arrays.equals(pw1, pw2));

	}


}
