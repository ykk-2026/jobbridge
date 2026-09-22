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

        log.info(this.getClass().getName() + ".getPostList Start!");

        String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));

        log.info("session memberId : " + memberId);

        CommunityPostDTO pDTO = new CommunityPostDTO();

        if (memberId.length() > 0) {
            pDTO.setMemberId(Long.parseLong(memberId));
        }

        List<CommunityPostDTO> rList = Optional.ofNullable(communityService.getPostList(pDTO))
                .orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".getPostList End!");

        return rList;
    }

    @ResponseBody
    @GetMapping(value = "getPostInfo")
    public CommunityPostDTO getPostInfo(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".getPostInfo Start!");

        String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));
        String postId = CmmUtil.nvl(request.getParameter("postId"), "0");

        log.info("session memberId : " + memberId);
        log.info("postId : " + postId);

        CommunityPostDTO pDTO = new CommunityPostDTO();
        pDTO.setId(Long.parseLong(postId));

        if (memberId.length() > 0) {
            pDTO.setMemberId(Long.parseLong(memberId));
        }

        CommunityPostDTO rDTO = Optional.ofNullable(communityService.getPostInfo(pDTO, true))
                .orElseGet(CommunityPostDTO::new);

        log.info(this.getClass().getName() + ".getPostInfo End!");

        return rDTO;
    }

    @ResponseBody
    @PostMapping(value = "insertPostInfo")
    public MsgDTO insertPostInfo(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".insertPostInfo Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));
            String category = CmmUtil.nvl(request.getParameter("category"), "FREE");
            String title = CmmUtil.nvl(request.getParameter("title")).trim();
            String content = CmmUtil.nvl(request.getParameter("content")).trim();

            log.info("session memberId : " + memberId);
            log.info("category : " + category);
            log.info("title : " + title);
            log.info("content : " + content);

            if (!category.equals("FREE") && !category.equals("QUESTION")
                    && !category.equals("TIP") && !category.equals("INFO")) {
                category = "FREE";
            }

            if (memberId.isEmpty()) {
                msg = "로그인이 필요합니다.";

            } else if (title.isEmpty() || content.isEmpty()) {
                msg = "제목과 내용을 입력해 주세요.";

            } else {
                CommunityPostDTO pDTO = new CommunityPostDTO();
                pDTO.setMemberId(Long.parseLong(memberId));
                pDTO.setCategory(category);
                pDTO.setTitle(title);
                pDTO.setContent(content);

                communityService.insertPostInfo(pDTO);

                res = 1;
                msg = "등록되었습니다.";
            }

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 0;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".insertPostInfo End!");
        }

        return dto;
    }

    @ResponseBody
    @PostMapping(value = "deletePostInfo")
    public MsgDTO deletePostInfo(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".deletePostInfo Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));
            String postId = CmmUtil.nvl(request.getParameter("postId"), "0");

            log.info("session memberId : " + memberId);
            log.info("postId : " + postId);

            if (memberId.isEmpty()) {
                msg = "로그인이 필요합니다.";

            } else {
                CommunityPostDTO pDTO = new CommunityPostDTO();
                pDTO.setId(Long.parseLong(postId));
                pDTO.setMemberId(Long.parseLong(memberId));

                if (communityService.deletePostInfo(pDTO) > 0) {
                    res = 1;
                    msg = "삭제되었습니다.";

                } else {
                    msg = "본인이 작성한 게시글만 삭제할 수 있습니다.";
                }
            }

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 0;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".deletePostInfo End!");
        }

        return dto;
    }

    @ResponseBody
    @PostMapping(value = "insertCommentInfo")
    public MsgDTO insertCommentInfo(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".insertCommentInfo Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));
            String postId = CmmUtil.nvl(request.getParameter("postId"), "0");
            String content = CmmUtil.nvl(request.getParameter("content")).trim();

            log.info("session memberId : " + memberId);
            log.info("postId : " + postId);
            log.info("content : " + content);

            if (memberId.isEmpty()) {
                msg = "로그인이 필요합니다.";

            } else if (content.isEmpty()) {
                msg = "댓글 내용을 입력해 주세요.";

            } else {
                CommunityCommentDTO pDTO = new CommunityCommentDTO();
                pDTO.setPostId(Long.parseLong(postId));
                pDTO.setMemberId(Long.parseLong(memberId));
                pDTO.setContent(content);

                res = communityService.insertCommentInfo(pDTO);

                log.info("댓글 등록 결과(res) : " + res);

                if (res == 1) {
                    msg = "댓글이 등록되었습니다.";

                } else if (res == 3) {
                    msg = "게시글을 찾을 수 없습니다.";

                } else {
                    msg = "오류로 인해 댓글 등록이 실패하였습니다.";
                }
            }

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 0;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".insertCommentInfo End!");
        }

        return dto;
    }

    @ResponseBody
    @PostMapping(value = "deleteCommentInfo")
    public MsgDTO deleteCommentInfo(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".deleteCommentInfo Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));
            String commentId = CmmUtil.nvl(request.getParameter("commentId"), "0");

            log.info("session memberId : " + memberId);
            log.info("commentId : " + commentId);

            if (memberId.isEmpty()) {
                msg = "로그인이 필요합니다.";

            } else {
                CommunityCommentDTO pDTO = new CommunityCommentDTO();
                pDTO.setId(Long.parseLong(commentId));
                pDTO.setMemberId(Long.parseLong(memberId));

                if (communityService.deleteCommentInfo(pDTO) > 0) {
                    res = 1;
                    msg = "삭제되었습니다.";

                } else {
                    msg = "본인이 작성한 댓글만 삭제할 수 있습니다.";
                }
            }

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 0;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".deleteCommentInfo End!");
        }

        return dto;
    }

    @ResponseBody
    @PostMapping(value = "insertReportInfo")
    public MsgDTO insertReportInfo(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".insertReportInfo Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));
            String postId = CmmUtil.nvl(request.getParameter("postId"), "0");
            String reason = CmmUtil.nvl(request.getParameter("reason"), "ETC");
            String detail = CmmUtil.nvl(request.getParameter("detail"));

            log.info("session memberId : " + memberId);
            log.info("postId : " + postId);
            log.info("reason : " + reason);

            if (memberId.isEmpty()) {
                msg = "로그인이 필요합니다.";

            } else {
                CommunityReportDTO pDTO = new CommunityReportDTO();
                pDTO.setPostId(Long.parseLong(postId));
                pDTO.setReporterMemberId(Long.parseLong(memberId));
                pDTO.setReason(reason);
                pDTO.setDetail(detail);

                res = communityService.insertReportInfo(pDTO);

                log.info("신고 결과(res) : " + res);

                if (res == 1) {
                    msg = "신고가 접수되었습니다.";

                } else if (res == 2) {
                    msg = "이미 신고한 게시글입니다.";

                } else {
                    msg = "오류로 인해 신고가 실패하였습니다.";
                }
            }

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 0;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".insertReportInfo End!");
        }

        return dto;
    }
}
