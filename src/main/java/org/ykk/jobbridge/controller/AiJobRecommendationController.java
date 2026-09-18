package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.service.IAiJobRecommendationService;
import org.ykk.jobbridge.util.CmmUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * /api/recommendations 로 시작되는 URL은 무조건 AiJobRecommendationController에서 처리
 * 구직자(JOB_SEEKER)로 로그인한 경우만 AI 추천 결과를 조회할 수 있음
 * */
@Slf4j
@RequestMapping(value = "/api/recommendations")
@RequiredArgsConstructor
@Controller
public class AiJobRecommendationController {

    private final IAiJobRecommendationService recommendationService;

    /**
     * 로그인한 구직자의 AI 채용공고 추천 리스트
     * <p>
     * 구직자가 아니거나 프로필이 없으면 빈 리스트가 전달됨
     */
    @ResponseBody
    @GetMapping(value = "getRecommendationList")
    public List<AiJobRecommendationDTO> getRecommendationList(HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".getRecommendationList Start!");

        String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"), "0");
        String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));

        log.info("session memberId : " + memberId);
        log.info("session userRole : " + userRole);

        List<AiJobRecommendationDTO> rList = new ArrayList<>();

        if (userRole.equals("JOB_SEEKER")) {
            AiJobRecommendationDTO pDTO = new AiJobRecommendationDTO();
            pDTO.setMemberId(Long.parseLong(memberId));

            rList = Optional.ofNullable(recommendationService.getRecommendationList(pDTO))
                    .orElseGet(ArrayList::new);
        }

        log.info(this.getClass().getName() + ".getRecommendationList End!");

        return rList;
    }
}
