package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.ykk.jobbridge.dto.JobApplicationDTO;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.service.JobApplicationService;

import java.util.List;

@RestController
@RequestMapping("/api/job-applications")
public class JobApplicationApiController {
    private final JobApplicationService jobApplicationService;

    public JobApplicationApiController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping
    public List<JobApplicationDTO> applications(HttpSession session) {
        Long memberId = requireJobSeeker(session).getId();
        return jobApplicationService.getApplications(memberId);
    }

    @GetMapping("/company")
    public List<JobApplicationDTO> companyApplications(HttpSession session) {
        Long companyMemberId = requireCompany(session).getId();
        return jobApplicationService.getCompanyApplications(companyMemberId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobApplicationDTO apply(@RequestBody JobApplicationDTO application, HttpSession session) {
        SessionMember member = requireJobSeeker(session);
        return jobApplicationService.apply(member.getId(), application);
    }

    private SessionMember requireJobSeeker(HttpSession session) {
        SessionMember member = (SessionMember) session.getAttribute("loginMember");
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (!"JOB_SEEKER".equals(member.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "구직자 계정만 지원할 수 있습니다.");
        }
        return member;
    }

    private SessionMember requireCompany(HttpSession session) {
        SessionMember member = (SessionMember) session.getAttribute("loginMember");
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (!"COMPANY".equals(member.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "기업회원만 지원자를 확인할 수 있습니다.");
        }
        return member;
    }
}
