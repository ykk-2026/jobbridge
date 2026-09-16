package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpSession;
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
import org.ykk.jobbridge.service.InterestJobService;

import java.util.List;

@RestController
@RequestMapping("/api/interest-jobs")
public class InterestJobApiController {

    private final InterestJobService interestJobService;

    public InterestJobApiController(InterestJobService interestJobService) {
        this.interestJobService = interestJobService;
    }

    @GetMapping
    public List<InterestJobDTO> interestJobs(HttpSession session) {
        Long memberId = requireLoginMember(session).getId();
        return interestJobService.getInterestJobs(memberId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterestJobDTO save(@RequestBody InterestJobDTO interestJob,
                               HttpSession session) {
        Long memberId = requireLoginMember(session).getId();
        return interestJobService.saveInterestJob(memberId, interestJob);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam String companyName,
                       @RequestParam String title,
                       HttpSession session) {
        Long memberId = requireLoginMember(session).getId();
        interestJobService.deleteInterestJob(memberId, companyName, title);
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
