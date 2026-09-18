package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.JobApplicationDTO;

import java.util.List;

public interface IJobApplicationService {

    /**
     * 구직자가 지원한 리스트
     *
     * @param pDTO 구직자 회원 고유번호(memberId)
     * @return 조회 결과
     */
    List<JobApplicationDTO> getApplicationList(JobApplicationDTO pDTO) throws Exception;

    /**
     * 기업회원 공고에 지원한 지원자 리스트
     *
     * @param pDTO 기업회원 고유번호(companyMemberId)
     * @return 조회 결과
     */
    List<JobApplicationDTO> getCompanyApplicationList(JobApplicationDTO pDTO) throws Exception;

    /**
     * 입사 지원
     *
     * @param pDTO 화면에서 입력된 지원서 값들
     * @return 1 : 성공 / 2 : 이미 지원한 공고 / 3 : 마감되었거나 없는 공고 / 0 : 실패
     */
    int insertApplicationInfo(JobApplicationDTO pDTO) throws Exception;

    /**
     * 전체 지원 수
     */
    int getApplicationCount() throws Exception;

}
