package org.ykk.jobbridge.dto;

import java.util.List;

public record AdminOverviewDTO(
        long totalUsers,
        long totalCompanies,
        long totalJobs,
        long totalApplications,
        long newUsersThisMonth,
        long newJobsThisMonth,
        List<AdminMemberDTO> members,
        List<JobSummaryDTO> jobs
) {
}
