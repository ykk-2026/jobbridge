package org.ykk.jobbridge.recommendation;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** 외부 JSON API(카카오, OpenAI)를 호출하는 공통 HTTP 클라이언트. 2xx가 아니면 예외를 던진다. */
final class JsonHttpClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();
    private final ObjectMapper objectMapper;

    JsonHttpClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    JsonNode get(String url, String authorization, Duration timeout) throws Exception {
        return send(HttpRequest.newBuilder(URI.create(url)).GET(), authorization, timeout);
    }

    JsonNode post(URI url, Object body, String authorization, Duration timeout) throws Exception {
        String json = objectMapper.writeValueAsString(body);
        return send(HttpRequest.newBuilder(url)
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8)), authorization, timeout);
    }

    private JsonNode send(HttpRequest.Builder builder, String authorization, Duration timeout) throws Exception {
        HttpRequest request = builder
                .timeout(timeout)
                .header("Authorization", authorization)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException(
                    request.uri().getHost() + " 요청 실패: HTTP " + response.statusCode());
        }
        return objectMapper.readTree(response.body());
    }
}
