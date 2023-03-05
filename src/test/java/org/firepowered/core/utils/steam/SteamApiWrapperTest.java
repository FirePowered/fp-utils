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
package org.firepowered.core.utils.steam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
@SuppressWarnings("javadoc")
public class SteamApiWrapperTest {

    /* Set to some nonempty default so test doesn't fail for wrong exception */
    private static String steamApi = "default_api";

    @BeforeAll
    public static void readApi() {
        steamApi = ResourceBundle.getBundle("steamapi").getString("key");
    }

    @Test
    public void testCustomAndUrl() throws SteamIDParserException, IOException, InterruptedException {
        SteamID master = SteamID.of(SteamTestConstants.ID_64, steamApi);
        SteamID custom = SteamID.of(SteamTestConstants.CUSTOM_URL, steamApi);
        SteamID custom_id = SteamID.of(SteamTestConstants.CUSTOM_ID, steamApi);
        SteamID profiles = SteamID.of(SteamTestConstants.PROFILES_URL, steamApi);

        SteamID[] toVerify = new SteamID[] { custom, custom_id, profiles };
        for (int i = 0; i < toVerify.length; i++) {
            assertTrue(master.equals(toVerify[i]));
        }

        Assertions.assertThrows(SteamIDParserException.class, () -> SteamID.of(SteamTestConstants.CUSTOM_FAKE_ID));
    }

    @Test
    public void testGetPersonaName() throws SteamIDParserException, IOException, InterruptedException {
        Assertions.assertThrows(AssertionError.class, () -> SteamApiWrapper.getPersonaName(null, null));
        Assertions.assertThrows(AssertionError.class, () -> SteamApiWrapper.getPersonaName(steamApi, null));

        SteamID id = SteamID.of(SteamTestConstants.GABEN_ID, steamApi);
        String personaName = SteamApiWrapper.getPersonaName(steamApi, id);
        assertEquals(SteamTestConstants.GABEN_NAME, personaName);

        assertEquals(null, SteamApiWrapper.getPersonaName("fake api key", id));
    }

    @Test
    public void testResolveVanityUrl() throws IOException, InterruptedException, SteamIDParserException {
        Assertions.assertThrows(AssertionError.class, () -> SteamApiWrapper.resolveVanityUrl(null, null));
        Assertions.assertThrows(AssertionError.class, () -> SteamApiWrapper.resolveVanityUrl(steamApi, null));

        SteamIDParserException exc = Assertions.assertThrows(SteamIDParserException.class,
                () -> SteamApiWrapper.resolveVanityUrl(steamApi, SteamTestConstants.CUSTOM_FAKE_ID));
        assertEquals(SteamTestConstants.CUSTOM_FAKE_ID, exc.getSteamIDText());

        assertEquals(null, SteamApiWrapper.resolveVanityUrl("fake api key", SteamTestConstants.GABEN_ID));
    }
}
