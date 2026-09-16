package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ykk.jobbridge.dto.LoginDTO;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.service.LoginService;

import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class LoginController {

    private static final String LOGIN_MEMBER = "loginMember";

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginDTO loginDTO,
            HttpServletRequest httpRequest
    ) {
        try {

            SessionMember loginMember = loginService.login(loginDTO);


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
        if (loginMember == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(loginMember);
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
