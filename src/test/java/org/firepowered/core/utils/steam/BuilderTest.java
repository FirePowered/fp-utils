package org.firepowered.core.utils.steam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;

import org.firepowered.core.utils.steam.Builder.Format;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class BuilderTest {

    private Builder base = SteamApiWrapper.newBuilder().apiInterface("i").method("m").version("1");

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testCloneEquals() {
        Builder based = (Builder) base.clone();
        Builder cloned = (Builder) based.clone();
        assertEquals(based, cloned);

        assertEquals(based.hashCode(), cloned.hashCode());

        assertEquals(based, based);
        assertFalse(based.equals("abc"));

        based.apiInterface("i2");
        assertNotEquals(based, cloned);

        based.method("m2");
        assertNotEquals(based, cloned);

        based.version("2");
        assertNotEquals(based, cloned);

        based.format(Format.XML);
        assertNotEquals(based, cloned);

        based.key("a");
        assertNotEquals(based, cloned);

        based.param("key1", "val1");
        assertNotEquals(based, cloned);
    }

    @Test
    public void testKey() {
        String key = "123";
        Builder b = (Builder) base.clone();
        b.key(key);

        Builder o = (Builder) base.clone();
        o.key(key.toCharArray());

        assertEquals(b, o);

        String url = b.build();
        assertTrue(url.endsWith("key=123"));

        b.key(new char[] { '1', '2', '3' });
        assertTrue(b.build().endsWith("key=123"));
    }

    @Test
    public void testFormat() {
        Builder b = (Builder) base.clone();
        assertTrue(b.build().endsWith("?format=json"));

        b.format(null);
        assertTrue(b.build().endsWith("?format=json"));

        b.format(Format.XML);
        assertTrue(b.build().endsWith("?format=xml"));

        b.format(Format.VDF);
        assertTrue(b.build().endsWith("?format=vdf"));
    }

    @Test
    public void testParams() {
        String key1 = "key1";
        String key2 = "key2";
        String val1 = "val1";
        String val2 = "val2";

        Map<String, String> pMap = Map.of(key1, val1, key2, val2);
        String[] keys = { key1, key2 };
        String[] vals = { val1, val2 };

        // 5 overloads
        final String[] PARAMS = { key1, val1, key2, val2 };
        final int ITER_CNT = 5;
        for (int i = 0; i < ITER_CNT; i++) {
            Builder b = (Builder) base.clone();
            switch (i) {
            case 0:
                b.param(key1, val1);
                b.param(key2, val2);
                break;
            case 1:
                b.param(Arrays.asList(keys), Arrays.asList(vals));
                break;
            case 2:
                b.param(keys, vals);
                break;
            case 3:
                b.param(PARAMS);
                break;
            case 4:
                b.param(pMap);
            }
            assertParam(b, PARAMS);
        }
    }

    private void assertParam(Builder b, String... params) {
        assert params.length == 4;
        String expected = MessageFormat.format("{0}={1}&{2}={3}", params[0], params[1], params[2], params[3]);
        assertTrue(b.build().endsWith(expected));
    }

    @Test
    public void testCall() {
        Builder b = new Builder();
        Assertions.assertThrows(AssertionError.class, () -> {
            // This throws because we haven't specified anything
            new Builder().build();
        });

        try {
            String page = b.apiInterface("ISteamNews").method("GetNewsForApp").version("v0002").param("appid", "440")
                    .call();
            assertTrue(!page.isEmpty());
        } catch (IOException e) {
            assertTrue(false);
        }
    }

}
