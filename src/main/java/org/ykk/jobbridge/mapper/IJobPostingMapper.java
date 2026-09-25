package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.JobPostingDTO;

import java.util.List;

@Mapper
public interface IJobPostingMapper {

    List<JobPostingDTO> getJobList() throws Exception;

    JobPostingDTO getJobInfo(JobPostingDTO pDTO) throws Exception;

    List<JobPostingDTO> getMyJobList(JobPostingDTO pDTO) throws Exception;

    int insertJobInfo(JobPostingDTO pDTO) throws Exception;

    int upsertKeadJob(JobPostingDTO pDTO) throws Exception;

    int updateJobInfo(JobPostingDTO pDTO) throws Exception;

    int updateJobClose(JobPostingDTO pDTO) throws Exception;

    int deleteJobInfo(JobPostingDTO pDTO) throws Exception;

    int getJobCount() throws Exception;

}
