package org.ykk.jobbridge.service.serviceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.ykk.jobbridge.dto.CommunityCommentDTO;
import org.ykk.jobbridge.dto.CommunityPostDTO;
import org.ykk.jobbridge.dto.CommunityReportDTO;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.mapper.CommunityMapper;
import org.ykk.jobbridge.service.CommunityService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CommunityServiceImpl implements CommunityService {

    private static final Set<String> CATEGORIES = new HashSet<>(
            Arrays.asList("FREE", "QUESTION", "TIP", "INFO"));

    private final CommunityMapper communityMapper;

    public CommunityServiceImpl(CommunityMapper communityMapper) {
        this.communityMapper = communityMapper;
    }

    @Override
    public List<CommunityPostDTO> getPosts(SessionMember member) {
        List<CommunityPostDTO> posts = communityMapper.findAllPosts();
        for (CommunityPostDTO post : posts) {
            addCommentsAndReports(post, member);
        }
        return posts;
    }

    @Override
    @Transactional
    public CommunityPostDTO createPost(CommunityPostDTO post, SessionMember member) {
        if (post == null || isBlank(post.getTitle()) || isBlank(post.getContent())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목과 내용을 입력해 주세요.");
        }

        post.setMemberId(member.getId());
        if (!CATEGORIES.contains(post.getCategory())) {
            post.setCategory("FREE");
        }
        post.setTitle(post.getTitle().trim());
        post.setContent(post.getContent().trim());
        communityMapper.insertPost(post);
        return addCommentsAndReports(communityMapper.findPostById(post.getId()), member);
    }

    @Override
    public CommunityPostDTO increaseViews(Long postId, SessionMember member) {
        if (communityMapper.incrementViews(postId) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        return addCommentsAndReports(communityMapper.findPostById(postId), member);
    }

    @Override
    public void deletePost(Long postId, Long memberId) {
        if (communityMapper.deletePost(postId, memberId) == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 게시글만 삭제할 수 있습니다.");
        }
    }

    @Override
    public CommunityCommentDTO createComment(Long postId, CommunityCommentDTO comment, SessionMember member) {
        if (comment == null || isBlank(comment.getContent())) {
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

        List<CommunityCommentDTO> comments = communityMapper.findComments(postId);
        for (CommunityCommentDTO savedComment : comments) {
            if (savedComment.getId().equals(comment.getId())) {
                return savedComment;
            }
        }
        return comment;
    }

    @Override
    public void deleteComment(Long commentId, Long memberId) {
        if (communityMapper.deleteComment(commentId, memberId) == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 댓글만 삭제할 수 있습니다.");
        }
    }

    @Override
    public CommunityReportDTO reportPost(Long postId, Long memberId) {
        if (communityMapper.countReport(postId, memberId) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 신고한 게시글입니다.");
        }

        CommunityReportDTO report = new CommunityReportDTO();
        report.setPostId(postId);
        report.setReporterMemberId(memberId);
        report.setReason("ETC");
        report.setStatus("PENDING");
        communityMapper.insertReport(report);
        return report;
    }

    private CommunityPostDTO addCommentsAndReports(CommunityPostDTO post, SessionMember member) {
        if (post == null) {
            return null;
        }
        post.setComments(communityMapper.findComments(post.getId()));
        if (member != null) {
            post.setReports(communityMapper.findReportsByMember(post.getId(), member.getId()));
        }
        return post;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
