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
import org.ykk.jobbridge.dto.JobApplicationDTO;
import org.ykk.jobbridge.dto.MsgDTO;
import org.ykk.jobbridge.service.IJobApplicationService;
import org.ykk.jobbridge.util.CmmUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * /api/job-applications 로 시작되는 URL은 무조건 JobApplicationController에서 처리
 * 입사 지원은 구직자(JOB_SEEKER)만, 지원자 목록 조회는 기업회원(COMPANY)만 가능함
 * */
@Slf4j
@RequestMapping(value = "/api/job-applications")
@RequiredArgsConstructor
@Controller
public class JobApplicationController {

    private final IJobApplicationService jobApplicationService;

    /**
     * 로그인한 구직자가 지원한 리스트
     */
    @ResponseBody
    @GetMapping(value = "getApplicationList")
    public List<JobApplicationDTO> getApplicationList(HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".getApplicationList Start!");

        String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"), "0");
        String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));

        log.info("session memberId : " + memberId);
        log.info("session userRole : " + userRole);

        List<JobApplicationDTO> rList = new ArrayList<>();

        if (userRole.equals("JOB_SEEKER")) {
            JobApplicationDTO pDTO = new JobApplicationDTO();
            pDTO.setMemberId(Long.parseLong(memberId));

            rList = Optional.ofNullable(jobApplicationService.getApplicationList(pDTO))
                    .orElseGet(ArrayList::new);
        }

        log.info(this.getClass().getName() + ".getApplicationList End!");

        return rList;
    }

    /**
     * 로그인한 기업회원의 공고에 지원한 지원자 리스트
     */
    @ResponseBody
    @GetMapping(value = "getCompanyApplicationList")
    public List<JobApplicationDTO> getCompanyApplicationList(HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".getCompanyApplicationList Start!");

        String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"), "0");
        String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));

        log.info("session memberId : " + memberId);
        log.info("session userRole : " + userRole);

        List<JobApplicationDTO> rList = new ArrayList<>();

        if (userRole.equals("COMPANY")) {
            JobApplicationDTO pDTO = new JobApplicationDTO();
            pDTO.setCompanyMemberId(Long.parseLong(memberId));

            rList = Optional.ofNullable(jobApplicationService.getCompanyApplicationList(pDTO))
                    .orElseGet(ArrayList::new);
        }

        log.info(this.getClass().getName() + ".getCompanyApplicationList End!");

        return rList;
    }

    /**
     * 입사 지원
     */
    @ResponseBody
    @PostMapping(value = "insertApplicationInfo")
    public MsgDTO insertApplicationInfo(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".insertApplicationInfo Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"), "0");
            String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));

            // 화면에서 "job-1" 형태로 넘어올 수 있어 앞의 "job-" 문자는 제거함
            String jobIdText = CmmUtil.nvl(request.getParameter("jobId")).trim().replaceFirst("^job-", ""); // 공고 번호
            String coverLetter = CmmUtil.nvl(request.getParameter("coverLetter"));

            String employmentType = CmmUtil.nvl(request.getParameter("employmentType")); // 고용형태

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("session memberId : " + memberId);
            log.info("jobId : " + jobIdText);
            log.info("coverLetter :"+ coverLetter);
            log.info("employmentType : " + employmentType);

            if (!userRole.equals("JOB_SEEKER")) {
                msg = "구직자 계정만 지원할 수 있습니다.";

            } else if (jobIdText.isEmpty()) {
                msg = "지원서 필수 정보가 누락되었습니다.";

            } else {
                Long jobId = Long.valueOf(jobIdText);
                JobApplicationDTO pDTO = new JobApplicationDTO();
                pDTO.setMemberId(Long.parseLong(memberId));
                pDTO.setJobId(jobId);
                pDTO.setCoverLetter(coverLetter);
                pDTO.setEmploymentType(employmentType);

                res = jobApplicationService.insertApplicationInfo(pDTO);

                log.info("입사 지원 결과(res) : " + res);

                if (res == 1) {
                    msg = "지원이 완료되었습니다.";

                } else if (res == 2) {
                    msg = "이미 지원한 채용공고입니다.";

                } else if (res == 3) {
                    msg = "마감되었거나 존재하지 않는 채용공고입니다.";

                } else {
                    msg = "오류로 인해 지원이 실패하였습니다.";
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

            log.info(this.getClass().getName() + ".insertApplicationInfo End!");
        }

        return dto;
    }
}
