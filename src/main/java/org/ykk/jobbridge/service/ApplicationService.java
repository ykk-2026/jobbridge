package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.ApplicationDTO;

import java.util.List;

public interface ApplicationService {

    void apply(ApplicationDTO applicationDTO);

    List<ApplicationDTO> findByJobId(Long jobId);
}
