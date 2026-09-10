package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.service.AiJobRecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AiJobRecommendationController {

    private final AiJobRecommendationService recommendationService;

    @GetMapping({"/recommendations", "/api/recommendations"})
    public List<AiJobRecommendationDTO> recommendations(HttpSession session) {
        return recommendationService.getRecommendations(requireLoginMemberId(session));
    }

    private Long requireLoginMemberId(HttpSession session) {
        Object memberId = session == null ? null : session.getAttribute("loginMemberId");
        if (memberId instanceof Number number) return number.longValue();
        if (memberId instanceof String value) {
            try {
                return Long.valueOf(value);
            } catch (NumberFormatException ignored) {
                // Invalid session values are handled as unauthenticated below.
            }
        }

        // Compatibility with the login implementation currently used by this project.
        Object loginMember = session == null ? null : session.getAttribute("loginMember");
        if (loginMember instanceof SessionMember member) return member.getId();
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }
}
