package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.JobApplicationDTO;

import java.util.List;

/**
 * JobApplicationMapper.xml과 매핑되는 인터페이스
 */
@Mapper
public interface IJobApplicationMapper {

    // 구직자가 지원한 리스트
    List<JobApplicationDTO> getApplicationList(JobApplicationDTO pDTO) throws Exception;

    // 기업회원 공고에 지원한 지원자 리스트
    List<JobApplicationDTO> getCompanyApplicationList(JobApplicationDTO pDTO) throws Exception;

    // 이미 지원한 공고인지 체크
    JobApplicationDTO getApplicationExists(JobApplicationDTO pDTO) throws Exception;

    // 입사 지원 등록
    int insertApplicationInfo(JobApplicationDTO pDTO) throws Exception;

    // 전체 지원 수
    int getApplicationCount() throws Exception;

}
