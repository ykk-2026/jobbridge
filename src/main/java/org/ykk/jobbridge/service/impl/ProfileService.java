package org.ykk.jobbridge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.mapper.IProfileMapper;
import org.ykk.jobbridge.service.IProfileService;
import org.ykk.jobbridge.util.CmmUtil;
import org.ykk.jobbridge.util.EducationLevelCodes;
import org.ykk.jobbridge.util.EmploymentTypeCodes;
import org.ykk.jobbridge.util.WorkTypeCodes;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService implements IProfileService {

    private final IProfileMapper profileMapper;

    @Override
    public JobSeekerProfileDTO getProfileInfo(JobSeekerProfileDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getProfileInfo Start!");

        return profileMapper.getProfileInfo(pDTO);
    }

    @Transactional
    @Override
    public int saveProfileInfo(JobSeekerProfileDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".saveProfileInfo Start!");

        int res = 0;

        pDTO.setGender(toGenderCode(pDTO.getGender()));
        pDTO.setEmploymentType(toEmploymentTypeCode(pDTO.getEmploymentType()));
        pDTO.setWorkType(WorkTypeCodes.normalize(pDTO.getWorkType()));
        pDTO.setEducationLevel(EducationLevelCodes.normalize(pDTO.getEducationLevel()));
        pDTO.setCareerType(toCareerTypeCode(pDTO.getCareerType()));
        pDTO.setContactMethod(toContactMethodCode(pDTO.getContactMethod()));

        JobSeekerProfileDTO existsDTO = profileMapper.getProfileExists(pDTO);

        int success;

        if (CmmUtil.nvl(existsDTO.getExistsYn()).equals("Y")) {
            log.info("프로필 수정 : " + pDTO.getMemberId());
            success = profileMapper.updateProfileInfo(pDTO);

        } else {
            log.info("프로필 등록 : " + pDTO.getMemberId());
            success = profileMapper.insertProfileInfo(pDTO);
        }

        profileMapper.updateMemberInfo(pDTO);

        if (success > 0) {
            res = 1;
        }

        log.info(this.getClass().getName() + ".saveProfileInfo End!");

        return res;
    }

    private String toGenderCode(String value) {
        String str = CmmUtil.nvl(value).trim();

        if (str.isEmpty()) return null;
        if (str.equals("남성")) return "MALE";
        if (str.equals("여성")) return "FEMALE";
        if (str.equals("기타") || str.equals("선택 안함")) return "OTHER";

        return str.toUpperCase();
    }

    private String toEmploymentTypeCode(String value) {
        return EmploymentTypeCodes.normalize(value);
    }

    private String toCareerTypeCode(String value) {
        String str = CmmUtil.nvl(value).trim();

        if (str.isEmpty()) return null;
        if (str.equals("신입")) return "ENTRY";
        if (str.equals("경력")) return "EXPERIENCED";
        if (str.equals("무관")) return "ANY";

        return str.toUpperCase();
    }

    private String toContactMethodCode(String value) {
        String str = CmmUtil.nvl(value).trim();

        if (str.isEmpty()) return null;
        if (str.equals("전화")) return "PHONE";
        if (str.equals("이메일")) return "EMAIL";
        if (str.equals("문자")) return "SMS";
        if (str.equals("카카오톡")) return "KAKAO";

        return str.toUpperCase();
    }
}
