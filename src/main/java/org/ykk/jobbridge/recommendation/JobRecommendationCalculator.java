package org.ykk.jobbridge.recommendation;

import org.springframework.stereotype.Component;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.dto.InterestJobDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JobRecommendationCalculator {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");
    private static final int JOB_POINTS = 30;
    private static final int REGION_POINTS = 15;
    private static final int EMPLOYMENT_TYPE_POINTS = 10;
    private static final int CAREER_POINTS = 10;
    private static final int SALARY_POINTS = 10;
    private static final int WORK_STYLE_POINTS = 10;
    private static final int ACCESSIBILITY_POINTS = 15;

    public AiJobRecommendationDTO calculate(Long memberId,
                                             JobSeekerProfileDTO profile,
                                             InterestJobDTO job) {
        List<String> matches = new ArrayList<>();
        List<String> mismatches = new ArrayList<>();

        int jobScore = textScore(profile.getDesiredJob(), JOB_POINTS,
                Arrays.asList(job.getTitle(), job.getJobCategory()), "희망 직무", matches, mismatches);
        int regionScore = textScore(profile.getDesiredRegion(), REGION_POINTS,
                Arrays.asList(job.getLocation()), "희망 지역", matches, mismatches);
        int employmentTypeScore = employmentTypeScore(profile, job, matches, mismatches);
        int careerScore = careerScore(profile, job, matches, mismatches);
        int salaryScore = salaryScore(profile, job, matches, mismatches);
        int workStyleScore = workStyleScore(profile, job, matches, mismatches);
        int accessibilityScore = accessibilityScore(profile, job, matches, mismatches);

        AiJobRecommendationDTO result = new AiJobRecommendationDTO();
        result.setMemberId(memberId);
        result.setJobId(job.getId());
        result.setJobScore(jobScore);
        result.setRegionScore(regionScore);
        result.setEmploymentTypeScore(employmentTypeScore);
        result.setCareerScore(careerScore);
        result.setSalaryScore(salaryScore);
        result.setWorkStyleScore(workStyleScore);
        result.setAccessibilityScore(accessibilityScore);
        result.setTotalScore(jobScore + regionScore + employmentTypeScore + careerScore
                + salaryScore + workStyleScore + accessibilityScore);
        if (matches.isEmpty()) {
            result.setRecommendationReason("일반 추천 조건에 해당합니다.");
        } else {
            result.setRecommendationReason(String.join(", ", matches));
        }
        if (mismatches.isEmpty()) {
            result.setMismatchReason("없음");
        } else {
            result.setMismatchReason(String.join(", ", mismatches));
        }
        result.setJob(job);
        return result;
    }

    private int textScore(String preference, int points, List<String> candidates, String label,
                          List<String> matches, List<String> mismatches) {
        if (unrestricted(preference)) {
            matches.add(label + " 제한 없음");
            return points;
        }

        String normalizedPreference = normalize(preference);
        boolean matched = false;
        for (String candidate : candidates) {
            if (candidate == null || candidate.isBlank()) {
                continue;
            }

            String normalizedCandidate = normalize(candidate);
            if (normalizedCandidate.contains(normalizedPreference)
                    || normalizedPreference.contains(normalizedCandidate)
                    || tokenMatches(preference, normalizedCandidate)) {
                matched = true;
                break;
            }
        }
        if (matched) {
            matches.add(label + " 일치");
            return points;
        }
        mismatches.add(label + " 불일치");
        return 0;
    }

    private boolean tokenMatches(String preference, String normalizedCandidate) {
        for (String token : preference.split("[,/|]")) {
            String normalizedToken = normalize(token);
            if (normalizedToken.length() >= 2 && normalizedCandidate.contains(normalizedToken)) {
                return true;
            }
        }
        return false;
    }

    private int employmentTypeScore(JobSeekerProfileDTO profile, InterestJobDTO job,
                                    List<String> matches, List<String> mismatches) {
        String preferred = employmentCode(profile.getEmploymentType());
        if (unrestricted(preferred)) {
            matches.add("고용형태 제한 없음");
            return EMPLOYMENT_TYPE_POINTS;
        }
        if (preferred.equals(employmentCode(job.getEmploymentType()))) {
            matches.add("고용형태 일치");
            return EMPLOYMENT_TYPE_POINTS;
        }
        mismatches.add("고용형태 불일치");
        return 0;
    }

    private int careerScore(JobSeekerProfileDTO profile, InterestJobDTO job,
                            List<String> matches, List<String> mismatches) {
        String careerType = normalizeCode(profile.getCareerType());
        if (unrestricted(careerType)) {
            matches.add("경력 제한 없음");
            return CAREER_POINTS;
        }

        String level = normalizeCode(job.getExperienceLevel());
        if (unrestricted(level)) {
            matches.add("공고 경력 제한 없음");
            return CAREER_POINTS;
        }
        boolean matched;
        if ("ENTRY".equals(careerType)) {
            matched = containsAny(level, "ENTRY", "NEWCOMER", "신입", "경력무관", "무관", "ANY");
        } else if ("EXPERIENCED".equals(careerType)) {
            Integer requiredYears = firstNumber(job.getExperienceLevel());
            matched = containsAny(level, "ENTRY", "NEWCOMER", "신입", "경력무관", "무관", "ANY")
                    || (requiredYears != null && profile.getCareerYears() != null
                    && profile.getCareerYears() >= requiredYears)
                    || (requiredYears == null && containsAny(level, "EXPERIENCED", "CAREER", "경력"));
        } else {
            matched = level.equals(careerType);
        }

        if (matched) {
            matches.add("경력 조건 충족");
            return CAREER_POINTS;
        }
        mismatches.add("경력 조건 미충족");
        return 0;
    }

    private int salaryScore(JobSeekerProfileDTO profile, InterestJobDTO job,
                            List<String> matches, List<String> mismatches) {
        Integer desiredSalary = profile.getMinSalary();
        if (desiredSalary == null || desiredSalary <= 0) {
            matches.add("연봉 제한 없음");
            return SALARY_POINTS;
        }
        Integer offeredMaximum = job.getSalaryMax();
        if (offeredMaximum != null && offeredMaximum >= desiredSalary) {
            matches.add("희망 최소 연봉 충족");
            return SALARY_POINTS;
        }
        if (offeredMaximum == null) {
            mismatches.add("연봉 정보 없음");
        } else {
            mismatches.add("희망 최소 연봉 미충족");
        }
        return 0;
    }

    private int workStyleScore(JobSeekerProfileDTO profile, InterestJobDTO job,
                               List<String> matches, List<String> mismatches) {
        List<Boolean> results = new ArrayList<>();
        if (Boolean.TRUE.equals(profile.getRemotePreferred())) {
            boolean matched = Boolean.TRUE.equals(job.getRemoteAvailable());
            results.add(matched);
            addReason(matched, "재택근무 가능", "재택근무 불가", matches, mismatches);
        }
        if (Boolean.TRUE.equals(profile.getFlexiblePreferred())) {
            boolean matched = Boolean.TRUE.equals(job.getFlexibleWorkAvailable());
            results.add(matched);
            addReason(matched, "유연근무 가능", "유연근무 불가", matches, mismatches);
        }
        if (results.isEmpty()) {
            matches.add("근무방식 제한 없음");
            return WORK_STYLE_POINTS;
        }
        return proportionalScore(WORK_STYLE_POINTS, results);
    }

    private int accessibilityScore(JobSeekerProfileDTO profile, InterestJobDTO job,
                                   List<String> matches, List<String> mismatches) {
        List<Boolean> results = new ArrayList<>();
        addRequired(profile.getWheelchairRequired(), job.getWheelchairAccessible(),
                "휠체어 접근 가능", "휠체어 접근 미지원", results, matches, mismatches);
        addRequired(profile.getAccessibleRestroomRequired(), job.getAccessibleRestroom(),
                "장애인 화장실 지원", "장애인 화장실 미지원", results, matches, mismatches);
        addRequired(profile.getDisabledParkingRequired(), job.getDisabledParking(),
                "장애인 주차 지원", "장애인 주차 미지원", results, matches, mismatches);
        addRequired(profile.getAssistiveDeviceRequired(), job.getAssistiveDeviceSupport(),
                "보조기기 지원", "보조기기 미지원", results, matches, mismatches);
        if (results.isEmpty()) {
            matches.add("접근성 필수조건 없음");
            return ACCESSIBILITY_POINTS;
        }
        return proportionalScore(ACCESSIBILITY_POINTS, results);
    }

    private void addRequired(Boolean required, Boolean supported, String match, String mismatch,
                             List<Boolean> results, List<String> matches, List<String> mismatches) {
        if (!Boolean.TRUE.equals(required)) return;
        boolean result = Boolean.TRUE.equals(supported);
        results.add(result);
        addReason(result, match, mismatch, matches, mismatches);
    }

    private void addReason(boolean matched, String match, String mismatch,
                           List<String> matches, List<String> mismatches) {
        if (matched) {
            matches.add(match);
        } else {
            mismatches.add(mismatch);
        }
    }

    private int proportionalScore(int maximum, List<Boolean> results) {
        int matchedCount = 0;
        for (Boolean result : results) {
            if (Boolean.TRUE.equals(result)) {
                matchedCount++;
            }
        }
        return (int) Math.round(maximum * matchedCount / (double) results.size());
    }

    private String employmentCode(String value) {
        String code = normalizeCode(value);
        if ("INTERNSHIP".equals(code)) {
            return "INTERN";
        }
        return code;
    }

    private String normalizeCode(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    }

    private boolean unrestricted(String value) {
        return value == null || value.isBlank() || "ANY".equalsIgnoreCase(value.trim())
                || "무관".equals(value.trim());
    }

    private boolean containsAny(String value, String... candidates) {
        for (String candidate : candidates) {
            if (value.contains(candidate)) {
                return true;
            }
        }
        return false;
    }

    private Integer firstNumber(String value) {
        if (value == null) {
            return null;
        }
        Matcher matcher = NUMBER_PATTERN.matcher(value);
        if (matcher.find()) {
            return Integer.valueOf(matcher.group(1));
        }
        return null;
    }
}
