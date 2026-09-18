package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

public interface IProfileService {

    /**
     * 구직자 프로필 조회
     *
     * @param pDTO 조회할 memberId 값
     * @return 조회 결과 (회원 기본 정보 포함)
     */
    JobSeekerProfileDTO getProfileInfo(JobSeekerProfileDTO pDTO) throws Exception;

    /**
     * 구직자 프로필 저장 (프로필이 없으면 등록, 있으면 수정)
     *
     * @param pDTO 화면에서 입력된 프로필 값들
     * @return 1 : 성공 / 0 : 실패
     */
    int saveProfileInfo(JobSeekerProfileDTO pDTO) throws Exception;

}
