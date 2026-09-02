package org.ykk.jobbridge.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.mapper.ProfileMapper;
import org.ykk.jobbridge.service.ProfileService;

import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {

    private static final Map<String, String> GENDER_CODES = Map.of(
            "남성", "MALE",
            "여성", "FEMALE",
            "기타", "OTHER",
            "선택 안함", "OTHER"
    );
    private static final Map<String, String> EMPLOYMENT_TYPE_CODES = Map.of(
            "정규직", "FULL_TIME",
            "아르바이트", "PART_TIME",
            "계약직", "CONTRACT",
            "인턴", "INTERNSHIP",
            "프리랜서", "FREELANCER",
            "무관", "ANY"
    );
    private static final Map<String, String> CAREER_TYPE_CODES = Map.of(
            "신입", "ENTRY",
            "경력", "EXPERIENCED",
            "무관", "ANY"
    );
    private static final Map<String, String> CONTACT_METHOD_CODES = Map.of(
            "전화", "PHONE",
            "이메일", "EMAIL",
            "문자", "SMS",
            "카카오톡", "KAKAO"
    );

    private final ProfileMapper profileMapper;
    private volatile boolean profileSchemaReady;

    @Override
    public JobSeekerProfileDTO getProfile(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("회원 고유번호가 없습니다.");
        }

        ensureProfileSchema();
        return profileMapper.findProfileByMemberId(memberId);
    }

    @Override
    @Transactional
    public int saveProfile(JobSeekerProfileDTO profileDTO) {
        if (profileDTO == null || profileDTO.getMemberId() == null) {
            throw new IllegalArgumentException("회원 정보가 없습니다.");
        }

        normalizeEnumValues(profileDTO);
        validateProfile(profileDTO);
        ensureProfileSchema();

        int profileCount = profileMapper.countProfileByMemberId(profileDTO.getMemberId());
        if (profileCount == 0) {
            int inserted = profileMapper.insertProfile(profileDTO);
            profileMapper.updateMember(profileDTO);
            return inserted;
        }

        int updated = profileMapper.updateProfile(profileDTO);
        profileMapper.updateMember(profileDTO);
        return updated;
    }

    @Override
    public boolean hasProfile(Long memberId) {
        if (memberId == null) {
            return false;
        }

        ensureProfileSchema();
        return profileMapper.countProfileByMemberId(memberId) > 0;
    }

    private synchronized void ensureProfileSchema() {
        if (profileSchemaReady) return;
        profileMapper.ensureNameColumn();
        profileMapper.ensureBirthDateColumn();
        profileMapper.ensureGenderColumn();
        profileMapper.ensureEmailColumn();
        profileMapper.ensurePhoneColumn();
        profileSchemaReady = true;
    }

    private void validateProfile(JobSeekerProfileDTO profileDTO) {
        Integer careerYears = profileDTO.getCareerYears();
        Integer minSalary = profileDTO.getMinSalary();

        if (careerYears != null && careerYears < 0) {
            throw new IllegalArgumentException("경력 연수는 0 이상이어야 합니다.");
        }

        if (minSalary != null && minSalary < 0) {
            throw new IllegalArgumentException("희망 최소 연봉은 0 이상이어야 합니다.");
        }

        if (profileDTO.getContactTimeStart() != null
                && profileDTO.getContactTimeEnd() != null
                && !profileDTO.getContactTimeStart().isBefore(profileDTO.getContactTimeEnd())) {
            throw new IllegalArgumentException("연락 가능 시작 시간은 종료 시간보다 빨라야 합니다.");
        }
    }

    private void normalizeEnumValues(JobSeekerProfileDTO profileDTO) {
        profileDTO.setGender(toDatabaseCode(profileDTO.getGender(), GENDER_CODES));
        profileDTO.setEmploymentType(toDatabaseCode(profileDTO.getEmploymentType(), EMPLOYMENT_TYPE_CODES));
        profileDTO.setCareerType(toDatabaseCode(profileDTO.getCareerType(), CAREER_TYPE_CODES));
        profileDTO.setContactMethod(toDatabaseCode(profileDTO.getContactMethod(), CONTACT_METHOD_CODES));
    }

    private String toDatabaseCode(String value, Map<String, String> labelCodes) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.trim();
        return labelCodes.getOrDefault(trimmed, trimmed.toUpperCase(Locale.ROOT));
    }
}
