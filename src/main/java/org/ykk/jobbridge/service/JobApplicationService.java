package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.JobApplicationDTO;

import java.util.List;

public interface JobApplicationService {

    List<JobApplicationDTO> getApplications(Long memberId);

    JobApplicationDTO apply(Long memberId, JobApplicationDTO application);
}
