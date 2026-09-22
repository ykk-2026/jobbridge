package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityCommentDTO {

    private Long id;
    private Long postId;
    private Long memberId;
    private String author;
    private String content;
    private String status;
    private String createdAt;
    private String updatedAt;

}
