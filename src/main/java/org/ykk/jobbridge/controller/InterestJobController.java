package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ykk.jobbridge.dto.InterestJobDTO;
import org.ykk.jobbridge.dto.MsgDTO;
import org.ykk.jobbridge.service.IInterestJobService;
import org.ykk.jobbridge.util.CmmUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/api/interest-jobs")
@RequiredArgsConstructor
@Controller
public class InterestJobController {

    private final IInterestJobService interestJobService;

    @ResponseBody
    @GetMapping(value = "getInterestJobList")
    public List<InterestJobDTO> getInterestJobList(HttpSession session) throws Exception {
        String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));
        if (memberId.isEmpty()) {
            return new ArrayList<>();
        }

        InterestJobDTO pDTO = new InterestJobDTO();
        pDTO.setMemberId(Long.parseLong(memberId));
        return Optional.ofNullable(interestJobService.getInterestJobList(pDTO))
                .orElseGet(ArrayList::new);
    }

    @ResponseBody
    @PostMapping(value = "insertInterestJobInfo")
    public MsgDTO insertInterestJobInfo(HttpServletRequest request, HttpSession session) {
        String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));
        String jobId = CmmUtil.nvl(request.getParameter("jobId")).trim();

        if (memberId.isEmpty()) {
            return message(0, "로그인이 필요합니다.");
        }
        if (jobId.isEmpty()) {
            return message(0, "채용공고 ID가 필요합니다.");
        }

        try {
            InterestJobDTO pDTO = new InterestJobDTO();
            pDTO.setMemberId(Long.parseLong(memberId));
            pDTO.setJobId(Long.parseLong(jobId));

            int result = interestJobService.insertInterestJobInfo(pDTO);
            if (result == 1) {
                return message(1, "관심 공고가 저장되었습니다.");
            }
            if (result == 2) {
                return message(1, "이미 저장된 관심 공고입니다.");
            }
            return message(0, "관심 공고를 저장하지 못했습니다.");
        } catch (NumberFormatException e) {
            return message(0, "채용공고 ID 형식이 올바르지 않습니다.");
        } catch (Exception e) {
            log.error("Failed to save interest job. memberId={}, jobId={}", memberId, jobId, e);
            return message(0, "존재하지 않는 채용공고이거나 저장 중 오류가 발생했습니다.");
        }
    }

    @ResponseBody
    @PostMapping(value = "deleteInterestJobInfo")
    public MsgDTO deleteInterestJobInfo(HttpServletRequest request, HttpSession session) {
        String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));
        String jobId = CmmUtil.nvl(request.getParameter("jobId")).trim();

        if (memberId.isEmpty()) {
            return message(0, "로그인이 필요합니다.");
        }
        if (jobId.isEmpty()) {
            return message(0, "채용공고 ID가 필요합니다.");
        }

        try {
            InterestJobDTO pDTO = new InterestJobDTO();
            pDTO.setMemberId(Long.parseLong(memberId));
            pDTO.setJobId(Long.parseLong(jobId));
            interestJobService.deleteInterestJobInfo(pDTO);
            return message(1, "삭제되었습니다.");
        } catch (NumberFormatException e) {
            return message(0, "채용공고 ID 형식이 올바르지 않습니다.");
        } catch (Exception e) {
            log.error("Failed to delete interest job. memberId={}, jobId={}", memberId, jobId, e);
            return message(0, "관심 공고 삭제 중 오류가 발생했습니다.");
        }
    }

    private MsgDTO message(int result, String message) {
        MsgDTO dto = new MsgDTO();
        dto.setResult(result);
        dto.setMsg(message);
        return dto;
    }
}
