package org.ykk.jobbridge.external.kead;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class KeadJobApiClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Value("${kead.api.url}")
    private String apiUrl;

    @Value("${kead.api.service-key:}")
    private String serviceKey;

    @Value("${kead.api.enabled:true}")
    private boolean enabled;

    public KeadJobXmlParser.Page getJobs(int pageNo, int numOfRows) throws Exception {
        if (!enabled) throw new IllegalStateException("KEAD API가 비활성화되어 있습니다.");
        if (serviceKey == null || serviceKey.isBlank()) {
            throw new IllegalStateException("KEAD_API_SERVICE_KEY 환경변수를 등록해 주세요.");
        }

        String separator = apiUrl.contains("?") ? "&" : "?";
        String url = apiUrl + separator
                + "serviceKey=" + URLEncoder.encode(serviceKey.trim(), StandardCharsets.UTF_8)
                + "&pageNo=" + pageNo
                + "&numOfRows=" + numOfRows;

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/xml")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException("KEAD API 요청 실패: HTTP " + response.statusCode());
        }
        return KeadJobXmlParser.parse(response.body());
    }
}
