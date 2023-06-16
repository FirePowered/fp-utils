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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.firepowered.core.utils.StringUtils;

/**
 * Representation of a SteamID. Static methods exist for creating an instance
 * from a string and converting one type to another.
 * <p>
 * See <a href=
 * "https://developer.valvesoftware.com/wiki/SteamID">developer.valvesoftware.com</a>
 * for more details about the format and conversions.
 *
 * @author Kyle Smith
 * @since 1.0
 */
public final class SteamID {

    /**
     * The pattern to match a SteamID32.
     */
    private final static String PATTERNSTR_ID32 = "^STEAM_([0-5]):([0-1]):([0-9]+)$";
    private final static Pattern PATTERN_ID32 = Pattern.compile(PATTERNSTR_ID32);

    /**
     * The pattern to match a Steam3ID. Note that only individual accounts (starting
     * with U) are supported.
     */
    private final static String PATTERNSTR_3ID = "^\\[U:([0-5]):([0-9]+)\\]$";
    private final static Pattern PATTERN_3ID = Pattern.compile(PATTERNSTR_3ID);

    /**
     * The pattern to match a custom profile link.
     */
    private final static String PATTERNSTR_URL = "^https?://steamcommunity\\.com/id/([\\w-]+)$";
    private final static Pattern PATTERN_URL = Pattern.compile(PATTERNSTR_URL);

    /**
     * The pattern to match a profile link.
     */
    private final static String PATTERNSTR_PROFILES_URL = "^https?://steamcommunity\\.com/profiles/(\\d+)";
    private final static Pattern PATTERN_PROFILES_URL = Pattern.compile(PATTERNSTR_PROFILES_URL);

    /**
     * Thirty-two ones. Used to mask the "account" part of the SteamID64.
     */
    private final static long ACCOUNT_MASK = 0xffff_ffffL;

    /**
     * Twenty ones. Used to mask the "instance" part of the SteamID64 (after being
     * right-shifted 32 times).
     */
    private final static long INSTANCE_MASK = 0x000f_ffffL;

    /**
     * The type derived from {@link #of(String)}.
     */
    private final SteamIDType idType;

    /** The universe the account is in. Most common are 0 and 1. STEAM_X. */
    private long universe;

    /**
     * The account number. This is the unique identifier for each user. STEAM_X:Y:Z.
     * Thus completes the circle of life.
     */
    private long account;

    /**
     * This isn't really documented but it is part of the SteamID64 and Steam3ID
     * (because Steam3ID is calculated with w which is calculated using instance).
     * According to SteamKit2, instance can either be all (0), desktop (1), console
     * (4), or web (8).
     */
    private long instance;

    /**
     * The type of account. Whether it is an individual, clan, gameserver, etc.
     */
    private long type;

    /**
     * Some SteamID32s have a 0 as the universe (STEAM_0). We store them interally
     * as 1, but this flag allows callers to know if it was originally 0.
     */
    private boolean wasUniverseCorrected = false;

    /**
     * Creates a new SteamID.
     *
     * @param universe  {@link #universe}
     * @param type      {@link #type}
     * @param instance  {@link #instance}
     * @param account   {@link #account}
     * @param corrected {@link #wasUniverseCorrected}
     */
    private SteamID(long universe, long type, long instance, long account, boolean corrected, SteamIDType idType) {
        this.universe = universe;
        this.type = type;
        this.instance = instance;
        this.account = account;
        this.wasUniverseCorrected = corrected;
        this.idType = idType;
    }

    /**
     * Given an arbitrary string, try to convert it to a {@link SteamID} instance.
     * This method will also attempt to resolve a vanity url if the type can't be
     * determined otherwise.
     *
     * @param str The string representing the SteamID, must not be {@code null}
     * @return A SteamID object, or {@code null} if one could not be determined.
     * @throws SteamIDParserException If the {@link SteamIDType type} of SteamID
     *                                could not be determined
     */
    public static SteamID of(final String str) throws SteamIDParserException {
        assert !StringUtils.isEmpty(str);
        String idStr = str.strip();
        try {
            SteamID ret = null;
            if (idStr.matches("^\\d+$")) {
                // SteamID64
                ret = of64(idStr);
            } else if (idStr.matches(PATTERNSTR_ID32)) {
                // SteamID32
                ret = of32(idStr);
            } else if (idStr.matches(PATTERNSTR_3ID)) {
                // Steam3ID
                ret = ofSteam3(idStr);
            } else if (idStr.matches(PATTERNSTR_PROFILES_URL)) {
                // Normal profile url (/profiles/765..)
                ret = ofProfiles(idStr);
            } else {
                ret = ofCustom(idStr);
            }

            if (ret == null) {
                throw new SteamIDParserException("SteamIDType was not able to be determined.", idStr);
            }

            // Check if it's valid
            sanityCheckID(ret, str);
            return ret;
        } catch (IOException | InterruptedException e) {
            throw new SteamIDParserException("Unable to resolve vanity URL as a last resort", idStr);
        }
    }

    /**
     * Given an arbitrary string, try to convert it to a {@link SteamID} instance.
     * This method will also attempt to resolve a vanity url if the type can't be
     * determined otherwise.
     *
     * @param str The string representing the SteamID, must not be {@code null} or
     *            empty
     * @param api API key
     * @return A SteamID object, or {@code null} if one could not be determined.
     * @throws SteamIDParserException If the SteamID type was unable to be
     *                                determined or the calculated SteamID was
     *                                invalid
     * @throws IOException            If an error occurred while resolving the
     *                                custom url
     * @throws InterruptedException   If the http request is interrupted
     * @deprecated Use {@link #of(String)} instead, the API parameter is not used
     */
    @Deprecated(since = "1.2", forRemoval = true)
    public static SteamID of(final String str, final String api)
            throws SteamIDParserException, IOException, InterruptedException {
        return of(str);
    }

    private static SteamID ofProfiles(final String str) {
        Matcher matcher = PATTERN_PROFILES_URL.matcher(str);
        matcher.find();
        return of64(matcher.group(1));
    }

    private static SteamID ofCustom(final String str) throws SteamIDParserException, IOException, InterruptedException {
        Matcher matcher = PATTERN_URL.matcher(str);
        String query = str;
        if (matcher.find()) {
            // It is a URL, we need to extract the ID from the end
            query = matcher.group(1);
        }
        return SteamApiWrapper.resolveVanityUrl(query);
    }

    private static SteamID of64(final String str) throws NumberFormatException {
        long id = Long.parseLong(str);
        // This is a 64 bit integer that contains different parts of the steamid to be
        // extracted.
        // We can perform bitwise operations with masks to get the parts.
        long account = id & ACCOUNT_MASK;
        long instance = (id >> 32) & INSTANCE_MASK;
        long type = (id >> 52) & 0xf;
        long universe = (id >> 56);
        return new SteamID(universe, type, instance, account, false, SteamIDType.STEAMID_64);
    }

    private static SteamID of32(final String str) {
        Matcher matcher = PATTERN_ID32.matcher(str);
        matcher.find();
        long universe = Long.parseLong(matcher.group(1));
        boolean corrected = false;
        if (universe == 0) {
            universe = 1;
            corrected = true;
        }
        long type = 1;
        long instance = 1;
        long account = Long.parseLong(matcher.group(3)) * 2 + Long.parseLong(matcher.group(2));
        return new SteamID(universe, type, instance, account, corrected, SteamIDType.STEAMID_32);
    }

    private static SteamID ofSteam3(final String str) {
        Matcher matcher = PATTERN_3ID.matcher(str);
        matcher.find();
        long universe = Long.parseLong(matcher.group(1));
        long account = Long.parseLong(matcher.group(2));
        long instance = 1;
        long type = 1;
        return new SteamID(universe, type, instance, account, false, SteamIDType.STEAM3_ID);
    }

    @Override
    public String toString() {
        return getSteamID64();
    }

    /**
     * Gets the SteamID64 (7656119...) representation of the current SteamID.
     *
     * @return The SteamID64
     */
    public String getSteamID64() {
        long ret = (universe << 56) | (type << 52) | (instance << 32) | account;
        return Long.toString(ret);
    }

    /**
     * Gets the SteamID32 (STEAM_X:Y:Z) representation of the current SteamID. This
     * method will do the correct thing in most cases.
     * <ol>
     * <li>If {@link #of(String)} was called with the SteamID32, this will return
     * that form (one or zero).
     * <li>Otherwise it will return the calculated universe which should always be
     * one.
     * </ol>
     *
     * @return The SteamID32
     */
    public String getSteamID32() {
        return getSteamID32(false);
    }

    /**
     * Gets the SteamID32 (STEAM_X:Y:Z) representation of the current SteamID. This
     * returns the ID32 with a zero universe (STEAM_0) if {@link #of(String)} was
     * given the ID32, otherwise it returns a one universe (STEAM_1).
     * <p>
     * Most callers may want to use {@link #getSteamID32()} as it takes into account
     * the string that it was created with.
     *
     * @param zeroUniverse Whether to put '0' as the universe (STEAM_0)
     * @return The SteamID32
     */
    public String getSteamID32(boolean zeroUniverse) {
        return String.format("STEAM_%d:%d:%d", wasUniverseCorrected || zeroUniverse ? 0 : universe, account & 1,
                (long) Math.floor(account / 2));
    }

    /**
     * Performs sanity checking on the created ID to make sure it's valid.
     *
     * @param id   The id to check
     * @param text The text which caused the SteamID to be created
     * @throws AssertionError If there was an internal error sanity checking the ID
     *                        (i.e., the created SteamID wasn't valid)
     */
    private static void sanityCheckID(SteamID id, String text) {
        String id64 = id.getSteamID64();
        final String ID64_START = "7656119"; // all SteamID64 must start with this
        if (!id64.startsWith(ID64_START)) {
            String substr = null;
            if (id64.length() >= ID64_START.length()) {
                substr = id64.substring(0, 7);
            } else {
                substr = id64;
            }
            throw new AssertionError(
                    String.format("Incorrect render of SteamID64 (expected %s at start, got %s) for input %s",
                            ID64_START, substr, text));
        }
        if (id.idType != SteamIDType.STEAMID_32 && id.universe == 0) {
            throw new AssertionError(String.format("Universe '0' in a non-SteamID32 for input %s", text));
        }
        // TODO Add more checks, although this might be all we need/is possible
    }

    /**
     * Gets the Steam3ID ([U:X:Y]) representation of the current SteamID. Note that
     * only individual accounts (starting with U) are supported.
     *
     * @return The Steam3ID.
     */
    public String getSteam3ID() {
        return String.format("[U:%d:%d]", universe, account);
    }

    private enum SteamIDType {
        STEAMID_64, STEAMID_32, STEAM3_ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, instance, type, universe);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SteamID)) {
            return false;
        }
        SteamID other = (SteamID) obj;
        return account == other.account && instance == other.instance && type == other.type
                && universe == other.universe;
    }
}