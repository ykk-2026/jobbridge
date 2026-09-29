package org.ykk.jobbridge.recommendation;

import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IAiJobMatchService {

    record JobMatchAssessment(int score, String reason, String source) {
    }

    Optional<JobMatchAssessment> assess(JobSeekerProfileDTO profile, JobPostingDTO job);

    default Map<Long, JobMatchAssessment> assessBatch(JobSeekerProfileDTO profile,
                                                       List<JobPostingDTO> jobs) {
        Map<Long, JobMatchAssessment> results = new LinkedHashMap<>();
        for (JobPostingDTO job : jobs) {
            if (job == null || job.getId() == null) continue;
            assess(profile, job).ifPresent(assessment -> results.put(job.getId(), assessment));
        }
        return results;
    }
}
