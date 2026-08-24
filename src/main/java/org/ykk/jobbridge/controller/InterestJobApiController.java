package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.ykk.jobbridge.dto.InterestJobDTO;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.mapper.InterestJobMapper;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interest-jobs")
public class InterestJobApiController {

    private final InterestJobMapper interestJobMapper;

    @GetMapping
    public List<InterestJobDTO> interestJobs(HttpSession session) {
        return interestJobMapper.findAll(requireLoginMember(session).getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterestJobDTO save(@RequestBody InterestJobDTO interestJob,
                               HttpSession session) {
        validate(interestJob);
        normalize(interestJob);
        interestJob.setMemberId(requireLoginMember(session).getId());
        if (interestJobMapper.countByCompanyAndTitle(
                interestJob.getMemberId(), interestJob.getCompanyName(), interestJob.getTitle()) == 0) {
            interestJobMapper.insert(interestJob);
        }
        return interestJob;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam String companyName,
                       @RequestParam String title,
                       HttpSession session) {
        interestJobMapper.deleteByCompanyAndTitle(
                requireLoginMember(session).getId(), companyName.trim(), title.trim());
    }

    private void validate(InterestJobDTO interestJob) {
        if (interestJob == null
                || isBlank(interestJob.getCompanyName())
                || isBlank(interestJob.getTitle())
                || isBlank(interestJob.getLocation())) {
            throw new IllegalArgumentException("관심 공고의 필수 정보가 없습니다.");
        }
    }

    private void normalize(InterestJobDTO interestJob) {
        interestJob.setCompanyName(interestJob.getCompanyName().trim());
        interestJob.setTitle(interestJob.getTitle().trim());
        interestJob.setLocation(interestJob.getLocation().trim());
        if (isBlank(interestJob.getJobCategory())) interestJob.setJobCategory("기타");
        if (isBlank(interestJob.getEmploymentType())) interestJob.setEmploymentType("FULL_TIME");
        if (isBlank(interestJob.getStatus())) interestJob.setStatus("OPEN");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private SessionMember requireLoginMember(HttpSession session) {
        SessionMember loginMember = (SessionMember) session.getAttribute("loginMember");
        if (loginMember == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return loginMember;
    }
}
