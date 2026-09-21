package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.JobApplicationDTO;

import java.util.List;




@Mapper
public interface IJobApplicationMapper {


    List<JobApplicationDTO> getApplicationList(JobApplicationDTO pDTO) throws Exception;


    List<JobApplicationDTO> getCompanyApplicationList(JobApplicationDTO pDTO) throws Exception;


    JobApplicationDTO getApplicationExists(JobApplicationDTO pDTO) throws Exception;


    int insertApplicationInfo(JobApplicationDTO pDTO) throws Exception;


    int getApplicationCount() throws Exception;

}
