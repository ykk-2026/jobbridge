package org.ykk.jobbridge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.dto.InterestJobDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.recommendation.JobRecommendationCalculator;
import org.ykk.jobbridge.service.AiJobRecommendationService;

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

        InterestJobDTO job = job("Java 백엔드 개발자", "INTERN", "서울 강남구", "신입", 3200);
        job.setRemoteAvailable(true);
        job.setWheelchairAccessible(true);
        job.setAccessibleRestroom(false);

        AiJobRecommendationDTO result = calculator.calculate(1L, profile, job);

        assertThat(result.getTotalScore()).isEqualTo(100);
        assertThat(result.getEmploymentTypeScore()).isEqualTo(10);
        assertThat(result.getAccessibilityScore()).isEqualTo(15);
        assertThat(result.getMismatchReason()).isEqualTo("없음");
    }

    @Test
    @Transactional
    void serviceUsesOnlyOpenUnexpiredJobsAndUpdatesDuplicateRecommendation() {
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
                INSERT INTO interest_job (
                    member_id, company_name, title, job_category, employment_type, location,
                    experience_level, deadline, status
                ) VALUES (1, 'Open Company', '백엔드 개발자', '개발자', 'FULL_TIME', '서울',
                          'ANY', ?, 'OPEN')
                """, LocalDate.now().plusDays(7));
        Long openJobId = jdbcTemplate.queryForObject(
                "SELECT id FROM interest_job WHERE company_name = 'Open Company'", Long.class);
        jdbcTemplate.update("""
                INSERT INTO interest_job (
                    member_id, company_name, title, job_category, employment_type, location,
                    experience_level, deadline, status
                ) VALUES (1, 'Closed Company', '백엔드 개발자', '개발자', 'FULL_TIME', '서울',
                          'ANY', ?, 'CLOSED')
                """, LocalDate.now().plusDays(7));
        jdbcTemplate.update("""
                INSERT INTO interest_job (
                    member_id, company_name, title, job_category, employment_type, location,
                    experience_level, deadline, status
                ) VALUES (1, 'Expired Company', '백엔드 개발자', '개발자', 'FULL_TIME', '서울',
                          'ANY', ?, 'OPEN')
                """, LocalDate.now().minusDays(1));

        List<AiJobRecommendationDTO> first = recommendationService.getRecommendations(1L);
        List<AiJobRecommendationDTO> second = recommendationService.getRecommendations(1L);

        assertThat(first).hasSize(1);
        assertThat(first.get(0).getJob().getCompanyName()).isEqualTo("Open Company");
        assertThat(second).hasSize(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ai_job_recommendation WHERE member_id = 1 AND job_id = ?",
                Integer.class, openJobId)).isEqualTo(1);
    }

    private InterestJobDTO job(String title, String employmentType, String location,
                               String experienceLevel, Integer salaryMax) {
        InterestJobDTO job = new InterestJobDTO();
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
