package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

@Mapper
public interface ProfileMapper {
    void ensureNameColumn();

    void ensureBirthDateColumn();

    void ensureGenderColumn();

    void ensureEmailColumn();

    void ensurePhoneColumn();

    int insertProfile(JobSeekerProfileDTO profileDTO);

    JobSeekerProfileDTO findProfileByMemberId(@Param("memberId") Long memberId);

    int updateProfile(JobSeekerProfileDTO profileDTO);

    int updateMember(JobSeekerProfileDTO profileDTO);

    int countProfileByMemberId(@Param("memberId") Long memberId);
}
