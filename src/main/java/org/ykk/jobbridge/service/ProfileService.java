package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

public interface ProfileService {

    // 프로필 조회
    JobSeekerProfileDTO getProfile(Long memberId);

    // 프로필 저장
    // 프로필이 없으면 등록, 있으면 수정
    int saveProfile(JobSeekerProfileDTO profileDTO);

    // 프로필 존재 여부
    boolean hasProfile(Long memberId);
}