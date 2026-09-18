package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.JobPostingDTO;

import java.util.List;

/**
 * JobPostingMapper.xml과 매핑되는 인터페이스
 */
@Mapper
public interface IJobPostingMapper {

    // 진행 중인 채용공고 리스트
    List<JobPostingDTO> getJobList() throws Exception;

    // 채용공고 상세보기
    JobPostingDTO getJobInfo(JobPostingDTO pDTO) throws Exception;

    // 기업회원이 등록한 채용공고 리스트
    List<JobPostingDTO> getMyJobList(JobPostingDTO pDTO) throws Exception;

    // 채용공고 등록
    int insertJobInfo(JobPostingDTO pDTO) throws Exception;

    // 채용공고 수정
    int updateJobInfo(JobPostingDTO pDTO) throws Exception;

    // 채용공고 마감
    int updateJobClose(JobPostingDTO pDTO) throws Exception;

    // 채용공고 삭제(상태값 DELETED로 변경)
    int deleteJobInfo(JobPostingDTO pDTO) throws Exception;

    // 전체 채용공고 수
    int getJobCount() throws Exception;

}
