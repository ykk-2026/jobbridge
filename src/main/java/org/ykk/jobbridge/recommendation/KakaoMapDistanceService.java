package org.ykk.jobbridge.recommendation;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KakaoMapDistanceService {

    private static final String GEOCODING_URL =
            "https://dapi.kakao.com/v2/local/search/address.json?query=";
    private static final String DIRECTIONS_URL =
            "https://apis-navi.kakaomobility.com/v1/directions?priority=RECOMMEND&summary=true";

    private final String apiKey;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final Map<String, Coordinate> coordinateCache = new ConcurrentHashMap<>();
    private final Map<String, RouteInfo> routeCache = new ConcurrentHashMap<>();

    public KakaoMapDistanceService(
            @Value("${kakao.rest-api-key:}") String apiKey,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    public Optional<RouteInfo> findDrivingRoute(String originAddress, String destinationAddress) {
        if (!isEnabled() || isBlank(originAddress) || isBlank(destinationAddress)) {
            return Optional.empty();
        }

        String cacheKey = normalize(originAddress) + "->" + normalize(destinationAddress);
        RouteInfo cached = routeCache.get(cacheKey);
        if (cached != null) return Optional.of(cached);

        try {
            Optional<Coordinate> origin = findCoordinate(originAddress);
            Optional<Coordinate> destination = findCoordinate(destinationAddress);
            if (origin.isEmpty() || destination.isEmpty()) return Optional.empty();

            String url = DIRECTIONS_URL
                    + "&origin=" + origin.get().longitude + "," + origin.get().latitude
                    + "&destination=" + destination.get().longitude + "," + destination.get().latitude;
            JsonNode body = requestJson(url);
            JsonNode summary = body.path("routes").path(0).path("summary");
            if (!summary.has("distance") || !summary.has("duration")) return Optional.empty();

            RouteInfo route = new RouteInfo(
                    summary.path("distance").asInt(),
                    summary.path("duration").asInt());
            routeCache.put(cacheKey, route);
            return Optional.of(route);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    public boolean isEnabled() {
        return !apiKey.isBlank();
    }

    private Optional<Coordinate> findCoordinate(String address) throws Exception {
        String cacheKey = normalize(address);
        Coordinate cached = coordinateCache.get(cacheKey);
        if (cached != null) return Optional.of(cached);

        String encodedAddress = URLEncoder.encode(address.trim(), StandardCharsets.UTF_8);
        JsonNode documents = requestJson(GEOCODING_URL + encodedAddress).path("documents");
        if (!documents.isArray() || documents.isEmpty()) return Optional.empty();

        JsonNode first = documents.get(0);
        Coordinate coordinate = new Coordinate(
                first.path("y").asDouble(),
                first.path("x").asDouble());
        coordinateCache.put(cacheKey, coordinate);
        return Optional.of(coordinate);
    }

    private JsonNode requestJson(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .header("Authorization", "KakaoAK " + apiKey)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Kakao API request failed: " + response.statusCode());
        }
        return objectMapper.readTree(response.body());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    private static class Coordinate {
        private final double latitude;
        private final double longitude;

        private Coordinate(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    public static class RouteInfo {
        private final int distanceMeters;
        private final int durationSeconds;

        public RouteInfo(int distanceMeters, int durationSeconds) {
            this.distanceMeters = distanceMeters;
            this.durationSeconds = durationSeconds;
        }

        public int getDistanceMeters() {
            return distanceMeters;
        }

        public int getDurationSeconds() {
            return durationSeconds;
        }
    }
}
