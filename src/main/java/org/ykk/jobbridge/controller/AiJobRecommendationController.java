package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.service.AiJobRecommendationService;

import java.util.List;

@RestController
public class AiJobRecommendationController {

    private final AiJobRecommendationService recommendationService;

    public AiJobRecommendationController(AiJobRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping({"/recommendations", "/api/recommendations"})
    public List<AiJobRecommendationDTO> recommendations(HttpSession session) {
        return recommendationService.getRecommendations(requireJobSeekerId(session));
    }

    private Long requireJobSeekerId(HttpSession session) {
        SessionMember member = (SessionMember) session.getAttribute("loginMember");
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (!"JOB_SEEKER".equals(member.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "구직자만 AI 추천을 이용할 수 있습니다.");
        }
        return member.getId();
    }
}
