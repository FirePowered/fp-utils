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

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.firepowered.core.utils.StringUtils;
import org.firepowered.core.utils.net.GenericHttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Wraps Steam API calls. Many methods here require an API key.
 *
 * @author Kyle Smith
 * @since 1.0
 */
public final class SteamApiWrapper {

    /**
     * Base URL for Steam community.
     */
    private static final String COMMUNITY_BASE = "https://steamcommunity.com";

    private static final Pattern STEAMID_MATCHER = Pattern.compile("<steamID64>(\\d+)<\\/steamID64>");

    private static final Pattern NAME_PATTERN = Pattern.compile("<steamID><!\\[CDATA\\[(.*)\\]{2}><\\/steamID>");

    private SteamApiWrapper() {
    }

    /**
     * Gets a Steam user's personaName (display name).
     *
     * @param steamid The {@link SteamID} object of the player, must not be
     *                {@code null}
     * @return The personaName, or {@code null} if the name was unable to be
     *         retrieved from the API
     */
    public static String getPersonaName(SteamID steamid) {
        assert steamid != null;
        String page;
        try {
            page = GenericHttpGet.getString(COMMUNITY_BASE + "/profiles/" + steamid.getSteamID64(),
                    Map.of("xml", "true"));
            Matcher m = NAME_PATTERN.matcher(page);
            if (m.find()) {
                return m.group(1);
            }
        } catch (IOException e) {
            //$FALL-THROUGH$
        }
        return null;
    }

    /**
     * Gets a Steam user's personaName (display name).
     *
     * @param api     API key, must not be {@code null} or empty
     * @param steamid The {@link SteamID} object of the player, must not be
     *                {@code null}
     * @return The personaName, or {@code null} if the name was unable to be
     *         retrieved from the API
     * @deprecated Use {@link #getPersonaName(SteamID)} instead, this call does not
     *             require an API key
     */
    @Deprecated(since = "1.2", forRemoval = true)
    public static String getPersonaName(String api, SteamID steamid) {
        assert !StringUtils.isEmpty(api) : "API key must not be empty";
        assert steamid != null : "SteamID must not be null";
        try {
            Builder builder = newBuilder().apiInterface(INTERFACE_STEAMUSER).method("GetPlayerSummaries")
                    .version("v0002").key(api.toCharArray()).param("steamids", steamid.getSteamID64());
            String res = builder.call();
            JSONArray players = new JSONObject(res).getJSONObject("response").getJSONArray("players");
            if (players.length() > 0) {
                return players.getJSONObject(0).getString("personaname");
            }
        } catch (NullPointerException | JSONException | IOException e) {
            // If any of those chains were null, then the request failed so return null to
            // let the caller know
        }
        return null;
    }

    /**
     * Attempts to resolve a vanity url (custom profile URL) into the
     * {@link SteamID} of the user to which it belongs. If the given {@code id} does
     * not resolve successfully, a {@link SteamIDParserException} is thrown with the
     * reason as the message.
     *
     * @param api API key, must not be {@code null} or empty
     * @param id  The vanityUrl (the part {@code (here)} in
     *            {@code https://steamcommunity.com/id/(here)}, must not be
     *            {@code null} or empty
     * @return The SteamID of the resolved id, or {@code null} if there was an error
     * @throws SteamIDParserException If the vanityUrl doesn't resolve to a SteamID
     *                                (or the resolved SteamID is invalid)
     * @deprecated Use {@link #resolveVanityUrl(String)} instead, this call does not
     *             require an API key
     */
    @Deprecated(since = "1.2", forRemoval = true)
    public static SteamID resolveVanityUrl(String api, String id) throws SteamIDParserException {
        assert !StringUtils.isEmpty(api) : "API key must not be empty";
        assert !StringUtils.isEmpty(id) : "Vanity URL must not be empty";
        try {
            Builder apiBuilder = newBuilder().apiInterface(INTERFACE_STEAMUSER).method("ResolveVanityURL")
                    .version("v0001").param("vanityurl", id).key(api.toCharArray());
            String res = apiBuilder.call();
            JSONObject responseBody = new JSONObject(res).getJSONObject("response");

            int success = responseBody.getInt("success");
            if (success == 1) {
                return SteamID.of(responseBody.getString("steamid"));
            }

            // Message is an optional field that only appears if the ID didn't resolve
            throw new SteamIDParserException(responseBody.getString("message"), id);
        } catch (NullPointerException | JSONException | IOException e) {
            // If any of those chains were null, then the request failed so return null to
            // let the caller know
        }
        return null;
    }

    /**
     * Attempts to resolve a vanity url (custom profile URL) into the
     * {@link SteamID} of the user to which it belongs.
     *
     * @param id The vanityUrl (the part {@code (here)} in
     *           {@code https://steamcommunity.com/id/(here)}, must not be
     *           {@code null} or empty
     * @return The SteamID of the resolved id, or {@code null} if it could not be
     *         determined
     * @throws SteamIDParserException If the retrieved SteamID is not valid
     */
    public static SteamID resolveVanityUrl(String id) throws SteamIDParserException {
        assert !StringUtils.isEmpty(id);
        String page;
        try {
            page = GenericHttpGet.getString(COMMUNITY_BASE + "/id/" + id, Map.of("xml", "true"));
            Matcher m = STEAMID_MATCHER.matcher(page);
            if (m.find()) {
                id = m.group(1);
                return SteamID.of(id);
            }
        } catch (IOException e) {
            //$FALL-THROUGH$
        }
        return null;
    }

    /**
     * Creates a new API wrapper builder.
     *
     * @return A blank builder
     * @see Builder
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /** Steam provides methods to fetch news feeds for each Steam game. */
    public static String INTERFACE_STEAMNEWS = "ISteamNews";

    /** Steam provides methods to fetch global stat information by game. */
    public static String INTERFACE_STEAMUSERSTATS = "ISteamUserStats";

    /** Steam provides API calls to provide information about Steam users. */
    public static String INTERFACE_STEAMUSER = "ISteamUser";

    /**
     * Team Fortress 2 provides API calls to use when accessing player item data.
     */
    public static String INTERFACE_TFITEMS = "ITFItems_440";
}
