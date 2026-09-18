package org.ykk.jobbridge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.mapper.IProfileMapper;
import org.ykk.jobbridge.service.IProfileService;
import org.ykk.jobbridge.util.CmmUtil;

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

        // 화면에서 한글로 넘어온 값(남성, 정규직 등)을 DB 코드값(MALE, FULL_TIME 등)으로 변경
        pDTO.setGender(toGenderCode(pDTO.getGender()));
        pDTO.setEmploymentType(toEmploymentTypeCode(pDTO.getEmploymentType()));
        pDTO.setCareerType(toCareerTypeCode(pDTO.getCareerType()));
        pDTO.setContactMethod(toContactMethodCode(pDTO.getContactMethod()));

        // 프로필이 이미 존재하는지 확인
        JobSeekerProfileDTO existsDTO = profileMapper.getProfileExists(pDTO);

        int success;

        if (CmmUtil.nvl(existsDTO.getExistsYn()).equals("Y")) {
            log.info("프로필 수정 : " + pDTO.getMemberId());
            success = profileMapper.updateProfileInfo(pDTO);

        } else {
            log.info("프로필 등록 : " + pDTO.getMemberId());
            success = profileMapper.insertProfileInfo(pDTO);
        }

        // 회원 기본 정보(이름, 생년월일, 성별, 이메일, 전화번호)도 함께 수정
        profileMapper.updateMemberInfo(pDTO);

        if (success > 0) {
            res = 1;
        }

        log.info(this.getClass().getName() + ".saveProfileInfo End!");

        return res;
    }

    /**
     * 성별 코드 변환 (남성 -> MALE)
     */
    private String toGenderCode(String value) {
        String str = CmmUtil.nvl(value).trim();

        if (str.isEmpty()) return null;
        if (str.equals("남성")) return "MALE";
        if (str.equals("여성")) return "FEMALE";
        if (str.equals("기타") || str.equals("선택 안함")) return "OTHER";

        return str.toUpperCase();
    }

    /**
     * 고용형태 코드 변환 (정규직 -> FULL_TIME)
     */
    private String toEmploymentTypeCode(String value) {
        String str = CmmUtil.nvl(value).trim();

        if (str.isEmpty()) return null;
        if (str.equals("정규직")) return "FULL_TIME";
        if (str.equals("아르바이트")) return "PART_TIME";
        if (str.equals("계약직")) return "CONTRACT";
        if (str.equals("인턴")) return "INTERNSHIP";
        if (str.equals("프리랜서")) return "FREELANCER";
        if (str.equals("무관")) return "ANY";

        return str.toUpperCase();
    }

    /**
     * 경력 구분 코드 변환 (신입 -> ENTRY)
     */
    private String toCareerTypeCode(String value) {
        String str = CmmUtil.nvl(value).trim();

        if (str.isEmpty()) return null;
        if (str.equals("신입")) return "ENTRY";
        if (str.equals("경력")) return "EXPERIENCED";
        if (str.equals("무관")) return "ANY";

        return str.toUpperCase();
    }

    /**
     * 연락 방법 코드 변환 (전화 -> PHONE)
     */
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
