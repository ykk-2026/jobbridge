package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * COMMUNITY_POST 테이블(커뮤니티 게시글)과 매핑되는 DTO
 */
@Getter
@Setter
public class CommunityPostDTO {

    private Long id; // 기본키, 게시글 번호
    private Long memberId; // 작성자 회원 고유번호
    private String category; // 분류(FREE, QUESTION, TIP, INFO)
    private String title; // 제목
    private String author; // 작성자명(MEMBER 테이블 JOIN)
    private String content; // 내용
    private int replies; // 댓글 수
    private int views; // 조회수
    private int viewCount; // 조회수
    private int likeCount; // 좋아요 수
    private String status; // 상태(ACTIVE, DELETED)
    private String createdAt; // 등록일
    private String updatedAt; // 수정일

    private List<CommunityCommentDTO> comments = new ArrayList<>(); // 댓글 목록
    private List<CommunityReportDTO> reports = new ArrayList<>(); // 로그인 사용자의 신고 내역

}
