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
import org.ykk.jobbridge.dto.MemberDTO;
import org.ykk.jobbridge.dto.MsgDTO;
import org.ykk.jobbridge.service.IMemberService;
import org.ykk.jobbridge.util.CmmUtil;
import org.ykk.jobbridge.util.EncryptUtil;

import java.util.Optional;

@Slf4j
@RequestMapping(value = "/api/members")
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final IMemberService memberService;

    @ResponseBody
    @GetMapping(value = "getLoginIdExists")
    public MemberDTO getLoginIdExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getLoginIdExists Start!");

        String loginId = CmmUtil.nvl(request.getParameter("loginId")).trim().toLowerCase();

        log.info("loginId : " + loginId);

        MemberDTO pDTO = new MemberDTO();
        pDTO.setLoginId(loginId);

        MemberDTO rDTO = Optional.ofNullable(memberService.getLoginIdExists(pDTO))
                .orElseGet(MemberDTO::new);

        log.info(this.getClass().getName() + ".getLoginIdExists End!");

        return rDTO;
    }

    @ResponseBody
    @GetMapping(value = "getEmailExists")
    public MemberDTO getEmailExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getEmailExists Start!");

        String email = CmmUtil.nvl(request.getParameter("email")).trim();

        log.info("email : " + email);

        MemberDTO pDTO = new MemberDTO();
        pDTO.setEmail(email);

        MemberDTO rDTO = Optional.ofNullable(memberService.getEmailExists(pDTO))
                .orElseGet(MemberDTO::new);

        log.info(this.getClass().getName() + ".getEmailExists End!");

        return rDTO;
    }

    @ResponseBody
    @PostMapping(value = "insertMemberInfo")
    public MsgDTO insertMemberInfo(HttpServletRequest request) {

        log.info(this.getClass().getName() + ".insertMemberInfo Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String loginId = CmmUtil.nvl(request.getParameter("loginId")).trim().toLowerCase();
            String password = CmmUtil.nvl(request.getParameter("password"));
            String passwordConfirm = CmmUtil.nvl(request.getParameter("passwordConfirm"));
            String name = CmmUtil.nvl(request.getParameter("name")).trim();
            String birthDate = CmmUtil.nvl(request.getParameter("birthDate")).trim();
            String gender = CmmUtil.nvl(request.getParameter("gender"), "OTHER");
            String email = CmmUtil.nvl(request.getParameter("email")).trim();
            String phone = CmmUtil.nvl(request.getParameter("phone")).trim();
            String role = CmmUtil.nvl(request.getParameter("role"), "JOB_SEEKER");
            String desiredJob = CmmUtil.nvl(request.getParameter("desiredJob")).trim();

            log.info("loginId : " + loginId);
            log.info("name : " + name);
            log.info("birthDate : " + birthDate);
            log.info("gender : " + gender);
            log.info("email : " + email);
            log.info("phone : " + phone);
            log.info("role : " + role);
            log.info("desiredJob : " + desiredJob);

            if (loginId.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
                msg = "필수 회원 정보를 모두 입력해 주세요.";

            } else if (!password.equals(passwordConfirm)) {
                msg = "비밀번호 확인이 일치하지 않습니다.";

            } else {

                MemberDTO pDTO = new MemberDTO();
                pDTO.setLoginId(loginId);
                pDTO.setPassword(EncryptUtil.encHashSHA256(password));
                pDTO.setName(name);
                pDTO.setBirthDate(birthDate);
                pDTO.setGender(gender);
                pDTO.setEmail(email);
                pDTO.setPhone(phone);
                pDTO.setRole(role);
                pDTO.setDesiredJob(desiredJob);

                res = memberService.insertMemberInfo(pDTO);

                log.info("회원가입 결과(res) : " + res);

                if (res == 1) {
                    msg = "회원가입이 완료되었습니다.";

                } else if (res == 2) {
                    msg = "이미 가입된 아이디 또는 이메일입니다.";

                } else {
                    msg = "오류로 인해 회원가입이 실패하였습니다.";
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

            log.info(this.getClass().getName() + ".insertMemberInfo End!");
        }

        return dto;
    }

    @ResponseBody
    @PostMapping(value = "login")
    public MsgDTO login(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".login Start!");

        int res = 0;
        String msg = "";
        MsgDTO dto = null;

        try {
            String loginId = CmmUtil.nvl(request.getParameter("loginId")).trim().toLowerCase();
            String password = CmmUtil.nvl(request.getParameter("password"));

            log.info("loginId : " + loginId);

            MemberDTO pDTO = new MemberDTO();
            pDTO.setLoginId(loginId);

            pDTO.setPassword(EncryptUtil.encHashSHA256(password));

            MemberDTO rDTO = memberService.getLogin(pDTO);

            if (rDTO != null && CmmUtil.nvl(rDTO.getLoginId()).length() > 0) {

                res = 1;
                msg = "로그인이 성공했습니다.";

                session.setAttribute("SESSION_MEMBER_ID", String.valueOf(rDTO.getId()));
                session.setAttribute("SESSION_USER_ID", CmmUtil.nvl(rDTO.getLoginId()));
                session.setAttribute("SESSION_USER_NAME", CmmUtil.nvl(rDTO.getName()));
                session.setAttribute("SESSION_USER_ROLE", CmmUtil.nvl(rDTO.getRole()));
                session.setAttribute("SESSION_COMPANY_NAME", "");

            } else {
                msg = "아이디 또는 비밀번호가 올바르지 않습니다.";
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

    @ResponseBody
    @GetMapping(value = "getLoginInfo")
    public MemberDTO getLoginInfo(HttpSession session) {

        log.info(this.getClass().getName() + ".getLoginInfo Start!");

        String memberId = CmmUtil.nvl((String) session.getAttribute("SESSION_MEMBER_ID"));
        String userId = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ID"));
        String userName = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_NAME"));
        String userRole = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ROLE"));

        log.info("session memberId : " + memberId);
        log.info("session userId : " + userId);

        MemberDTO rDTO = new MemberDTO();

        if (userId.length() > 0) {
            rDTO.setId(Long.parseLong(memberId));
            rDTO.setLoginId(userId);
            rDTO.setName(userName);
            rDTO.setRole(userRole);
        }

        log.info(this.getClass().getName() + ".getLoginInfo End!");

        return rDTO;
    }

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
