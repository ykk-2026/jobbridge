package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.InterestJobDTO;

import java.util.List;

public interface IInterestJobService {

    List<InterestJobDTO> getInterestJobList(InterestJobDTO pDTO) throws Exception;

    int insertInterestJobInfo(InterestJobDTO pDTO) throws Exception;

    void deleteInterestJobInfo(InterestJobDTO pDTO) throws Exception;

}
