package com.example.receiptprocessor.utility;

public class Objects {
	private Objects() {
		throw new IllegalStateException("Utility class");
	}
	/**
	 * Helps to avoid using {@code @SuppressWarnings({"unchecked"})} when casting to a generic type.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T uncheckedCast(Object obj) {
		return (T) obj;
	}
}
