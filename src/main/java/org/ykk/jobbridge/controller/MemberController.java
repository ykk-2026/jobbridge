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

/*
 * Controller 선언해야만 Spring 프레임워크에서 Controller인지 인식 가능
 * 자바 서블릿 역할 수행
 *
 * slf4j는 스프링 프레임워크에서 로그 처리하는 인터페이스 기술이며,
 * 로그처리 기술인 log4j와 logback과 인터페이스 역할 수행함
 * 스프링 프레임워크는 기본으로 logback을 채택해서 로그 처리함
 *
 * /api/members 로 시작되는 URL은 무조건 MemberController에서 처리
 * 화면은 React에서 그리기 때문에 모든 함수는 @ResponseBody를 추가하여 결과를 JSON 구조로 전달함
 * */
@Slf4j
@RequestMapping(value = "/api/members")
@RequiredArgsConstructor
@Controller
public class MemberController {

    // @RequiredArgsConstructor 를 통해 메모리에 올라간 서비스 객체를 Controller에서 사용할 수 있게 주입함
    private final IMemberService memberService;

    /**
     * 회원 아이디 중복 체크
     * <p>
     * Ajax를 통해 정보를 전달받으며, 결과는 반드시 JSON 구조로 전달함
     */
    @ResponseBody
    @GetMapping(value = "getLoginIdExists")
    public MemberDTO getLoginIdExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getLoginIdExists Start!");

        String loginId = CmmUtil.nvl(request.getParameter("loginId")).trim().toLowerCase(); // 회원 아이디

        /*
         * ####################################################################################
         * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
         * ####################################################################################
         */
        log.info("loginId : " + loginId);

        MemberDTO pDTO = new MemberDTO();
        pDTO.setLoginId(loginId);

        // 회원 아이디를 통해 중복된 아이디인지 조회
        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        MemberDTO rDTO = Optional.ofNullable(memberService.getLoginIdExists(pDTO))
                .orElseGet(MemberDTO::new);

        log.info(this.getClass().getName() + ".getLoginIdExists End!");

        return rDTO;
    }

    /**
     * 이메일 중복 체크
     */
    @ResponseBody
    @GetMapping(value = "getEmailExists")
    public MemberDTO getEmailExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getEmailExists Start!");

        String email = CmmUtil.nvl(request.getParameter("email")).trim(); // 이메일

        log.info("email : " + email);

        MemberDTO pDTO = new MemberDTO();
        pDTO.setEmail(email);

        MemberDTO rDTO = Optional.ofNullable(memberService.getEmailExists(pDTO))
                .orElseGet(MemberDTO::new);

        log.info(this.getClass().getName() + ".getEmailExists End!");

        return rDTO;
    }

    /**
     * 회원가입 (구직자)
     * <p>
     * 비밀번호는 복호화 불가능한 해시 암호화(SHA-256)하여 저장함
     */
    @ResponseBody
    @PostMapping(value = "insertMemberInfo")
    public MsgDTO insertMemberInfo(HttpServletRequest request) {

        log.info(this.getClass().getName() + ".insertMemberInfo Start!");

        int res = 0; // 회원가입 결과
        String msg = ""; // 회원가입 결과에 대한 메시지를 전달할 변수
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String loginId = CmmUtil.nvl(request.getParameter("loginId")).trim().toLowerCase(); // 아이디
            String password = CmmUtil.nvl(request.getParameter("password")); // 비밀번호
            String passwordConfirm = CmmUtil.nvl(request.getParameter("passwordConfirm")); // 비밀번호 확인
            String name = CmmUtil.nvl(request.getParameter("name")).trim(); // 이름
            String birthDate = CmmUtil.nvl(request.getParameter("birthDate")).trim(); // 생년월일
            String gender = CmmUtil.nvl(request.getParameter("gender"), "OTHER"); // 성별
            String email = CmmUtil.nvl(request.getParameter("email")).trim(); // 이메일
            String phone = CmmUtil.nvl(request.getParameter("phone")).trim(); // 전화번호
            String role = CmmUtil.nvl(request.getParameter("role"), "JOB_SEEKER"); // 회원 구분
            String desiredJob = CmmUtil.nvl(request.getParameter("desiredJob")).trim(); // 희망 직무

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
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
                /*
                 * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
                 */
                MemberDTO pDTO = new MemberDTO();
                pDTO.setLoginId(loginId);
                pDTO.setPassword(EncryptUtil.encHashSHA256(password)); // 비밀번호는 절대로 복호화되지 않도록 해시 암호화
                pDTO.setName(name);
                pDTO.setBirthDate(birthDate);
                pDTO.setGender(gender);
                pDTO.setEmail(email);
                pDTO.setPhone(phone);
                pDTO.setRole(role);
                pDTO.setDesiredJob(desiredJob);

                /*
                 * 회원가입
                 */
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
            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e;
            res = 0;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            // 결과 메시지 전달하기
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".insertMemberInfo End!");
        }

        return dto;
    }

    /**
     * 로그인 처리
     * <p>
     * 사용자가 입력한 비밀번호를 동일한 해시 암호화 알고리즘으로 암호화시키고,
     * DB에 저장된 값과 일치하는지 체크하는 방식으로 로그인 처리함
     * 로그인 후, 로그인 여부를 체크하기 위해 SESSION 객체에 회원정보를 저장함
     */
    @ResponseBody
    @PostMapping(value = "login")
    public MsgDTO login(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".login Start!");

        int res = 0; // 로그인 처리 결과를 저장할 변수 (로그인 성공 : 1, 아이디, 비밀번호 불일치로 인한 실패 : 0, 시스템 에러 : 2)
        String msg = ""; // 로그인 결과에 대한 메시지를 전달할 변수
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String loginId = CmmUtil.nvl(request.getParameter("loginId")).trim().toLowerCase(); // 아이디
            String password = CmmUtil.nvl(request.getParameter("password")); // 비밀번호

            log.info("loginId : " + loginId);

            MemberDTO pDTO = new MemberDTO();
            pDTO.setLoginId(loginId);

            // 비밀번호는 절대로 복호화되지 않도록 해시 알고리즘으로 암호화함
            pDTO.setPassword(EncryptUtil.encHashSHA256(password));

            // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기 위한 서비스 호출
            MemberDTO rDTO = memberService.getLogin(pDTO);

            /*
             * 로그인을 성공했다면, 회원정보가 존재함
             * 로그인 여부를 체크하기 위해 세션에 회원 고유번호, 아이디, 이름, 구분을 저장함
             */
            if (rDTO != null && CmmUtil.nvl(rDTO.getLoginId()).length() > 0) {

                res = 1;
                msg = "로그인이 성공했습니다.";

                // 로그인 성공 시 이전 세션 값은 지우고 새로 저장
                session.setAttribute("SESSION_MEMBER_ID", String.valueOf(rDTO.getId()));
                session.setAttribute("SESSION_USER_ID", CmmUtil.nvl(rDTO.getLoginId()));
                session.setAttribute("SESSION_USER_NAME", CmmUtil.nvl(rDTO.getName()));
                session.setAttribute("SESSION_USER_ROLE", CmmUtil.nvl(rDTO.getRole()));
                session.setAttribute("SESSION_COMPANY_NAME", "");

            } else {
                msg = "아이디 또는 비밀번호가 올바르지 않습니다.";
            }

        } catch (Exception e) {
            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "시스템 문제로 로그인이 실패했습니다.";
            res = 2;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            // 결과 메시지 전달하기
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".login End!");
        }

        return dto;
    }

    /**
     * 로그인된 회원정보 조회 (세션 값 전달)
     * <p>
     * 로그인하지 않았다면, 빈 값이 전달됨
     */
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

    /**
     * 로그아웃 처리 (세션 삭제)
     */
    @ResponseBody
    @PostMapping(value = "logout")
    public MsgDTO logout(HttpSession session) {

        log.info(this.getClass().getName() + ".logout Start!");

        // 세션에 저장된 로그인 정보 모두 삭제
        session.invalidate();

        MsgDTO dto = new MsgDTO();
        dto.setResult(1);
        dto.setMsg("로그아웃 되었습니다.");

        log.info(this.getClass().getName() + ".logout End!");

        return dto;
    }
}
