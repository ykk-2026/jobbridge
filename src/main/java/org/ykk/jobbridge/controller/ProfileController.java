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

/*
 * /api/profiles 로 시작되는 URL은 무조건 ProfileController에서 처리
 * 구직자 프로필 조회, 저장 기능 수행
 * */
@Slf4j
@RequestMapping(value = "/api/profiles")
@RequiredArgsConstructor
@Controller
public class ProfileController {

    private final IProfileService profileService;

    /**
     * 구직자 프로필 조회
     */
    @ResponseBody
    @GetMapping(value = "getProfileInfo")
    public JobSeekerProfileDTO getProfileInfo(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getProfileInfo Start!");

        String memberId = CmmUtil.nvl(request.getParameter("memberId"), "0"); // 회원 고유번호

        /*
         * ####################################################################################
         * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
         * ####################################################################################
         */
        log.info("memberId : " + memberId);

        JobSeekerProfileDTO pDTO = new JobSeekerProfileDTO();
        pDTO.setMemberId(Long.parseLong(memberId));

        JobSeekerProfileDTO rDTO = Optional.ofNullable(profileService.getProfileInfo(pDTO))
                .orElseGet(JobSeekerProfileDTO::new);

        log.info(this.getClass().getName() + ".getProfileInfo End!");

        return rDTO;
    }

    /**
     * 구직자 프로필 저장 (프로필이 없으면 등록, 있으면 수정)
     * <p>
     * 본인 프로필만 저장 가능하며, 저장 후 세션의 이름도 변경된 이름으로 갱신함
     */
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

            String memberId = CmmUtil.nvl(request.getParameter("memberId"), "0"); // 회원 고유번호

            // 회원 기본 정보
            String name = CmmUtil.nvl(request.getParameter("name")).trim(); // 이름
            String birthDate = CmmUtil.nvl(request.getParameter("birthDate")); // 생년월일
            String gender = CmmUtil.nvl(request.getParameter("gender")); // 성별
            String email = CmmUtil.nvl(request.getParameter("email")); // 이메일
            String phone = CmmUtil.nvl(request.getParameter("phone")); // 전화번호

            // 프로필 정보
            String profileImageUrl = CmmUtil.nvl(request.getParameter("profileImageUrl")); // 프로필 이미지
            String residenceRegion = CmmUtil.nvl(request.getParameter("residenceRegion")); // 거주 지역
            String desiredJob = CmmUtil.nvl(request.getParameter("desiredJob")); // 희망 직무
            String desiredRegion = CmmUtil.nvl(request.getParameter("desiredRegion")); // 희망 지역
            String employmentType = CmmUtil.nvl(request.getParameter("employmentType")); // 희망 고용형태
            String careerType = CmmUtil.nvl(request.getParameter("careerType")); // 경력 구분
            String careerYears = CmmUtil.nvl(request.getParameter("careerYears")); // 경력 연수
            String minSalary = CmmUtil.nvl(request.getParameter("minSalary")); // 희망 최소 연봉
            String remotePreferred = CmmUtil.nvl(request.getParameter("remotePreferred"), "false");
            String flexiblePreferred = CmmUtil.nvl(request.getParameter("flexiblePreferred"), "false");
            String wheelchairRequired = CmmUtil.nvl(request.getParameter("wheelchairRequired"), "false");
            String accessibleRestroomRequired = CmmUtil.nvl(request.getParameter("accessibleRestroomRequired"), "false");
            String disabledParkingRequired = CmmUtil.nvl(request.getParameter("disabledParkingRequired"), "false");
            String assistiveDeviceRequired = CmmUtil.nvl(request.getParameter("assistiveDeviceRequired"), "false");
            String hybridPreferred = CmmUtil.nvl(request.getParameter("hybridPreferred"), "false");
            String onsitePreferred = CmmUtil.nvl(request.getParameter("onsitePreferred"), "false");
            String contactTimeStart = CmmUtil.nvl(request.getParameter("contactTimeStart")); // 연락 가능 시작
            String contactTimeEnd = CmmUtil.nvl(request.getParameter("contactTimeEnd")); // 연락 가능 종료
            String contactMethod = CmmUtil.nvl(request.getParameter("contactMethod")); // 연락 방법
            String introduction = CmmUtil.nvl(request.getParameter("introduction")); // 자기소개
            String profilePublic = CmmUtil.nvl(request.getParameter("profilePublic"), "false"); // 공개 여부

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
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
                pDTO.setCareerType(careerType);
                pDTO.setContactTimeStart(contactTimeStart);
                pDTO.setContactTimeEnd(contactTimeEnd);
                pDTO.setContactMethod(contactMethod);
                pDTO.setIntroduction(introduction);

                // 숫자 값은 입력하지 않았으면 null
                if (careerYears.length() > 0) {
                    pDTO.setCareerYears(Integer.parseInt(careerYears));
                }
                if (minSalary.length() > 0) {
                    pDTO.setMinSalary(Integer.parseInt(minSalary));
                }

                // 체크박스 값은 "true" / "false" 문자열로 전달되기 때문에 Boolean으로 변환
                pDTO.setRemotePreferred(Boolean.parseBoolean(remotePreferred));
                pDTO.setFlexiblePreferred(Boolean.parseBoolean(flexiblePreferred));
                pDTO.setWheelchairRequired(Boolean.parseBoolean(wheelchairRequired));
                pDTO.setAccessibleRestroomRequired(Boolean.parseBoolean(accessibleRestroomRequired));
                pDTO.setDisabledParkingRequired(Boolean.parseBoolean(disabledParkingRequired));
                pDTO.setAssistiveDeviceRequired(Boolean.parseBoolean(assistiveDeviceRequired));
                pDTO.setHybridPreferred(Boolean.parseBoolean(hybridPreferred));
                pDTO.setOnsitePreferred(Boolean.parseBoolean(onsitePreferred));
                pDTO.setProfilePublic(Boolean.parseBoolean(profilePublic));

                res = profileService.saveProfileInfo(pDTO);

                log.info("프로필 저장 결과(res) : " + res);

                if (res == 1) {
                    msg = "저장되었습니다.";

                    // 본인 프로필을 저장했다면 세션에 저장된 이름도 갱신
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
