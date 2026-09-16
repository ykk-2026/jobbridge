package org.ykk.jobbridge.dto;

import java.util.List;

public class AdminOverviewDTO {

    private long totalUsers;
    private long totalCompanies;
    private long totalJobs;
    private long totalApplications;
    private long newUsersThisMonth;
    private long newJobsThisMonth;
    private List<AdminMemberDTO> members;
    private List<JobSummaryDTO> jobs;

    public AdminOverviewDTO(long totalUsers, long totalCompanies, long totalJobs,
                            long totalApplications, long newUsersThisMonth,
                            long newJobsThisMonth, List<AdminMemberDTO> members,
                            List<JobSummaryDTO> jobs) {
        this.totalUsers = totalUsers;
        this.totalCompanies = totalCompanies;
        this.totalJobs = totalJobs;
        this.totalApplications = totalApplications;
        this.newUsersThisMonth = newUsersThisMonth;
        this.newJobsThisMonth = newJobsThisMonth;
        this.members = members;
        this.jobs = jobs;
    }

    public long getTotalUsers() { return totalUsers; }
    public long getTotalCompanies() { return totalCompanies; }
    public long getTotalJobs() { return totalJobs; }
    public long getTotalApplications() { return totalApplications; }
    public long getNewUsersThisMonth() { return newUsersThisMonth; }
    public long getNewJobsThisMonth() { return newJobsThisMonth; }
    public List<AdminMemberDTO> getMembers() { return members; }
    public List<JobSummaryDTO> getJobs() { return jobs; }
}
