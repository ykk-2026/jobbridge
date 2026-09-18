package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.CommunityCommentDTO;
import org.ykk.jobbridge.dto.CommunityPostDTO;
import org.ykk.jobbridge.dto.CommunityReportDTO;

import java.util.List;

/**
 * CommunityMapper.xml과 매핑되는 인터페이스
 */
@Mapper
public interface ICommunityMapper {

    // 게시글 리스트
    List<CommunityPostDTO> getPostList() throws Exception;

    // 게시글 상세보기
    CommunityPostDTO getPostInfo(CommunityPostDTO pDTO) throws Exception;

    // 게시글 등록
    int insertPostInfo(CommunityPostDTO pDTO) throws Exception;

    // 게시글 조회수 증가
    int updatePostViews(CommunityPostDTO pDTO) throws Exception;

    // 게시글 삭제(본인 글만, 상태값 DELETED로 변경)
    int deletePostInfo(CommunityPostDTO pDTO) throws Exception;

    // 댓글 리스트
    List<CommunityCommentDTO> getCommentList(CommunityCommentDTO pDTO) throws Exception;

    // 댓글 등록
    int insertCommentInfo(CommunityCommentDTO pDTO) throws Exception;

    // 댓글 삭제(본인 댓글만)
    int deleteCommentInfo(CommunityCommentDTO pDTO) throws Exception;

    // 로그인한 회원이 해당 게시글에 신고한 내역
    List<CommunityReportDTO> getReportList(CommunityReportDTO pDTO) throws Exception;

    // 이미 신고했는지 체크
    CommunityReportDTO getReportExists(CommunityReportDTO pDTO) throws Exception;

    // 게시글 신고 등록
    int insertReportInfo(CommunityReportDTO pDTO) throws Exception;

}
