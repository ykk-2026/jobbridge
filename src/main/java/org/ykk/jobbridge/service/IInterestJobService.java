package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.InterestJobDTO;

import java.util.List;

public interface IInterestJobService {

    /**
     * 관심 공고 리스트
     *
     * @param pDTO 회원 고유번호(memberId)
     * @return 조회 결과
     */
    List<InterestJobDTO> getInterestJobList(InterestJobDTO pDTO) throws Exception;

    /**
     * 관심 공고 등록 (이미 저장된 공고는 다시 저장하지 않음)
     *
     * @param pDTO 화면에서 전달된 공고 값들
     * @return 1 : 성공 / 2 : 이미 저장됨 / 0 : 실패
     */
    int insertInterestJobInfo(InterestJobDTO pDTO) throws Exception;

    /**
     * 관심 공고 삭제
     *
     * @param pDTO 삭제할 memberId, companyName, title 값
     */
    void deleteInterestJobInfo(InterestJobDTO pDTO) throws Exception;

}
