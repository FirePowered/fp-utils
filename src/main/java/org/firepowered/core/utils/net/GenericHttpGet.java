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
package org.firepowered.core.utils.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

/**
 * Utility for generic GET requests. This class can either be used by itself or
 * to help implement another utility.
 *
 * @author Kyle Smith
 * @since 1.0
 */
public final class GenericHttpGet {

    private GenericHttpGet() {
    }

    /**
     * Performs a GET request on the given {@code url} and returns the body as a
     * string.
     *
     * @param url The URL
     * @return The response body
     * @throws IOException If there is an Exception while sending the request
     */
    public static String getString(URL url) throws IOException {
        return getString(url, Collections.emptyMap());
    }

    /**
     * Performs a GET request on the given {@code url} and returns the body as a
     * string.
     *
     * @param url The URL
     * @return The response body
     * @throws MalformedURLException If the given url is not valid
     * @throws IOException If there is an Exception while sending the request
     */
    public static String getString(String url) throws MalformedURLException, IOException {
        return getString(new URL(url));
    }

    /**
     * Performs a GET request on the given {@code url} with the given query
     * parameters and returns the body as a string.
     *
     * @param url        The URL
     * @param parameters Query parameters as key-value pairs
     * @return The response body
     * @throws MalformedURLException If the given url is not valid
     * @throws IOException           If there is an Exception while sending the
     *                               request
     */
    public static String getString(String url, Map<String, String> parameters) throws MalformedURLException, IOException {
        return getString(new URL(url), parameters);
    }

    /**
     * Performs a GET request on the given {@code url} with the given query
     * parameters and returns the body as a string.
     *
     * @param url        The URL
     * @param parameters Query parameters as key-value pairs
     * @return The response body
     * @throws IOException If there is an Exception while sending the request
     */
    public static String getString(URL url, Map<String, String> parameters) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (!parameters.isEmpty()) {
            conn.setDoOutput(true);
            try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
                dos.writeBytes(createParamString(parameters));
            }
        }

        StringBuilder res = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                res.append(line);
            }
        } finally {
            conn.disconnect();
        }
        return res.toString();

    }

    /**
     * Creates a querystring ({@code key1=value1&key2=value2}, etc.) with the given
     * parameter map.
     *
     * @param params The parameter map
     * @return The querystring
     */
    public static String createParamString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            result.append(param.getKey()).append('=').append(param.getValue()).append('&');
        }
        String res = result.toString();
        if (!res.isEmpty()) {
            res = res.substring(0, res.length() - 1);
        }
        return res;
    }

}
