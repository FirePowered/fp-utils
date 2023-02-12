package org.firepowered.core.utils.steam;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link SteamApiWrapper}.
 * 
 * @author Kyle Smith
 * @since 1.0
 */
public class SteamApiWrapperTest {

    private static String steamApi;

    @BeforeAll
    public static void readApi() {
        steamApi = ResourceBundle.getBundle("steamapi").getString("key");
    }

    @Test
    public void testGetPersonaName() throws SteamIDParserException, IOException, InterruptedException {
        Assertions.assertThrows(AssertionError.class, () -> SteamApiWrapper.getPersonaName(null, null));
        Assertions.assertThrows(AssertionError.class, () -> SteamApiWrapper.getPersonaName(steamApi, null));

        SteamID id = SteamID.of("GabeLoganNewell", steamApi);
        String personaName = SteamApiWrapper.getPersonaName(steamApi, id);
        assertEquals("Rabscuttle", personaName);

        assertEquals(null, SteamApiWrapper.getPersonaName("fake api key", id));
    }

    @Test
    public void testResolveVanityUrl() throws IOException, InterruptedException, SteamIDParserException {
        Assertions.assertThrows(AssertionError.class, () -> SteamApiWrapper.resolveVanityUrl(null, null));
        Assertions.assertThrows(AssertionError.class, () -> SteamApiWrapper.resolveVanityUrl(steamApi, null));

        String fakeId = "firepowered_fake_test";
        SteamIDParserException exc = Assertions.assertThrows(SteamIDParserException.class,
                () -> SteamApiWrapper.resolveVanityUrl(steamApi, fakeId));
        assertEquals("firepowered_fake_test", exc.getSteamIDText());

        assertEquals(null, SteamApiWrapper.resolveVanityUrl("fake api key", "GabeLoganNewell"));
    }
}
