package com.util;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Generates random OTP tokens using the letters A-Z and digits 0-9.
 */
public class DefaultOtpGenerator {
	private static final char[] CHARS = {'0', '1', '2', '3',
		'4', '5', '6', '7', '8', '9'};
	
	private Random rand;
	private int length;
		
	/**
	 * Creates a new generator.
	 * @param length String length of tokens that will be generated.
	 */
	public DefaultOtpGenerator(int length) {
		this.length = length;
		this.rand = new SecureRandom();
	}

	public String generateToken() {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(CHARS[rand.nextInt(CHARS.length)]);
		}		
		return sb.toString();
	}
	
	public DefaultOtpGenerator() {
		
	}
}