package com.complete.plugin;

import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class HttpClientHelperTest {
    @Test
    public void throwsExceptionOnNon2xx() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/fail", exchange -> {
            byte[] body = "oops".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        int port = server.getAddress().getPort();
        HttpClientHelper client = new HttpClientHelper("http://localhost:" + port, "", 1000, 1000);
        JSONObject obj = new JSONObject();
        Exception ex = assertThrows(HttpRequestException.class, () -> client.post("/fail", obj));
        assertTrue(ex.getMessage().contains("oops"));
        server.stop(0);
    }
}
