package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * COMMUNITY_COMMENT 테이블(댓글)과 매핑되는 DTO
 */
@Getter
@Setter
public class CommunityCommentDTO {

    private Long id; // 기본키, 댓글 번호
    private Long postId; // 게시글 번호
    private Long memberId; // 작성자 회원 고유번호
    private String author; // 작성자명(MEMBER 테이블 JOIN)
    private String content; // 내용
    private String status; // 상태(ACTIVE, DELETED)
    private String createdAt; // 등록일
    private String updatedAt; // 수정일

}
