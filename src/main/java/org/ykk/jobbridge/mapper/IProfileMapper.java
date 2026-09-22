package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

@Mapper
public interface IProfileMapper {

    JobSeekerProfileDTO getProfileInfo(JobSeekerProfileDTO pDTO) throws Exception;

    JobSeekerProfileDTO getProfileExists(JobSeekerProfileDTO pDTO) throws Exception;

    int insertProfileInfo(JobSeekerProfileDTO pDTO) throws Exception;

    int updateProfileInfo(JobSeekerProfileDTO pDTO) throws Exception;

    int updateMemberInfo(JobSeekerProfileDTO pDTO) throws Exception;

}
