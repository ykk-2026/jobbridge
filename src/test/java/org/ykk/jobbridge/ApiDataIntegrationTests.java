package org.ykk.jobbridge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.JoinRequest;
import org.ykk.jobbridge.dto.CompanyLoginDTO;
import org.ykk.jobbridge.dto.CompanySignupDTO;
import org.ykk.jobbridge.dto.CommunityPostDTO;
import org.ykk.jobbridge.dto.InterestJobDTO;
import org.ykk.jobbridge.dto.JobApplicationDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.mapper.AdminMapper;
import org.ykk.jobbridge.service.CompanyService;
import org.ykk.jobbridge.mapper.CommunityMapper;
import org.ykk.jobbridge.mapper.InterestJobMapper;
import org.ykk.jobbridge.mapper.JobApplicationMapper;
import org.ykk.jobbridge.service.MemberService;
import org.ykk.jobbridge.service.JobCatalogService;
import org.ykk.jobbridge.service.ProfileService;

import java.time.LocalDate;
import java.util.Map;

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
    private CompanyService companyService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InterestJobMapper interestJobMapper;

    @Autowired
    private JobApplicationMapper jobApplicationMapper;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private CommunityMapper communityMapper;

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
    void companyCanSignupAndLogin() {
        CompanySignupDTO signup = new CompanySignupDTO();
        signup.setLoginId("company.user");
        signup.setPassword("Password!1");
        signup.setPasswordConfirm("Password!1");
        signup.setName("Company Manager");
        signup.setEmail("manager@example.com");
        signup.setPhone("010-2222-3333");
        signup.setCompanyName("Bridge Company");
        signup.setBusinessNumber("123-45-67890");
        signup.setRepresentativeName("CEO Kim");
        signup.setCompanyAddress("Seoul");
        signup.setIndustry("IT");
        signup.setEmployeeCount(10);

        companyService.signup(signup);

        Map<String, Object> savedMember = jdbcTemplate.queryForMap(
                "SELECT role, status, birth_date, gender FROM member WHERE id = ?",
                signup.getId()
        );
        assertThat(savedMember.get("ROLE")).isEqualTo("COMPANY");
        assertThat(savedMember.get("STATUS")).isEqualTo("ACTIVE");
        assertThat(savedMember.get("BIRTH_DATE")).isNull();
        assertThat(savedMember.get("GENDER")).isNull();
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM company_profile WHERE member_id = ? AND business_number = ?",
                Integer.class,
                signup.getId(),
                "123-45-67890"
        )).isEqualTo(1);

        CompanyLoginDTO login = new CompanyLoginDTO();
        login.setLoginId("company.user");
        login.setPassword("Password!1");

        CompanyService.CompanyLoginResult result = companyService.login(login);
        assertThat(result.member().getRole()).isEqualTo("COMPANY");
        assertThat(result.profile().getCompanyName()).isEqualTo("Bridge Company");
    }

    @Test
    @Transactional
    void companySignupRejectsDuplicateBusinessNumber() {
        CompanySignupDTO first = companySignup("company.one", "one@example.com", "555-55-55555");
        companyService.signup(first);

        CompanySignupDTO duplicate = companySignup("company.two", "two@example.com", "555-55-55555");

        assertThatThrownBy(() -> companyService.signup(duplicate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 등록된 사업자등록번호입니다.");
    }

    @Test
    @Transactional
    void companyLoginRejectsInvalidPasswordMissingAndInactiveAccounts() {
        CompanySignupDTO signup = companySignup("company.login", "login@example.com", "777-77-77777");
        companyService.signup(signup);

        CompanyLoginDTO wrongPassword = new CompanyLoginDTO();
        wrongPassword.setLoginId("company.login");
        wrongPassword.setPassword("Wrong!1");

        assertThatThrownBy(() -> companyService.login(wrongPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디 또는 비밀번호가 올바르지 않습니다.");

        CompanyLoginDTO missing = new CompanyLoginDTO();
        missing.setLoginId("missing.company");
        missing.setPassword("Password!1");

        assertThatThrownBy(() -> companyService.login(missing))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디 또는 비밀번호가 올바르지 않습니다.");

        jdbcTemplate.update("""
                INSERT INTO member (login_id, password, name, email, phone, role, status)
                VALUES (?, ?, ?, ?, ?, 'COMPANY', 'SUSPENDED')
                """, "inactive.company", passwordEncoder.encode("Password!1"), "Inactive Manager",
                "inactive@example.com", "010-4444-5555");
        Long inactiveMemberId = jdbcTemplate.queryForObject(
                "SELECT id FROM member WHERE login_id = ?",
                Long.class,
                "inactive.company"
        );
        jdbcTemplate.update("""
                INSERT INTO company_profile (
                    member_id, company_name, business_number, representative_name, company_address
                ) VALUES (?, ?, ?, ?, ?)
                """, inactiveMemberId, "Inactive Company", "888-88-88888", "CEO Lee", "Seoul");

        CompanyLoginDTO inactive = new CompanyLoginDTO();
        inactive.setLoginId("inactive.company");
        inactive.setPassword("Password!1");

        assertThatThrownBy(() -> companyService.login(inactive))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용할 수 없는 계정입니다.");
    }

    @Test
    @Transactional
    void jobSeekerCannotUseCompanyLogin() {
        CompanyLoginDTO login = new CompanyLoginDTO();
        login.setLoginId("minjun.kim");
        login.setPassword("Password!1");

        assertThatThrownBy(() -> companyService.login(login))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기업회원 계정이 아닙니다.");
    }

    private CompanySignupDTO companySignup(String loginId, String email, String businessNumber) {
        CompanySignupDTO signup = new CompanySignupDTO();
        signup.setLoginId(loginId);
        signup.setPassword("Password!1");
        signup.setPasswordConfirm("Password!1");
        signup.setName("Company Manager");
        signup.setEmail(email);
        signup.setPhone("010-2222-3333");
        signup.setCompanyName("Bridge Company");
        signup.setBusinessNumber(businessNumber);
        signup.setRepresentativeName("CEO Kim");
        signup.setCompanyAddress("Seoul");
        signup.setIndustry("IT");
        signup.setEmployeeCount(10);
        return signup;
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
  
    @Test
    @Transactional
    void communityPostIsStoredAndReadFromDatabase() {
        CommunityPostDTO post = new CommunityPostDTO();
        post.setMemberId(1L);
        post.setCategory("FREE");
        post.setTitle("데이터베이스 저장 테스트");
        post.setContent("브라우저 저장소가 아니라 community_post에 저장됩니다.");

        assertThat(communityMapper.insertPost(post)).isEqualTo(1);
        assertThat(post.getId()).isNotNull();
        assertThat(communityMapper.findPostById(post.getId()).getTitle()).isEqualTo(post.getTitle());

        assertThat(communityMapper.incrementViews(post.getId())).isEqualTo(1);
        assertThat(communityMapper.findPostById(post.getId()).getViewCount()).isEqualTo(1);

        assertThat(communityMapper.deletePost(post.getId(), 1L)).isEqualTo(1);
        assertThat(communityMapper.findAllPosts()).isEmpty();
    }
}
