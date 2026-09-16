package org.ykk.jobbridge.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getReplies() { return replies; }
    public void setReplies(int replies) { this.replies = replies; }
    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<CommunityCommentDTO> getComments() { return comments; }
    public void setComments(List<CommunityCommentDTO> comments) { this.comments = comments; }
    public List<CommunityReportDTO> getReports() { return reports; }
    public void setReports(List<CommunityReportDTO> reports) { this.reports = reports; }
}
