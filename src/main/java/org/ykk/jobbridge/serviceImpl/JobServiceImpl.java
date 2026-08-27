package org.ykk.jobbridge.serviceImpl;

import org.springframework.stereotype.Service;
import org.ykk.jobbridge.dto.JobDTO;
import org.ykk.jobbridge.mapper.JobMapper;
import org.ykk.jobbridge.service.JobService;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    private final JobMapper jobMapper;

    public JobServiceImpl(JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }

    @Override
    public List<JobDTO> findAll() {
        return jobMapper.findAll();
    }

    @Override
    public JobDTO findById(Long id) {
        return jobMapper.findById(id);
    }
}
