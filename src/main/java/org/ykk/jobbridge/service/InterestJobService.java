package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.InterestJobDTO;

import java.util.List;

public interface InterestJobService {

    List<InterestJobDTO> getInterestJobs(Long memberId);

    InterestJobDTO saveInterestJob(Long memberId, InterestJobDTO interestJob);

    void deleteInterestJob(Long memberId, String companyName, String title);
}
