package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.CommunityCommentDTO;
import org.ykk.jobbridge.dto.CommunityPostDTO;
import org.ykk.jobbridge.dto.CommunityReportDTO;
import org.ykk.jobbridge.dto.SessionMember;

import java.util.List;

public interface CommunityService {

    List<CommunityPostDTO> getPosts(SessionMember member);

    CommunityPostDTO createPost(CommunityPostDTO post, SessionMember member);

    CommunityPostDTO increaseViews(Long postId, SessionMember member);

    void deletePost(Long postId, Long memberId);

    CommunityCommentDTO createComment(Long postId, CommunityCommentDTO comment, SessionMember member);

    void deleteComment(Long commentId, Long memberId);

    CommunityReportDTO reportPost(Long postId, Long memberId);
}
