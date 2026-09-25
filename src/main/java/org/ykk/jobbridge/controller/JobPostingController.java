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
import org.ykk.jobbridge.dto.KeadJobSyncResultDTO;
import org.ykk.jobbridge.dto.MsgDTO;
import org.ykk.jobbridge.service.IJobPostingService;
import org.ykk.jobbridge.service.IKeadJobService;
import org.ykk.jobbridge.util.CmmUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/api/jobs")
@RequiredArgsConstructor
@Controller
public class JobPostingController {

    private final IJobPostingService jobPostingService;
    private final IKeadJobService keadJobService;

    @ResponseBody
    @PostMapping(value = "syncKeadJobs")
    public KeadJobSyncResultDTO syncKeadJobs() throws Exception {
        log.info(this.getClass().getName() + ".syncKeadJobs Start!");
        return keadJobService.syncJobs();
    }

    @ResponseBody
    @GetMapping(value = "getJobList")
    public List<JobPostingDTO> getJobList() throws Exception {

        log.info(this.getClass().getName() + ".getJobList Start!");

        List<JobPostingDTO> rList = Optional.ofNullable(jobPostingService.getJobList())
                .orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".getJobList End!");

        return rList;
    }

    @ResponseBody
    @GetMapping(value = "getJobInfo")
    public JobPostingDTO getJobInfo(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getJobInfo Start!");

        String jobId = CmmUtil.nvl(request.getParameter("jobId"), "0");

        log.info("jobId : " + jobId);

        JobPostingDTO pDTO = new JobPostingDTO();
        pDTO.setId(Long.parseLong(jobId));

        JobPostingDTO rDTO = Optional.ofNullable(jobPostingService.getJobInfo(pDTO))
                .orElseGet(JobPostingDTO::new);

        log.info(this.getClass().getName() + ".getJobInfo End!");

        return rDTO;
    }

    @ResponseBody
    @GetMapping(value = "getMyJobList")
    public List<JobPostingDTO> getMyJobList(HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".getMyJobList Start!");

        String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"), "0");
        String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));

        log.info("session memberId : " + memberId);
        log.info("session userRole : " + userRole);

        List<JobPostingDTO> rList = new ArrayList<>();

        if (userRole.equals("COMPANY")) {
            JobPostingDTO pDTO = new JobPostingDTO();
            pDTO.setCompanyMemberId(Long.parseLong(memberId));

            rList = Optional.ofNullable(jobPostingService.getMyJobList(pDTO))
                    .orElseGet(ArrayList::new);
        }

        log.info(this.getClass().getName() + ".getMyJobList End!");

        return rList;
    }

    @ResponseBody
    @PostMapping(value = "insertJobInfo")
    public MsgDTO insertJobInfo(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".insertJobInfo Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"), "0");
            String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));

            log.info("session memberId : " + memberId);
            log.info("session userRole : " + userRole);

            if (!userRole.equals("COMPANY")) {
                msg = "기업회원만 채용공고를 등록할 수 있습니다.";

            } else {

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
            String jobId = CmmUtil.nvl(request.getParameter("jobId"), "0");

            log.info("session memberId : " + memberId);
            log.info("jobId : " + jobId);

            if (!userRole.equals("COMPANY")) {
                msg = "기업회원만 채용공고를 수정할 수 있습니다.";

            } else {
                JobPostingDTO pDTO = getJobPostingParam(request);
                pDTO.setId(Long.parseLong(jobId));
                pDTO.setCompanyMemberId(Long.parseLong(memberId));

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
            String jobId = CmmUtil.nvl(request.getParameter("jobId"), "0");

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
            String jobId = CmmUtil.nvl(request.getParameter("jobId"), "0");

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

    private JobPostingDTO getJobPostingParam(HttpServletRequest request) {

        String companyName = CmmUtil.nvl(request.getParameter("companyName")).trim();
        String title = CmmUtil.nvl(request.getParameter("title")).trim();
        String jobCategory = CmmUtil.nvl(request.getParameter("jobCategory")).trim();
        String employmentType = CmmUtil.nvl(request.getParameter("employmentType")).trim();
        String location = CmmUtil.nvl(request.getParameter("location")).trim();
        String salaryMin = CmmUtil.nvl(request.getParameter("salaryMin"));
        String salaryAmount = CmmUtil.nvl(request.getParameter("salaryAmount"));
        String salaryType = CmmUtil.nvl(request.getParameter("salaryType"));
        String workType = CmmUtil.nvl(request.getParameter("workType"), "ANY");
        String experienceLevel = CmmUtil.nvl(request.getParameter("experienceLevel"));
        String educationLevel = CmmUtil.nvl(request.getParameter("educationLevel"));
        String description = CmmUtil.nvl(request.getParameter("description"));
        String requirements = CmmUtil.nvl(request.getParameter("requirements"));
        String preferredQualifications = CmmUtil.nvl(request.getParameter("preferredQualifications"));
        String accessibilityInfo = CmmUtil.nvl(request.getParameter("accessibilityInfo"));
        String wheelchairAccessible = CmmUtil.nvl(request.getParameter("wheelchairAccessible"), "false");
        String accessibleRestroom = CmmUtil.nvl(request.getParameter("accessibleRestroom"), "false");
        String disabledParking = CmmUtil.nvl(request.getParameter("disabledParking"), "false");
        String restAreaAvailable = CmmUtil.nvl(request.getParameter("restAreaAvailable"), "false");
        String elevatorAvailable = CmmUtil.nvl(request.getParameter("elevatorAvailable"), "false");
        String assistiveDeviceSupport = CmmUtil.nvl(request.getParameter("assistiveDeviceSupport"), "false");
        String deadline = CmmUtil.nvl(request.getParameter("deadline"));

        log.info("companyName : " + companyName);
        log.info("title : " + title);
        log.info("jobCategory : " + jobCategory);
        log.info("employmentType : " + employmentType);
        log.info("location : " + location);
        log.info("salaryMin : " + salaryMin);
        log.info("salaryAmount : " + salaryAmount);
        log.info("salaryType : " + salaryType);
        log.info("deadline : " + deadline);

        JobPostingDTO pDTO = new JobPostingDTO();
        pDTO.setCompanyName(companyName);
        pDTO.setTitle(title);
        pDTO.setJobCategory(jobCategory);
        pDTO.setEmploymentType(employmentType);
        pDTO.setLocation(location);
        pDTO.setSalaryType(salaryType);
        pDTO.setWorkType(workType);
        pDTO.setExperienceLevel(experienceLevel);
        pDTO.setEducationLevel(educationLevel);
        pDTO.setDescription(description);
        pDTO.setRequirements(requirements);
        pDTO.setPreferredQualifications(preferredQualifications);
        pDTO.setAccessibilityInfo(accessibilityInfo);
        pDTO.setDeadline(deadline);

        if (salaryMin.length() > 0) {
            pDTO.setSalaryMin(Integer.parseInt(salaryMin));
        }

        if (salaryAmount.length() > 0) {
            pDTO.setSalaryAmount(Long.parseLong(salaryAmount.replace(",", "")));
        }

        pDTO.setWheelchairAccessible(Boolean.parseBoolean(wheelchairAccessible));
        pDTO.setAccessibleRestroom(Boolean.parseBoolean(accessibleRestroom));
        pDTO.setDisabledParking(Boolean.parseBoolean(disabledParking));
        pDTO.setRestAreaAvailable(Boolean.parseBoolean(restAreaAvailable));
        pDTO.setElevatorAvailable(Boolean.parseBoolean(elevatorAvailable));
        pDTO.setAssistiveDeviceSupport(Boolean.parseBoolean(assistiveDeviceSupport));

        return pDTO;
    }
}
