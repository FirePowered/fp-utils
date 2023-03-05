package org.firepowered.core.utils.steam;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link SteamID}.
 * 
 * @author Kyle Smith
 * @since 1.0
 */
@SuppressWarnings("javadoc")
public class SteamIDTest {

    @Test
    public void testOf() throws SteamIDParserException {
        Assertions.assertThrows(AssertionError.class, () -> SteamID.of(null));

        SteamID from64 = SteamID.of(SteamTestConstants.ID_64);
        assertNotNull(from64);

        SteamID from32 = SteamID.of(SteamTestConstants.ID_32);
        assertNotNull(from32);

        SteamID from3 = SteamID.of(SteamTestConstants.ID_3);
        assertNotNull(from3);

        assertTrue(from64.equals(from32));
        assertTrue(from32.equals(from3));

        Assertions.assertThrows(SteamIDParserException.class, () -> SteamID.of("this is not a steamid!!!"));
    }

    @Test
    public void testToString() throws SteamIDParserException {
        SteamID id = SteamID.of(SteamTestConstants.ID_64);
        assertTrue(SteamTestConstants.ID_64.equals(id.toString()));

        assertTrue(SteamTestConstants.ID_3.equals(id.getSteam3ID()));
        assertTrue(SteamTestConstants.ID_32.equals(id.getSteamID32(true)));
    }

    @Test
    public void testCorrectedID32() throws SteamIDParserException {
        SteamID origin = SteamID.of(SteamTestConstants.ID_32); // universe = 0
        SteamID next = SteamID.of(SteamTestConstants.ID_32.replace("STEAM_0", "STEAM_1"));
        assertTrue(origin.equals(next)); // The steamids are equal...
        assertFalse(SteamTestConstants.ID_32.equals(next.getSteamID32())); // But the origin one is printed with a 0
                                                                           // still even
        // though it's really a 0
        assertTrue(SteamTestConstants.ID_32.equals(origin.getSteamID32()));
    }

    @SuppressWarnings("unlikely-arg-type") // yes I am aware
    @Test
    public void testEquals() throws SteamIDParserException {
        SteamID id = SteamID.of(SteamTestConstants.ID_3);
        assertFalse(id.equals("this is not a steamid!!!"));

        SteamID other = SteamID.of(SteamTestConstants.ID_64_OTHER);
        assertFalse(other.equals(id));
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
