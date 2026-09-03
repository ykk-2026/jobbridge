package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
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
import org.ykk.jobbridge.mapper.CommunityMapper;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/posts")
public class CommunityApiController {
    private static final Set<String> CATEGORIES = Set.of("FREE", "QUESTION", "TIP", "INFO");

    private final CommunityMapper communityMapper;

    @GetMapping
    public List<CommunityPostDTO> posts(HttpSession session) {
        SessionMember member = (SessionMember) session.getAttribute("loginMember");
        List<CommunityPostDTO> posts = communityMapper.findAllPosts();
        posts.forEach(post -> hydrate(post, member));
        return posts;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public CommunityPostDTO create(@RequestBody CommunityPostDTO post, HttpSession session) {
        SessionMember member = requireLogin(session);
        if (post == null || blank(post.getTitle()) || blank(post.getContent())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목과 내용을 입력해 주세요.");
        }

        post.setMemberId(member.getId());
        post.setCategory(CATEGORIES.contains(post.getCategory()) ? post.getCategory() : "FREE");
        post.setTitle(post.getTitle().trim());
        post.setContent(post.getContent().trim());
        communityMapper.insertPost(post);
        return hydrate(communityMapper.findPostById(post.getId()), member);
    }

    @PostMapping("/{id}/views")
    public CommunityPostDTO incrementViews(@PathVariable Long id, HttpSession session) {
        if (communityMapper.incrementViews(id) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        return hydrate(communityMapper.findPostById(id), (SessionMember) session.getAttribute("loginMember"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, HttpSession session) {
        if (communityMapper.deletePost(id, requireLogin(session).getId()) == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 게시글만 삭제할 수 있습니다.");
        }
    }

    @PostMapping("/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommunityCommentDTO createComment(@PathVariable Long postId,
                                              @RequestBody CommunityCommentDTO comment,
                                              HttpSession session) {
        SessionMember member = requireLogin(session);
        if (comment == null || blank(comment.getContent())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "댓글 내용을 입력해 주세요.");
        }
        if (communityMapper.findPostById(postId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        comment.setPostId(postId);
        comment.setMemberId(member.getId());
        comment.setAuthor(member.getName());
        comment.setContent(comment.getContent().trim());
        comment.setStatus("ACTIVE");
        communityMapper.insertComment(comment);
        return communityMapper.findComments(postId).stream()
                .filter(saved -> saved.getId().equals(comment.getId()))
                .findFirst().orElse(comment);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long postId, @PathVariable Long commentId, HttpSession session) {
        if (communityMapper.deleteComment(commentId, requireLogin(session).getId()) == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 댓글만 삭제할 수 있습니다.");
        }
    }

    @PostMapping("/{postId}/reports")
    @ResponseStatus(HttpStatus.CREATED)
    public CommunityReportDTO report(@PathVariable Long postId, HttpSession session) {
        SessionMember member = requireLogin(session);
        if (communityMapper.countReport(postId, member.getId()) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 신고한 게시글입니다.");
        }
        CommunityReportDTO report = new CommunityReportDTO();
        report.setPostId(postId);
        report.setReporterMemberId(member.getId());
        report.setReason("ETC");
        report.setStatus("PENDING");
        communityMapper.insertReport(report);
        return report;
    }

    private CommunityPostDTO hydrate(CommunityPostDTO post, SessionMember member) {
        if (post == null) return null;
        post.setComments(communityMapper.findComments(post.getId()));
        if (member != null) post.setReports(communityMapper.findReportsByMember(post.getId(), member.getId()));
        return post;
    }

    private SessionMember requireLogin(HttpSession session) {
        SessionMember member = (SessionMember) session.getAttribute("loginMember");
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return member;
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
