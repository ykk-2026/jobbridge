package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.MsgDTO;
import org.ykk.jobbridge.service.IJobPostingService;
import org.ykk.jobbridge.util.CmmUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * /api/jobs 로 시작되는 URL은 무조건 JobPostingController에서 처리
 * 채용공고 조회는 누구나 가능하고, 등록/수정/마감/삭제는 기업회원(COMPANY)만 가능함
 * */
@Slf4j
@RequestMapping(value = "/api/jobs")
@RequiredArgsConstructor
@Controller
public class JobPostingController {

    private final IJobPostingService jobPostingService;

    /**
     * 진행 중인 채용공고 리스트
     */
    @ResponseBody
    @GetMapping(value = "getJobList")
    public List<JobPostingDTO> getJobList() throws Exception {

        log.info(this.getClass().getName() + ".getJobList Start!");

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<JobPostingDTO> rList = Optional.ofNullable(jobPostingService.getJobList())
                .orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".getJobList End!");

        return rList;
    }

    /**
     * 채용공고 상세보기
     */
    @ResponseBody
    @GetMapping(value = "getJobInfo")
    public JobPostingDTO getJobInfo(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getJobInfo Start!");

        String jobId = CmmUtil.nvl(request.getParameter("jobId"), "0"); // 공고 번호(PK)

        /*
         * ####################################################################################
         * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
         * ####################################################################################
         */
        log.info("jobId : " + jobId);

        JobPostingDTO pDTO = new JobPostingDTO();
        pDTO.setId(Long.parseLong(jobId));

        JobPostingDTO rDTO = Optional.ofNullable(jobPostingService.getJobInfo(pDTO))
                .orElseGet(JobPostingDTO::new);

        log.info(this.getClass().getName() + ".getJobInfo End!");

        return rDTO;
    }

    /**
     * 로그인한 기업회원이 등록한 채용공고 리스트
     */
    @ResponseBody
    @GetMapping(value = "getMyJobList")
    public List<JobPostingDTO> getMyJobList(HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".getMyJobList Start!");

        String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"), "0");
        String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));

        log.info("session memberId : " + memberId);
        log.info("session userRole : " + userRole);

        List<JobPostingDTO> rList = new ArrayList<>();

        // 기업회원만 본인 공고 조회 가능
        if (userRole.equals("COMPANY")) {
            JobPostingDTO pDTO = new JobPostingDTO();
            pDTO.setCompanyMemberId(Long.parseLong(memberId));

            rList = Optional.ofNullable(jobPostingService.getMyJobList(pDTO))
                    .orElseGet(ArrayList::new);
        }

        log.info(this.getClass().getName() + ".getMyJobList End!");

        return rList;
    }

    /**
     * 채용공고 등록
     * <p>
     * 화면에서 Ajax를 통해 값을 전달하며, 결과는 JSON 구조로 전달함
     */
    @ResponseBody
    @PostMapping(value = "insertJobInfo")
    public MsgDTO insertJobInfo(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".insertJobInfo Start!");

        int res = 0;
        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"), "0");
            String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));

            log.info("session memberId : " + memberId);
            log.info("session userRole : " + userRole);

            if (!userRole.equals("COMPANY")) {
                msg = "기업회원만 채용공고를 등록할 수 있습니다.";

            } else {
                // 화면에서 전달된 값 받기
                JobPostingDTO pDTO = getJobPostingParam(request);
                pDTO.setCompanyMemberId(Long.parseLong(memberId));

                if (CmmUtil.nvl(pDTO.getCompanyName()).isEmpty() || CmmUtil.nvl(pDTO.getTitle()).isEmpty()
                        || CmmUtil.nvl(pDTO.getJobCategory()).isEmpty()
                        || CmmUtil.nvl(pDTO.getEmploymentType()).isEmpty()
                        || CmmUtil.nvl(pDTO.getLocation()).isEmpty()) {
                    msg = "회사명, 공고 제목, 직무, 고용형태, 근무지는 필수입니다.";

                } else {
                    jobPostingService.insertJobInfo(pDTO);

                    res = 1;
                    msg = "등록되었습니다.";
                }
            }

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 0;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".insertJobInfo End!");
        }

        return dto;
    }

    /**
     * 채용공고 수정
     * <p>
     * 등록과 유사하며, 수정을 위해 반드시 PK값인 jobId를 받아야 함
     */
    @ResponseBody
    @PostMapping(value = "updateJobInfo")
    public MsgDTO updateJobInfo(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".updateJobInfo Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"), "0");
            String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));
            String jobId = CmmUtil.nvl(request.getParameter("jobId"), "0"); // 공고 번호(PK)

            log.info("session memberId : " + memberId);
            log.info("jobId : " + jobId);

            if (!userRole.equals("COMPANY")) {
                msg = "기업회원만 채용공고를 수정할 수 있습니다.";

            } else {
                JobPostingDTO pDTO = getJobPostingParam(request);
                pDTO.setId(Long.parseLong(jobId));
                pDTO.setCompanyMemberId(Long.parseLong(memberId));

                // 수정된 건수가 0이면 본인 회사의 공고가 아님
                if (jobPostingService.updateJobInfo(pDTO) > 0) {
                    res = 1;
                    msg = "수정되었습니다.";

                } else {
                    msg = "본인 회사의 채용공고만 수정할 수 있습니다.";
                }
            }

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 0;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".updateJobInfo End!");
        }

        return dto;
    }

    /**
     * 채용공고 마감
     */
    @ResponseBody
    @PostMapping(value = "updateJobClose")
    public MsgDTO updateJobClose(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".updateJobClose Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"), "0");
            String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));
            String jobId = CmmUtil.nvl(request.getParameter("jobId"), "0"); // 공고 번호(PK)

            log.info("session memberId : " + memberId);
            log.info("jobId : " + jobId);

            if (!userRole.equals("COMPANY")) {
                msg = "기업회원만 채용공고를 마감할 수 있습니다.";

            } else {
                JobPostingDTO pDTO = new JobPostingDTO();
                pDTO.setId(Long.parseLong(jobId));
                pDTO.setCompanyMemberId(Long.parseLong(memberId));

                if (jobPostingService.updateJobClose(pDTO) > 0) {
                    res = 1;
                    msg = "마감되었습니다.";

                } else {
                    msg = "본인 회사의 진행 중인 공고만 마감할 수 있습니다.";
                }
            }

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 0;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".updateJobClose End!");
        }

        return dto;
    }

    /**
     * 채용공고 삭제
     */
    @ResponseBody
    @PostMapping(value = "deleteJobInfo")
    public MsgDTO deleteJobInfo(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".deleteJobInfo Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"), "0");
            String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));
            String jobId = CmmUtil.nvl(request.getParameter("jobId"), "0"); // 공고 번호(PK)

            log.info("session memberId : " + memberId);
            log.info("jobId : " + jobId);

            if (!userRole.equals("COMPANY")) {
                msg = "기업회원만 채용공고를 삭제할 수 있습니다.";

            } else {
                JobPostingDTO pDTO = new JobPostingDTO();
                pDTO.setId(Long.parseLong(jobId));
                pDTO.setCompanyMemberId(Long.parseLong(memberId));

                if (jobPostingService.deleteJobInfo(pDTO) > 0) {
                    res = 1;
                    msg = "삭제되었습니다.";

                } else {
                    msg = "본인 회사의 채용공고만 삭제할 수 있습니다.";
                }
            }

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 0;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".deleteJobInfo End!");
        }

        return dto;
    }

    /**
     * 화면에서 전달된 채용공고 값들을 받아 DTO에 저장
     * 등록과 수정에서 동일하게 사용함
     */
    private JobPostingDTO getJobPostingParam(HttpServletRequest request) {

        String companyName = CmmUtil.nvl(request.getParameter("companyName")).trim(); // 회사명
        String title = CmmUtil.nvl(request.getParameter("title")).trim(); // 공고 제목
        String jobCategory = CmmUtil.nvl(request.getParameter("jobCategory")).trim(); // 직무
        String employmentType = CmmUtil.nvl(request.getParameter("employmentType")).trim(); // 고용형태
        String location = CmmUtil.nvl(request.getParameter("location")).trim(); // 근무지
        String salaryMin = CmmUtil.nvl(request.getParameter("salaryMin")); // 최소 급여
        String salaryMax = CmmUtil.nvl(request.getParameter("salaryMax")); // 최대 급여
        String experienceLevel = CmmUtil.nvl(request.getParameter("experienceLevel")); // 경력 조건
        String educationLevel = CmmUtil.nvl(request.getParameter("educationLevel")); // 학력 조건
        String description = CmmUtil.nvl(request.getParameter("description")); // 업무 내용
        String requirements = CmmUtil.nvl(request.getParameter("requirements")); // 자격 요건
        String preferredQualifications = CmmUtil.nvl(request.getParameter("preferredQualifications")); // 우대 사항
        String accessibilityInfo = CmmUtil.nvl(request.getParameter("accessibilityInfo")); // 접근성 정보
        String wheelchairAccessible = CmmUtil.nvl(request.getParameter("wheelchairAccessible"), "false"); // 휠체어
        String accessibleRestroom = CmmUtil.nvl(request.getParameter("accessibleRestroom"), "false"); // 장애인 화장실
        String disabledParking = CmmUtil.nvl(request.getParameter("disabledParking"), "false"); // 장애인 주차
        String remoteAvailable = CmmUtil.nvl(request.getParameter("remoteAvailable"), "false"); // 재택근무
        String flexibleWorkAvailable = CmmUtil.nvl(request.getParameter("flexibleWorkAvailable"), "false"); // 유연근무
        String assistiveDeviceSupport = CmmUtil.nvl(request.getParameter("assistiveDeviceSupport"), "false"); // 보조기기
        String deadline = CmmUtil.nvl(request.getParameter("deadline")); // 마감일

        /*
         * ####################################################################################
         * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
         * ####################################################################################
         */
        log.info("companyName : " + companyName);
        log.info("title : " + title);
        log.info("jobCategory : " + jobCategory);
        log.info("employmentType : " + employmentType);
        log.info("location : " + location);
        log.info("salaryMin : " + salaryMin);
        log.info("salaryMax : " + salaryMax);
        log.info("deadline : " + deadline);
        log.info("remoteAvailable : " + remoteAvailable);

        /*
         * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
         */
        JobPostingDTO pDTO = new JobPostingDTO();
        pDTO.setCompanyName(companyName);
        pDTO.setTitle(title);
        pDTO.setJobCategory(jobCategory);
        pDTO.setEmploymentType(employmentType);
        pDTO.setLocation(location);
        pDTO.setExperienceLevel(experienceLevel);
        pDTO.setEducationLevel(educationLevel);
        pDTO.setDescription(description);
        pDTO.setRequirements(requirements);
        pDTO.setPreferredQualifications(preferredQualifications);
        pDTO.setAccessibilityInfo(accessibilityInfo);
        pDTO.setDeadline(deadline);

        // 급여는 숫자로 변환하며, 입력하지 않았으면 null
        if (salaryMin.length() > 0) {
            pDTO.setSalaryMin(Integer.parseInt(salaryMin));
        }
        if (salaryMax.length() > 0) {
            pDTO.setSalaryMax(Integer.parseInt(salaryMax));
        }

        // 체크박스 값은 "true" / "false" 문자열로 전달되기 때문에 Boolean으로 변환
        pDTO.setWheelchairAccessible(Boolean.parseBoolean(wheelchairAccessible));
        pDTO.setAccessibleRestroom(Boolean.parseBoolean(accessibleRestroom));
        pDTO.setDisabledParking(Boolean.parseBoolean(disabledParking));
        pDTO.setRemoteAvailable(Boolean.parseBoolean(remoteAvailable));
        pDTO.setFlexibleWorkAvailable(Boolean.parseBoolean(flexibleWorkAvailable));
        pDTO.setAssistiveDeviceSupport(Boolean.parseBoolean(assistiveDeviceSupport));

        return pDTO;
    }
}
