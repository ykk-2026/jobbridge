package org.ykk.jobbridge.recommendation;

import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

import java.util.Optional;





public interface IAiJobMatchService {


    record JobMatchAssessment(int score, String reason, String source) {
    }






    Optional<JobMatchAssessment> assess(JobSeekerProfileDTO profile, JobPostingDTO job);
}
