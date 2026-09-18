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
import org.ykk.jobbridge.dto.CompanyProfileDTO;
import org.ykk.jobbridge.dto.














        MemberDTO;
import org.ykk.jobbridge.dto.MsgDTO;
import org.ykk.jobbridge.service.ICompanyService;
import org.ykk.jobbridge.service.IMemberService;
import org.ykk.jobbridge.util.CmmUtil;
import org.ykk.jobbridge.util.EncryptUtil;

import java.util.Optional;

/*
 * /api/companies 로 시작되는 URL은 무조건 CompanyController에서 처리
 * 기업회원 가입, 기업회원 로그인, 기업정보 조회 기능 수행
 * */
@Slf4j
@RequestMapping(value = "/api/companies")
@RequiredArgsConstructor
@Controller
public class CompanyController {

    private final ICompanyService companyService;

    // 기업회원 로그인은 MEMBER 테이블로 처리하기 때문에 회원 서비스도 함께 사용함
    private final IMemberService memberService;

    /**
     * 기업회원 가입
     * <p>
     * 담당자 정보는 MEMBER 테이블, 기업정보는 COMPANY_PROFILE 테이블에 저장함
     */
    @ResponseBody
    @PostMapping(value = "insertCompanyInfo")
    public MsgDTO insertCompanyInfo(HttpServletRequest request) {

        log.info(this.getClass().getName() + ".insertCompanyInfo Start!");

        int res = 0; // 가입 결과
        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            // 담당자 회원정보
            String loginId = CmmUtil.nvl(request.getParameter("loginId")).trim().toLowerCase(); // 아이디
            String password = CmmUtil.nvl(request.getParameter("password")); // 비밀번호
            String passwordConfirm = CmmUtil.nvl(request.getParameter("passwordConfirm")); // 비밀번호 확인
            String name = CmmUtil.nvl(request.getParameter("name")).trim(); // 담당자 이름
            String email = CmmUtil.nvl(request.getParameter("email")).trim(); // 이메일
            String phone = CmmUtil.nvl(request.getParameter("phone")).trim(); // 전화번호

            // 기업정보
            String companyName = CmmUtil.nvl(request.getParameter("companyName")).trim(); // 회사명
            String businessNumber = CmmUtil.nvl(request.getParameter("businessNumber")).trim(); // 사업자등록번호
            String representativeName = CmmUtil.nvl(request.getParameter("representativeName")).trim(); // 대표자명
            String industry = CmmUtil.nvl(request.getParameter("industry")); // 업종
            String companyAddress = CmmUtil.nvl(request.getParameter("companyAddress")).trim(); // 회사 주소
            String companyDetailAddress = CmmUtil.nvl(request.getParameter("companyDetailAddress")); // 상세 주소
            String companyPhone = CmmUtil.nvl(request.getParameter("companyPhone")); // 회사 전화번호
            String websiteUrl = CmmUtil.nvl(request.getParameter("websiteUrl")); // 홈페이지
            String logoUrl = CmmUtil.nvl(request.getParameter("logoUrl")); // 로고
            String companyDescription = CmmUtil.nvl(request.getParameter("companyDescription")); // 회사 소개
            String employeeCount = CmmUtil.nvl(request.getParameter("employeeCount")); // 직원 수
            String establishedDate = CmmUtil.nvl(request.getParameter("establishedDate")); // 설립일

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("loginId : " + loginId);
            log.info("name : " + name);
            log.info("email : " + email);
            log.info("phone : " + phone);
            log.info("companyName : " + companyName);
            log.info("businessNumber : " + businessNumber);
            log.info("representativeName : " + representativeName);
            log.info("companyAddress : " + companyAddress);
            log.info("employeeCount : " + employeeCount);
            log.info("establishedDate : " + establishedDate);

            if (loginId.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty()
                    || companyName.isEmpty() || businessNumber.isEmpty() || representativeName.isEmpty()
                    || companyAddress.isEmpty()) {
                msg = "필수 기업회원 정보를 모두 입력해 주세요.";

            } else if (!password.equals(passwordConfirm)) {
                msg = "비밀번호가 일치하지 않습니다.";

            } else {
                // 담당자 회원정보 DTO
                MemberDTO pDTO = new MemberDTO();
                pDTO.setLoginId(loginId);
                pDTO.setPassword(EncryptUtil.encHashSHA256(password)); // 비밀번호 해시 암호화
                pDTO.setName(name);
                pDTO.setEmail(email);
                pDTO.setPhone(phone);
                pDTO.setRole("COMPANY");

                // 기업정보 DTO
                CompanyProfileDTO cDTO = new CompanyProfileDTO();
                cDTO.setCompanyName(companyName);
                cDTO.setBusinessNumber(businessNumber);
                cDTO.setRepresentativeName(representativeName);
                cDTO.setIndustry(industry);
                cDTO.setCompanyAddress(companyAddress);
                cDTO.setCompanyDetailAddress(companyDetailAddress);
                cDTO.setCompanyPhone(companyPhone);
                cDTO.setWebsiteUrl(websiteUrl);
                cDTO.setLogoUrl(logoUrl);
                cDTO.setCompanyDescription(companyDescription);
                cDTO.setEstablishedDate(establishedDate);

                // 직원 수는 숫자로 변환하며, 입력하지 않았으면 null
                if (employeeCount.length() > 0) {
                    cDTO.setEmployeeCount(Integer.parseInt(employeeCount));
                }

                res = companyService.insertCompanyInfo(pDTO, cDTO);

                log.info("기업회원 가입 결과(res) : " + res);

                if (res == 1) {
                    msg = "기업회원 가입이 완료되었습니다.";

                } else if (res == 2) {
                    msg = "이미 가입된 아이디, 이메일 또는 사업자등록번호입니다.";

                } else {
                    msg = "오류로 인해 기업회원 가입이 실패하였습니다.";
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

            log.info(this.getClass().getName() + ".insertCompanyInfo End!");
        }

        return dto;
    }

    /**
     * 기업회원 로그인 처리
     * <p>
     * 회원 로그인과 동일하지만, 회원 구분이 COMPANY인 경우만 로그인 처리하고 세션에 회사명도 저장함
     */
    @ResponseBody
    @PostMapping(value = "login")
    public MsgDTO login(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".login Start!");

        int res = 0; // 로그인 성공 : 1, 실패 : 0, 시스템 에러 : 2
        String msg = "";
        MsgDTO dto = null;

        try {
            String loginId = CmmUtil.nvl(request.getParameter("loginId")).trim().toLowerCase(); // 아이디
            String password = CmmUtil.nvl(request.getParameter("password")); // 비밀번호

            log.info("loginId : " + loginId);

            MemberDTO pDTO = new MemberDTO();
            pDTO.setLoginId(loginId);
            pDTO.setPassword(EncryptUtil.encHashSHA256(password));

            MemberDTO rDTO = memberService.getLogin(pDTO);

            if (rDTO == null || CmmUtil.nvl(rDTO.getLoginId()).isEmpty()) {
                msg = "아이디 또는 비밀번호가 올바르지 않습니다.";

            } else if (!CmmUtil.nvl(rDTO.getRole()).equals("COMPANY")) {
                msg = "기업회원 계정이 아닙니다.";

            } else {
                // 기업정보 조회하여 회사명을 세션에 저장
                CompanyProfileDTO cDTO = new CompanyProfileDTO();
                cDTO.setMemberId(rDTO.getId());

                CompanyProfileDTO companyDTO = Optional.ofNullable(companyService.getCompanyInfo(cDTO))
                        .orElseGet(CompanyProfileDTO::new);

                res = 1;
                msg = "로그인이 성공했습니다.";

                session.setAttribute("SESSION_MEMBER_ID", String.valueOf(rDTO.getId()));
                session.setAttribute("SESSION_USER_ID", CmmUtil.nvl(rDTO.getLoginId()));
                session.setAttribute("SESSION_USER_NAME", CmmUtil.nvl(rDTO.getName()));
                session.setAttribute("SESSION_USER_ROLE", CmmUtil.nvl(rDTO.getRole()));
                session.setAttribute("SESSION_COMPANY_NAME", CmmUtil.nvl(companyDTO.getCompanyName()));
            }

        } catch (Exception e) {
            msg = "시스템 문제로 로그인이 실패했습니다.";
            res = 2;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".login End!");
        }

        return dto;
    }

    /**
     * 로그인된 기업정보 조회
     * <p>
     * 기업회원으로 로그인하지 않았다면, 빈 값이 전달됨
     */
    @ResponseBody
    @GetMapping(value = "getCompanyInfo")
    public CompanyProfileDTO getCompanyInfo(HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".getCompanyInfo Start!");

        String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));
        String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));

        log.info("session memberId : " + memberId);
        log.info("session userRole : " + userRole);

        CompanyProfileDTO rDTO = new CompanyProfileDTO();

        if (userRole.equals("COMPANY")) {
            CompanyProfileDTO pDTO = new CompanyProfileDTO();
            pDTO.setMemberId(Long.parseLong(memberId));

            rDTO = Optional.ofNullable(companyService.getCompanyInfo(pDTO))
                    .orElseGet(CompanyProfileDTO::new);
        }

        log.info(this.getClass().getName() + ".getCompanyInfo End!");

        return rDTO;
    }

    /**
     * 로그아웃 처리 (세션 삭제)
     */
    @ResponseBody
    @PostMapping(value = "logout")
    public MsgDTO logout(HttpSession session) {

        log.info(this.getClass().getName() + ".logout Start!");

        session.invalidate();

        MsgDTO dto = new MsgDTO();
        dto.setResult(1);
        dto.setMsg("로그아웃 되었습니다.");

        log.info(this.getClass().getName() + ".logout End!");

        return dto;
    }
}
