package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ykk.jobbridge.dto.CommunityCommentDTO;
import org.ykk.jobbridge.dto.CommunityPostDTO;
import org.ykk.jobbridge.dto.CommunityReportDTO;
import org.ykk.jobbridge.dto.MsgDTO;
import org.ykk.jobbridge.service.ICommunityService;
import org.ykk.jobbridge.util.CmmUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/api/community")
@RequiredArgsConstructor
@Controller
public class CommunityController {

    private final ICommunityService communityService;

    @ResponseBody
    @GetMapping(value = "getPostList")
    public List<CommunityPostDTO> getPostList(HttpSession session) throws Exception {
        CommunityPostDTO dto = new CommunityPostDTO();
        dto.setMemberId(currentMemberId(session));
        return Optional.ofNullable(communityService.getPostList(dto)).orElseGet(ArrayList::new);
    }

    @ResponseBody
    @GetMapping(value = "getPostInfo")
    public CommunityPostDTO getPostInfo(@RequestParam(name = "postId", defaultValue = "0") Long postId,
                                        HttpSession session) throws Exception {
        CommunityPostDTO dto = new CommunityPostDTO();
        dto.setId(postId);
        dto.setMemberId(currentMemberId(session));
        return Optional.ofNullable(communityService.getPostInfo(dto, true))
                .orElseGet(CommunityPostDTO::new);
    }

    @ResponseBody
    @PostMapping(value = "insertPostInfo")
    public MsgDTO insertPostInfo(@ModelAttribute CommunityPostDTO dto, HttpSession session) throws Exception {
        dto.setMemberId(requireMemberId(session));
        dto.setCategory(normalizeCategory(dto.getCategory()));
        dto.setTitle(CmmUtil.nvl(dto.getTitle()).trim());
        dto.setContent(CmmUtil.nvl(dto.getContent()).trim());

        if (dto.getTitle().isEmpty() || dto.getContent().isEmpty()) {
            return message(0, "제목과 내용을 입력해 주세요.");
        }

        communityService.insertPostInfo(dto);
        return message(1, "등록되었습니다.");
    }

    @ResponseBody
    @PostMapping(value = "deletePostInfo")
    public MsgDTO deletePostInfo(@RequestParam(name = "postId", defaultValue = "0") Long postId,
                                 HttpSession session) throws Exception {
        CommunityPostDTO dto = new CommunityPostDTO();
        dto.setId(postId);
        dto.setMemberId(requireMemberId(session));

        return communityService.deletePostInfo(dto) > 0
                ? message(1, "삭제되었습니다.")
                : message(0, "본인이 작성한 게시글만 삭제할 수 있습니다.");
    }

    @ResponseBody
    @PostMapping(value = "insertCommentInfo")
    public MsgDTO insertCommentInfo(@ModelAttribute CommunityCommentDTO dto,
                                    HttpSession session) throws Exception {
        dto.setMemberId(requireMemberId(session));
        dto.setContent(CmmUtil.nvl(dto.getContent()).trim());
        if (dto.getContent().isEmpty()) return message(0, "댓글 내용을 입력해 주세요.");

        int result = communityService.insertCommentInfo(dto);
        if (result == 1) return message(1, "댓글이 등록되었습니다.");
        if (result == 3) return message(0, "게시글을 찾을 수 없습니다.");
        return message(0, "오류로 인해 댓글 등록이 실패하였습니다.");
    }

    @ResponseBody
    @PostMapping(value = "deleteCommentInfo")
    public MsgDTO deleteCommentInfo(@RequestParam(name = "commentId", defaultValue = "0") Long commentId,
                                    HttpSession session) throws Exception {
        CommunityCommentDTO dto = new CommunityCommentDTO();
        dto.setId(commentId);
        dto.setMemberId(requireMemberId(session));

        return communityService.deleteCommentInfo(dto) > 0
                ? message(1, "삭제되었습니다.")
                : message(0, "본인이 작성한 댓글만 삭제할 수 있습니다.");
    }

    @ResponseBody
    @PostMapping(value = "insertReportInfo")
    public MsgDTO insertReportInfo(@ModelAttribute CommunityReportDTO dto,
                                   HttpSession session) throws Exception {
        dto.setReporterMemberId(requireMemberId(session));
        if (dto.getReason() == null) dto.setReason("ETC");

        int result = communityService.insertReportInfo(dto);
        if (result == 1) return message(1, "신고가 접수되었습니다.");
        if (result == 2) return message(0, "이미 신고한 게시글입니다.");
        return message(0, "오류로 인해 신고가 실패하였습니다.");
    }

    @ResponseBody
    @ExceptionHandler(LoginRequiredException.class)
    public MsgDTO handleLoginRequired() {
        return message(0, "로그인이 필요합니다.");
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public MsgDTO handleException(Exception e) {
        log.error("커뮤니티 요청 처리 실패", e);
        return message(0, "실패하였습니다. : " + e.getMessage());
    }

    private Long currentMemberId(HttpSession session) {
        String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));
        return memberId.isEmpty() ? null : Long.parseLong(memberId);
    }

    private Long requireMemberId(HttpSession session) {
        Long memberId = currentMemberId(session);
        if (memberId == null) throw new LoginRequiredException();
        return memberId;
    }

    private String normalizeCategory(String category) {
        String value = CmmUtil.nvl(category, "FREE");
        return List.of("FREE", "QUESTION", "TIP", "INFO").contains(value) ? value : "FREE";
    }

    private MsgDTO message(int result, String text) {
        MsgDTO dto = new MsgDTO();
        dto.setResult(result);
        dto.setMsg(text);
        return dto;
    }

    private static class LoginRequiredException extends RuntimeException {
    }
}
