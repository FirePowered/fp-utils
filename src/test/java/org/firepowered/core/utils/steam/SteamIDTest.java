package org.firepowered.core.utils.steam;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link SteamID}.
 * 
 * @author Kyle Smith
 * @since 1.0
 */
@SuppressWarnings("javadoc")
public class SteamIDTest {
    private final String ID_64 = "76561198091343023";
    private final String ID_32 = "STEAM_0:1:65538647";
    private final String ID_3 = "[U:1:131077295]";
    private final String CUSTOM_ID = "dragonbanshee";
    private final String CUSTOM_URL = "https://steamcommunity.com/id/" + CUSTOM_ID;
    private final String PROFILES_URL = "https://steamcommunity.com/profiles/" + ID_64;

    @Test
    public void testOf() {
        try {
            SteamID.of(null);
        } catch (AssertionError | SteamIDParserException e) {
            assertTrue(true);
        }

        SteamID from64 = null;
        try {
            from64 = SteamID.of(ID_64);
            assertNotNull(from64);
        } catch (SteamIDParserException e) {
            assertTrue(false);
        }

        SteamID from32 = null;
        try {
            from32 = SteamID.of(ID_32);
            assertNotNull(from32);
        } catch (SteamIDParserException e) {
            assertTrue(false);
        }

        SteamID from3 = null;
        try {
            from3 = SteamID.of(ID_3);
            assertNotNull(from3);
        } catch (SteamIDParserException e) {
            assertTrue(false);
        }

        assertTrue(from64.equals(from32));
        assertTrue(from32.equals(from3));

        try {
            SteamID.of("this is not a steamid!!!");
        } catch (SteamIDParserException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testToString() {
        try {
            SteamID id = SteamID.of(ID_64);
            String expected = String.format("steamID64: %s\nsteamID32: %s\nsteam3ID: %s", ID_64, ID_32, ID_3);
            assertFalse(expected.equals(id.toString()));
        } catch (SteamIDParserException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testCorrectedID32() {
        try {
            SteamID origin = SteamID.of(ID_32); // universe = 0
            SteamID next = SteamID.of(ID_32.replace("STEAM_0", "STEAM_1"));
            assertTrue(origin.equals(next)); // The steamids are equal...
            assertFalse(ID_32.equals(next.getSteamID32(false))); // But the origin one is printed with a 0 still even
                                                                 // though it's
            // really a 0
            assertTrue(ID_32.equals(origin.getSteamID32(false)));
        } catch (SteamIDParserException e) {
            assertTrue(false);
        }
    }

    @SuppressWarnings("unlikely-arg-type") // yes I am aware
    @Test
    public void testEquals() {
        try {
            SteamID id = SteamID.of(ID_3);
            assertFalse(id.equals("this is not a steamid!!!"));

            SteamID other = SteamID.of("76561198059316053");
            assertFalse(other.equals(id));
        } catch (SteamIDParserException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testCustomAndUrl() {
        try {
            SteamID master = SteamID.of(ID_64);
            SteamID custom = SteamID.of(CUSTOM_URL);
            SteamID custom_id = SteamID.of(CUSTOM_ID);
            SteamID profiles = SteamID.of(PROFILES_URL);

            SteamID[] toVerify = new SteamID[] { custom, custom_id, profiles };
            for (int i = 0; i < toVerify.length; i++) {
                assertTrue(master.equals(toVerify[i]));
            }

            // Hopefully nobody takes this
            try {
                SteamID.of("firepowered_fake_test");
            } catch (SteamIDParserException e) {
                assertTrue(true);
            }
        } catch (SteamIDParserException e) {
            assertTrue(false);
        }
    }
}
