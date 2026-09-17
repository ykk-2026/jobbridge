package org.ykk.jobbridge.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class JobPostingDTO {

    private Long id;
    private Long companyMemberId;
    private String companyName;
    private String title;
    private String jobCategory;
    private String employmentType;
    private String location;
    private Integer salaryMin;
    private Integer salaryMax;
    private String experienceLevel;
    private String educationLevel;
    private String description;
    private String requirements;
    private String preferredQualifications;
    private String accessibilityInfo;
    private Boolean wheelchairAccessible;
    private Boolean accessibleRestroom;
    private Boolean disabledParking;
    private Boolean remoteAvailable;
    private Boolean flexibleWorkAvailable;
    private Boolean assistiveDeviceSupport;
    private LocalDate deadline;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCompanyMemberId() { return companyMemberId; }
    public void setCompanyMemberId(Long companyMemberId) { this.companyMemberId = companyMemberId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getJobCategory() { return jobCategory; }
    public void setJobCategory(String jobCategory) { this.jobCategory = jobCategory; }
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getSalaryMin() { return salaryMin; }
    public void setSalaryMin(Integer salaryMin) { this.salaryMin = salaryMin; }
    public Integer getSalaryMax() { return salaryMax; }
    public void setSalaryMax(Integer salaryMax) { this.salaryMax = salaryMax; }
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public String getPreferredQualifications() { return preferredQualifications; }
    public void setPreferredQualifications(String preferredQualifications) { this.preferredQualifications = preferredQualifications; }
    public String getAccessibilityInfo() { return accessibilityInfo; }
    public void setAccessibilityInfo(String accessibilityInfo) { this.accessibilityInfo = accessibilityInfo; }
    public Boolean getWheelchairAccessible() { return wheelchairAccessible; }
    public void setWheelchairAccessible(Boolean wheelchairAccessible) { this.wheelchairAccessible = wheelchairAccessible; }
    public Boolean getAccessibleRestroom() { return accessibleRestroom; }
    public void setAccessibleRestroom(Boolean accessibleRestroom) { this.accessibleRestroom = accessibleRestroom; }
    public Boolean getDisabledParking() { return disabledParking; }
    public void setDisabledParking(Boolean disabledParking) { this.disabledParking = disabledParking; }
    public Boolean getRemoteAvailable() { return remoteAvailable; }
    public void setRemoteAvailable(Boolean remoteAvailable) { this.remoteAvailable = remoteAvailable; }
    public Boolean getFlexibleWorkAvailable() { return flexibleWorkAvailable; }
    public void setFlexibleWorkAvailable(Boolean flexibleWorkAvailable) { this.flexibleWorkAvailable = flexibleWorkAvailable; }
    public Boolean getAssistiveDeviceSupport() { return assistiveDeviceSupport; }
    public void setAssistiveDeviceSupport(Boolean assistiveDeviceSupport) { this.assistiveDeviceSupport = assistiveDeviceSupport; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
