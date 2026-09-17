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
import org.ykk.jobbridge.service.AiJobRecommendationService;
import org.ykk.jobbridge.service.JobPostingService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("h2")
class AiJobRecommendationTests {

    @Autowired
    private JobRecommendationCalculator calculator;

    @Autowired
    private AiJobRecommendationService recommendationService;

    @Autowired
    private JobPostingService jobPostingService;

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
        profile.setRemotePreferred(true);
        profile.setWheelchairRequired(true);
        profile.setAccessibleRestroomRequired(false);

        JobPostingDTO job = job("Java 백엔드 개발자", "INTERN", "서울 강남구", "신입", 3200);
        job.setRemoteAvailable(true);
        job.setWheelchairAccessible(true);
        job.setAccessibleRestroom(false);

        AiJobRecommendationDTO result = calculator.calculate(1L, profile, job);

        assertThat(result.getTotalScore()).isEqualTo(100);
        assertThat(result.getEmploymentTypeScore()).isEqualTo(10);
        assertThat(result.getAccessibilityScore()).isEqualTo(10);
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

        assertThat(sameDistrict.getRegionScore()).isEqualTo(20);
        assertThat(sameCity.getRegionScore()).isBetween(15, 19);
        assertThat(nearby.getRegionScore()).isEqualTo(8);
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

        assertThat(result.getRegionScore()).isBetween(8, 19);
        assertThat(result.getJobScore()).isBetween(0, 25);
        assertThat(result.getRegionScore()).isBetween(0, 20);
        assertThat(result.getEmploymentTypeScore()).isBetween(0, 10);
        assertThat(result.getCareerScore()).isBetween(0, 10);
        assertThat(result.getSalaryScore()).isBetween(0, 15);
        assertThat(result.getWorkStyleScore()).isBetween(0, 10);
        assertThat(result.getAccessibilityScore()).isBetween(0, 10);
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
        profile.setRemotePreferred(true);

        JobPostingDTO matching = job("Java 백엔드 개발자", "FULL_TIME", "서울 강남구", "경력 3년", 4500);
        matching.setRemoteAvailable(true);
        JobPostingDTO different = job("매장 서비스 직원", "PART_TIME", "부산 해운대구", "경력 7년", 2500);

        AiJobRecommendationDTO matchingResult = calculator.calculate(1L, profile, matching);
        AiJobRecommendationDTO differentResult = calculator.calculate(1L, profile, different);

        assertThat(matchingResult.getTotalScore()).isGreaterThan(differentResult.getTotalScore());
        assertThat(matchingResult.getTotalScore() - differentResult.getTotalScore()).isGreaterThanOrEqualTo(40);
    }

    @Test
    @Transactional
    void serviceUsesOnlyOpenUnexpiredJobsAndUpdatesDuplicateRecommendation() {
        // Runtime table creation can commit an H2 transaction, so prepare it before test data.
        jobPostingService.countJobs();

        jdbcTemplate.update("""
                INSERT INTO job_seeker_profile (
                    member_id, desired_job, desired_region, employment_type, career_type,
                    career_years, min_salary, remote_preferred, flexible_preferred,
                    wheelchair_required, accessible_restroom_required,
                    disabled_parking_required, assistive_device_required
                ) VALUES (1, '개발자', '서울', 'ANY', 'ANY', 0, 0, FALSE, FALSE,
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

        List<AiJobRecommendationDTO> first = recommendationService.getRecommendations(1L);
        List<AiJobRecommendationDTO> second = recommendationService.getRecommendations(1L);

        assertThat(first).hasSize(1);
        assertThat(first.get(0).getJob().getCompanyName()).isEqualTo("추천 공고 회사");
        assertThat(second).hasSize(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ai_job_posting_recommendation WHERE member_id = 1 AND job_id = ?",
                Integer.class, openJobId)).isEqualTo(1);
    }

    private JobPostingDTO job(String title, String employmentType, String location,
                              String experienceLevel, Integer salaryMax) {
        JobPostingDTO job = new JobPostingDTO();
        job.setId(1L);
        job.setTitle(title);
        job.setJobCategory("IT");
        job.setEmploymentType(employmentType);
        job.setLocation(location);
        job.setExperienceLevel(experienceLevel);
        job.setSalaryMax(salaryMax);
        return job;
    }
}
