package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.JobApplicationDTO;

import java.util.List;

public interface IJobApplicationService {







    List<JobApplicationDTO> getApplicationList(JobApplicationDTO pDTO) throws Exception;







    List<JobApplicationDTO> getCompanyApplicationList(JobApplicationDTO pDTO) throws Exception;







    int insertApplicationInfo(JobApplicationDTO pDTO) throws Exception;




    int getApplicationCount() throws Exception;

}
