package org.ykk.jobbridge.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.mapper.ProfileMapper;
import org.ykk.jobbridge.service.ProfileService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {

    private final ProfileMapper profileMapper;

    @Override
    public JobSeekerProfileDTO getProfile(Long memberId) {

        if (memberId == null) {
            throw new IllegalArgumentException("회원 고유번호가 없습니다.");
        }

        return profileMapper.findProfileByMemberId(memberId);
    }

    @Override
    @Transactional
    public int saveProfile(JobSeekerProfileDTO profileDTO) {

        if (profileDTO == null || profileDTO.getMemberId() == null) {
            throw new IllegalArgumentException("회원 정보가 없습니다.");
        }

        validateProfile(profileDTO);

        int profileCount =
                profileMapper.countProfileByMemberId(
                        profileDTO.getMemberId()
                );

        if (profileCount == 0) {
            return profileMapper.insertProfile(profileDTO);
        }

        return profileMapper.updateProfile(profileDTO);
    }

    @Override
    public boolean hasProfile(Long memberId) {

        if (memberId == null) {
            return false;
        }

        return profileMapper.countProfileByMemberId(memberId) > 0;
    }

    private void validateProfile(JobSeekerProfileDTO profileDTO) {

        Integer careerYears = profileDTO.getCareerYears();
        Integer minSalary = profileDTO.getMinSalary();

        if (careerYears != null && careerYears < 0) {
            throw new IllegalArgumentException(
                    "경력 연수는 0 이상이어야 합니다."
            );
        }

        if (minSalary != null && minSalary < 0) {
            throw new IllegalArgumentException(
                    "희망 최소 연봉은 0 이상이어야 합니다."
            );
        }

        if (profileDTO.getContactTimeStart() != null
                && profileDTO.getContactTimeEnd() != null
                && !profileDTO.getContactTimeStart()
                .isBefore(profileDTO.getContactTimeEnd())) {

            throw new IllegalArgumentException(
                    "연락 가능 시작 시간은 종료 시간보다 빨라야 합니다."
            );
        }
    }
}