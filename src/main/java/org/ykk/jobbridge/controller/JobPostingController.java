package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.service.JobPostingService;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobPostingController {

    private final JobPostingService jobPostingService;

    public JobPostingController(JobPostingService jobPostingService) {
        this.jobPostingService = jobPostingService;
    }

    @GetMapping
    public List<JobPostingDTO> getOpenJobs(@RequestParam(defaultValue = "20") int limit) {
        return jobPostingService.getOpenJobs(limit);
    }

    @GetMapping("/{jobId}")
    public JobPostingDTO getJob(@PathVariable Long jobId) {
        return jobPostingService.getJob(jobId);
    }

    @GetMapping("/my")
    public List<JobPostingDTO> getMyJobs(HttpSession session) {
        SessionMember company = requireCompany(session);
        return jobPostingService.getMyJobs(company.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobPostingDTO createJob(@RequestBody JobPostingDTO jobPosting, HttpSession session) {
        SessionMember company = requireCompany(session);
        return jobPostingService.createJob(company.getId(), jobPosting);
    }

    @PutMapping("/{jobId}")
    public JobPostingDTO updateJob(@PathVariable Long jobId,
                                   @RequestBody JobPostingDTO jobPosting,
                                   HttpSession session) {
        SessionMember company = requireCompany(session);
        return jobPostingService.updateJob(company.getId(), jobId, jobPosting);
    }

    @PatchMapping("/{jobId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeJob(@PathVariable Long jobId, HttpSession session) {
        SessionMember company = requireCompany(session);
        jobPostingService.closeJob(company.getId(), jobId);
    }

    @DeleteMapping("/{jobId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJob(@PathVariable Long jobId, HttpSession session) {
        SessionMember company = requireCompany(session);
        jobPostingService.deleteJob(company.getId(), jobId);
    }

    private SessionMember requireCompany(HttpSession session) {
        SessionMember member = (SessionMember) session.getAttribute("loginMember");
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (!"COMPANY".equals(member.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "기업회원만 채용공고를 관리할 수 있습니다.");
        }
        return member;
    }
}
