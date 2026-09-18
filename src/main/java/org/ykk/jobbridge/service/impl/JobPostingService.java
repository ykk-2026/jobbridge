package org.ykk.jobbridge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.mapper.IJobPostingMapper;
import org.ykk.jobbridge.service.IJobPostingService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class JobPostingService implements IJobPostingService {

    private final IJobPostingMapper jobPostingMapper;

    @Override
    public List<JobPostingDTO> getJobList() throws Exception {

        log.info(this.getClass().getName() + ".getJobList Start!");

        return jobPostingMapper.getJobList();
    }

    @Override
    public JobPostingDTO getJobInfo(JobPostingDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getJobInfo Start!");

        return jobPostingMapper.getJobInfo(pDTO);
    }

    @Override
    public List<JobPostingDTO> getMyJobList(JobPostingDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getMyJobList Start!");

        return jobPostingMapper.getMyJobList(pDTO);
    }

    @Transactional
    @Override
    public void insertJobInfo(JobPostingDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertJobInfo Start!");

        jobPostingMapper.insertJobInfo(pDTO);
    }

    @Transactional
    @Override
    public int updateJobInfo(JobPostingDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updateJobInfo Start!");

        return jobPostingMapper.updateJobInfo(pDTO);
    }

    @Transactional
    @Override
    public int updateJobClose(JobPostingDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updateJobClose Start!");

        return jobPostingMapper.updateJobClose(pDTO);
    }

    @Transactional
    @Override
    public int deleteJobInfo(JobPostingDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteJobInfo Start!");

        return jobPostingMapper.deleteJobInfo(pDTO);
    }

    @Override
    public int getJobCount() throws Exception {

        log.info(this.getClass().getName() + ".getJobCount Start!");

        return jobPostingMapper.getJobCount();
    }
}
