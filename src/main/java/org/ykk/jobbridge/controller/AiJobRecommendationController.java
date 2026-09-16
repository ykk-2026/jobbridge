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
        return recommendationService.getRecommendations(requireLoginMemberId(session));
    }

    private Long requireLoginMemberId(HttpSession session) {
        Object memberId = null;
        if (session != null) {
            memberId = session.getAttribute("loginMemberId");
        }
        if (memberId instanceof Number) {
            return ((Number) memberId).longValue();
        }
        if (memberId instanceof String) {
            try {
                return Long.valueOf((String) memberId);
            } catch (NumberFormatException ignored) {
                // Invalid session values are handled as unauthenticated below.
            }
        }

        // Compatibility with the login implementation currently used by this project.
        Object loginMember = null;
        if (session != null) {
            loginMember = session.getAttribute("loginMember");
        }
        if (loginMember instanceof SessionMember) {
            return ((SessionMember) loginMember).getId();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }
}
