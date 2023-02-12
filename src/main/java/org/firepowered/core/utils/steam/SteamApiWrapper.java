package org.firepowered.core.utils.steam;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

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
     * URL for GetPlayerSummaries api method. Has two required arguments:
     * <ul>
     * <li>{@code key} (API key)</li>
     * <li>{@code steamids} (an array of steamid64s)</li>
     * </ul>
     */
    private static final String ENDPOINT_PLAYER_SUMMARIES = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/";

    /**
     * URL for resolving a vanity (custom) profile url. Has two required arguments:
     * <ul>
     * <li>{@code key} (API key)</li>
     * <li>{@code vanityurl} (the custom id, the part after /id/)</li>
     * </ul>
     */
    private static final String ENDPOINT_VANITY_URL = "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/";

    private SteamApiWrapper() {
    }

    /**
     * Gets a Steam user's personaName (display name).
     * 
     * @param api     API key, must not be {@code null} or empty
     * @param steamid The {@link SteamID} object of the player, must not be
     *                {@code null}
     * @return The personaName, or {@code null} if the name was unable to be
     *         retrieved from the API
     * @throws IOException          If an error occurred while sending the request
     * @throws InterruptedException If the request is interrupted
     */
    public static String getPersonaName(String api, SteamID steamid) throws IOException, InterruptedException {
        assert !StringUtils.isEmpty(api) : "API key must not be empty";
        assert steamid != null : "SteamID must not be null";
        try {
            String res = GenericHttpGet.getString(new URI(ENDPOINT_PLAYER_SUMMARIES),
                    Map.of("key", api, "steamids", steamid.getSteamID64()));
            JSONArray players = new JSONObject(res).getJSONObject("response").getJSONArray("players");
            if (players.length() > 0) {
                return players.getJSONObject(0).getString("personaname");
            }
        } catch (URISyntaxException e) {
            // Won't happen because we know the URI is valid
            throw new AssertionError(e);
        } catch (NullPointerException | JSONException e) {
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
     * @return The SteamID of the resolved id, or {@code null}
     * @throws IOException            If an error occurred while sending the request
     * @throws InterruptedException   If the request is interrupted
     * @throws SteamIDParserException If the vanityUrl doesn't resolve to a SteamID
     *                                (or the resolved SteamID is invalid)
     */
    public static SteamID resolveVanityUrl(String api, String id)
            throws IOException, InterruptedException, SteamIDParserException {
        assert !StringUtils.isEmpty(api) : "API key must not be empty";
        assert !StringUtils.isEmpty(id) : "Vanity URL must not be empty";
        try {
            String res = GenericHttpGet.getString(new URI(ENDPOINT_VANITY_URL), Map.of("key", api, "vanityurl", id));
            JSONObject responseBody = new JSONObject(res).getJSONObject("response");

            int success = responseBody.getInt("success");
            if (success == 1) {
                return SteamID.of(responseBody.getString("steamid"));
            }

            // Message is an optional field that only appears if the ID didn't resolve
            throw new SteamIDParserException(responseBody.getString("message"), id);
        } catch (URISyntaxException e) {
            // Won't happen because we know the URI is valid
            throw new AssertionError(e);
        } catch (NullPointerException | JSONException e) {
            // If any of those chains were null, then the request failed so return null to
            // let the caller know
        }
        return null;
    }

}
