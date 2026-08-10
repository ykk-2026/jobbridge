package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

@Mapper
public interface ProfileMapper {

    // 프로필 최초 등록
    int insertProfile(JobSeekerProfileDTO profileDTO);

    // 회원 고유번호로 프로필 조회
    JobSeekerProfileDTO findProfileByMemberId(
            @Param("memberId") Long memberId
    );

    // 프로필 수정
    int updateProfile(JobSeekerProfileDTO profileDTO);

    // 프로필 화면에서 수정한 회원 기본 정보 저장
    int updateMember(JobSeekerProfileDTO profileDTO);

    // 해당 회원의 프로필 존재 여부
    int countProfileByMemberId(
            @Param("memberId") Long memberId
    );
}
