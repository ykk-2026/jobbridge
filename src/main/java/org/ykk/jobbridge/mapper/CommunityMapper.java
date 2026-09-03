package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Param;
import org.ykk.jobbridge.dto.CommunityPostDTO;
import org.ykk.jobbridge.dto.CommunityCommentDTO;
import org.ykk.jobbridge.dto.CommunityReportDTO;

import java.util.List;

public interface CommunityMapper {
    List<CommunityPostDTO> findAllPosts();
    CommunityPostDTO findPostById(@Param("id") Long id);
    int insertPost(CommunityPostDTO post);
    int incrementViews(@Param("id") Long id);
    int deletePost(@Param("id") Long id, @Param("memberId") Long memberId);
    List<CommunityCommentDTO> findComments(@Param("postId") Long postId);
    int insertComment(CommunityCommentDTO comment);
    int deleteComment(@Param("id") Long id, @Param("memberId") Long memberId);
    List<CommunityReportDTO> findReportsByMember(@Param("postId") Long postId,
                                                  @Param("memberId") Long memberId);
    int countReport(@Param("postId") Long postId, @Param("memberId") Long memberId);
    int insertReport(CommunityReportDTO report);
}
