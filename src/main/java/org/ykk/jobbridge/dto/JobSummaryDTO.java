package org.ykk.jobbridge.dto;

import java.time.LocalDate;

public class JobSummaryDTO {

    private String id;
    private String company;
    private String title;
    private String location;
    private int score;
    private int applicants;
    private String status;
    private LocalDate posted;

    public JobSummaryDTO(String id, String company, String title, String location,
                         int score, int applicants, String status, LocalDate posted) {
        this.id = id;
        this.company = company;
        this.title = title;
        this.location = location;
        this.score = score;
        this.applicants = applicants;
        this.status = status;
        this.posted = posted;
    }

    public String getId() { return id; }
    public String getCompany() { return company; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public int getScore() { return score; }
    public int getApplicants() { return applicants; }
    public String getStatus() { return status; }
    public LocalDate getPosted() { return posted; }
}
