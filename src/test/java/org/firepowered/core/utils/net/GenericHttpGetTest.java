package org.firepowered.core.utils.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class GenericHttpGetTest {

    @Test
    public void testGet() throws URISyntaxException, IOException, InterruptedException {
        String url = "https://firepoweredgaming.com/core";

        String res = GenericHttpGet.getString(new URI(url));
        assertEquals("Success", res);
    }
}
