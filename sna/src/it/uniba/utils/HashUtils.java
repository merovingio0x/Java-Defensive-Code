package it.uniba.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class HashUtils {
	
	private HashUtils()
	{
		
	}
	
	public static MessageDigest getDigest()
	{
		try {
			return MessageDigest.getInstance("SHA-256");
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		} 
		
	}
	
	public static byte[] generateSalt(int n) {
		SecureRandom random = new SecureRandom();
		final byte bytes[] = new byte[n];
		random.nextBytes(bytes);
		return bytes;
	}

	public static void clearArray(byte[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = 0;
		}
	}
	
	public static byte[] appendArrays(byte[] a, byte[] b) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write(a);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			outputStream.write(b);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return outputStream.toByteArray();
	}
	
	public static byte[] toBytes(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
		return bytes;
	}

}
