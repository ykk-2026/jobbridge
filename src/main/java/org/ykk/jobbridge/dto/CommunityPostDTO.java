package org.ykk.jobbridge.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommunityPostDTO {
    private Long id;
    private Long memberId;
    private String category;
    private String title;
    private String author;
    private String content;
    private int replies;
    private int views;
    private int viewCount;
    private int likeCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommunityCommentDTO> comments = new ArrayList<>();
    private List<CommunityReportDTO> reports = new ArrayList<>();
}
