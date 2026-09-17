package org.ykk.jobbridge.recommendation;

import org.springframework.stereotype.Component;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JobRecommendationCalculator {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");
    private static final int JOB_POINTS = 25;
    private static final int REGION_POINTS = 20;
    private static final int EMPLOYMENT_POINTS = 10;
    private static final int CAREER_POINTS = 10;
    private static final int SALARY_POINTS = 15;
    private static final int WORK_STYLE_POINTS = 10;
    private static final int ACCESSIBILITY_POINTS = 10;
    private static final Map<String, Set<String>> NEARBY_REGIONS = createNearbyRegions();
    private static final Map<String, Coordinate> SEOUL_DISTRICT_COORDINATES = createSeoulDistrictCoordinates();
    private final KakaoMapDistanceService mapDistanceService;

    public JobRecommendationCalculator(KakaoMapDistanceService mapDistanceService) {
        this.mapDistanceService = mapDistanceService;
    }

    public AiJobRecommendationDTO calculate(Long memberId,
                                             JobSeekerProfileDTO profile,
                                             JobPostingDTO job) {
        List<String> matches = new ArrayList<>();
        List<String> mismatches = new ArrayList<>();

        int jobScore = limitScore(jobScore(profile, job, matches, mismatches), JOB_POINTS);
        int regionScore = limitScore(regionScore(profile.getResidenceRegion(), profile.getDesiredRegion(),
                job.getLocation(), matches, mismatches), REGION_POINTS);
        int employmentScore = limitScore(employmentScore(profile, job, matches, mismatches), EMPLOYMENT_POINTS);
        int careerScore = limitScore(careerScore(profile, job, matches, mismatches), CAREER_POINTS);
        int salaryScore = limitScore(salaryScore(profile, job, matches, mismatches), SALARY_POINTS);
        int workStyleScore = limitScore(workStyleScore(profile, job, matches, mismatches), WORK_STYLE_POINTS);
        int accessibilityScore = limitScore(accessibilityScore(profile, job, matches, mismatches), ACCESSIBILITY_POINTS);

        AiJobRecommendationDTO result = new AiJobRecommendationDTO();
        result.setMemberId(memberId);
        result.setJobId(job.getId());
        result.setJobScore(jobScore);
        result.setRegionScore(regionScore);
        result.setEmploymentTypeScore(employmentScore);
        result.setCareerScore(careerScore);
        result.setSalaryScore(salaryScore);
        result.setWorkStyleScore(workStyleScore);
        result.setAccessibilityScore(accessibilityScore);
        result.setTotalScore(Math.min(100, jobScore + regionScore + employmentScore + careerScore
                + salaryScore + workStyleScore + accessibilityScore));
        result.setRecommendationReason(matches.isEmpty() ? "일치하는 조건이 아직 없습니다." : String.join(", ", matches));
        result.setMismatchReason(mismatches.isEmpty() ? "없음" : String.join(", ", mismatches));
        result.setJob(job);
        return result;
    }

    private int jobScore(JobSeekerProfileDTO profile, JobPostingDTO job,
                         List<String> matches, List<String> mismatches) {
        String desiredJob = profile.getDesiredJob();
        if (unrestricted(desiredJob)) {
            matches.add("희망 직무 미입력: 기본 13/25점");
            return 13;
        }

        String candidate = normalize(String.join(" ", safe(job.getTitle()), safe(job.getJobCategory()),
                safe(job.getRequirements()), safe(job.getDescription())));
        String desired = normalize(desiredJob);
        String category = normalize(job.getJobCategory());
        if (candidate.contains(desired) || (!category.isBlank() && desired.contains(category))) {
            matches.add("희망 직무와 매우 유사: 25/25점");
            return JOB_POINTS;
        }

        List<String> tokens = meaningfulTokens(desiredJob);
        int matched = 0;
        for (String token : tokens) {
            if (candidate.contains(token)) matched++;
        }
        if (matched > 0) {
            int score = Math.min(23, 5 + (int) Math.round(18.0 * matched / tokens.size()));
            matches.add("희망 직무 키워드 " + matched + "/" + tokens.size() + "개 일치: " + score + "/25점");
            return score;
        }
        mismatches.add("희망 직무와 관련 키워드가 적음: 2/25점");
        return 2;
    }

    private int regionScore(String residenceAddress, String desiredRegion, String jobRegion,
                            List<String> matches, List<String> mismatches) {
        String originAddress = unrestricted(residenceAddress) ? desiredRegion : residenceAddress;
        if (unrestricted(originAddress)) {
            matches.add("현재 거주지 미입력: 기본 10/20점");
            return 10;
        }
        if (jobRegion == null || jobRegion.isBlank()) {
            mismatches.add("공고의 근무지역 정보 없음: 5/20점");
            return 5;
        }

        java.util.Optional<KakaoMapDistanceService.RouteInfo> route =
                mapDistanceService.findDrivingRoute(originAddress, jobRegion);
        if (route.isPresent()) {
            return drivingRouteScore(route.get(), matches, mismatches);
        }

        int bestScore = 0;
        String bestReason = "";
        for (String desired : originAddress.split("[,/|]")) {
            RegionResult result = compareRegion(desired.trim(), jobRegion.trim());
            if (result.score > bestScore) {
                bestScore = result.score;
                bestReason = result.reason;
            }
        }
        if (bestScore >= 14) matches.add(bestReason);
        else mismatches.add(bestReason);
        return bestScore;
    }

    private int drivingRouteScore(KakaoMapDistanceService.RouteInfo route,
                                  List<String> matches, List<String> mismatches) {
        int minutes = Math.max(1, (int) Math.round(route.getDurationSeconds() / 60.0));
        double kilometers = route.getDistanceMeters() / 1000.0;
        int score;
        if (minutes <= 20) score = 20;
        else if (minutes <= 30) score = 18;
        else if (minutes <= 45) score = 15;
        else if (minutes <= 60) score = 12;
        else if (minutes <= 90) score = 8;
        else score = 3;

        String reason = String.format(Locale.ROOT,
                "현재 거주지에서 자동차 약 %d분(%.1fkm): %d/20점", minutes, kilometers, score);
        if (score >= 12) matches.add(reason);
        else mismatches.add(reason);
        return score;
    }

    private RegionResult compareRegion(String desired, String actual) {
        String normalizedDesired = normalizeRegion(desired);
        String normalizedActual = normalizeRegion(actual);
        if (normalizedDesired.equals(normalizedActual)) {
            return new RegionResult(20, "희망 지역과 정확히 일치: 20/20점");
        }
        if (normalizedActual.contains(normalizedDesired) || normalizedDesired.contains(normalizedActual)) {
            return new RegionResult(20, "희망 지역 범위에 포함: 20/20점");
        }

        RegionResult distanceResult = compareSeoulDistrictDistance(desired, actual);
        if (distanceResult != null) {
            return distanceResult;
        }

        Set<String> desiredTokens = regionTokens(desired);
        Set<String> actualTokens = regionTokens(actual);
        Set<String> common = new HashSet<>(desiredTokens);
        common.retainAll(actualTokens);
        String desiredProvince = province(desired);
        String actualProvince = province(actual);

        if (!common.isEmpty() && (!common.equals(Set.of(desiredProvince)) || desiredProvince.isBlank())) {
            return new RegionResult(18, "같은 시·군·구 지역: 18/20점");
        }
        if (!desiredProvince.isBlank() && desiredProvince.equals(actualProvince)) {
            return new RegionResult(14, "같은 광역지역: 14/20점");
        }
        if (isNearby(desiredProvince, actualProvince)) {
            return new RegionResult(8, "인접한 광역지역: 8/20점");
        }
        return new RegionResult(2, "희망 지역과 거리가 먼 지역: 2/20점");
    }

    private int employmentScore(JobSeekerProfileDTO profile, JobPostingDTO job,
                                List<String> matches, List<String> mismatches) {
        String preferred = employmentCode(profile.getEmploymentType());
        String offered = employmentCode(job.getEmploymentType());
        if (unrestricted(preferred)) {
            matches.add("고용형태 미선택: 기본 6/10점");
            return 6;
        }
        if (preferred.equals(offered)) {
            matches.add("희망 고용형태 일치: 10/10점");
            return EMPLOYMENT_POINTS;
        }
        if (isRegularEmployment(preferred) && isRegularEmployment(offered)) {
            mismatches.add("비슷한 상시 고용형태: 5/10점");
            return 5;
        }
        mismatches.add("희망 고용형태와 다름: 1/10점");
        return 1;
    }

    private int careerScore(JobSeekerProfileDTO profile, JobPostingDTO job,
                            List<String> matches, List<String> mismatches) {
        String preferred = normalizeCode(profile.getCareerType());
        String required = normalizeCode(job.getExperienceLevel());
        if (unrestricted(preferred)) {
            matches.add("경력 조건 미선택: 기본 6/10점");
            return 6;
        }
        if (unrestricted(required)) {
            matches.add("공고가 경력 무관: 10/10점");
            return CAREER_POINTS;
        }
        if ("ENTRY".equals(preferred)) {
            if (containsAny(required, "ENTRY", "NEWCOMER", "신입")) {
                matches.add("신입 지원 조건 일치: 10/10점");
                return CAREER_POINTS;
            }
            mismatches.add("경력직 공고: 2/10점");
            return 2;
        }

        int ownedYears = profile.getCareerYears() == null ? 0 : profile.getCareerYears();
        Integer requiredYears = firstNumber(job.getExperienceLevel());
        if (requiredYears == null) {
            matches.add("경력직 조건과 유형 일치: 8/10점");
            return 8;
        }
        int difference = requiredYears - ownedYears;
        if (difference <= 0) {
            matches.add("요구 경력 충족: 10/10점");
            return CAREER_POINTS;
        }
        if (difference == 1) {
            mismatches.add("요구 경력보다 1년 부족: 7/10점");
            return 7;
        }
        if (difference <= 3) {
            mismatches.add("요구 경력보다 " + difference + "년 부족: 4/10점");
            return 4;
        }
        mismatches.add("요구 경력 차이가 큼: 1/10점");
        return 1;
    }

    private int salaryScore(JobSeekerProfileDTO profile, JobPostingDTO job,
                            List<String> matches, List<String> mismatches) {
        Integer desired = profile.getMinSalary();
        if (desired == null || desired <= 0) {
            matches.add("희망 급여 미입력: 기본 8/15점");
            return 8;
        }
        Integer offered = job.getSalaryMax() != null ? job.getSalaryMax() : job.getSalaryMin();
        if (offered == null || offered <= 0) {
            mismatches.add("공고의 급여 정보 없음: 6/15점");
            return 6;
        }
        double ratio = offered / (double) desired;
        if (ratio >= 1.0) {
            matches.add("희망 급여 충족: 15/15점");
            return SALARY_POINTS;
        }
        if (ratio >= 0.9) return salaryPartial(12, offered, desired, mismatches);
        if (ratio >= 0.8) return salaryPartial(9, offered, desired, mismatches);
        if (ratio >= 0.7) return salaryPartial(6, offered, desired, mismatches);
        return salaryPartial(2, offered, desired, mismatches);
    }

    private int salaryPartial(int score, int offered, int desired, List<String> mismatches) {
        mismatches.add("희망 급여 " + desired + "만원 대비 " + offered + "만원: " + score + "/15점");
        return score;
    }

    private int workStyleScore(JobSeekerProfileDTO profile, JobPostingDTO job,
                               List<String> matches, List<String> mismatches) {
        List<Boolean> results = new ArrayList<>();
        addCondition(profile.getRemotePreferred(), job.getRemoteAvailable(), "재택근무", results, matches, mismatches);
        addCondition(profile.getFlexiblePreferred(), job.getFlexibleWorkAvailable(), "유연근무", results, matches, mismatches);
        addCondition(profile.getHybridPreferred(), Boolean.TRUE.equals(job.getRemoteAvailable())
                || Boolean.TRUE.equals(job.getFlexibleWorkAvailable()), "하이브리드 근무", results, matches, mismatches);
        addCondition(profile.getOnsitePreferred(), !Boolean.TRUE.equals(job.getRemoteAvailable()), "출근 근무", results, matches, mismatches);
        if (results.isEmpty()) {
            matches.add("근무방식 미선택: 기본 5/10점");
            return 5;
        }
        int score = proportionalScore(WORK_STYLE_POINTS, results);
        return score;
    }

    private int accessibilityScore(JobSeekerProfileDTO profile, JobPostingDTO job,
                                   List<String> matches, List<String> mismatches) {
        List<Boolean> results = new ArrayList<>();
        addCondition(profile.getWheelchairRequired(), job.getWheelchairAccessible(), "휠체어 접근", results, matches, mismatches);
        addCondition(profile.getAccessibleRestroomRequired(), job.getAccessibleRestroom(), "장애인 화장실", results, matches, mismatches);
        addCondition(profile.getDisabledParkingRequired(), job.getDisabledParking(), "장애인 주차", results, matches, mismatches);
        addCondition(profile.getAssistiveDeviceRequired(), job.getAssistiveDeviceSupport(), "보조공학기기", results, matches, mismatches);
        if (results.isEmpty()) {
            matches.add("필수 접근성 조건 미선택: 기본 5/10점");
            return 5;
        }
        return proportionalScore(ACCESSIBILITY_POINTS, results);
    }

    private void addCondition(Boolean selected, Boolean supported, String label,
                              List<Boolean> results, List<String> matches, List<String> mismatches) {
        if (!Boolean.TRUE.equals(selected)) return;
        boolean matched = Boolean.TRUE.equals(supported);
        results.add(matched);
        if (matched) matches.add(label + " 조건 충족");
        else mismatches.add(label + " 조건 미지원");
    }

    private int proportionalScore(int maximum, List<Boolean> results) {
        int matched = 0;
        for (Boolean result : results) if (Boolean.TRUE.equals(result)) matched++;
        return (int) Math.round(maximum * matched / (double) results.size());
    }

    private int limitScore(int score, int maximum) {
        return Math.max(0, Math.min(score, maximum));
    }

    private RegionResult compareSeoulDistrictDistance(String desired, String actual) {
        if (!isSeoul(desired) || !isSeoul(actual)) return null;

        Coordinate desiredCoordinate = findSeoulDistrictCoordinate(desired);
        Coordinate actualCoordinate = findSeoulDistrictCoordinate(actual);
        if (desiredCoordinate == null || actualCoordinate == null) return null;

        double distanceKm = distanceKm(desiredCoordinate, actualCoordinate);
        int score = limitScore((int) Math.round(20 - distanceKm * 0.3), REGION_POINTS);
        score = Math.max(8, score);
        String reason = String.format(Locale.ROOT,
                "희망 지역과 약 %.1fkm 거리: %d/20점", distanceKm, score);
        return new RegionResult(score, reason);
    }

    private boolean isSeoul(String region) {
        String normalized = normalize(region);
        return normalized.contains("서울") || SEOUL_DISTRICT_COORDINATES.keySet().stream()
                .anyMatch(normalized::contains);
    }

    private Coordinate findSeoulDistrictCoordinate(String region) {
        String normalized = normalize(region);
        for (Map.Entry<String, Coordinate> entry : SEOUL_DISTRICT_COORDINATES.entrySet()) {
            if (normalized.contains(normalize(entry.getKey()))) return entry.getValue();
        }
        return null;
    }

    private double distanceKm(Coordinate first, Coordinate second) {
        double earthRadiusKm = 6371.0;
        double latitudeDistance = Math.toRadians(second.latitude - first.latitude);
        double longitudeDistance = Math.toRadians(second.longitude - first.longitude);
        double firstLatitude = Math.toRadians(first.latitude);
        double secondLatitude = Math.toRadians(second.latitude);
        double haversine = Math.sin(latitudeDistance / 2) * Math.sin(latitudeDistance / 2)
                + Math.cos(firstLatitude) * Math.cos(secondLatitude)
                * Math.sin(longitudeDistance / 2) * Math.sin(longitudeDistance / 2);
        return earthRadiusKm * 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
    }

    private static Map<String, Coordinate> createSeoulDistrictCoordinates() {
        Map<String, Coordinate> coordinates = new HashMap<>();
        coordinates.put("종로구", new Coordinate(37.5735, 126.9790));
        coordinates.put("중구", new Coordinate(37.5641, 126.9979));
        coordinates.put("용산구", new Coordinate(37.5326, 126.9900));
        coordinates.put("성동구", new Coordinate(37.5633, 127.0369));
        coordinates.put("광진구", new Coordinate(37.5385, 127.0823));
        coordinates.put("동대문구", new Coordinate(37.5744, 127.0396));
        coordinates.put("중랑구", new Coordinate(37.6063, 127.0927));
        coordinates.put("성북구", new Coordinate(37.5894, 127.0167));
        coordinates.put("강북구", new Coordinate(37.6398, 127.0255));
        coordinates.put("도봉구", new Coordinate(37.6688, 127.0471));
        coordinates.put("노원구", new Coordinate(37.6542, 127.0568));
        coordinates.put("은평구", new Coordinate(37.6027, 126.9291));
        coordinates.put("서대문구", new Coordinate(37.5791, 126.9368));
        coordinates.put("마포구", new Coordinate(37.5663, 126.9019));
        coordinates.put("양천구", new Coordinate(37.5170, 126.8666));
        coordinates.put("강서구", new Coordinate(37.5509, 126.8496));
        coordinates.put("구로구", new Coordinate(37.4955, 126.8874));
        coordinates.put("금천구", new Coordinate(37.4569, 126.8955));
        coordinates.put("영등포구", new Coordinate(37.5264, 126.8962));
        coordinates.put("동작구", new Coordinate(37.5124, 126.9393));
        coordinates.put("관악구", new Coordinate(37.4784, 126.9516));
        coordinates.put("서초구", new Coordinate(37.4837, 127.0324));
        coordinates.put("강남구", new Coordinate(37.5172, 127.0473));
        coordinates.put("송파구", new Coordinate(37.5145, 127.1059));
        coordinates.put("강동구", new Coordinate(37.5301, 127.1238));
        return coordinates;
    }

    private boolean isRegularEmployment(String code) {
        return "FULL_TIME".equals(code) || "CONTRACT".equals(code);
    }

    private String employmentCode(String value) {
        String code = normalizeCode(value);
        if ("INTERNSHIP".equals(code)) return "INTERN";
        if ("정규직".equals(value)) return "FULL_TIME";
        if ("계약직".equals(value)) return "CONTRACT";
        if ("인턴".equals(value)) return "INTERN";
        if ("아르바이트".equals(value) || "파트타임".equals(value)) return "PART_TIME";
        return code;
    }

    private List<String> meaningfulTokens(String value) {
        List<String> result = new ArrayList<>();
        for (String token : value.split("[\\s,/|]+")) {
            String normalized = normalize(token);
            if (normalized.length() >= 2) result.add(normalized);
        }
        if (result.isEmpty()) result.add(normalize(value));
        return result;
    }

    private String normalizeRegion(String value) {
        return normalize(value)
                .replace("특별자치도", "").replace("특별자치시", "")
                .replace("특별시", "").replace("광역시", "")
                .replace("경기도", "경기").replace("강원도", "강원")
                .replace("충청북도", "충북").replace("충청남도", "충남")
                .replace("전라북도", "전북").replace("전라남도", "전남")
                .replace("경상북도", "경북").replace("경상남도", "경남")
                .replace("제주도", "제주");
    }

    private Set<String> regionTokens(String value) {
        String cleaned = value.replace("특별자치도", " ").replace("특별자치시", " ")
                .replace("특별시", " ").replace("광역시", " ").replace("도", " ")
                .replace("시", " ").replace("군", " ").replace("구", " ")
                .replace("읍", " ").replace("면", " ").replace("동", " ");
        Set<String> tokens = new HashSet<>();
        for (String token : cleaned.split("\\s+")) {
            String normalized = normalizeRegion(token);
            if (!normalized.isBlank()) tokens.add(normalized);
        }
        String province = province(value);
        if (!province.isBlank()) tokens.add(province);
        return tokens;
    }

    private String province(String value) {
        String normalized = normalizeRegion(value);
        for (String region : Arrays.asList("서울", "경기", "인천", "강원", "충북", "충남", "대전", "세종",
                "전북", "전남", "광주", "경북", "대구", "경남", "울산", "부산", "제주")) {
            if (normalized.contains(region)) return region;
        }
        return "";
    }

    private boolean isNearby(String first, String second) {
        if (first.isBlank() || second.isBlank()) return false;
        return NEARBY_REGIONS.getOrDefault(first, Set.of()).contains(second);
    }

    private static Map<String, Set<String>> createNearbyRegions() {
        Map<String, Set<String>> map = new HashMap<>();
        connect(map, "서울", "경기", "인천");
        connect(map, "경기", "인천", "강원", "충북", "충남");
        connect(map, "강원", "충북", "경북");
        connect(map, "충북", "충남", "대전", "세종", "경북");
        connect(map, "충남", "대전", "세종", "전북");
        connect(map, "대전", "세종", "전북");
        connect(map, "전북", "전남", "광주", "경북", "경남");
        connect(map, "전남", "광주", "경남");
        connect(map, "경북", "대구", "울산", "경남");
        connect(map, "대구", "경남");
        connect(map, "경남", "울산", "부산");
        connect(map, "울산", "부산");
        return map;
    }

    private static void connect(Map<String, Set<String>> map, String first, String... others) {
        for (String other : others) {
            map.computeIfAbsent(first, key -> new HashSet<>()).add(other);
            map.computeIfAbsent(other, key -> new HashSet<>()).add(first);
        }
    }

    private String normalizeCode(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalize(String value) {
        return value == null ? "" : value.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    }

    private boolean unrestricted(String value) {
        return value == null || value.isBlank() || "ANY".equalsIgnoreCase(value.trim()) || "무관".equals(value.trim());
    }

    private boolean containsAny(String value, String... candidates) {
        for (String candidate : candidates) if (value.contains(candidate)) return true;
        return false;
    }

    private Integer firstNumber(String value) {
        if (value == null) return null;
        Matcher matcher = NUMBER_PATTERN.matcher(value);
        return matcher.find() ? Integer.valueOf(matcher.group(1)) : null;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private static class RegionResult {
        private final int score;
        private final String reason;

        private RegionResult(int score, String reason) {
            this.score = score;
            this.reason = reason;
        }
    }

    private static class Coordinate {
        private final double latitude;
        private final double longitude;

        private Coordinate(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
