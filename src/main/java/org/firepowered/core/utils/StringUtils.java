package org.firepowered.core.utils;

/**
 * Common {@link String} utility methods. All methods can handle {@code null}s
 * safely, further details in their javadocs.
 * 
 * @author Kyle Smith
 * @since 1.0
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Returns whether a string is {@code null} or empty (either an empty string or
     * a string with only whitespace.
     * 
     * @param str The string, may be {@code null} as {@code null} strings are
     *            considered empty
     * @return True if the string is empty, false otherwise
     * @since 1.0
     */
    public static boolean isEmpty(String str) {
        return str == null || str.strip().length() == 0;
    }

}
