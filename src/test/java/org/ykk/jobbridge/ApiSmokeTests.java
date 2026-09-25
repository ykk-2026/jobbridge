package org.ykk.jobbridge;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiSmokeTests {

    @Autowired
    private MockMvc mockMvc;

    private static final MockHttpSession seekerSession = new MockHttpSession();
    private static final MockHttpSession companySession = new MockHttpSession();

    private static String jobId;
    private static String memberId;

    @Test
    @Order(1)
    void memberJoinAndLogin() throws Exception {

        mockMvc.perform(get("/api/members/getLoginIdExists").param("loginId", "smoke.user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.existsYn").value("N"));

        mockMvc.perform(post("/api/members/insertMemberInfo")
                        .param("loginId", "Smoke.User")
                        .param("password", "pass1234")
                        .param("passwordConfirm", "pass1234")
                        .param("name", "스모크 사용자")
                        .param("birthDate", "1999-01-02")
                        .param("gender", "OTHER")
                        .param("email", "smoke@example.com")
                        .param("phone", "010-0000-0000")
                        .param("role", "JOB_SEEKER")
                        .param("desiredJob", "백엔드 개발자"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(1));

        mockMvc.perform(post("/api/members/insertMemberInfo")
                        .param("loginId", "smoke.user")
                        .param("password", "pass1234")
                        .param("passwordConfirm", "pass1234")
                        .param("name", "중복")
                        .param("email", "other@example.com")
                        .param("phone", "010-0000-0001"))
                .andExpect(jsonPath("$.result").value(2));

        mockMvc.perform(get("/api/members/getLoginIdExists").param("loginId", "smoke.user"))
                .andExpect(jsonPath("$.existsYn").value("Y"));

        mockMvc.perform(post("/api/members/login").session(seekerSession)
                        .param("loginId", "smoke.user").param("password", "wrong"))
                .andExpect(jsonPath("$.msg").value("아이디 또는 비밀번호가 올바르지 않습니다."));

        mockMvc.perform(post("/api/members/login").session(seekerSession)
                        .param("loginId", "smoke.user").param("password", "pass1234"))
                .andExpect(jsonPath("$.result").value(1));

        memberId = mockMvc.perform(get("/api/members/getLoginInfo").session(seekerSession))
                .andExpect(jsonPath("$.loginId").value("smoke.user"))
                .andExpect(jsonPath("$.name").value("스모크 사용자"))
                .andExpect(jsonPath("$.role").value("JOB_SEEKER"))
                .andReturn().getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(post("/api/members/login").session(new MockHttpSession())
                        .param("loginId", "minjun.kim").param("password", "1234"))
                .andExpect(jsonPath("$.result").value(1));
    }

    @Test
    @Order(2)
    void companyJoinAndLogin() throws Exception {
        mockMvc.perform(post("/api/companies/insertCompanyInfo")
                        .param("loginId", "smoke.company")
                        .param("password", "pass1234")
                        .param("passwordConfirm", "pass1234")
                        .param("name", "담당자")
                        .param("email", "company@example.com")
                        .param("phone", "02-000-0000")
                        .param("companyName", "스모크 회사")
                        .param("businessNumber", "123-45-67890")
                        .param("representativeName", "대표")
                        .param("companyAddress", "서울특별시 강남구")
                        .param("employeeCount", "10")
                        .param("establishedDate", ""))
                .andExpect(jsonPath("$.result").value(1));

        mockMvc.perform(post("/api/companies/login").session(new MockHttpSession())
                        .param("loginId", "smoke.user").param("password", "pass1234"))
                .andExpect(jsonPath("$.msg").value("기업회원 계정이 아닙니다."));

        mockMvc.perform(post("/api/companies/login").session(companySession)
                        .param("loginId", "smoke.company").param("password", "pass1234"))
                .andExpect(jsonPath("$.result").value(1));

        mockMvc.perform(get("/api/companies/getCompanyInfo").session(companySession))
                .andExpect(jsonPath("$.companyName").value("스모크 회사"))
                .andExpect(jsonPath("$.loginId").value("smoke.company"))
                .andExpect(jsonPath("$.role").value("COMPANY"))
                .andExpect(jsonPath("$.employeeCount").value(10));
    }

    @Test
    @Order(3)
    void jobPosting() throws Exception {

        mockMvc.perform(post("/api/jobs/insertJobInfo").session(seekerSession)
                        .param("companyName", "x").param("title", "x").param("jobCategory", "x")
                        .param("employmentType", "x").param("location", "x"))
                .andExpect(jsonPath("$.msg").value("기업회원만 채용공고를 등록할 수 있습니다."));

        mockMvc.perform(post("/api/jobs/insertJobInfo").session(companySession)
                        .param("companyName", "스모크 회사")
                        .param("title", "백엔드 개발자 채용")
                        .param("jobCategory", "개발")
                        .param("employmentType", "FULL_TIME")
                        .param("location", "서울특별시 강남구")
                        .param("salaryMin", "3000")
                        .param("workType", "REMOTE")
                        .param("educationLevel", "MIDDLE_SCHOOL")
                        .param("wheelchairAccessible", "false")
                        .param("deadline", "2099-12-31"))
                .andExpect(jsonPath("$.result").value(1));

        jobId = mockMvc.perform(get("/api/jobs/getJobList"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("백엔드 개발자 채용"))
                .andExpect(jsonPath("$[0].salaryMin").value(3000))
                .andExpect(jsonPath("$[0].workType").value("REMOTE"))
                .andExpect(jsonPath("$[0].educationLevel").value("MIDDLE_SCHOOL"))
                .andExpect(jsonPath("$[0].deadline").value("2099-12-31"))
                .andReturn().getResponse().getContentAsString()
                .replaceAll(".*?\"id\":(\\d+).*", "$1");

        mockMvc.perform(get("/api/jobs/getJobInfo").param("jobId", jobId))
                .andExpect(jsonPath("$.companyName").value("스모크 회사"));

        mockMvc.perform(get("/api/jobs/getMyJobList").session(companySession))
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(post("/api/jobs/updateJobInfo").session(companySession)
                        .param("jobId", jobId)
                        .param("companyName", "스모크 회사")
                        .param("title", "백엔드 개발자 채용(수정)")
                        .param("jobCategory", "개발")
                        .param("employmentType", "FULL_TIME")
                        .param("location", "서울특별시 강남구"))
                .andExpect(jsonPath("$.result").value(1));

        mockMvc.perform(get("/api/jobs/getJobInfo").param("jobId", jobId))
                .andExpect(jsonPath("$.title").value("백엔드 개발자 채용(수정)"));
    }

    @Test
    @Order(4)
    void jobApplicationAndInterestJob() throws Exception {
        mockMvc.perform(post("/api/job-applications/insertApplicationInfo").session(seekerSession)
                        .param("jobId", "job-" + jobId)
                        .param("applicantName", "지원서에 저장된 이름")
                        .param("phone", "010-9999-9999")
                        .param("email", "application@example.com")
                        .param("employmentType", "FULL_TIME"))
                .andExpect(jsonPath("$.result").value(1));

        mockMvc.perform(post("/api/job-applications/insertApplicationInfo").session(seekerSession)
                        .param("jobId", jobId)
                        .param("applicantName", "스모크 사용자")
                        .param("phone", "010-0000-0000")
                        .param("email", "smoke@example.com"))
                .andExpect(jsonPath("$.msg").value("이미 지원한 채용공고입니다."));

        mockMvc.perform(get("/api/job-applications/getApplicationList").session(seekerSession))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].jobTitle").value("백엔드 개발자 채용(수정)"))
                .andExpect(jsonPath("$[0].applicantName").value("스모크 사용자"))
                .andExpect(jsonPath("$[0].phone").value("010-0000-0000"))
                .andExpect(jsonPath("$[0].email").value("smoke@example.com"));

        mockMvc.perform(get("/api/job-applications/getCompanyApplicationList").session(companySession))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].applicantName").value("스모크 사용자"))
                .andExpect(jsonPath("$[0].phone").value("010-0000-0000"))
                .andExpect(jsonPath("$[0].email").value("smoke@example.com"));

        mockMvc.perform(post("/api/interest-jobs/insertInterestJobInfo").session(seekerSession)
                        .param("jobId", jobId)
                        .param("wheelchairAccessible", "true"))
                .andExpect(jsonPath("$.result").value(1));

        mockMvc.perform(get("/api/interest-jobs/getInterestJobList").session(seekerSession))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].jobId").value(Long.parseLong(jobId)))
                .andExpect(jsonPath("$[0].title").value("백엔드 개발자 채용(수정)"))

                .andExpect(jsonPath("$[0].wheelchairAccessible").value(false));

        mockMvc.perform(post("/api/interest-jobs/deleteInterestJobInfo").session(seekerSession)
                        .param("jobId", jobId))
                .andExpect(jsonPath("$.result").value(1));

        mockMvc.perform(get("/api/interest-jobs/getInterestJobList").session(seekerSession))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(5)
    void community() throws Exception {

        mockMvc.perform(post("/api/community/insertPostInfo")
                        .param("title", "제목").param("content", "내용"))
                .andExpect(jsonPath("$.msg").value("로그인이 필요합니다."));

        mockMvc.perform(post("/api/community/insertPostInfo").session(seekerSession)
                        .param("category", "QUESTION").param("title", "질문").param("content", "내용입니다"))
                .andExpect(jsonPath("$.result").value(1));

        String postId = mockMvc.perform(get("/api/community/getPostList").session(seekerSession))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].author").value("스모크 사용자"))
                .andExpect(jsonPath("$[0].views").value(0))
                .andReturn().getResponse().getContentAsString()
                .replaceAll(".*?\"id\":(\\d+).*", "$1");

        mockMvc.perform(get("/api/community/getPostInfo").session(seekerSession).param("postId", postId))
                .andExpect(jsonPath("$.views").value(1))
                .andExpect(jsonPath("$.category").value("QUESTION"));

        mockMvc.perform(post("/api/community/insertCommentInfo").session(companySession)
                        .param("postId", postId).param("content", "댓글입니다"))
                .andExpect(jsonPath("$.result").value(1));

        mockMvc.perform(get("/api/community/getPostInfo").session(seekerSession).param("postId", postId))
                .andExpect(jsonPath("$.replies").value(1))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].author").value("담당자"));

        mockMvc.perform(post("/api/community/insertReportInfo").session(companySession)
                        .param("postId", postId))
                .andExpect(jsonPath("$.result").value(1));

        mockMvc.perform(post("/api/community/insertReportInfo").session(companySession)
                        .param("postId", postId))
                .andExpect(jsonPath("$.msg").value("이미 신고한 게시글입니다."));

        mockMvc.perform(post("/api/community/deletePostInfo").session(companySession).param("postId", postId))
                .andExpect(jsonPath("$.msg").value("본인이 작성한 게시글만 삭제할 수 있습니다."));

        mockMvc.perform(post("/api/community/deletePostInfo").session(seekerSession).param("postId", postId))
                .andExpect(jsonPath("$.result").value(1));

        mockMvc.perform(get("/api/community/getPostList"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(6)
    void profileAndRecommendation() throws Exception {

        mockMvc.perform(post("/api/profiles/saveProfileInfo").session(companySession)
                        .param("memberId", memberId).param("name", "해커"))
                .andExpect(jsonPath("$.msg").value("본인 프로필만 수정할 수 있습니다."));

        mockMvc.perform(post("/api/profiles/saveProfileInfo").session(seekerSession)
                        .param("memberId", memberId)
                        .param("name", "스모크 사용자2")
                        .param("birthDate", "1999-01-02")
                        .param("gender", "남성")
                        .param("email", "smoke@example.com")
                        .param("phone", "010-0000-0000")
                        .param("desiredJob", "백엔드 개발자")
                        .param("desiredRegion", "서울 강남구")
                        .param("employmentType", "정규직")
                        .param("careerType", "신입")
                        .param("careerYears", "")
                        .param("minSalary", "2500")
                        .param("workType", "REMOTE")
                        .param("educationLevel", "ELEMENTARY_SCHOOL")
                        .param("contactTimeStart", "09:00")
                        .param("contactTimeEnd", "18:00")
                        .param("contactMethod", "이메일"))
                .andExpect(jsonPath("$.result").value(1));

        mockMvc.perform(get("/api/profiles/getProfileInfo").param("memberId", memberId))
                .andExpect(jsonPath("$.name").value("스모크 사용자2"))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.employmentType").value("FULL_TIME"))
                .andExpect(jsonPath("$.careerType").value("ENTRY"))
                .andExpect(jsonPath("$.contactMethod").value("EMAIL"))
                .andExpect(jsonPath("$.minSalary").value(2500))
                .andExpect(jsonPath("$.workType").value("REMOTE"))
                .andExpect(jsonPath("$.educationLevel").value("ELEMENTARY_SCHOOL"));

        mockMvc.perform(get("/api/members/getLoginInfo").session(seekerSession))
                .andExpect(jsonPath("$.name").value("스모크 사용자2"));

        mockMvc.perform(get("/api/recommendations/getRecommendationList").session(seekerSession))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].job.title").value("백엔드 개발자 채용(수정)"))
                .andExpect(jsonPath("$[0].jobMatchSource").value("RULE_FALLBACK"));

        mockMvc.perform(get("/api/recommendations/getRecommendationList").session(companySession))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(7)
    void closeAndDeleteJobAndLogout() throws Exception {
        mockMvc.perform(post("/api/jobs/updateJobClose").session(companySession).param("jobId", jobId))
                .andExpect(jsonPath("$.result").value(1));

        mockMvc.perform(get("/api/jobs/getJobList"))
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(post("/api/jobs/deleteJobInfo").session(companySession).param("jobId", jobId))
                .andExpect(jsonPath("$.result").value(1));

        mockMvc.perform(get("/api/jobs/getMyJobList").session(companySession))
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/api/admin/getOverview").session(companySession))
                .andExpect(jsonPath("$.members").doesNotExist());

        mockMvc.perform(post("/api/members/logout").session(seekerSession))
                .andExpect(jsonPath("$.result").value(1));

        mockMvc.perform(get("/api/status"))
                .andExpect(jsonPath("$.msg").value("UP"));
    }
}
