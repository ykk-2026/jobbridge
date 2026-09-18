package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.JobPostingDTO;

import java.util.List;

public interface IJobPostingService {

    /**
     * 진행 중인 채용공고 리스트
     *
     * @return 조회 결과
     */
    List<JobPostingDTO> getJobList() throws Exception;

    /**
     * 채용공고 상세보기
     *
     * @param pDTO 상세내용 조회할 id 값
     * @return 조회 결과
     */
    JobPostingDTO getJobInfo(JobPostingDTO pDTO) throws Exception;

    /**
     * 기업회원이 등록한 채용공고 리스트
     *
     * @param pDTO 기업회원 고유번호(companyMemberId)
     * @return 조회 결과
     */
    List<JobPostingDTO> getMyJobList(JobPostingDTO pDTO) throws Exception;

    /**
     * 채용공고 등록
     *
     * @param pDTO 화면에서 입력된 채용공고 값들
     */
    void insertJobInfo(JobPostingDTO pDTO) throws Exception;

    /**
     * 채용공고 수정
     *
     * @param pDTO 화면에서 입력된 수정될 채용공고 값들
     * @return 수정된 건수 (0이면 본인 회사의 공고가 아님)
     */
    int updateJobInfo(JobPostingDTO pDTO) throws Exception;

    /**
     * 채용공고 마감
     *
     * @param pDTO 마감할 id, companyMemberId 값
     * @return 수정된 건수
     */
    int updateJobClose(JobPostingDTO pDTO) throws Exception;

    /**
     * 채용공고 삭제
     *
     * @param pDTO 삭제할 id, companyMemberId 값
     * @return 삭제된 건수
     */
    int deleteJobInfo(JobPostingDTO pDTO) throws Exception;

    /**
     * 전체 채용공고 수
     */
    int getJobCount() throws Exception;

}
