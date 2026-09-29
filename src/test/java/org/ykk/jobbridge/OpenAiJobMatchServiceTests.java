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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAiJobMatchServiceTests {

    private HttpServer server;
    private final AtomicReference<String> requestBody = new AtomicReference<>();
    private final AtomicInteger requestCount = new AtomicInteger();

    @BeforeEach
    void startServer() throws Exception {
        requestCount.set(0);
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/v1/chat/completions", exchange -> {
            requestCount.incrementAndGet();
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            requestBody.set(body);
            String response = body.contains("\\\"postings\\\"")
                    ? """
                      {"choices":[{"message":{"role":"assistant","content":"{\\"assessments\\":[{\\"jobId\\":101,\\"score\\":25,\\"reason\\":\\"Good job fit.\\"},{\\"jobId\\":102,\\"score\\":12,\\"reason\\":\\"Partial job fit.\\"}]}"}}]}
                      """
                    : """
                      {"choices":[{"message":{"role":"assistant","content":"{\\"score\\":27,\\"matchedSkills\\":[\\"Java\\",\\"Spring Boot\\"],\\"missingSkills\\":[\\"AWS\\"],\\"reason\\":\\"The job skills match.\\"}"}}]}
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
        OpenAiJobMatchService service = service();

        JobSeekerProfileDTO profile = profile();
        JobPostingDTO job = new JobPostingDTO();
        job.setTitle("Spring Boot backend engineer");
        job.setJobCategory("Development");
        job.setRequirements("Java and Spring Boot API development");

        Optional<JobMatchAssessment> result = service.assess(profile, job);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().score()).isEqualTo(27);
        assertThat(result.orElseThrow().source()).isEqualTo("GENERATIVE_AI");

        JsonNode sent = new ObjectMapper().readTree(requestBody.get());
        assertThat(sent.path("model").asText()).isEqualTo("test-model");
        assertThat(sent.path("response_format").path("type").asText()).isEqualTo("json_schema");
        assertThat(sent.path("messages").get(0).path("role").asText()).isEqualTo("system");
        assertThat(sent.path("messages").get(1).path("role").asText()).isEqualTo("user");
        assertThat(requestBody.get()).doesNotContain(
                "private@example.com", "010-1234-5678", "Private Name");
    }

    @Test
    void assessesMultipleJobsWithOneHttpRequest() {
        OpenAiJobMatchService service = service();
        JobSeekerProfileDTO profile = profile();

        JobPostingDTO first = new JobPostingDTO();
        first.setId(101L);
        first.setTitle("Backend developer");
        first.setJobCategory("Server development");
        first.setRequirements("Java and Spring Boot");

        JobPostingDTO second = new JobPostingDTO();
        second.setId(102L);
        second.setTitle("Cloud engineer");
        second.setJobCategory("Infrastructure");
        second.setRequirements("AWS and Linux");

        Map<Long, JobMatchAssessment> results = service.assessBatch(profile, List.of(first, second));

        assertThat(requestCount.get()).isEqualTo(1);
        assertThat(results).hasSize(2);
        assertThat(results.get(101L).score()).isEqualTo(25);
        assertThat(results.get(102L).score()).isEqualTo(12);
        assertThat(requestBody.get()).contains("postings", "101", "102");
        assertThat(requestBody.get()).contains("json_object");
        assertThat(requestBody.get()).doesNotContain("reasoning_effort");
        assertThat(requestBody.get()).doesNotContain(
                "private@example.com", "010-1234-5678", "Private Name");
    }

    @Test
    void sendsOneHttpRequestForOneHundredJobs() {
        List<JobPostingDTO> jobs = LongStream.rangeClosed(1, 100)
                .mapToObj(id -> {
                    JobPostingDTO job = new JobPostingDTO();
                    job.setId(id);
                    job.setTitle("Job " + id);
                    job.setJobCategory("Category " + id);
                    job.setRequirements("Requirement " + id);
                    return job;
                })
                .toList();

        service().assessBatch(profile(), jobs);

        assertThat(requestCount.get()).isEqualTo(1);
        assertThat(requestBody.get()).contains("Job 1", "Job 100", "postings");
    }

    private OpenAiJobMatchService service() {
        return new OpenAiJobMatchService(
                true, "test-key", "test-model",
                "http://localhost:" + server.getAddress().getPort() + "/v1/chat/completions",
                new ObjectMapper());
    }

    private JobSeekerProfileDTO profile() {
        JobSeekerProfileDTO profile = new JobSeekerProfileDTO();
        profile.setName("Private Name");
        profile.setEmail("private@example.com");
        profile.setPhone("010-1234-5678");
        profile.setDesiredJob("Java developer");
        profile.setCareerType("EXPERIENCED");
        profile.setCareerYears(3);
        return profile;
    }
}
