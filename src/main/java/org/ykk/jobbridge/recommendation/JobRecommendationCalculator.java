package org.ykk.jobbridge.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.recommendation.IAiJobMatchService.JobMatchAssessment;
import org.ykk.jobbridge.recommendation.KakaoMapDistanceService.DrivingRoute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.ykk.jobbridge.util.NumberUtils.clamp;
import static org.ykk.jobbridge.util.NumberUtils.zeroIfNull;
import static org.ykk.jobbridge.util.TextUtils.compact;
import static org.ykk.jobbridge.util.TextUtils.firstNumber;
import static org.ykk.jobbridge.util.TextUtils.isBlank;
import static org.ykk.jobbridge.util.TextUtils.isUnrestricted;
import static org.ykk.jobbridge.util.TextUtils.nullToEmpty;
import static org.ykk.jobbridge.util.TextUtils.toCode;















@Slf4j
@Component
public class JobRecommendationCalculator {

    static final int JOB_FIT_POINTS = 30;
    static final int REGION_POINTS = 15;
    static final int EMPLOYMENT_TYPE_POINTS = 10;
    static final int CAREER_POINTS = 10;
    static final int SALARY_POINTS = 10;
    static final int WORK_STYLE_POINTS = 10;
    static final int ACCESSIBILITY_POINTS = 15;


    private static final int AI_MATCH_THRESHOLD = 18;


    private static final Map<String, String> EMPLOYMENT_TYPE_ALIASES = Map.ofEntries(
            Map.entry("INTERNSHIP", "INTERN"),
            Map.entry("FULL_TIME_CONVERSION", "CONVERSION_TYPE"),
            Map.entry("FREELANCER", "FREELANCE"),
            Map.entry("무관", "ANY"),
            Map.entry("정규직", "FULL_TIME"),
            Map.entry("계약직", "CONTRACT"),
            Map.entry("무기계약직", "PERMANENT_CONTRACT"),
            Map.entry("정규직 전환형", "CONVERSION_TYPE"),
            Map.entry("시간제·파트타임", "PART_TIME"),
            Map.entry("시간제", "PART_TIME"),
            Map.entry("알바", "PART_TIME"),
            Map.entry("아르바이트", "PART_TIME"),
            Map.entry("파트타임", "PART_TIME"),
            Map.entry("인턴", "INTERN"),
            Map.entry("인턴십", "INTERN"),
            Map.entry("파견직", "DISPATCH"),
            Map.entry("프리랜서", "FREELANCE"));


    private static final Map<Set<String>, Integer> EMPLOYMENT_TYPE_SCORES = Map.ofEntries(
            Map.entry(Set.of("FULL_TIME", "CONVERSION_TYPE"), 8),
            Map.entry(Set.of("FULL_TIME", "PERMANENT_CONTRACT"), 8),
            Map.entry(Set.of("CONTRACT", "PERMANENT_CONTRACT"), 8),
            Map.entry(Set.of("FULL_TIME", "CONTRACT"), 6),
            Map.entry(Set.of("CONTRACT", "CONVERSION_TYPE"), 6),
            Map.entry(Set.of("PERMANENT_CONTRACT", "CONVERSION_TYPE"), 6),
            Map.entry(Set.of("CONVERSION_TYPE", "INTERN"), 6),
            Map.entry(Set.of("PART_TIME", "INTERN"), 6),
            Map.entry(Set.of("FULL_TIME", "PART_TIME"), 3),
            Map.entry(Set.of("FULL_TIME", "INTERN"), 3),
            Map.entry(Set.of("FULL_TIME", "DISPATCH"), 3),
            Map.entry(Set.of("CONTRACT", "PART_TIME"), 3),
            Map.entry(Set.of("CONTRACT", "INTERN"), 3),
            Map.entry(Set.of("CONTRACT", "DISPATCH"), 3),
            Map.entry(Set.of("PERMANENT_CONTRACT", "PART_TIME"), 3),
            Map.entry(Set.of("PERMANENT_CONTRACT", "INTERN"), 3),
            Map.entry(Set.of("PERMANENT_CONTRACT", "DISPATCH"), 3),
            Map.entry(Set.of("CONVERSION_TYPE", "PART_TIME"), 3),
            Map.entry(Set.of("CONVERSION_TYPE", "DISPATCH"), 3),
            Map.entry(Set.of("PART_TIME", "DISPATCH"), 3),
            Map.entry(Set.of("PART_TIME", "FREELANCE"), 3),
            Map.entry(Set.of("INTERN", "DISPATCH"), 3),
            Map.entry(Set.of("DISPATCH", "FREELANCE"), 3));

    private final KakaoMapDistanceService mapDistanceService;
    private final IAiJobMatchService aiJobMatchService;


    public JobRecommendationCalculator(KakaoMapDistanceService mapDistanceService,
                                       IAiJobMatchService aiJobMatchService) {
        this.mapDistanceService = mapDistanceService;
        this.aiJobMatchService = aiJobMatchService;
    }

    public AiJobRecommendationDTO calculate(Long memberId,
                                             JobSeekerProfileDTO profile,
                                             JobPostingDTO job) {

        log.info(this.getClass().getName() + ".calculate Start! (jobId : " + job.getId() + ")");

        MatchNotes notes = new MatchNotes();
        JobMatchAssessment jobFit = jobFitScore(profile, job, notes);

        AiJobRecommendationDTO result = new AiJobRecommendationDTO();
        result.setMemberId(memberId);
        result.setJobId(job.getId());
        result.setJob(job);

        result.setJobScore(jobFit.score());
        result.setJobMatchSource(jobFit.source());
        result.setJobMatchReason(jobFit.reason());
        result.setRegionScore(regionScore(profile, job, notes));
        result.setEmploymentTypeScore(employmentTypeScore(profile, job, notes));
        result.setCareerScore(careerScore(profile, job, notes));
        result.setSalaryScore(salaryScore(profile, job, notes));
        result.setWorkStyleScore(workStyleScore(profile, job, notes));
        result.setAccessibilityScore(accessibilityScore(profile, job, notes));

        result.setTotalScore(result.getJobScore() + result.getRegionScore()
                + result.getEmploymentTypeScore() + result.getCareerScore() + result.getSalaryScore()
                + result.getWorkStyleScore() + result.getAccessibilityScore());
        result.setRecommendationReason(notes.recommendationReason());
        result.setMismatchReason(notes.mismatchReason());

        log.info("jobId : " + job.getId() + " / totalScore : " + result.getTotalScore());

        return result;
    }




    private JobMatchAssessment jobFitScore(JobSeekerProfileDTO profile, JobPostingDTO job, MatchNotes notes) {
        Optional<JobMatchAssessment> ai = aiJobMatchService.assess(profile, job);
        if (ai.isPresent()) {
            JobMatchAssessment assessment = ai.get();
            int score = clamp(assessment.score(), 0, JOB_FIT_POINTS);
            notes.note(score >= AI_MATCH_THRESHOLD,
                    "직무 적합성: " + assessment.reason(),
                    "AI 직무 분석: " + assessment.reason() + " (" + score + "/" + JOB_FIT_POINTS + "점)",
                    score);
            return new JobMatchAssessment(score, assessment.reason(), assessment.source());
        }

        int score = keywordJobFitScore(profile, job, notes);
        return new JobMatchAssessment(score, "키워드 규칙으로 직무 적합도를 계산했습니다.", "RULE_FALLBACK");
    }

    private static int keywordJobFitScore(JobSeekerProfileDTO profile, JobPostingDTO job, MatchNotes notes) {
        String desiredJob = profile.getDesiredJob();
        if (isUnrestricted(desiredJob)) return 0;

        String jobText = compact(String.join(" ",
                nullToEmpty(job.getTitle()), nullToEmpty(job.getJobCategory()),
                nullToEmpty(job.getRequirements()), nullToEmpty(job.getDescription())));
        String desired = compact(desiredJob);
        String category = compact(job.getJobCategory());
        if (jobText.contains(desired) || (!category.isBlank() && desired.contains(category))) {
            return notes.match("희망 직무와 매우 유사", JOB_FIT_POINTS, JOB_FIT_POINTS);
        }

        List<String> keywords = keywords(desiredJob);
        long matched = keywords.stream().filter(jobText::contains).count();
        if (matched == 0) return notes.mismatch("희망 직무와 관련 키워드가 적음", 2, JOB_FIT_POINTS);

        int score = Math.min(28, 6 + (int) Math.round(22.0 * matched / keywords.size()));
        return notes.note(true, "희망 직무 관련 키워드가 일부 일치",
                "희망 직무 키워드 " + matched + "/" + keywords.size() + "개 일치: " + score + "/" + JOB_FIT_POINTS + "점",
                score);
    }


    private static List<String> keywords(String desiredJob) {
        List<String> keywords = new ArrayList<>();
        for (String token : desiredJob.split("[\\s,/|]+")) {
            String word = compact(token);
            if (word.length() >= 2) keywords.add(word);
        }
        if (keywords.isEmpty()) keywords.add(compact(desiredJob));
        return keywords;
    }




    private int regionScore(JobSeekerProfileDTO profile, JobPostingDTO job, MatchNotes notes) {

        String origin = isUnrestricted(profile.getResidenceRegion())
                ? profile.getDesiredRegion() : profile.getResidenceRegion();
        if (isUnrestricted(origin)) return 0;

        String destination = job.getLocation();
        if (isBlank(destination)) return notes.mismatch("공고의 근무지역 정보 없음", 4, REGION_POINTS);

        Optional<DrivingRoute> route = mapDistanceService.findDrivingRoute(origin, destination);
        if (route.isPresent()) return drivingScore(route.get(), notes);


        KoreaRegions.Match best = Arrays.stream(origin.split("[,/|]"))
                .map(desired -> KoreaRegions.compare(desired.trim(), destination.trim()))
                .max(Comparator.comparingInt(KoreaRegions.Match::score))
                .orElseThrow();
        return notes.note(best.score() >= 11, best.label(), best.score(), REGION_POINTS);
    }

    private static int drivingScore(DrivingRoute route, MatchNotes notes) {
        int minutes = route.minutes();
        int score = clamp(17 - (minutes + 9) / 10, 1, REGION_POINTS);

        String label = String.format(Locale.ROOT,
                "현재 거주지에서 자동차 약 %d분(%.1fkm)", minutes, route.kilometers());
        return notes.note(score >= 9, label, score, REGION_POINTS);
    }



    private static int employmentTypeScore(JobSeekerProfileDTO profile, JobPostingDTO job, MatchNotes notes) {
        String preferred = employmentTypeCode(profile.getEmploymentType());
        String offered = employmentTypeCode(job.getEmploymentType());
        if (isUnrestricted(preferred)) {
            return notes.match("희망 고용형태 무관", 7, EMPLOYMENT_TYPE_POINTS);
        }
        if (isUnrestricted(offered)) {
            return notes.match("공고 고용형태 무관", 7, EMPLOYMENT_TYPE_POINTS);
        }

        if (preferred.equals(offered)) {
            return notes.match("희망 고용형태 일치", EMPLOYMENT_TYPE_POINTS, EMPLOYMENT_TYPE_POINTS);
        }

        int score = EMPLOYMENT_TYPE_SCORES.getOrDefault(Set.of(preferred, offered), 1);
        String label = Map.of(8, "희망 고용형태와 매우 유사", 6, "희망 고용형태와 어느 정도 유사",
                3, "희망 고용형태와 차이가 큼", 1, "희망 고용형태와 거의 관련 없음").get(score);
        return notes.note(score >= 6, label, score, EMPLOYMENT_TYPE_POINTS);
    }

    private static String employmentTypeCode(String value) {
        String code = toCode(value);
        return EMPLOYMENT_TYPE_ALIASES.getOrDefault(code, code);
    }



    private static int careerScore(JobSeekerProfileDTO profile, JobPostingDTO job, MatchNotes notes) {
        String preferred = toCode(profile.getCareerType());
        String required = toCode(job.getExperienceLevel());
        if (isUnrestricted(required)) return notes.match("공고가 경력 무관", CAREER_POINTS, CAREER_POINTS);

        if ("ENTRY".equals(preferred)) {
            boolean acceptsEntry = Stream.of("ENTRY", "NEWCOMER", "신입").anyMatch(required::contains);
            return acceptsEntry
                    ? notes.match("신입 지원 조건 일치", CAREER_POINTS, CAREER_POINTS)
                    : notes.mismatch("경력직 공고", 2, CAREER_POINTS);
        }

        Integer requiredYears = firstNumber(job.getExperienceLevel());
        if (requiredYears == null) return notes.match("경력 구분 일치, 요구 연수 미지정", 8, CAREER_POINTS);

        int shortage = requiredYears - zeroIfNull(profile.getCareerYears());
        if (shortage <= 0) return notes.match("경력 구분 일치, 요구 연수 충족", CAREER_POINTS, CAREER_POINTS);
        if (shortage == 1) return notes.mismatch("요구 경력보다 1년 부족", 7, CAREER_POINTS);
        if (shortage <= 3) return notes.mismatch("요구 경력보다 " + shortage + "년 부족", 4, CAREER_POINTS);
        return notes.mismatch("요구 경력 차이가 큼", 1, CAREER_POINTS);
    }




    private static int salaryScore(JobSeekerProfileDTO profile, JobPostingDTO job, MatchNotes notes) {
        Integer desired = profile.getMinSalary();
        if (desired == null || desired <= 0) return 6;

        Integer offered = job.getSalaryMin();
        if (offered == null || offered <= 0) return notes.mismatch("공고의 급여 정보 없음 또는 협의", 5, SALARY_POINTS);

        double ratio = offered / (double) desired;
        if (ratio >= 1.0) return notes.match("희망 급여 충족", SALARY_POINTS, SALARY_POINTS);

        int score;
        if (ratio >= 0.95) score = 9;
        else if (ratio >= 0.9) score = 8;
        else if (ratio >= 0.8) score = 6;
        else if (ratio >= 0.7) score = 4;
        else score = 2;
        return notes.mismatch("희망 급여 " + desired + "만원 대비 " + offered + "만원", score, SALARY_POINTS);
    }




    private static int workStyleScore(JobSeekerProfileDTO profile, JobPostingDTO job, MatchNotes notes) {
        String preferred = toCode(profile.getWorkType());
        String offered = toCode(job.getWorkType());
        if (isUnrestricted(preferred) || isUnrestricted(offered)) return 5;

        boolean matched = preferred.equals(offered)
                || "HYBRID".equals(offered) && Set.of("OFFICE", "REMOTE").contains(preferred);
        return matched ? notes.match("희망 근무방식 충족", WORK_STYLE_POINTS, WORK_STYLE_POINTS)
                : notes.mismatch("희망 근무방식과 다름", 0, WORK_STYLE_POINTS);
    }


    private static int accessibilityScore(JobSeekerProfileDTO profile, JobPostingDTO job, MatchNotes notes) {
        List<Check> wanted = Stream.of(
                new Check(profile.getWheelchairRequired(), job.getWheelchairAccessible(), "휠체어 접근"),
                new Check(profile.getAccessibleRestroomRequired(), job.getAccessibleRestroom(), "장애인 화장실"),
                new Check(profile.getDisabledParkingRequired(), job.getDisabledParking(), "장애인 주차"),
                new Check(profile.getAssistiveDeviceRequired(), job.getAssistiveDeviceSupport(), "보조공학기기"),
                new Check(profile.getRestAreaRequired(), job.getRestAreaAvailable(), "장애인 휴게공간"),
                new Check(profile.getElevatorRequired(), job.getElevatorAvailable(), "엘리베이터 이용"))
                .filter(Check::isWanted).toList();
        if (wanted.isEmpty()) return 6;

        long satisfied = wanted.stream().filter(Check::isSupported).count();
        wanted.forEach(check -> {
            if (check.isSupported()) notes.match(check.label() + " 조건 충족");
            else notes.mismatch(check.label() + " 조건 미지원");
        });
        int total = wanted.size();
        return satisfied == 0 ? 0 : satisfied == total ? ACCESSIBILITY_POINTS : satisfied * 5 >= total * 4 ? 8
                : satisfied * 5 >= total * 3 ? 6 : satisfied * 5 >= total * 2 ? 4 : 2;
    }


    private record Check(Boolean wanted, Boolean supported, String label) {

        boolean isWanted() {
            return Boolean.TRUE.equals(wanted);
        }

        boolean isSupported() {
            return Boolean.TRUE.equals(supported);
        }
    }

}
