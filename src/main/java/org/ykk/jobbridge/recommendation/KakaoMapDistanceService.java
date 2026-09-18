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

/**
 * 카카오 API로 두 주소 사이의 자동차 경로(거리·시간)를 조회한다.
 * API 키가 없거나 호출에 실패하면 빈 Optional을 돌려주고, 호출한 쪽에서 행정구역 비교로 대신 계산한다.
 */
@Slf4j
@Service
public class KakaoMapDistanceService {

    /** 자동차 경로 요약. */
    public record DrivingRoute(int distanceMeters, int durationSeconds) {

        public double kilometers() {
            return distanceMeters / 1000.0;
        }

        /** 최소 1분. */
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
    /** 주소 → "경도,위도" (카카오 길찾기가 쓰는 순서) */
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

        // API 키가 없으면 카카오 호출하지 않고, 호출한 쪽에서 행정구역 이름으로 계산함
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
            // 카카오 호출이 실패해도 추천은 계속되어야 하기 때문에 로그만 남기고 빈 값을 돌려줌
            log.info("카카오 API 호출 실패 : " + e);
            return Optional.empty();
        }
    }

    /** 주소 → "경도,위도". 카카오 주소 검색 결과의 첫 번째 항목을 사용한다. */
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
