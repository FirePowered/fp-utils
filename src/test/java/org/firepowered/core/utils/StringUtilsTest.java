package org.firepowered.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testing {@link StringUtils}.
 * 
 * @author Kyle Smith (smith.kyle1734@gmail.com)
 * @since 1.0
 */
@SuppressWarnings("javadoc")
public class StringUtilsTest {

    @Test
    public void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty("not empty"));
        assertTrue(StringUtils.isEmpty("     "));
    }

    @Test
    public void testRandomString_default() {
        int mode = 0;
        String random = StringUtils.randomString(5, mode);
        assertTrue(Pattern.matches("[A-Za-z0-9]+", random));

        final int finalMode = 4096;
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.randomString(5, finalMode));
    }

    @Test
    public void testRandomString_alpha() {
        int mode = StringUtils.RANDOM_ALPHA;
        String random = StringUtils.randomString(5, mode);
        assertTrue(Pattern.matches("[A-Za-z]{5}", random));
    }

    @Test
    public void testRandomString_num() {
        int mode = StringUtils.RANDOM_NUM;
        String random = StringUtils.randomString(5, mode);
        Integer.valueOf(random);
        assertTrue(true);
    }

    @Test
    public void testRandomString_spec() {
        int mode = StringUtils.RANDOM_SPEC;
        String random = StringUtils.randomString(5, mode);
        assertTrue(Pattern.matches("[!@#$%^&*-_]{5}", random));
    }

    @Test
    public void testRandomStringAlphaNum() {
        String rand = StringUtils.randomStringAlphaNum(5);
        assertEquals(5, rand.length());

        Assertions.assertThrows(AssertionError.class, () -> StringUtils.randomStringAlphaNum(0));
    }
}
