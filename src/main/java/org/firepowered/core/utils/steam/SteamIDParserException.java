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

/**
 * An Exception class representing that there was an error parsing a
 * {@link SteamID}. The original text can be accessed using the
 * {@link #getSteamIDText()} method.
 * 
 * @author Kyle Smith
 * @since 1.0
 */
public final class SteamIDParserException extends Exception {

    private static final long serialVersionUID = -4079398366098895944L;

    private String text;

    /**
     * Creates a new exception with the given message and the text that tried to be
     * a SteamID.
     * 
     * @param message Exception message, passed to the constructor of
     *                {@link Exception}
     * @param text    The SteamID text that caused the exception, must not be
     *                {@code null}
     */
    SteamIDParserException(String message, String text) {
        super(message);
        assert text != null : "Null SteamID text";
        this.text = text;
    }

    /**
     * Gets the text that caused the Exception to be created, never {@code null}.
     *
     * @return The text
     */
    public String getSteamIDText() {
        return text;
    }
}