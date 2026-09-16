package org.ykk.jobbridge.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ykk.jobbridge.dto.JobSummaryDTO;
import org.ykk.jobbridge.service.JobCatalogService;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobApiController {

    private final JobCatalogService jobCatalogService;

    public JobApiController(JobCatalogService jobCatalogService) {
        this.jobCatalogService = jobCatalogService;
    }

    @GetMapping
    public List<JobSummaryDTO> jobs(@RequestParam(defaultValue = "20") int limit) {
        return jobCatalogService.findAll(limit);
    }
}
