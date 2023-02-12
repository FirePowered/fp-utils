package org.firepowered.core.utils.steam;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link SteamID}.
 * 
 * @author Kyle Smith
 * @since 1.0
 */
public class SteamIDTest {
    private final String ID_64 = "76561198091343023";
    private final String ID_32 = "STEAM_0:1:65538647";
    private final String ID_3 = "[U:1:131077295]";
    private final String CUSTOM_ID = "dragonbanshee";
    private final String CUSTOM_URL = "https://steamcommunity.com/id/" + CUSTOM_ID;
    private final String PROFILES_URL = "https://steamcommunity.com/profiles/" + ID_64;

    private static String steamApi;

    @BeforeAll
    public static void readApi() {
        steamApi = ResourceBundle.getBundle("steamapi").getString("key");
    }

    @Test
    public void testOf() throws SteamIDParserException {
        Assertions.assertThrows(AssertionError.class, () -> SteamID.of(null));

        SteamID from64 = SteamID.of(ID_64);
        assertNotNull(from64);

        SteamID from32 = SteamID.of(ID_32);
        assertNotNull(from32);

        SteamID from3 = SteamID.of(ID_3);
        assertNotNull(from3);

        assertTrue(from64.equals(from32));
        assertTrue(from32.equals(from3));

        Assertions.assertThrows(SteamIDParserException.class, () -> SteamID.of("this is not a steamid!!!"));
    }

    @Test
    public void testToString() throws SteamIDParserException {
        SteamID id = SteamID.of(ID_64);
        assertTrue(ID_64.equals(id.toString()));

        assertTrue(ID_3.equals(id.getSteam3ID()));
        assertTrue(ID_32.equals(id.getSteamID32(true)));
    }

    @Test
    public void testCorrectedID32() throws SteamIDParserException {
        SteamID origin = SteamID.of(ID_32); // universe = 0
        SteamID next = SteamID.of(ID_32.replace("STEAM_0", "STEAM_1"));
        assertTrue(origin.equals(next)); // The steamids are equal...
        assertFalse(ID_32.equals(next.getSteamID32())); // But the origin one is printed with a 0 still even
                                                        // though it's really a 0
        assertTrue(ID_32.equals(origin.getSteamID32()));
    }

    @SuppressWarnings("unlikely-arg-type") // yes I am aware
    @Test
    public void testEquals() throws SteamIDParserException {
        SteamID id = SteamID.of(ID_3);
        assertFalse(id.equals("this is not a steamid!!!"));

        SteamID other = SteamID.of("76561198059316053");
        assertFalse(other.equals(id));
    }

    @Test
    public void testCustomAndUrl() throws SteamIDParserException, IOException, InterruptedException {
        SteamID master = SteamID.of(ID_64, steamApi);
        SteamID custom = SteamID.of(CUSTOM_URL, steamApi);
        SteamID custom_id = SteamID.of(CUSTOM_ID, steamApi);
        SteamID profiles = SteamID.of(PROFILES_URL, steamApi);

        SteamID[] toVerify = new SteamID[] { custom, custom_id, profiles };
        for (int i = 0; i < toVerify.length; i++) {
            assertTrue(master.equals(toVerify[i]));
        }

        Assertions.assertThrows(SteamIDParserException.class, () -> SteamID.of("firepowered_fake_test"));
    }

    @Test
    public void parserExceptionTest() {
        Assertions.assertThrows(AssertionError.class, () -> {
            throw new SteamIDParserException("Exception with no text!", null);
        });

        SteamIDParserException exc = Assertions.assertThrows(SteamIDParserException.class,
                () -> SteamID.of("this is not a steamid!!!"));
        assertTrue("this is not a steamid!!!".equals(exc.getSteamIDText()));
    }

}
