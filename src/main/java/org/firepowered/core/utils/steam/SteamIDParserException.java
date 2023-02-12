package org.firepowered.core.utils.steam;

/**
 * An Exception class representing that there was an error parsing a
 * {@link SteamID}. The original text can be accessed using the
 * {@link #getSteamIDText()} method.
 * 
 * @author Kyle Smith
 * @since 1.0
 */
final class SteamIDParserException extends Exception {

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