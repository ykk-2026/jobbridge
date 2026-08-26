package org.ykk.jobbridge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.JoinRequest;
import org.ykk.jobbridge.dto.InterestJobDTO;
import org.ykk.jobbridge.dto.JobApplicationDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.mapper.AdminMapper;
import org.ykk.jobbridge.mapper.InterestJobMapper;
import org.ykk.jobbridge.mapper.JobApplicationMapper;
import org.ykk.jobbridge.service.MemberService;
import org.ykk.jobbridge.service.JobCatalogService;
import org.ykk.jobbridge.service.ProfileService;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("h2")
class ApiDataIntegrationTests {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private JobCatalogService jobCatalogService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private InterestJobMapper interestJobMapper;

    @Autowired
    private JobApplicationMapper jobApplicationMapper;

    @Autowired
    private ProfileService profileService;

    @Test
    void adminOverviewSourcesCanReadMembersAndJobs() {
        assertThat(adminMapper.countMembers()).isEqualTo(1);
        assertThat(adminMapper.findMembers()).hasSize(1);
        assertThat(adminMapper.findMembers().get(0).getName()).isNotBlank();
        assertThat(jobCatalogService.findAll()).hasSize(4);
    }

    @Test
    @Transactional
    void memberCannotJoinWithoutRequiredEmail() {
        JoinRequest request = new JoinRequest();
        request.setLoginId("noemailuser");
        request.setPassword("Password!1");
        request.setPasswordConfirm("Password!1");
        request.setName("이메일 없는 회원");
        request.setBirthDate(LocalDate.of(2000, 1, 1));
        request.setGender("OTHER");
        request.setEmail("");
        request.setPhone("010-1234-5678");
        request.setRole("JOB_SEEKER");

        assertThatThrownBy(() -> memberService.join(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("필수 회원 정보를 모두 입력해 주세요.");
    }

    @Test
    @Transactional
    void interestJobCanBeSavedAndRemoved() {
        InterestJobDTO interestJob = new InterestJobDTO();
        interestJob.setMemberId(1L);
        interestJob.setCompanyName("Samsung SDS");
        interestJob.setTitle("Java 백엔드 개발자");
        interestJob.setJobCategory("IT");
        interestJob.setEmploymentType("FULL_TIME");
        interestJob.setLocation("서울 송파구");
        interestJob.setStatus("OPEN");

        assertThat(interestJobMapper.insert(interestJob)).isEqualTo(1);
        assertThat(interestJob.getId()).isNotNull();
        assertThat(interestJobMapper.countByCompanyAndTitle(
                interestJob.getMemberId(), interestJob.getCompanyName(), interestJob.getTitle())).isEqualTo(1);
        assertThat(interestJobMapper.findAll(interestJob.getMemberId())).hasSize(1);

        assertThat(interestJobMapper.deleteByCompanyAndTitle(
                interestJob.getMemberId(), interestJob.getCompanyName(), interestJob.getTitle())).isEqualTo(1);
        assertThat(interestJobMapper.findAll(interestJob.getMemberId())).isEmpty();
    }

    @Test
    @Transactional
    void jobApplicationIsInsertedForLoggedInMemberData() {
        jobApplicationMapper.ensureCompanyNameColumn();
        jobApplicationMapper.ensureJobTitleColumn();
        jobApplicationMapper.ensureApplicantNameColumn();
        jobApplicationMapper.ensurePhoneColumn();
        jobApplicationMapper.ensureEmailColumn();
        jobApplicationMapper.ensureEmploymentTypeColumn();
        jobApplicationMapper.ensureStatusColumn();
        jobApplicationMapper.ensureCreatedAtColumn();

        JobApplicationDTO application = new JobApplicationDTO();
        application.setMemberId(1L);
        application.setJobId("2");
        application.setCompanyName("Kakao");
        application.setJobTitle("Frontend Developer");
        application.setApplicantName("Test User");
        application.setPhone("010-1234-5678");
        application.setEmail("test@example.com");
        application.setEmploymentType("FULL_TIME");
        application.setStatus("APPLIED");

        assertThat(jobApplicationMapper.insert(application)).isEqualTo(1);
        assertThat(application.getId()).isNotNull();
        assertThat(jobApplicationMapper.countByMemberAndJob(1L, "2")).isEqualTo(1);
        assertThat(jobApplicationMapper.findAllByMemberId(1L)).hasSize(1);
    }

    @Test
    @Transactional
    void koreanProfileLabelsAreConvertedToDatabaseEnumCodes() {
        JobSeekerProfileDTO profile = profileService.getProfile(1L);
        profile.setGender("남성");
        profile.setEmploymentType("아르바이트");
        profile.setCareerType("경력");
        profile.setContactMethod("이메일");

        assertThat(profileService.saveProfile(profile)).isEqualTo(1);

        JobSeekerProfileDTO saved = profileService.getProfile(1L);
        assertThat(saved.getGender()).isEqualTo("MALE");
        assertThat(saved.getEmploymentType()).isEqualTo("PART_TIME");
        assertThat(saved.getCareerType()).isEqualTo("EXPERIENCED");
        assertThat(saved.getContactMethod()).isEqualTo("EMAIL");
    }
}
