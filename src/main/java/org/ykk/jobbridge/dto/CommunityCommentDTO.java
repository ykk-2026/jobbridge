package org.ykk.jobbridge.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommunityCommentDTO {
    private Long id;
    private Long postId;
    private Long memberId;
    private String author;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
