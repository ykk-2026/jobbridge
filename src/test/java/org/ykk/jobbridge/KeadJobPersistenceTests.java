package org.ykk.jobbridge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.mapper.IJobPostingMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class KeadJobPersistenceTests {

    @Autowired
    private IJobPostingMapper jobPostingMapper;

    @Test
    void insertsAndUpdatesExternalJobWithoutCompanyMember() throws Exception {
        JobPostingDTO job = createJob();

        jobPostingMapper.upsertKeadJob(job);
        job.setSalaryAmount(90_000L);
        job.setSalaryMin(2349);
        jobPostingMapper.upsertKeadJob(job);

        List<JobPostingDTO> jobs = jobPostingMapper.getJobList();
        JobPostingDTO saved = jobs.stream()
                .filter(value -> "test-kead-job".equals(value.getExternalJobId()))
                .findFirst()
                .orElseThrow();

        assertThat(saved.getCompanyMemberId()).isNull();
        assertThat(saved.getSource()).isEqualTo("KEAD");
        assertThat(saved.getWorkType()).isNull();
        assertThat(saved.getSalaryAmount()).isEqualTo(90_000L);
        assertThat(saved.getContactNumber()).isEqualTo("1588-1519");
        assertThat(saved.getAccessibilityVerified()).isFalse();
    }

    private static JobPostingDTO createJob() {
        JobPostingDTO job = new JobPostingDTO();
        job.setCompanyName("한국전력공사 서울본부");
        job.setTitle("사무 보조원");
        job.setJobCategory("사무 보조원");
        job.setEmploymentType("CONTRACT");
        job.setLocation("서울특별시 중구");
        job.setSalaryAmount(82_560L);
        job.setSalaryType("DAILY");
        job.setSalaryMin(2155);
        job.setWorkType(null);
        job.setExperienceLevel("0년");
        job.setEducationLevel("무관");
        job.setAccessibilityInfo("편의시설 정보 미제공");
        job.setExternalJobId("test-kead-job");
        job.setContactNumber("1588-1519");
        job.setEntryType("무관");
        job.setManagingAgency("한국장애인고용공단 서울지역본부");
        job.setRecruitmentStartDate("2026-09-15");
        job.setExternalApplyDate("2026-09-15");
        job.setExternalRegisteredDate("2026-09-15");
        job.setDeadline("2026-09-29");
        return job;
    }
}
