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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Map;

import org.apache.hc.core5.net.URIBuilder;

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
     * Performs a GET request on the given {@code uri} and returns the body as a
     * string. To use query parameters, call {@link #getString(URI, Map)} with a
     * {@link Map} of key/values instead.
     * 
     * @param uri The URI
     * @return The response body
     * @throws URISyntaxException   If the URI is invalid
     * @throws IOException          IF there is an Exception while sending the
     *                              request
     * @throws InterruptedException If the request is interrupted
     */
    public static String getString(URI uri) throws URISyntaxException, IOException, InterruptedException {
        return getString(uri, Collections.emptyMap());
    }

    /**
     * Performs a GET request on the given {@code uri} with the given query
     * parameters and returns the body as a string.
     * 
     * @param uri        The URI
     * @param parameters Query parameters as key-value pairs
     * @return The response body
     * @throws URISyntaxException   If the URI is invalid
     * @throws IOException          IF there is an Exception while sending the
     *                              request
     * @throws InterruptedException If the request is interrupted
     */
    public static String getString(URI uri, Map<String, String> parameters)
            throws URISyntaxException, IOException, InterruptedException {
        URIBuilder builder = new URIBuilder(uri);
        parameters.forEach((k, v) -> builder.addParameter(k, v));
        HttpRequest req = HttpRequest.newBuilder(builder.build()).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        return res.body();
    }

}
