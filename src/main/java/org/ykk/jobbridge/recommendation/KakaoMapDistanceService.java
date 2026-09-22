package org.ykk.jobbridge.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.ykk.jobbridge.util.TextUtils.compact;
import static org.ykk.jobbridge.util.TextUtils.isBlank;
import static org.ykk.jobbridge.util.TextUtils.trimToEmpty;

@Slf4j
@Service
public class KakaoMapDistanceService {

    public record DrivingRoute(int distanceMeters, int durationSeconds) {

        public double kilometers() {
            return distanceMeters / 1000.0;
        }

        public int minutes() {
            return Math.max(1, (int) Math.round(durationSeconds / 60.0));
        }
    }

    private static final String GEOCODING_URL = "https://dapi.kakao.com/v2/local/search/address.json?query=";
    private static final String DIRECTIONS_URL =
            "https://apis-navi.kakaomobility.com/v1/directions?priority=RECOMMEND&summary=true";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final boolean enabled;
    private final String apiKey;
    private final JsonHttpClient http;

    private final Map<String, String> coordinateCache = new ConcurrentHashMap<>();
    private final Map<String, DrivingRoute> routeCache = new ConcurrentHashMap<>();

    public KakaoMapDistanceService(
            @Value("${kakao.enabled:true}") boolean enabled,
            @Value("${kakao.rest-api-key:}") String apiKey,
            ObjectMapper objectMapper) {
        this.enabled = enabled;
        this.apiKey = trimToEmpty(apiKey);
        this.http = new JsonHttpClient(objectMapper);
    }

    public boolean isEnabled() {
        return enabled && !apiKey.isBlank();
    }

    public Optional<DrivingRoute> findDrivingRoute(String originAddress, String destinationAddress) {

        log.info(this.getClass().getName() + ".findDrivingRoute Start!");

        if (!isEnabled() || isBlank(originAddress) || isBlank(destinationAddress)) {
            return Optional.empty();
        }

        log.info("origin : " + originAddress + " / destination : " + destinationAddress);

        String cacheKey = compact(originAddress) + "->" + compact(destinationAddress);
        DrivingRoute cached = routeCache.get(cacheKey);
        if (cached != null) return Optional.of(cached);

        try {
            Optional<String> origin = geocode(originAddress);
            Optional<String> destination = geocode(destinationAddress);
            if (origin.isEmpty() || destination.isEmpty()) return Optional.empty();

            String url = DIRECTIONS_URL + "&origin=" + origin.get() + "&destination=" + destination.get();
            JsonNode summary = http.get(url, authorization(), TIMEOUT).path("routes").path(0).path("summary");
            if (!summary.has("distance") || !summary.has("duration")) return Optional.empty();

            DrivingRoute route = new DrivingRoute(
                    summary.path("distance").asInt(), summary.path("duration").asInt());
            routeCache.put(cacheKey, route);
            return Optional.of(route);
        } catch (Exception e) {

            log.info("카카오 API 호출 실패 : " + e);
            return Optional.empty();
        }
    }

    private Optional<String> geocode(String address) throws Exception {
        String cacheKey = compact(address);
        String cached = coordinateCache.get(cacheKey);
        if (cached != null) return Optional.of(cached);

        String url = GEOCODING_URL + URLEncoder.encode(address.trim(), StandardCharsets.UTF_8);
        JsonNode documents = http.get(url, authorization(), TIMEOUT).path("documents");
        if (!documents.isArray() || documents.isEmpty()) return Optional.empty();

        JsonNode first = documents.get(0);
        String coordinate = first.path("x").asText() + "," + first.path("y").asText();
        coordinateCache.put(cacheKey, coordinate);
        return Optional.of(coordinate);
    }

    private String authorization() {
        return "KakaoAK " + apiKey;
    }
}
