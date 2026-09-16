package org.ykk.jobbridge.service.serviceImpl;

import org.springframework.stereotype.Service;
import org.ykk.jobbridge.dto.InterestJobDTO;
import org.ykk.jobbridge.mapper.InterestJobMapper;
import org.ykk.jobbridge.service.InterestJobService;

import java.util.List;

@Service
public class InterestJobServiceImpl implements InterestJobService {

    private final InterestJobMapper interestJobMapper;

    public InterestJobServiceImpl(InterestJobMapper interestJobMapper) {
        this.interestJobMapper = interestJobMapper;
    }

    @Override
    public List<InterestJobDTO> getInterestJobs(Long memberId) {
        return interestJobMapper.findAll(memberId);
    }

    @Override
    public InterestJobDTO saveInterestJob(Long memberId, InterestJobDTO interestJob) {
        validate(interestJob);
        normalize(interestJob);
        interestJob.setMemberId(memberId);

        int savedCount = interestJobMapper.countByCompanyAndTitle(
                memberId, interestJob.getCompanyName(), interestJob.getTitle());
        if (savedCount == 0) {
            interestJobMapper.insert(interestJob);
        }
        return interestJob;
    }

    @Override
    public void deleteInterestJob(Long memberId, String companyName, String title) {
        interestJobMapper.deleteByCompanyAndTitle(memberId, companyName.trim(), title.trim());
    }

    private void validate(InterestJobDTO interestJob) {
        if (interestJob == null
                || isBlank(interestJob.getCompanyName())
                || isBlank(interestJob.getTitle())
                || isBlank(interestJob.getLocation())) {
            throw new IllegalArgumentException("관심 공고의 필수 정보가 없습니다.");
        }
    }

    private void normalize(InterestJobDTO interestJob) {
        interestJob.setCompanyName(interestJob.getCompanyName().trim());
        interestJob.setTitle(interestJob.getTitle().trim());
        interestJob.setLocation(interestJob.getLocation().trim());

        if (isBlank(interestJob.getJobCategory())) {
            interestJob.setJobCategory("기타");
        }
        if (isBlank(interestJob.getEmploymentType())) {
            interestJob.setEmploymentType("FULL_TIME");
        }
        if (isBlank(interestJob.getStatus())) {
            interestJob.setStatus("OPEN");
        }
        if (interestJob.getWheelchairAccessible() == null) {
            interestJob.setWheelchairAccessible(false);
        }
        if (interestJob.getAccessibleRestroom() == null) {
            interestJob.setAccessibleRestroom(false);
        }
        if (interestJob.getDisabledParking() == null) {
            interestJob.setDisabledParking(false);
        }
        if (interestJob.getRemoteAvailable() == null) {
            interestJob.setRemoteAvailable(false);
        }
        if (interestJob.getFlexibleWorkAvailable() == null) {
            interestJob.setFlexibleWorkAvailable(false);
        }
        if (interestJob.getAssistiveDeviceSupport() == null) {
            interestJob.setAssistiveDeviceSupport(false);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
