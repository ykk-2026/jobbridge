package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public MsgDTO insertJobInfo(@ModelAttribute JobPostingDTO pDTO, HttpSession session) {

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

                applyDefaults(pDTO);
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
    public MsgDTO updateJobInfo(@ModelAttribute JobPostingDTO pDTO,
                                @RequestParam(name = "jobId", defaultValue = "0") Long jobId,
                                HttpSession session) {

        log.info(this.getClass().getName() + ".updateJobInfo Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"), "0");
            String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));
            log.info("session memberId : " + memberId);
            log.info("jobId : " + jobId);

            if (!userRole.equals("COMPANY")) {
                msg = "기업회원만 채용공고를 수정할 수 있습니다.";

            } else {
                applyDefaults(pDTO);
                pDTO.setId(jobId);
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
    public MsgDTO updateJobClose(@RequestParam(name = "jobId", defaultValue = "0") Long jobId,
                                 HttpSession session) {

        log.info(this.getClass().getName() + ".updateJobClose Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"), "0");
            String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));
            log.info("session memberId : " + memberId);
            log.info("jobId : " + jobId);

            if (!userRole.equals("COMPANY")) {
                msg = "기업회원만 채용공고를 마감할 수 있습니다.";

            } else {
                JobPostingDTO pDTO = new JobPostingDTO();
                pDTO.setId(jobId);
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
    public MsgDTO deleteJobInfo(@RequestParam(name = "jobId", defaultValue = "0") Long jobId,
                                HttpSession session) {

        log.info(this.getClass().getName() + ".deleteJobInfo Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"), "0");
            String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));
            log.info("session memberId : " + memberId);
            log.info("jobId : " + jobId);

            if (!userRole.equals("COMPANY")) {
                msg = "기업회원만 채용공고를 삭제할 수 있습니다.";

            } else {
                JobPostingDTO pDTO = new JobPostingDTO();
                pDTO.setId(jobId);
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

    private void applyDefaults(JobPostingDTO dto) {
        dto.setCompanyName(CmmUtil.nvl(dto.getCompanyName()).trim());
        dto.setTitle(CmmUtil.nvl(dto.getTitle()).trim());
        dto.setJobCategory(CmmUtil.nvl(dto.getJobCategory()).trim());
        dto.setEmploymentType(CmmUtil.nvl(dto.getEmploymentType()).trim());
        dto.setLocation(CmmUtil.nvl(dto.getLocation()).trim());
        if (dto.getWorkType() == null) dto.setWorkType("ANY");
        if (dto.getWheelchairAccessible() == null) dto.setWheelchairAccessible(false);
        if (dto.getAccessibleRestroom() == null) dto.setAccessibleRestroom(false);
        if (dto.getDisabledParking() == null) dto.setDisabledParking(false);
        if (dto.getRestAreaAvailable() == null) dto.setRestAreaAvailable(false);
        if (dto.getElevatorAvailable() == null) dto.setElevatorAvailable(false);
        if (dto.getAssistiveDeviceSupport() == null) dto.setAssistiveDeviceSupport(false);
    }
}
