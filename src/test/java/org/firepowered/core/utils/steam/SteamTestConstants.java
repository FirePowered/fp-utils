package org.firepowered.core.utils.steam;

import org.firepowered.core.utils.StringUtils;

/**
 * Constants for SteamID and related tests.
 * 
 * @author Kyle Smith (smith.kyle1734@gmail.com)
 * @since 1.1
 */
interface SteamTestConstants {

    /** SteamID64 format equivalent */
    final static String ID_64 = "76561198091343023";

    /** SteamID64 format equivalent */
    final static String ID_64_OTHER = "76561198059316053";

    /** SteamID32 format equivalent */
    final static String ID_32 = "STEAM_0:1:65538647";

    /** Steam3 format equivalent */
    final static String ID_3 = "[U:1:131077295]";

    /** Custom url ID part */
    final static String CUSTOM_ID = "dragonbanshee";

    /** Custom url */
    final static String CUSTOM_URL = "https://steamcommunity.com/id/" + CUSTOM_ID;

    /** Profiles url with SteamID64 */
    final static String PROFILES_URL = "https://steamcommunity.com/profiles/" + ID_64;

    /** Fake custom ID */
    final static String CUSTOM_FAKE_ID = StringUtils.randomStringAlphaNum(32);

    /** Gabe Newell custom ID */
    final static String GABEN_ID = "GabeLoganNewell";

    /** Gabe Newell persona name */
    final static String GABEN_NAME = "Rabscuttle";
}
