package org.ykk.jobbridge.service.serviceImpl;

import org.springframework.stereotype.Service;
import org.ykk.jobbridge.dto.AdminOverviewDTO;
import org.ykk.jobbridge.mapper.AdminMapper;
import org.ykk.jobbridge.service.AdminService;
import org.ykk.jobbridge.service.JobPostingService;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final JobPostingService jobPostingService;

    public AdminServiceImpl(AdminMapper adminMapper, JobPostingService jobPostingService) {
        this.adminMapper = adminMapper;
        this.jobPostingService = jobPostingService;
    }

    @Override
    public AdminOverviewDTO getOverview() {
        return new AdminOverviewDTO(
                adminMapper.countMembers(),
                adminMapper.countCompanies(),
                jobPostingService.countJobs(),
                0,
                0,
                0,
                adminMapper.findMembers(),
                jobPostingService.getOpenJobs(100)
        );
    }
}
