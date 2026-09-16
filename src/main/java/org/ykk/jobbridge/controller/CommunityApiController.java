package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.ykk.jobbridge.dto.CommunityPostDTO;
import org.ykk.jobbridge.dto.CommunityCommentDTO;
import org.ykk.jobbridge.dto.CommunityReportDTO;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.service.CommunityService;

import java.util.List;

@RestController
@RequestMapping("/api/community/posts")
public class CommunityApiController {
    private final CommunityService communityService;

    public CommunityApiController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping
    public List<CommunityPostDTO> posts(HttpSession session) {
        SessionMember member = (SessionMember) session.getAttribute("loginMember");
        return communityService.getPosts(member);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommunityPostDTO create(@RequestBody CommunityPostDTO post, HttpSession session) {
        SessionMember member = requireLogin(session);
        return communityService.createPost(post, member);
    }

    @PostMapping("/{id}/views")
    public CommunityPostDTO incrementViews(@PathVariable Long id, HttpSession session) {
        SessionMember member = (SessionMember) session.getAttribute("loginMember");
        return communityService.increaseViews(id, member);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, HttpSession session) {
        communityService.deletePost(id, requireLogin(session).getId());
    }

    @PostMapping("/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommunityCommentDTO createComment(@PathVariable Long postId,
                                              @RequestBody CommunityCommentDTO comment,
                                              HttpSession session) {
        SessionMember member = requireLogin(session);
        return communityService.createComment(postId, comment, member);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long postId, @PathVariable Long commentId, HttpSession session) {
        communityService.deleteComment(commentId, requireLogin(session).getId());
    }

    @PostMapping("/{postId}/reports")
    @ResponseStatus(HttpStatus.CREATED)
    public CommunityReportDTO report(@PathVariable Long postId, HttpSession session) {
        SessionMember member = requireLogin(session);
        return communityService.reportPost(postId, member.getId());
    }

    private SessionMember requireLogin(HttpSession session) {
        SessionMember member = (SessionMember) session.getAttribute("loginMember");
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return member;
    }
}
