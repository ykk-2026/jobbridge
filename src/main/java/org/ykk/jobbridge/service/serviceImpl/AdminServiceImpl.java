package org.ykk.jobbridge.service.serviceImpl;

import org.springframework.stereotype.Service;
import org.ykk.jobbridge.dto.AdminOverviewDTO;
import org.ykk.jobbridge.mapper.AdminMapper;
import org.ykk.jobbridge.service.AdminService;
import org.ykk.jobbridge.service.JobCatalogService;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final JobCatalogService jobCatalogService;

    public AdminServiceImpl(AdminMapper adminMapper, JobCatalogService jobCatalogService) {
        this.adminMapper = adminMapper;
        this.jobCatalogService = jobCatalogService;
    }

    @Override
    public AdminOverviewDTO getOverview() {
        return new AdminOverviewDTO(
                adminMapper.countMembers(),
                adminMapper.countCompanies(),
                jobCatalogService.count(),
                0,
                0,
                0,
                adminMapper.findMembers(),
                jobCatalogService.findAll()
        );
    }
}
