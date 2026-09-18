package org.ykk.jobbridge;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.recommendation.IAiJobMatchService.JobMatchAssessment;
import org.ykk.jobbridge.recommendation.OpenAiJobMatchService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAiJobMatchServiceTests {

    private HttpServer server;
    private final AtomicReference<String> requestBody = new AtomicReference<>();

    @BeforeEach
    void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/v1/chat/completions", exchange -> {
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            String response = """
                    {"choices":[{"message":{"role":"assistant","content":"{\\"score\\":27,\\"matchedSkills\\":[\\"Java\\",\\"Spring Boot\\"],\\"missingSkills\\":[\\"AWS\\"],\\"reason\\":\\"백엔드 기술과 담당업무가 일치합니다.\\"}"}}]}
                    """;
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void returnsStructuredAiAssessmentWithoutSendingContactInformation() throws Exception {
        OpenAiJobMatchService service = new OpenAiJobMatchService(
                true, "test-key", "test-model",
                "http://localhost:" + server.getAddress().getPort() + "/v1/chat/completions",
                new ObjectMapper());

        JobSeekerProfileDTO profile = new JobSeekerProfileDTO();
        profile.setName("홍길동");
        profile.setEmail("private@example.com");
        profile.setPhone("010-1234-5678");
        profile.setDesiredJob("Java 백엔드 개발자");
        profile.setCareerType("EXPERIENCED");
        profile.setCareerYears(3);

        JobPostingDTO job = new JobPostingDTO();
        job.setTitle("Spring Boot 백엔드 엔지니어");
        job.setJobCategory("개발");
        job.setRequirements("Java와 Spring Boot API 개발 경험");

        Optional<JobMatchAssessment> result = service.assess(profile, job);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().score()).isEqualTo(27);
        assertThat(result.orElseThrow().source()).isEqualTo("GENERATIVE_AI");

        JsonNode sent = new ObjectMapper().readTree(requestBody.get());
        assertThat(sent.path("model").asText()).isEqualTo("test-model");
        assertThat(sent.path("response_format").path("type").asText()).isEqualTo("json_schema");
        assertThat(sent.path("messages").get(0).path("role").asText()).isEqualTo("system");
        assertThat(sent.path("messages").get(1).path("role").asText()).isEqualTo("user");
        assertThat(requestBody.get()).doesNotContain("private@example.com", "010-1234-5678", "홍길동");
    }
}
