package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ykk.jobbridge.dto.JoinRequest;
import org.ykk.jobbridge.dto.LoginRequest;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.service.MemberService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private static final String LOGIN_MEMBER = "loginMember";

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/check-login-id")
    public Map<String, Object> checkLoginId(@RequestParam String loginId) {
        boolean available = memberService.isLoginIdAvailable(loginId);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put(
                "message",
                available
                        ? "사용 가능한 아이디입니다."
                        : "사용할 수 없거나 이미 사용 중인 아이디입니다."
        );
        return response;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> join(@RequestBody JoinRequest request) {
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

    @PostMapping("/login")
    public ResponseEntity<?> login(
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

    @GetMapping("/me")
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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }
}
