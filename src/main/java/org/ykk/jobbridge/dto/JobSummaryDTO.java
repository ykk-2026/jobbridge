package org.ykk.jobbridge.dto;

import java.time.LocalDate;

public record JobSummaryDTO(
        String id,
        String company,
        String title,
        String location,
        int score,
        int applicants,
        String status,
        LocalDate posted
) {
}
