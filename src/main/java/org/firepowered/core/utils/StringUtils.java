/**
 *  Copyright (C) 2023 FirePowered LLC.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.firepowered.core.utils;

import java.util.Random;

/**
 * Common {@link String} utility methods. All methods can handle {@code null}s
 * safely, further details in their javadocs.
 *
 * @author Kyle Smith
 * @since 1.0
 */
public final class StringUtils {

    private static final String ALPHA_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA_LOWER = ALPHA_UPPER.toLowerCase();
    private static final String NUMBERS = "0123456789";
    private static final String SPECIALS = "!@#$%^&*_-";
    private static final Random RNG = new Random();

    private static final String ALPHANUMERIC = ALPHA_UPPER + ALPHA_LOWER + NUMBERS;

    /**
     * Flag indicating to include alphabetical characters (letters) when generating
     * a string.
     */
    public static final int RANDOM_ALPHA = 1 << 0;

    /**
     * Flag indicating to include numbers when generating a string.
     */
    public static final int RANDOM_NUM = 1 << 1;

    /**
     * Flag indicating to include special characters ({@code !@#$%^&*_-}) when
     * generating a string.
     */
    public static final int RANDOM_SPEC = 1 << 2;

    private StringUtils() {
    }

    /**
     * Returns whether a string is {@code null} or empty (either an empty string or
     * a string with only whitespace.
     *
     * @param str The string, may be {@code null} as {@code null} strings are
     *            considered empty
     * @return True if the string is empty, false otherwise
     */
    public static boolean isEmpty(String str) {
        return str == null || str.strip().length() == 0;
    }

    private static String randomStringInternal(int length, String chars) {
        assert length > 0 : "A non-zero length must be provided";
        if (isEmpty(chars)) {
            // This is an internal error that should never be reached
            throw new RuntimeException("Empty charset");
        }
        char[] res = new char[length];
        for (int i = 0; i < length; i++) {
            int idx = RNG.nextInt(chars.length());
            res[i] = chars.charAt(idx);
        }
        return new String(res);
    }

    /**
     * Generates a random string of the given length. This version only includes
     * alphanumeric characters ({@code [A-Za-z0-9]}).
     *
     * @param length Length of the string, must be greater than 0
     * @return A random string
     */
    public static String randomStringAlphaNum(int length) {
        return randomStringInternal(length, ALPHANUMERIC);
    }

    /**
     * Generates a random string of the given length with the given settings. The
     * {@code mode} argument is a bitstring containing values from {@code RANDOM_*}
     * to determine which characters should be included in the generated string.
     * <p>
     * If {@code mode} is less than 0, {@link #randomStringAlphaNum(int)} will be
     * called to generate a random alphanumeric string as the default.
     *
     * @param length Length of the string to generate, must be greater than 0
     * @param mode   Bitstring of {@code RANDOM_*} flags defined in this class
     * @return A random string
     * @throws IllegalArgumentException If {@code mode} doesn't include any of the
     *                                  flags
     * @see StringUtils#RANDOM_ALPHA
     * @see StringUtils#RANDOM_NUM
     * @see StringUtils#RANDOM_SPEC
     */
    public static String randomString(int length, int mode) {
        if (mode <= 0) {
            return randomStringAlphaNum(length);
        }
        String chars = "";
        if ((mode & RANDOM_ALPHA) == RANDOM_ALPHA) {
            chars += ALPHA_UPPER + ALPHA_LOWER;
        }

        if ((mode & RANDOM_NUM) == RANDOM_NUM) {
            chars += NUMBERS;
        }

        if ((mode & RANDOM_SPEC) == RANDOM_SPEC) {
            chars += SPECIALS;
        }

        if (isEmpty(chars)) {
            throw new IllegalArgumentException(
                    "Invalid mode bitstring as no character sets were selected for inclusion");
        }
        return randomStringInternal(length, chars);
    }
}
