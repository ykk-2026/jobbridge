package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.JobPostingDTO;

import java.util.List;

public interface IJobPostingService {

    List<JobPostingDTO> getJobList() throws Exception;

    JobPostingDTO getJobInfo(JobPostingDTO pDTO) throws Exception;

    List<JobPostingDTO> getMyJobList(JobPostingDTO pDTO) throws Exception;

    void insertJobInfo(JobPostingDTO pDTO) throws Exception;

    int updateJobInfo(JobPostingDTO pDTO) throws Exception;

    int updateJobClose(JobPostingDTO pDTO) throws Exception;

    int deleteJobInfo(JobPostingDTO pDTO) throws Exception;

    int getJobCount() throws Exception;

}
