package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.JobDTO;

import java.util.List;

public interface JobService {

    List<JobDTO> findAll();

    JobDTO findById(Long id);
}
