package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.InterestJobDTO;

import java.util.List;




@Mapper
public interface IInterestJobMapper {


    List<InterestJobDTO> getInterestJobList(InterestJobDTO pDTO) throws Exception;


    InterestJobDTO getInterestJobExists(InterestJobDTO pDTO) throws Exception;


    int insertInterestJobInfo(InterestJobDTO pDTO) throws Exception;


    int deleteInterestJobInfo(InterestJobDTO pDTO) throws Exception;

}
