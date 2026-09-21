package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;





@Getter
@Setter
public class JobSeekerProfileDTO {


    private Long memberId;
    private String name;
    private String birthDate;
    private String gender;
    private String email;
    private String phone;


    private Long profileId;
    private String profileImageUrl;
    private String residenceRegion;
    private String desiredJob;
    private String desiredRegion;
    private String employmentType;
    private String careerType;
    private Integer careerYears;
    private Integer minSalary;
    private String workType;

    private Boolean wheelchairRequired;
    private Boolean accessibleRestroomRequired;
    private Boolean disabledParkingRequired;
    private Boolean assistiveDeviceRequired;
    private Boolean restAreaRequired;
    private Boolean elevatorRequired;

    private String contactTimeStart;
    private String contactTimeEnd;
    private String contactMethod;

    private String introduction;
    private Boolean profilePublic;

    private String createdAt;
    private String updatedAt;

    private String existsYn;

}
