package org.ykk.jobbridge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.recommendation.JobRecommendationCalculator;
import org.ykk.jobbridge.recommendation.IAiJobMatchService.JobMatchAssessment;
import org.ykk.jobbridge.recommendation.KakaoMapDistanceService;
import org.ykk.jobbridge.service.IAiJobRecommendationService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("h2")
class AiJobRecommendationTests {

    // 서비스 함수가 throws Exception 이므로 테스트 함수도 throws Exception 선언

    @Autowired
    private JobRecommendationCalculator calculator;

    @Autowired
    private IAiJobRecommendationService recommendationService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void calculatorAppliesEmploymentMappingAndOnlySelectedAccessibilityRequirements() {
        JobSeekerProfileDTO profile = new JobSeekerProfileDTO();
        profile.setDesiredJob("백엔드 개발자");
        profile.setDesiredRegion("서울");
        profile.setEmploymentType("INTERNSHIP");
        profile.setCareerType("ENTRY");
        profile.setMinSalary(3000);
        profile.setWorkType("REMOTE");
        profile.setWheelchairRequired(true);
        profile.setAccessibleRestroomRequired(false);

        JobPostingDTO job = job("Java 백엔드 개발자", "INTERN", "서울 강남구", "신입", 3200);
        job.setWorkType("REMOTE");
        job.setWheelchairAccessible(true);
        job.setAccessibleRestroom(false);

        AiJobRecommendationDTO result = calculator.calculate(1L, profile, job);

        assertThat(result.getTotalScore()).isEqualTo(100);
        assertThat(result.getEmploymentTypeScore()).isEqualTo(10);
        assertThat(result.getJobScore()).isEqualTo(30);
        assertThat(result.getAccessibilityScore()).isEqualTo(15);
        assertThat(result.getJobMatchSource()).isEqualTo("RULE_FALLBACK");
        assertThat(result.getMismatchReason()).isEqualTo("없음");
    }

    @Test
    void regionScoreChangesByAdministrativeDistance() {
        JobSeekerProfileDTO profile = new JobSeekerProfileDTO();
        profile.setDesiredJob("개발자");
        profile.setDesiredRegion("서울특별시 송파구");
        profile.setEmploymentType("ANY");
        profile.setCareerType("ANY");

        AiJobRecommendationDTO sameDistrict = calculator.calculate(
                1L, profile, job("개발자", "FULL_TIME", "서울특별시 송파구", "ANY", 3000));
        AiJobRecommendationDTO sameCity = calculator.calculate(
                1L, profile, job("개발자", "FULL_TIME", "서울특별시 강남구", "ANY", 3000));
        AiJobRecommendationDTO nearby = calculator.calculate(
                1L, profile, job("개발자", "FULL_TIME", "경기도 성남시", "ANY", 3000));
        AiJobRecommendationDTO farAway = calculator.calculate(
                1L, profile, job("개발자", "FULL_TIME", "부산광역시 해운대구", "ANY", 3000));

        assertThat(sameDistrict.getRegionScore()).isEqualTo(15);
        assertThat(sameCity.getRegionScore()).isBetween(11, 14);
        assertThat(nearby.getRegionScore()).isEqualTo(6);
        assertThat(farAway.getRegionScore()).isEqualTo(2);
    }

    @Test
    void seoulDistrictDistanceProducesGradualScoreAndScoresStayWithinMaximums() {
        JobSeekerProfileDTO profile = new JobSeekerProfileDTO();
        profile.setDesiredJob("ANY");
        profile.setDesiredRegion("\uC11C\uC6B8\uD2B9\uBCC4\uC2DC \uC740\uD3C9\uAD6C");
        profile.setEmploymentType("ANY");
        profile.setCareerType("ANY");

        AiJobRecommendationDTO result = calculator.calculate(
                1L, profile, job("Developer", "FULL_TIME",
                        "\uC11C\uC6B8\uD2B9\uBCC4\uC2DC \uC1A1\uD30C\uAD6C", "ANY", 3000));

        assertThat(result.getRegionScore()).isBetween(6, 14);
        assertThat(result.getJobScore()).isBetween(0, 30);
        assertThat(result.getRegionScore()).isBetween(0, 15);
        assertThat(result.getEmploymentTypeScore()).isBetween(0, 10);
        assertThat(result.getCareerScore()).isBetween(0, 10);
        assertThat(result.getSalaryScore()).isBetween(0, 10);
        assertThat(result.getWorkStyleScore()).isBetween(0, 10);
        assertThat(result.getAccessibilityScore()).isBetween(0, 15);
        assertThat(result.getTotalScore()).isBetween(0, 100);
        assertThat(result.getRecommendationReason() + result.getMismatchReason()).contains("km");
    }

    @Test
    void detailedProfileProducesDifferentScoresForDifferentJobs() {
        JobSeekerProfileDTO profile = new JobSeekerProfileDTO();
        profile.setDesiredJob("백엔드 개발자");
        profile.setDesiredRegion("서울 강남구");
        profile.setEmploymentType("FULL_TIME");
        profile.setCareerType("EXPERIENCED");
        profile.setCareerYears(3);
        profile.setMinSalary(4000);
        profile.setWorkType("REMOTE");

        JobPostingDTO matching = job("Java 백엔드 개발자", "FULL_TIME", "서울 강남구", "경력 3년", 4500);
        matching.setWorkType("REMOTE");
        JobPostingDTO different = job("매장 서비스 직원", "PART_TIME", "부산 해운대구", "경력 7년", 2500);

        AiJobRecommendationDTO matchingResult = calculator.calculate(1L, profile, matching);
        AiJobRecommendationDTO differentResult = calculator.calculate(1L, profile, different);

        assertThat(matchingResult.getTotalScore()).isGreaterThan(differentResult.getTotalScore());
        assertThat(matchingResult.getTotalScore() - differentResult.getTotalScore()).isGreaterThanOrEqualTo(40);
    }

    @Test
    void calculatorUsesGenerativeAiJobAssessmentWhenAvailable() {
        KakaoMapDistanceService disabledMap = new KakaoMapDistanceService(false, "", new ObjectMapper());
        JobRecommendationCalculator aiCalculator = new JobRecommendationCalculator(
                disabledMap,
                (profile, job) -> Optional.of(new JobMatchAssessment(
                        27, "Spring Boot 백엔드 업무와 희망 직무가 일치합니다.", "GENERATIVE_AI")));

        JobSeekerProfileDTO profile = new JobSeekerProfileDTO();
        profile.setDesiredJob("Java 서버 개발자");
        profile.setDesiredRegion("서울");
        profile.setEmploymentType("ANY");
        profile.setCareerType("ANY");

        AiJobRecommendationDTO result = aiCalculator.calculate(
                1L, profile, job("Spring Boot 백엔드 엔지니어", "FULL_TIME", "서울", "ANY", 3000));

        assertThat(result.getJobScore()).isEqualTo(27);
        assertThat(result.getJobMatchSource()).isEqualTo("GENERATIVE_AI");
        assertThat(result.getJobMatchReason()).contains("Spring Boot");
        assertThat(result.getRecommendationReason()).doesNotContain("AI 직무 분석", "/30점");
        assertThat(result.getRecommendationReason()).contains("부족한 조건은 없습니다");
    }

    @Test
    void recommendationReasonExplainsMissingConditionsWithoutScoreFormula() {
        JobSeekerProfileDTO profile = new JobSeekerProfileDTO();
        profile.setDesiredJob("백엔드 개발자");
        profile.setDesiredRegion("서울 강남구");
        profile.setEmploymentType("FULL_TIME");
        profile.setCareerType("EXPERIENCED");
        profile.setCareerYears(1);
        profile.setMinSalary(5000);
        profile.setWorkType("REMOTE");

        JobPostingDTO job = job("매장 서비스 직원", "PART_TIME", "부산 해운대구", "경력 5년", 2800);
        AiJobRecommendationDTO result = calculator.calculate(1L, profile, job);

        assertThat(result.getRecommendationReason()).contains("아쉬운 부분은");
        assertThat(result.getRecommendationReason()).doesNotContain("/30점", "/15점", "/10점");
    }

    @Test
    @Transactional
    void serviceUsesOnlyOpenUnexpiredJobsAndUpdatesDuplicateRecommendation() throws Exception {
        jdbcTemplate.update("""
                INSERT INTO job_seeker_profile (
                    member_id, desired_job, desired_region, employment_type, career_type,
                    career_years, min_salary, work_type,
                    wheelchair_required, accessible_restroom_required,
                    disabled_parking_required, assistive_device_required
                ) VALUES (1, '개발자', '서울', 'ANY', 'ANY', 0, 0, 'ANY',
                          FALSE, FALSE, FALSE, FALSE)
                """);
        jdbcTemplate.update("""
                INSERT INTO member (login_id, password, name, email, phone, role, status)
                VALUES ('recommend.company', 'unused', '추천 기업 담당자',
                        'recommend.company@example.com', '010-9999-9999', 'COMPANY', 'ACTIVE')
                """);
        Long companyMemberId = jdbcTemplate.queryForObject(
                "SELECT id FROM member WHERE login_id = 'recommend.company'", Long.class);
        jdbcTemplate.update("""
                INSERT INTO company_profile (
                    member_id, company_name, business_number, representative_name, company_address
                ) VALUES (?, 'Open Company', '999-88-77776', '대표자', '서울')
                """, companyMemberId);
        jdbcTemplate.update("""
                INSERT INTO job_posting (
                    company_member_id, company_name, title, job_category, employment_type, location,
                    experience_level, deadline, status
                ) VALUES (?, '추천 공고 회사', '백엔드 개발자', '개발자', 'FULL_TIME', '서울',
                          'ANY', ?, 'OPEN')
                """, companyMemberId, LocalDate.now().plusDays(7));
        Long openJobId = jdbcTemplate.queryForObject(
                "SELECT id FROM job_posting WHERE company_member_id = ? AND status = 'OPEN'",
                Long.class, companyMemberId);
        jdbcTemplate.update("""
                INSERT INTO job_posting (
                    company_member_id, company_name, title, job_category, employment_type, location,
                    experience_level, deadline, status
                ) VALUES (?, '추천 공고 회사', '백엔드 개발자', '개발자', 'FULL_TIME', '서울',
                          'ANY', ?, 'CLOSED')
                """, companyMemberId, LocalDate.now().plusDays(7));
        jdbcTemplate.update("""
                INSERT INTO job_posting (
                    company_member_id, company_name, title, job_category, employment_type, location,
                    experience_level, deadline, status
                ) VALUES (?, '추천 공고 회사', '백엔드 개발자', '개발자', 'FULL_TIME', '서울',
                          'ANY', ?, 'OPEN')
                """, companyMemberId, LocalDate.now().minusDays(1));

        AiJobRecommendationDTO pDTO = new AiJobRecommendationDTO();
        pDTO.setMemberId(1L);

        List<AiJobRecommendationDTO> first = recommendationService.getRecommendationList(pDTO);
        List<AiJobRecommendationDTO> second = recommendationService.getRecommendationList(pDTO);

        assertThat(first).hasSize(1);
        assertThat(first.get(0).getJob().getCompanyName()).isEqualTo("추천 공고 회사");
        assertThat(second).hasSize(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ai_job_posting_recommendation WHERE member_id = 1 AND job_id = ?",
                Integer.class, openJobId)).isEqualTo(1);
    }

    private JobPostingDTO job(String title, String employmentType, String location,
                              String experienceLevel, Integer salaryMin) {
        JobPostingDTO job = new JobPostingDTO();
        job.setId(1L);
        job.setTitle(title);
        job.setJobCategory("IT");
        job.setEmploymentType(employmentType);
        job.setLocation(location);
        job.setExperienceLevel(experienceLevel);
        job.setSalaryMin(salaryMin);
        return job;
    }
}
