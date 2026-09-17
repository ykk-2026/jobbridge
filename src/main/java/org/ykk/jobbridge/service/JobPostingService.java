package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.JobPostingDTO;

import java.util.List;

public interface JobPostingService {

    List<JobPostingDTO> getOpenJobs(int limit);

    JobPostingDTO getJob(Long jobId);

    List<JobPostingDTO> getMyJobs(Long companyMemberId);

    JobPostingDTO createJob(Long companyMemberId, JobPostingDTO jobPosting);

    JobPostingDTO updateJob(Long companyMemberId, Long jobId, JobPostingDTO jobPosting);

    void closeJob(Long companyMemberId, Long jobId);

    void deleteJob(Long companyMemberId, Long jobId);

    long countJobs();
}
