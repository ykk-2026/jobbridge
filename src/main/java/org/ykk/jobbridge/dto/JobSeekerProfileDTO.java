package org.ykk.jobbridge.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class JobSeekerProfileDTO {

    // member 테이블 정보
    private Long memberId;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private String email;
    private String phone;

    // job_seeker_profile 테이블 정보
    private Long profileId;
    private String profileImageUrl;
    private String residenceRegion;
    private String desiredJob;
    private String desiredRegion;
    private String employmentType;
    private String careerType;
    private Integer careerYears;
    private Integer minSalary;

    private Boolean remotePreferred;
    private Boolean flexiblePreferred;
    private Boolean wheelchairRequired;
    private Boolean accessibleRestroomRequired;
    private Boolean disabledParkingRequired;
    private Boolean assistiveDeviceRequired;
    private Boolean hybridPreferred;
    private Boolean onsitePreferred;

    private LocalTime contactTimeStart;
    private LocalTime contactTimeEnd;
    private String contactMethod;

    private String introduction;
    private Boolean profilePublic;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public String getResidenceRegion() { return residenceRegion; }
    public void setResidenceRegion(String residenceRegion) { this.residenceRegion = residenceRegion; }
    public String getDesiredJob() { return desiredJob; }
    public void setDesiredJob(String desiredJob) { this.desiredJob = desiredJob; }
    public String getDesiredRegion() { return desiredRegion; }
    public void setDesiredRegion(String desiredRegion) { this.desiredRegion = desiredRegion; }
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
    public String getCareerType() { return careerType; }
    public void setCareerType(String careerType) { this.careerType = careerType; }
    public Integer getCareerYears() { return careerYears; }
    public void setCareerYears(Integer careerYears) { this.careerYears = careerYears; }
    public Integer getMinSalary() { return minSalary; }
    public void setMinSalary(Integer minSalary) { this.minSalary = minSalary; }
    public Boolean getRemotePreferred() { return remotePreferred; }
    public void setRemotePreferred(Boolean remotePreferred) { this.remotePreferred = remotePreferred; }
    public Boolean getFlexiblePreferred() { return flexiblePreferred; }
    public void setFlexiblePreferred(Boolean flexiblePreferred) { this.flexiblePreferred = flexiblePreferred; }
    public Boolean getWheelchairRequired() { return wheelchairRequired; }
    public void setWheelchairRequired(Boolean wheelchairRequired) { this.wheelchairRequired = wheelchairRequired; }
    public Boolean getAccessibleRestroomRequired() { return accessibleRestroomRequired; }
    public void setAccessibleRestroomRequired(Boolean accessibleRestroomRequired) { this.accessibleRestroomRequired = accessibleRestroomRequired; }
    public Boolean getDisabledParkingRequired() { return disabledParkingRequired; }
    public void setDisabledParkingRequired(Boolean disabledParkingRequired) { this.disabledParkingRequired = disabledParkingRequired; }
    public Boolean getAssistiveDeviceRequired() { return assistiveDeviceRequired; }
    public void setAssistiveDeviceRequired(Boolean assistiveDeviceRequired) { this.assistiveDeviceRequired = assistiveDeviceRequired; }
    public Boolean getHybridPreferred() { return hybridPreferred; }
    public void setHybridPreferred(Boolean hybridPreferred) { this.hybridPreferred = hybridPreferred; }
    public Boolean getOnsitePreferred() { return onsitePreferred; }
    public void setOnsitePreferred(Boolean onsitePreferred) { this.onsitePreferred = onsitePreferred; }
    public LocalTime getContactTimeStart() { return contactTimeStart; }
    public void setContactTimeStart(LocalTime contactTimeStart) { this.contactTimeStart = contactTimeStart; }
    public LocalTime getContactTimeEnd() { return contactTimeEnd; }
    public void setContactTimeEnd(LocalTime contactTimeEnd) { this.contactTimeEnd = contactTimeEnd; }
    public String getContactMethod() { return contactMethod; }
    public void setContactMethod(String contactMethod) { this.contactMethod = contactMethod; }
    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public Boolean getProfilePublic() { return profilePublic; }
    public void setProfilePublic(Boolean profilePublic) { this.profilePublic = profilePublic; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
