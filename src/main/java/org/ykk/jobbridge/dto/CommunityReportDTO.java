package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * COMMUNITY_REPORT 테이블(게시글 신고)과 매핑되는 DTO
 */
@Getter
@Setter
public class CommunityReportDTO {

    private Long id; // 기본키
    private Long postId; // 게시글 번호
    private Long reporterMemberId; // 신고자 회원 고유번호
    private String reason; // 신고 사유
    private String detail; // 상세 내용
    private String status; // 처리 상태(PENDING 등)
    private String createdAt; // 신고일

    private String existsYn; // 이미 신고했는지 여부(Y/N)

}
