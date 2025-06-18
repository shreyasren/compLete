package com.complete.plugin;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import com.complete.plugin.HttpRequestException;

public class HttpClientHelper {
    private final CloseableHttpClient client;
    private final String url;
    private final String apiKey;

    public HttpClientHelper(String url, String apiKey, int connectTimeout, int readTimeout) {
        this.url = url;
        this.apiKey = apiKey;
        RequestConfig cfg = RequestConfig.custom()
            .setConnectTimeout(connectTimeout)
            .setResponseTimeout(readTimeout)
            .build();
        this.client = HttpClients.custom().setDefaultRequestConfig(cfg).build();
    }

    public String post(String path, JSONObject body) throws Exception {
        HttpPost post = new HttpPost(url + path);
        post.setHeader("Authorization", "Bearer " + apiKey);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));

        return client.execute(post, resp -> {
            String respBody = new String(
                resp.getEntity().getContent().readAllBytes(),
                StandardCharsets.UTF_8
            );
            int code = resp.getCode();
            if (code < 200 || code >= 300) {
                throw new HttpRequestException(code, respBody);
            }
            return respBody;
        });
    }
}
