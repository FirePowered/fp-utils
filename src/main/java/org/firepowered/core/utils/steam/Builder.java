package org.firepowered.core.utils.steam;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.firepowered.core.utils.StringUtils;
import org.firepowered.core.utils.net.GenericHttpGet;

/**
 * A builder for an API call. It is necessary to specify an interface with
 * {@link #apiInterface(String)}, a method with {@link #method(String)}, and a
 * version with {@link #version(String)}. Some API calls may require a key, in
 * which {@link #key(char[])} must be specified.
 *
 * @since 1.2
 */
public class Builder implements Cloneable {

    /**
     * The data from the API call will be returned in this format.
     *
     * @since 1.2
     */
    public enum Format {

        /** JSON format, this is the default */
        JSON,

        /** XML format */
        XML,

        /** VDF (Valve data format) format */
        VDF
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(key);
        result = prime * result + Objects.hash(apiInterface, format, method, param, version);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Builder)) {
            return false;
        }
        Builder other = (Builder) obj;
        return Objects.equals(apiInterface, other.apiInterface) && format == other.format
                && Arrays.equals(key, other.key) && Objects.equals(method, other.method)
                && Objects.equals(param, other.param) && Objects.equals(version, other.version);
    }

    private static final String BASE_URL_FMT = "http://api.steampowered.com/{0}/{1}/{2}/";

    private String apiInterface;
    private String method;
    private String version;
    private char[] key;
    private Format format = Format.JSON;
    private Map<String, String> param;

    /**
     * Creates a new empty builder.
     */
    Builder() {
        this.param = new LinkedHashMap<>();
    }

    /**
     * Sets the interface for this builder. This should be one of
     * {@code INTERFACE_*}.
     *
     * @param apiInterface The interface, may not be {@code null} or empty
     * @return this
     */
    public Builder apiInterface(String apiInterface) {
        assert !StringUtils.isEmpty(apiInterface);
        this.apiInterface = apiInterface;
        return this;
    }

    /**
     * Sets the api method to call.
     *
     * @param method The method, may not be {@code null} or empty
     * @return this
     */
    public Builder method(String method) {
        assert !StringUtils.isEmpty(method);
        this.method = method;
        return this;
    }

    /**
     * Sets the method version to use. This may or may not start with a {@code v}.
     * If it does not, one will be added.
     *
     * @param version The version, may not be {@code null} or empty
     * @return this
     */
    public Builder version(String version) {
        assert !StringUtils.isEmpty(version);
        version = version.toLowerCase();
        if (version.charAt(0) != 'v') {
            version = "v" + version;
        }
        this.version = version;
        return this;
    }

    /**
     * Sets the API key for this request. Some API calls may not require a key.
     *
     * @param key The key, may not be {@code null} or empty
     * @return this
     */
    public Builder key(char[] key) {
        assert key != null && key.length > 0;
        this.key = key;
        return this;
    }

    /**
     * Sets the API key for this request. Some API calls may not require a key.
     * <p>
     * It is recommended to use {@link #key(char[])} to avoid passing objects which
     * may be visible in bytecode.
     *
     * @param key The key, may not be {@code null} or empty
     * @return this
     */
    public Builder key(String key) {
        assert !StringUtils.isEmpty(key);
        this.key = key.toCharArray();
        return this;
    }

    /**
     * Sets the format of the returned data. If this is not specified,
     * {@link Format#JSON} is used.
     *
     * @param format The format, if {@code null} it will not be set and the default
     *               (JSON) will be used
     * @return this
     * @see Format
     */
    public Builder format(Format format) {
        if (format == null) {
            format = Format.JSON;
        }
        this.format = format;
        return this;
    }

    /**
     * Sets a request parameter. This will appear in the URL as {@code ?key=val}.
     *
     * @param key The key
     * @param val The value
     * @return this
     */
    public Builder param(String key, String val) {
        this.param.put(key, val);
        return this;
    }

    /**
     * Sets request parameters. These will appear in the URL as {@code ?key=val} for
     * each item in the list. The lists must be the same size.
     *
     * @param keys The keys
     * @param vals The values
     * @return this
     */
    public Builder param(List<String> keys, List<String> vals) {
        return param(keys.toArray(String[]::new), vals.toArray(String[]::new));
    }

    /**
     * Sets request parameters. These will appear in the URL as {@code ?key=val} for
     * each item in the list. The lists must be the same size.
     *
     * @param keys The keys
     * @param vals The values
     * @return this
     */
    public Builder param(String[] keys, String[] vals) {
        assert keys.length == vals.length : "Differing number of keys and values";
        for (int i = 0; i < keys.length; i++) {
            param(keys[i], vals[i]);
        }
        return this;
    }

    /**
     * Sets request parameters. These will appear in the URL as {@code ?key=val} for
     * each item in the list.
     * <p>
     * The arguments are similar to the many {@link Map#of(Object, Object)}
     * overrides, for example {@code param("Key 1", "Value 1", "Key 2", "Value 2")}
     * etc.
     *
     * @param items The items to set
     * @return this
     */
    public Builder param(String... items) {
        assert items.length % 2 == 0 : "Differing number of keys and values";
        for (int i = 0; i < items.length; i += 2) {
            param(items[i], items[i + 1]);
        }
        return this;
    }

    /**
     * Sets request parameters. These will appear in the URL as {@code ?key=val} for
     * each item in this map. This method <b>adds</b> to, not replaces, the
     * parameters, so it is safe to use after already setting others.
     *
     * @param params The parameter map
     * @return this
     */
    public Builder param(Map<String, String> params) {
        params.forEach(this::param);
        return this;
    }

    /**
     * Returns a copy of this builder by recreating it with all the initialized
     * fields.
     *
     * @return The new builder
     */
    @Override
    public Object clone() {
        try {
            Builder clone = (Builder) super.clone();
            clone.param(new LinkedHashMap<>(param));
            return clone;
        } catch (CloneNotSupportedException e) {
            // will not happen
        }
        return null;
    }

    /**
     * Converts this builder into a URL. Interface, method, and version must have
     * been set prior to calling this.
     *
     * @return The URL representing the builder
     */
    public String build() {
        assert !(StringUtils.isEmpty(apiInterface) || StringUtils.isEmpty(method) || StringUtils.isEmpty(version))
                : "An interface, method, and version must be specified";

        StringBuilder url = new StringBuilder(MessageFormat.format(BASE_URL_FMT, apiInterface, method, version));
        url.append("?format=").append(format.name().toLowerCase());
        if (key != null && key.length > 0) {
            url.append("&key=").append(new String(key));
        }

        for (Map.Entry<String, String> param : this.param.entrySet()) {
            url.append('&').append(param.getKey()).append('=').append(param.getValue());
        }
        return url.toString();
    }

    /**
     * Performs an API call with the given builder's settings.
     *
     * @return The response
     * @throws IOException If there was an error sending the request
     */
    public String call() throws IOException {
        String url = build();
        try {
            return GenericHttpGet.getString(new URL(url));
        } catch (MalformedURLException e) {
            // Consider as an internal error
            throw new AssertionError(e);
        }
    }

}