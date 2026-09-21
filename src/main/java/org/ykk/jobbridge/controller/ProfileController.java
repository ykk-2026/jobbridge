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
    public MsgDTO saveProfileInfo(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".saveProfileInfo Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String sessionMemberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));
            String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));

            String memberId = CmmUtil.nvl(request.getParameter("memberId"), "0");


            String name = CmmUtil.nvl(request.getParameter("name")).trim();
            String birthDate = CmmUtil.nvl(request.getParameter("birthDate"));
            String gender = CmmUtil.nvl(request.getParameter("gender"));
            String email = CmmUtil.nvl(request.getParameter("email"));
            String phone = CmmUtil.nvl(request.getParameter("phone"));


            String profileImageUrl = CmmUtil.nvl(request.getParameter("profileImageUrl"));
            String residenceRegion = CmmUtil.nvl(request.getParameter("residenceRegion"));
            String desiredJob = CmmUtil.nvl(request.getParameter("desiredJob"));
            String desiredRegion = CmmUtil.nvl(request.getParameter("desiredRegion"));
            String employmentType = CmmUtil.nvl(request.getParameter("employmentType"));
            String careerType = CmmUtil.nvl(request.getParameter("careerType"));
            String careerYears = CmmUtil.nvl(request.getParameter("careerYears"));
            String minSalary = CmmUtil.nvl(request.getParameter("minSalary"));
            String workType = CmmUtil.nvl(request.getParameter("workType"), "ANY");
            String wheelchairRequired = CmmUtil.nvl(request.getParameter("wheelchairRequired"), "false");
            String accessibleRestroomRequired = CmmUtil.nvl(request.getParameter("accessibleRestroomRequired"), "false");
            String disabledParkingRequired = CmmUtil.nvl(request.getParameter("disabledParkingRequired"), "false");
            String assistiveDeviceRequired = CmmUtil.nvl(request.getParameter("assistiveDeviceRequired"), "false");
            String restAreaRequired = CmmUtil.nvl(request.getParameter("restAreaRequired"), "false");
            String elevatorRequired = CmmUtil.nvl(request.getParameter("elevatorRequired"), "false");
            String contactTimeStart = CmmUtil.nvl(request.getParameter("contactTimeStart"));
            String contactTimeEnd = CmmUtil.nvl(request.getParameter("contactTimeEnd"));
            String contactMethod = CmmUtil.nvl(request.getParameter("contactMethod"));
            String introduction = CmmUtil.nvl(request.getParameter("introduction"));
            String profilePublic = CmmUtil.nvl(request.getParameter("profilePublic"), "false");






            log.info("session memberId : " + sessionMemberId);
            log.info("memberId : " + memberId);
            log.info("name : " + name);
            log.info("birthDate : " + birthDate);
            log.info("desiredJob : " + desiredJob);
            log.info("desiredRegion : " + desiredRegion);
            log.info("employmentType : " + employmentType);
            log.info("careerType : " + careerType);
            log.info("careerYears : " + careerYears);
            log.info("minSalary : " + minSalary);
            log.info("contactTimeStart : " + contactTimeStart);
            log.info("contactTimeEnd : " + contactTimeEnd);

            if (sessionMemberId.isEmpty()) {
                msg = "로그인이 필요합니다.";

            } else if (!sessionMemberId.equals(memberId) && !userRole.equals("ADMIN")) {
                msg = "본인 프로필만 수정할 수 있습니다.";

            } else {
                JobSeekerProfileDTO pDTO = new JobSeekerProfileDTO();
                pDTO.setMemberId(Long.parseLong(memberId));
                pDTO.setName(name);
                pDTO.setBirthDate(birthDate);
                pDTO.setGender(gender);
                pDTO.setEmail(email);
                pDTO.setPhone(phone);
                pDTO.setProfileImageUrl(profileImageUrl);
                pDTO.setResidenceRegion(residenceRegion);
                pDTO.setDesiredJob(desiredJob);
                pDTO.setDesiredRegion(desiredRegion);
                pDTO.setEmploymentType(employmentType);
                pDTO.setWorkType(workType);
                pDTO.setCareerType(careerType);
                pDTO.setContactTimeStart(contactTimeStart);
                pDTO.setContactTimeEnd(contactTimeEnd);
                pDTO.setContactMethod(contactMethod);
                pDTO.setIntroduction(introduction);


                if (careerYears.length() > 0) {
                    pDTO.setCareerYears(Integer.parseInt(careerYears));
                }
                if (minSalary.length() > 0) {
                    pDTO.setMinSalary(Integer.parseInt(minSalary));
                }


                pDTO.setWheelchairRequired(Boolean.parseBoolean(wheelchairRequired));
                pDTO.setAccessibleRestroomRequired(Boolean.parseBoolean(accessibleRestroomRequired));
                pDTO.setDisabledParkingRequired(Boolean.parseBoolean(disabledParkingRequired));
                pDTO.setAssistiveDeviceRequired(Boolean.parseBoolean(assistiveDeviceRequired));
                pDTO.setRestAreaRequired(Boolean.parseBoolean(restAreaRequired));
                pDTO.setElevatorRequired(Boolean.parseBoolean(elevatorRequired));
                pDTO.setProfilePublic(Boolean.parseBoolean(profilePublic));

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
}
