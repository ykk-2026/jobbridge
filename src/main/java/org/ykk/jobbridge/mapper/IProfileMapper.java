package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

/**
 * ProfileMapper.xml과 매핑되는 인터페이스
 */
@Mapper
public interface IProfileMapper {

    // 구직자 프로필 조회(MEMBER 테이블 JOIN)
    JobSeekerProfileDTO getProfileInfo(JobSeekerProfileDTO pDTO) throws Exception;

    // 프로필 존재 여부 체크
    JobSeekerProfileDTO getProfileExists(JobSeekerProfileDTO pDTO) throws Exception;

    // 프로필 등록
    int insertProfileInfo(JobSeekerProfileDTO pDTO) throws Exception;

    // 프로필 수정
    int updateProfileInfo(JobSeekerProfileDTO pDTO) throws Exception;

    // 회원 기본 정보(이름, 생년월일 등) 수정
    int updateMemberInfo(JobSeekerProfileDTO pDTO) throws Exception;

}
