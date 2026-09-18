package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.InterestJobDTO;

import java.util.List;

/**
 * InterestJobMapper.xml과 매핑되는 인터페이스
 */
@Mapper
public interface IInterestJobMapper {

    // 관심 공고 리스트
    List<InterestJobDTO> getInterestJobList(InterestJobDTO pDTO) throws Exception;

    // 이미 저장된 관심 공고인지 체크
    InterestJobDTO getInterestJobExists(InterestJobDTO pDTO) throws Exception;

    // 관심 공고 등록
    int insertInterestJobInfo(InterestJobDTO pDTO) throws Exception;

    // 관심 공고 삭제
    int deleteInterestJobInfo(InterestJobDTO pDTO) throws Exception;

}
