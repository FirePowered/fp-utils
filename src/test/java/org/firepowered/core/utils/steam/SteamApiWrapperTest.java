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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link SteamApiWrapper}.
 *
 * @author Kyle Smith
 * @since 1.0
 */
@SuppressWarnings("javadoc")
public class SteamApiWrapperTest {

    @Test
    public void testCustomAndUrl() throws SteamIDParserException, IOException, InterruptedException {
        SteamID master = SteamID.of(SteamTestConstants.ID_64);
        SteamID custom = SteamID.of(SteamTestConstants.CUSTOM_URL);
        SteamID custom_id = SteamID.of(SteamTestConstants.CUSTOM_ID);
        SteamID profiles = SteamID.of(SteamTestConstants.PROFILES_URL);

        SteamID[] toVerify = new SteamID[] { custom, custom_id, profiles };
        for (SteamID element : toVerify) {
            assertTrue(master.equals(element));
        }

        assertThrows(SteamIDParserException.class, () -> SteamID.of(SteamTestConstants.CUSTOM_FAKE_ID));
    }

    @Test
    public void testGetPersonaName() throws SteamIDParserException, IOException, InterruptedException {
        assertThrows(AssertionError.class, () -> SteamApiWrapper.getPersonaName(null));

        SteamID id = SteamID.of(SteamTestConstants.GABEN_ID);
        String personaName = SteamApiWrapper.getPersonaName(id);
        assertEquals(SteamTestConstants.GABEN_NAME, personaName);
    }

    @Test
    public void testResolveVanityUrl() throws IOException, InterruptedException, SteamIDParserException {
        assertNull(SteamApiWrapper.resolveVanityUrl(SteamTestConstants.CUSTOM_FAKE_ID));
        assertEquals(SteamID.of(SteamTestConstants.ID_64),
                SteamApiWrapper.resolveVanityUrl(SteamTestConstants.CUSTOM_ID));
        assertThrows(AssertionError.class, () -> SteamApiWrapper.resolveVanityUrl(null));
    }
}
