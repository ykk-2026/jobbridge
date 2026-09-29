package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.dto.MsgDTO;
import org.ykk.jobbridge.service.IProfileService;
import org.ykk.jobbridge.util.CmmUtil;

import java.util.Optional;

@Slf4j
@RequestMapping(value = "/api/profiles")
@RequiredArgsConstructor
@Controller
public class ProfileController {

    private final IProfileService profileService;

    @ResponseBody
    @GetMapping(value = "getProfileInfo")
    public JobSeekerProfileDTO getProfileInfo(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getProfileInfo Start!");

        String memberId = CmmUtil.nvl(request.getParameter("memberId"), "0");

        log.info("memberId : " + memberId);

        JobSeekerProfileDTO pDTO = new JobSeekerProfileDTO();
        pDTO.setMemberId(Long.parseLong(memberId));

        JobSeekerProfileDTO rDTO = Optional.ofNullable(profileService.getProfileInfo(pDTO))
                .orElseGet(JobSeekerProfileDTO::new);

        log.info(this.getClass().getName() + ".getProfileInfo End!");

        return rDTO;
    }

    @ResponseBody
    @PostMapping(value = "saveProfileInfo")
    public MsgDTO saveProfileInfo(@RequestBody JobSeekerProfileDTO pDTO, HttpSession session) {

        log.info(this.getClass().getName() + ".saveProfileInfo Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String sessionMemberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));
            String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));

            String memberId = String.valueOf(pDTO.getMemberId());
            String name = CmmUtil.nvl(pDTO.getName()).trim();

            log.info("session memberId : " + sessionMemberId);
            log.info("memberId : " + memberId);
            log.info("name : " + name);

            if (sessionMemberId.isEmpty()) {
                msg = "로그인이 필요합니다.";

            } else if (pDTO.getMemberId() == null
                    || (!sessionMemberId.equals(memberId) && !userRole.equals("ADMIN"))) {
                msg = "본인 프로필만 수정할 수 있습니다.";

            } else {
                pDTO.setName(name);
                applyDefaults(pDTO);

                res = profileService.saveProfileInfo(pDTO);

                log.info("프로필 저장 결과(res) : " + res);

                if (res == 1) {
                    msg = "저장되었습니다.";

                    if (sessionMemberId.equals(memberId) && name.length() > 0) {
                        session.setAttribute("SESSION_USER_NAME", name);
                    }

                } else {
                    msg = "오류로 인해 저장이 실패하였습니다.";
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

            log.info(this.getClass().getName() + ".saveProfileInfo End!");
        }

        return dto;
    }

    private void applyDefaults(JobSeekerProfileDTO dto) {
        if (dto.getWorkType() == null) dto.setWorkType("ANY");
        if (dto.getEducationLevel() == null) dto.setEducationLevel("ANY");
        if (dto.getWheelchairRequired() == null) dto.setWheelchairRequired(false);
        if (dto.getAccessibleRestroomRequired() == null) dto.setAccessibleRestroomRequired(false);
        if (dto.getDisabledParkingRequired() == null) dto.setDisabledParkingRequired(false);
        if (dto.getAssistiveDeviceRequired() == null) dto.setAssistiveDeviceRequired(false);
        if (dto.getRestAreaRequired() == null) dto.setRestAreaRequired(false);
        if (dto.getElevatorRequired() == null) dto.setElevatorRequired(false);
        if (dto.getProfilePublic() == null) dto.setProfilePublic(false);
    }
}
