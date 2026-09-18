package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.CommunityCommentDTO;
import org.ykk.jobbridge.dto.CommunityPostDTO;
import org.ykk.jobbridge.dto.CommunityReportDTO;

import java.util.List;

public interface ICommunityService {

    /**
     * 게시글 리스트 (댓글, 로그인 사용자의 신고 내역 포함)
     *
     * @param pDTO 로그인한 회원 고유번호(memberId), 로그인 안 했으면 null
     * @return 조회 결과
     */
    List<CommunityPostDTO> getPostList(CommunityPostDTO pDTO) throws Exception;

    /**
     * 게시글 상세보기
     *
     * @param pDTO 상세내용 조회할 id 값, 로그인한 회원 고유번호(memberId)
     * @param type 조회수 증가여부
     * @return 조회 결과
     */
    CommunityPostDTO getPostInfo(CommunityPostDTO pDTO, boolean type) throws Exception;

    /**
     * 게시글 등록
     *
     * @param pDTO 화면에서 입력된 게시글 값들
     */
    void insertPostInfo(CommunityPostDTO pDTO) throws Exception;

    /**
     * 게시글 삭제 (본인 글만)
     *
     * @param pDTO 삭제할 id, memberId 값
     * @return 삭제된 건수 (0이면 본인 글이 아님)
     */
    int deletePostInfo(CommunityPostDTO pDTO) throws Exception;

    /**
     * 댓글 등록
     *
     * @param pDTO 화면에서 입력된 댓글 값들
     * @return 1 : 성공 / 3 : 없는 게시글 / 0 : 실패
     */
    int insertCommentInfo(CommunityCommentDTO pDTO) throws Exception;

    /**
     * 댓글 삭제 (본인 댓글만)
     *
     * @param pDTO 삭제할 id, memberId 값
     * @return 삭제된 건수 (0이면 본인 댓글이 아님)
     */
    int deleteCommentInfo(CommunityCommentDTO pDTO) throws Exception;

    /**
     * 게시글 신고
     *
     * @param pDTO 신고할 postId, reporterMemberId 값
     * @return 1 : 성공 / 2 : 이미 신고함 / 0 : 실패
     */
    int insertReportInfo(CommunityReportDTO pDTO) throws Exception;

}
