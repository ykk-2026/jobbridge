package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

public interface IProfileService {

    JobSeekerProfileDTO getProfileInfo(JobSeekerProfileDTO pDTO) throws Exception;

    int saveProfileInfo(JobSeekerProfileDTO pDTO) throws Exception;

}
