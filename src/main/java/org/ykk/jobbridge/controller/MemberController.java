package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ykk.jobbridge.dto.JoinRequest;
import org.ykk.jobbridge.dto.LoginRequest;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.service.MemberService;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MemberController {

    private static final String LOGIN_MEMBER =
            "loginMember";

    private final MemberService memberService;

    public MemberController(
            MemberService memberService
    ) {
        this.memberService = memberService;
    }

    /**
     * 회원가입 화면
     */
    @GetMapping("/join")
    public String joinForm(Model model) {

        JoinRequest request =
                new JoinRequest();

        request.setRole("JOB_SEEKER");
        request.setGender("MALE");

        model.addAttribute(
                "joinRequest",
                request
        );

        return "join";
    }

    /**
     * 회원가입 처리
     */
    @PostMapping("/join")
    public String join(
            @ModelAttribute JoinRequest request,
            Model model
    ) {

        try {
            memberService.join(request);

            return "redirect:/login?joined=true";

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            request.setPassword("");
            request.setPasswordConfirm("");

            model.addAttribute(
                    "joinRequest",
                    request
            );

            model.addAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            return "join";
        }
    }

    /**
     * 로그인 화면
     */
    @GetMapping("/login")
    public String loginForm(
            HttpSession session,
            Model model
    ) {

        if (session.getAttribute(
                LOGIN_MEMBER
        ) != null) {
            return "redirect:/";
        }

        model.addAttribute(
                "loginRequest",
                new LoginRequest()
        );

        return "login";
    }

    /**
     * 로그인 처리
     */
    @PostMapping("/login")
    public String login(
            @ModelAttribute LoginRequest request,
            HttpServletRequest httpRequest,
            Model model
    ) {

        try {
            SessionMember loginMember =
                    memberService.login(request);

            HttpSession oldSession =
                    httpRequest.getSession(false);

            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession newSession =
                    httpRequest.getSession(true);

            newSession.setAttribute(
                    LOGIN_MEMBER,
                    loginMember
            );

            return "redirect:/";

        } catch (IllegalArgumentException e) {

            request.setPassword("");

            model.addAttribute(
                    "loginRequest",
                    request
            );

            model.addAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            return "login";
        }
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public String logout(
            HttpServletRequest request
    ) {

        HttpSession session =
                request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "redirect:/login?logout=true";
    }

    /**
     * 아이디 중복 확인
     */
    @GetMapping("/api/members/check-login-id")
    @ResponseBody
    public Map<String, Object> checkLoginId(
            @RequestParam String loginId
    ) {

        boolean available =
                memberService.isLoginIdAvailable(
                        loginId
                );

        Map<String, Object> response =
                new HashMap<>();

        response.put("available", available);

        response.put(
                "message",
                available
                        ? "사용 가능한 아이디입니다."
                        : "사용할 수 없거나 이미 사용 중인 아이디입니다."
        );

        return response;
    }

    @PostMapping("/api/members")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> joinApi(
            @RequestBody JoinRequest request
    ) {
        try {
            memberService.join(request);

            Map<String, Object> response = new HashMap<>();
            response.put("memberId", request.getId());
            response.put("loginId", request.getLoginId());
            response.put("name", request.getName());
            response.put("message", "회원가입이 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/api/members/login")
    @ResponseBody
    public ResponseEntity<?> loginApi(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        try {
            SessionMember loginMember = memberService.login(request);
            HttpSession oldSession = httpRequest.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession newSession = httpRequest.getSession(true);
            newSession.setAttribute(LOGIN_MEMBER, loginMember);
            return ResponseEntity.ok(loginMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/api/members/me")
    @ResponseBody
    public ResponseEntity<SessionMember> currentMember(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.noContent().build();
        }

        SessionMember loginMember = (SessionMember) session.getAttribute(LOGIN_MEMBER);
        return loginMember == null
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(loginMember);
    }

    @PostMapping("/api/members/logout")
    @ResponseBody
    public ResponseEntity<Void> logoutApi(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }
}
